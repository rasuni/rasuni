package rasuni.titan;

import com.sleepycat.je.LockMode;
import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Duration;
import com.thinkaurelius.titan.core.schema.SchemaManager;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager.IsolationLevel;
import com.thinkaurelius.titan.diskstorage.configuration.ReadConfiguration;
import com.thinkaurelius.titan.diskstorage.idmanagement.ConflictAvoidanceMode;
import com.thinkaurelius.titan.diskstorage.util.time.StandardDuration;
import com.thinkaurelius.titan.diskstorage.util.time.Timestamps;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.NOPLoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import rasuni.acoustid.AcoustId;
import rasuni.acoustid.Response;
import rasuni.acoustid.Result;
import rasuni.check.Assert;
import rasuni.functional.IConsumer;
import rasuni.functional.IConsumer2;
import rasuni.functional.IExpression;
import rasuni.functional.IExpression2;
import rasuni.functional.IExpression3;
import rasuni.functional.IExpression4;
import rasuni.functional.IProvider;
import rasuni.graph.EnumKey;
import rasuni.graph.Key;
import rasuni.musicbrainz.Area;
import rasuni.musicbrainz.Artist;
import rasuni.musicbrainz.ArtistCredit;
import rasuni.musicbrainz.Collection;
import rasuni.musicbrainz.Disc;
import rasuni.musicbrainz.Entity;
import rasuni.musicbrainz.EntityList;
import rasuni.musicbrainz.Event;
import rasuni.musicbrainz.ISRC;
import rasuni.musicbrainz.ISRCList;
import rasuni.musicbrainz.Label;
import rasuni.musicbrainz.Medium;
import rasuni.musicbrainz.MetaData;
import rasuni.musicbrainz.NameCredit;
import rasuni.musicbrainz.Place;
import rasuni.musicbrainz.Recording;
import rasuni.musicbrainz.RecordingList;
import rasuni.musicbrainz.Relation;
import rasuni.musicbrainz.RelationList;
import rasuni.musicbrainz.Release;
import rasuni.musicbrainz.ReleaseEvent;
import rasuni.musicbrainz.ReleaseEventList;
import rasuni.musicbrainz.ReleaseGroup;
import rasuni.musicbrainz.ReleaseList;
import rasuni.musicbrainz.Resource;
import rasuni.musicbrainz.Series;
import rasuni.musicbrainz.Track;
import rasuni.musicbrainz.Url;
import rasuni.musicbrainz.Work;
import rasuni.musicbrainz.YearMonthDay;
import rasuni.webservice.Parameter;
import rasuni.webservice.WebService;

/**
 * @author SigristR
 *
 */
public final class TitanCollector
{
	/**
	 * Create a string key
	 *
	 * @param name
	 *            the key name
	 * @return the string key
	 */
	public static Key<String> string(String name)
	{
		return new Key<>(name, String.class);
	}

	/**
	 * Create an unexpected exception
	 * @return the new exception instance
	 */
	public static RuntimeException unexpected()
	{
		return new RuntimeException("Unexpected");
	}

	/**
	 * Unconditional fail
	 */
	public static void fail()
	{
		throw unexpected();
	}

	/**
	 * Return the first member or null if empty
	 *
	 * @param <R>
	 *            the result type
	 *
	 * @param <T>
	 *            member type
	 * @param iterable
	 *            the iterable
	 * @param first
	 *            first found result
	 * @param empty
	 *            collection empty result
	 * @return the first member or null
	 */
	private static <R, T> R getFirst(Iterable<T> iterable, IExpression<R, T> first, IProvider<R> empty)
	{
		Iterator<T> iterator = iterable.iterator();
		return iterator.hasNext() ? first.apply(iterator.next()) : empty.provide();
	}

	/**
	 * Return the first member or null if empty
	 *
	 * @param <T>
	 *            member type
	 * @param iterable
	 *            the iterable
	 * @return the first member or null
	 */
	private static <T> T getFirst(Iterable<T> iterable)
	{
		return getFirst(iterable, f -> f, () -> null);
	}

	private static Vertex getRoot(final TitanGraph tg)
	{
		return getFirst(tg.getVertices("systemId", 0));
	}

	/**
	 * Navigate to a single object
	 *
	 * @param expression
	 *            the expression to get an iterable for a direction and a label
	 * @param vertex
	 *            the input vertex
	 * @param direction
	 *            the direction
	 * @param label
	 *            the label
	 *
	 * @param <T>
	 *            the object
	 * @return the single object
	 */
	private static <T> T getSingle(IExpression3<Iterable<T>, Vertex, Direction, String> expression, Vertex vertex, Direction direction, String label)
	{
		return getFirst(expression.apply(vertex, direction, label));
	}

	/**
	 * Navigate to a referenced object
	 *
	 * @param expression
	 *            the expression to get an iterable for a direction and a label
	 * @param vertex
	 *            the input vertex
	 * @param label
	 *            the label
	 *
	 * @param <T>
	 *            the object type
	 * @return the single object
	 */
	static <T> T getReferenced(IExpression3<Iterable<T>, Vertex, Direction, String> expression, Vertex vertex, String label)
	{
		return getSingle(expression, vertex, Direction.OUT, label);
	}

	private static <T> T getCurrentTask(final IExpression3<Iterable<T>, Vertex, Direction, String> expression, final TitanGraph tg)
	{
		return getReferenced(expression, getRoot(tg), "system.currentTask");
	}

	private static Vertex getCurrent(final TitanGraph tg)
	{
		return getCurrentTask(Vertex::getVertices, tg);
	}

	/**
	 * Set the next task
	 *
	 * @param predecessor
	 *            the predecessor
	 * @param successor
	 *            the successor
	 */
	private static void setNextTask(Vertex predecessor, Vertex successor)
	{
		predecessor.addEdge("nextTask", successor);
	}

	/**
	 * Get the single incoming edge with the specified label
	 *
	 * @param vertex
	 *            vertex where the edge comes in
	 * @param label
	 *            the label
	 * @return the found edge
	 */
	public static Edge getSingleIncoming(Vertex vertex, String label)
	{
		return getSingle(Vertex::getEdges, vertex, Direction.IN, label);
	}

	/**
	 * Get the tail of an edge
	 *
	 * @param edge
	 *            the edge
	 * @return the tail vertex
	 */
	public static Vertex getTail(Edge edge)
	{
		return edge.getVertex(Direction.OUT);
	}

	/**
	 * Re-Assign the previous task to the provided task
	 *
	 * @param current
	 *            the task to read the previous from
	 * @param newNext
	 *            the new next task for the previous
	 */
	private static void replacePrevious(Vertex current, Vertex newNext)
	{
		Edge eLastTask = getSingleIncoming(current, "nextTask");
		Vertex last = getTail(eLastTask);
		eLastTask.remove();
		setNextTask(last, newNext);
	}

	private static void enqueue(final TitanGraph tg, final Vertex vEntry)
	{
		final Vertex current = getCurrent(tg);
		replacePrevious(current, vEntry);
		setNextTask(vEntry, current);
	}

	private static <T> T ifNull(Object object, T isNull, IProvider<T> notNull)
	{
		return object == null ? isNull : notNull.provide();
	}

	/**
	 * Construct a string from an array by joining the array members
	 *
	 * @param members
	 *            the array
	 * @param separator
	 *            the separator
	 * @return the joined string
	 */
	public static String join(ISequence<String> members, char separator)
	{
		return ifNull(members, "", () ->
		{
			StringBuilder builder = new StringBuilder();
			ISequence<String> remaining = members;
			for (;;)
			{
				builder.append(remaining.getHead());
				remaining = remaining.getTail();
				if (remaining == null)
				{
					break;
				}
				builder.append(separator);
			}
			return builder.toString();
		});
	}

	private static <T> ISequence<String> map(ISequence<T> sequence, IExpression<String, T> toString)
	{
		return ifNull(sequence, null, (IProvider<ISequence<String>>) () ->
		{
			return new ISequence<String>()
					{
				@Override
				public String getHead()
				{
					return toString.apply(sequence.getHead());
				}

				@Override
				public ISequence<String> getTail()
				{
					return map(sequence.getTail(), toString);
				}
					};
		});
	}

	/**
	 * Construct a string from an array by joining the array members
	 *
	 * @param <T>
	 *            the member type
	 * @param members
	 *            the array
	 * @param toString
	 *            the toString expression
	 * @param separator
	 *            the separator
	 * @return the joined string
	 */
	public static <T> String join(ISequence<T> members, IExpression<String, T> toString, char separator)
	{
		return join(map(members, toString), separator);
	}

	private static void space(PrintStream out)
	{
		out.print(' ');
	}

	/**
	 * Create a sequence from an array
	 * @param <T> the member type
	 * @param array the array
	 * @param pos the start index
	 * @return the corresponding sequence
	 */
	public static <T> ISequence<T> sequence(T[] array, int pos)
	{
		return array.length == pos ? null : new ISequence<T>()
		{
			@Override
			public T getHead()
			{
				return array[pos];
			}

			@Override
			public ISequence<T> getTail()
			{
				return sequence(array, pos + 1);
			}
		};
	}

	/**
	 * join and log some texts
	 *
	 * @param texts
	 *            the texts to join
	 */
	static void log(String[] texts, PrintStream out)
	{
		out.println(join(TitanCollector.sequence(texts, 0), s -> s, ' '));
	}

	private static void indent(PrintStream out)
	{
		out.print("  ");
	}

	private static void logi(PrintStream out, String text)
	{
		indent(out);
		out.println(text);
	}

	private static <R, I> R include(PrintStream out, String addLog, final String[] logs, IExpression<R, I> expr, I i)
	{
		out.print(addLog);
		log(logs, out);
		return expr.apply(i);
	}

	/**
	 * Create an integer key
	 *
	 * @param name
	 *            the key name
	 * @return the integer key
	 */
	private static Key<Integer> integer(String name)
	{
		return new Key<>(name, Integer.class);
	}

	/**
	 * Return the key
	 *
	 * @param k
	 *            the enum key
	 *
	 * @return return the key
	 */
	public static Key<Integer> key(EnumKey<?> k)
	{
		return integer(k.getName());
	}

	/**
	 * Set the value to the element
	 *
	 * @param element
	 *            the element
	 * @param key
	 *            the key to set
	 * @param value
	 *            the new value
	 */
	static void set(Element element, Key<?> key, Object value)
	{
		element.setProperty(key.getName(), value);
	}

	private static void setProperty(Element element, String name, Enum<?> value)
	{
		element.setProperty(name, value.ordinal());
	}

	/**
	 * Create a task
	 *
	 * @param tg
	 *            the titan graph
	 * @param tt the task type
	 * @return the task vertex
	 */
	public static Vertex newTask(TitanGraph tg, TaskType tt)
	{
		Vertex v = tg.addVertex(null);
		setProperty(v, "task.type", tt);
		return v;
	}

	private static Vertex enqueueNewTask(TitanGraph tg, TaskType taskType)
	{
		Vertex v = newTask(tg, taskType);
		enqueue(tg, v);
		return v;
	}

	private static <R, I> R include(final Iterable<I> iterable, PrintStream out, final String[] logs, IExpression<R, I> found, IExpression<R, Vertex> added, final TitanGraph tg, final TaskType taskType)
	{
		return getFirst(iterable, (I first) ->
		{
			return include(out, "  already added ", logs, found, first);
		}, () ->
		{
			return include(out, "  adding ", logs, added, enqueueNewTask(tg, taskType));
		});
	}

	private static <R> R includeMBEntity(final Resource resource, final TitanGraph tg, final String mbid, PrintStream out, final IExpression<String[], String> description, IExpression<R, Vertex> found, IExpression<R, Vertex> added)
	{
		final String idProperty = resource.getMBID();
		return include(tg.getVertices(idProperty, mbid), out, description.apply(resource.getName()), found, (Vertex v) ->
		{
			setProperty(v, "resource.kind", resource);
			v.setProperty(idProperty, mbid);
			return added.apply(v);
		}, tg, TaskType.MB_RESOURCE);
	}

	private static <R, E> R includeEntity11(final IExpression<String, E> id, final E entity, final Resource resource, final TitanGraph tg, PrintStream out, final IExpression<String, E> description, IConsumer<String> inspect, IExpression<R, Vertex> found,
			IExpression<R, Vertex> added)
	{
		final String mbid = id.apply(entity);
		return includeMBEntity(resource, tg, mbid, out, (String resourceName) -> new String[] { resourceName, mbid, description.apply(entity) }, (Vertex v) ->
		{
			inspect.accept(mbid);
			return found.apply(v);
		}, added);
	}

	private static <R, E> R includeEntity(IExpression<String, E> id, final E entity, final Resource resource, final TitanGraph tg, PrintStream out, final IExpression<String, E> description, IConsumer<String> inspect, IExpression<R, Vertex> found,
			IExpression<R, Vertex> added)
	{
		return includeEntity11(id, entity, resource, tg, out, description, inspect, found, added);
	}

	private static <E> boolean includeEntity(final E entity, IExpression<String, E> id, final Resource resource, final TitanGraph tg, PrintStream out, final IExpression<String, E> description, IConsumer<String> inspect, IConsumer<Vertex> checkVertex)
	{
		return entity != null && includeEntity(id, entity, resource, tg, out, description, inspect, (Vertex v) ->
		{
			checkVertex.accept(v);
			return false;
		}, (Vertex v) -> true);
	}

	private static <E extends Entity> boolean addEntity(final E entity, final Resource resource, final TitanGraph tg, PrintStream out)
	{
		return includeEntity(entity, Entity::getId, resource, tg, out, Object::toString, mbid ->
		{ // empty
		}, vertex ->
		{ // empty
		});
	}

	private static void printSpace(PrintStream out, String s)
	{
		out.print(s);
		space(out);
	}

	private static <T> boolean addResource(T entity, IExpression<String, T> getId, PrintStream out, String resourceName, IExpression<String, T> getName, TitanGraph tg, String key, Resource resource)
	{
		return ifNull(entity, Boolean.FALSE, (IProvider<Boolean>) () ->
		{
			final String mbid = getId.apply(entity);
			final IConsumer<String> l = (String s) ->
			{
				indent(out);
				printSpace(out, s);
				printSpace(out, resourceName);
				printSpace(out, mbid);
				out.println(getName.apply(entity));
			};
			return getFirst(tg.getVertices(key, mbid), (Vertex first) ->
			{
				l.accept("already added");
				return Boolean.FALSE;
			}, () ->
			{
				Vertex v = enqueueNewTask(tg, TaskType.MB_RESOURCE);
				setProperty(v, "resource.kind", resource);
				v.setProperty(key, mbid);
				l.accept("adding");
				return Boolean.TRUE;
			});
		});
	}

	private static boolean addRecording(final Recording recording, PrintStream out, final TitanGraph tg)
	{
		return addResource(recording, Recording::getId, out, "recording", Recording::getTitle, tg, "recording.mbid", Resource.RECORDING);
	}

	private static boolean addRelease(final Release release, PrintStream out, final TitanGraph tg)
	{
		return addResource(release, Release::getId, out, "release", Release::getTitle, tg, "release.mbid", Resource.RELEASE);
	}

	private static boolean addArea(final Area area, PrintStream out, final TitanGraph tg)
	{
		return addResource(area, Area::getId, out, "area", Area::getName, tg, "area.mbid", Resource.AREA);
	}

	/**
	 * Defines a property key in the database. Checks weather the key is already
	 * defined. If it is defined check, whether the definitions match the
	 * supplied definitions. Creates the key, if the property key is new to the
	 * database.
	 *
	 * @param k
	 *            the key
	 * @param graph
	 *            the graph data base
	 * @param existing
	 *            the existing provider
	 * @param created
	 *            the created expression
	 * @param <R>
	 *            the return type
	 *
	 * @return the key if created, null else
	 */
	public static <R> R definePropertyKey(Key<?> k, SchemaManager graph, IProvider<R> existing, IExpression<R, PropertyKey> created)
	{
		final String name = k.getName();
		return graph.containsPropertyKey(name) ? existing.provide() : created.apply(graph.makePropertyKey(name).cardinality(Cardinality.SINGLE).dataType(k.getType()).make());
	}

	/**
	 * Register a primary key
	 *
	 * @param key
	 *            the key
	 *
	 * @param tg
	 *            the titan graph
	 * @return the titan key
	 */
	public static boolean primaryKey(Key<?> key, TitanGraph tg)
	{
		final TitanManagement managementSystem = tg.getManagementSystem();
		try
		{
			return definePropertyKey(key, managementSystem, () -> false, pk ->
			{
				managementSystem.buildIndex(key.getName() + ".index", Vertex.class).addKey(pk).unique().buildCompositeIndex();
				managementSystem.commit();
				return true;
			});
		}
		finally
		{
			if (managementSystem.isOpen())
			{
				managementSystem.rollback();
			}
		}
	}

	/**
	 * Fail if given condition is true
	 *
	 * @param condition
	 *            the condition to check
	 */
	public static void failIf(boolean condition)
	{
		if (condition)
		{
			fail();
		}
	}

	/**
	 * Checks if asked type is excpected type and return the provided value
	 * @param <A> the asked type class
	 * @param <E> the expected type class
	 * @param askedType the asked type
	 * @param expectedType the expected type
	 * @param value the value
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	public static <A, E> A provideSetting(Class<A> askedType, Class<E> expectedType, E value)
	{
		failIf(askedType != expectedType);
		return (A) value;
	}

	/**
	 * Provide a boolean setting
	 * @param <A> the asked type class
	 * @param askedType the asked type
	 * @param value the value
	 * @return the value
	 */
	public static <A> A provideBooleanSetting(Class<A> askedType, Boolean value)
	{
		return provideSetting(askedType, Boolean.class, value);
	}

	/**
	 * Provide a string setting
	 * @param <A> the asked type class
	 * @param askedType the asked type
	 * @param value the value
	 * @return the value
	 */
	public static <A> A provideStringSetting(Class<A> askedType, String value)
	{
		return provideSetting(askedType, String.class, value);
	}

	/**
	 * Provide a duration setting
	 * @param <A> the asked type class
	 * @param askedType the asked type
	 * @param value the value
	 * @return the value
	 */
	public static <A> A provideDurationSetting(Class<A> askedType, Duration value)
	{
		return provideSetting(askedType, Duration.class, value);
	}

	/**
	 * Provide a integer setting
	 * @param <A> the asked type class
	 * @param askedType the asked type
	 * @param value the value
	 * @return the value
	 */
	public static <A> A provideIntegerSetting(Class<A> askedType, int value)
	{
		return provideSetting(askedType, Integer.class, Integer.valueOf(value));
	}

	/**
	 * Log a debug message
	 * @param debugEnabled flag indicating whether debug is enabled
	 * @param quietMode true if quiet mode
	 * @param out the output stream to use
	 * @param s1 the first string
	 * @param s2 the second string
	 * @param s3 the third string
	 */
	public static void debug(boolean debugEnabled, boolean quietMode, PrintStream out, String s1, Object s2, String s3)
	{
		LogLog.debug(debugEnabled, quietMode, out, s1 + s2 + s3);
	}

	@SuppressWarnings("deprecation")
	private static void selectAndConfigure(URL url, Runnable isNull)
	{
		if (url == null)
		{
			isNull.run();
		}
		else
		{
			debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
			try
			{
				OptionConverter.selectAndConfigure(url, OptionConverter.getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY, null), LogManager.getLoggerRepository());
			}
			catch (NoClassDefFoundError e)
			{
				LogLog.warn("Error during default initialization", e);
			}
		}
	}

	/**
	 * Configure log options
	 * @param java1 do we have java 1
	 * @param ignoreTCL ignore the tcl
	 * @param resource the resurce
	 * @param isNull action to call when null
	 */
	@SuppressWarnings("deprecation")
	public static void configureLogOptions(boolean java1, boolean ignoreTCL, String resource, Runnable isNull)
	{
		// If we have a non-null url, then delegate the rest of the
		// configuration to the OptionConverter.selectAndConfigure
		// method.
		try
		{
			if (java1 || ignoreTCL)
			{
				// We could not find resource. Ler us now try with the
				// classloader that loaded this class.
				ClassLoader classLoader = Loader.class.getClassLoader();
				if (classLoader == null)
				{
					// It
					// may be the case that clazz was loaded by the Extentsion
					// class
					// loader which the parent of the system class loader. Hence
					// the
					// code below.
					debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
					selectAndConfigure(ClassLoader.getSystemResource(resource), isNull);
				}
				else
				{
					LogLog.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [" + resource + "] using " + classLoader + " class loader.");
					selectAndConfigure(classLoader.getResource(resource), () ->
					{
						// Last ditch attempt: get the resource from the class
						// path.
						// It
						// may be the case that clazz was loaded by the
						// Extentsion
						// class
						// loader which the parent of the system class loader.
						// Hence
						// the
						// code below.
						debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
						selectAndConfigure(ClassLoader.getSystemResource(resource), isNull);
					});
				}
			}
			else
			{
				ClassLoader classLoader = Loader.getTCL();
				if (classLoader != null)
				{
					LogLog.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [" + resource + "] using context classloader " + classLoader + ".");
					URL url1 = classLoader.getResource(resource);
					if (url1 != null)
					{
						URL url = url1;
						debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
						try
						{
							OptionConverter.selectAndConfigure(url, OptionConverter.getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY, null), LogManager.getLoggerRepository());
						}
						catch (NoClassDefFoundError e)
						{
							LogLog.warn("Error during default initialization", e);
						}
					}
					else
					{
						// We could not find resource. Ler us now try with the
						// classloader that loaded this class.
						classLoader = Loader.class.getClassLoader();
						if (classLoader != null)
						{
							LogLog.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [" + resource + "] using " + classLoader + " class loader.");
							url1 = classLoader.getResource(resource);
							if (url1 != null)
							{
								URL url = url1;
								debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
								try
								{
									OptionConverter.selectAndConfigure(url, OptionConverter.getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY, null), LogManager.getLoggerRepository());
								}
								catch (NoClassDefFoundError e)
								{
									LogLog.warn("Error during default initialization", e);
								}
							}
							else
							{
								// Last ditch attempt: get the resource from the
								// class
								// path.
								// It
								// may be the case that clazz was loaded by the
								// Extentsion
								// class
								// loader which the parent of the system class
								// loader.
								// Hence
								// the
								// code below.
								debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
								URL url = ClassLoader.getSystemResource(resource);
								selectAndConfigure(url, isNull);
							}
						}
						else
						{
							// Last ditch attempt: get the resource from the
							// class
							// path.
							// It
							// may be the case that clazz was loaded by the
							// Extentsion
							// class
							// loader which the parent of the system class
							// loader.
							// Hence
							// the
							// code below.
							debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
							URL url = ClassLoader.getSystemResource(resource);
							selectAndConfigure(url, isNull);
						}
					}
				}
				else
				{
					// We could not find resource. Ler us now try with the
					// classloader that loaded this class.
					classLoader = Loader.class.getClassLoader();
					if (classLoader != null)
					{
						LogLog.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [" + resource + "] using " + classLoader + " class loader.");
						URL url1 = classLoader.getResource(resource);
						if (url1 != null)
						{
							URL url = url1;
							debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
							try
							{
								OptionConverter.selectAndConfigure(url, OptionConverter.getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY, null), LogManager.getLoggerRepository());
							}
							catch (NoClassDefFoundError e)
							{
								LogLog.warn("Error during default initialization", e);
							}
						}
						else
						{
							// Last ditch attempt: get the resource from the
							// class
							// path.
							// It
							// may be the case that clazz was loaded by the
							// Extentsion
							// class
							// loader which the parent of the system class
							// loader.
							// Hence
							// the
							// code below.
							debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
							URL url = ClassLoader.getSystemResource(resource);
							selectAndConfigure(url, isNull);
						}
					}
					else
					{
						// Last ditch attempt: get the resource from the class
						// path.
						// It
						// may be the case that clazz was loaded by the
						// Extentsion
						// class
						// loader which the parent of the system class loader.
						// Hence
						// the
						// code below.
						debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
						URL url = ClassLoader.getSystemResource(resource);
						selectAndConfigure(url, isNull);
					}
				}
			}
		}
		catch (IllegalAccessException t)
		{
			LogLog.warn(Loader.TSTR, t);
			// Last ditch attempt: get the resource from the class path. It
			// may be the case that clazz was loaded by the Extentsion class
			// loader which the parent of the system class loader. Hence the
			// code below.
			debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
			URL url = ClassLoader.getSystemResource(resource);
			selectAndConfigure(url, isNull);
		}
		catch (InvocationTargetException t)
		{
			if (t.getTargetException() instanceof InterruptedException || t.getTargetException() instanceof InterruptedIOException)
			{
				Thread.currentThread().interrupt();
			}
			LogLog.warn(Loader.TSTR, t);
			// Last ditch attempt: get the resource from the class path. It
			// may be the case that clazz was loaded by the Extentsion class
			// loader which the parent of the system class loader. Hence the
			// code below.
			debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
			URL url = ClassLoader.getSystemResource(resource);
			selectAndConfigure(url, isNull);
		}
		catch (Throwable t)
		{
			//
			// can't be InterruptedException or InterruptedIOException
			// since not declared, must be error or RuntimeError.
			LogLog.warn(Loader.TSTR, t);
			// Last ditch attempt: get the resource from the class path. It
			// may be the case that clazz was loaded by the Extentsion class
			// loader which the parent of the system class loader. Hence the
			// code below.
			debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Trying to find [", resource, "] using ClassLoader.getSystemResource().");
			URL url = ClassLoader.getSystemResource(resource);
			selectAndConfigure(url, isNull);
		}
	}

	/**
	 * Run the collector
	 *
	 * @param db
	 *            the database name
	 * @param roots
	 *            the root entries
	 * @param checkAccuracy
	 *            check for accuracy
	 * @param playListPath
	 *            the base directory of all playlists
	 */
	@SuppressWarnings("deprecation")
	public static void run(final String db, final Iterable<Iterable<String>> roots, final boolean checkAccuracy, Iterable<String> playListPath)
	{
		String debugKey = OptionConverter.getSystemProperty("log4j.debug", null);
		if (debugKey == null)
		{
			debugKey = OptionConverter.getSystemProperty(LogLog.CONFIG_DEBUG_KEY, null);
		}
		if (debugKey != null)
		{
			LogLog.g_debugEnabled = OptionConverter.toBoolean(debugKey, true);
		}
		Level debug = new Level(Priority.DEBUG_INT, "DEBUG", 7);
		Level.DEBUG = debug;
		final DefaultRepositorySelector defaultRepositorySelector = new DefaultRepositorySelector(new Hierarchy(new RootLogger(debug)));
		LogManager.g_repositorySelector = defaultRepositorySelector;
		/** Search for the properties file log4j.properties in the CLASSPATH.  */
		final String override = OptionConverter.getSystemProperty(LogManager.DEFAULT_INIT_OVERRIDE_KEY, null);
		// if there is no default init override, then get the resource
		// specified by the user or the default config file.
		if (override == null || "false".equalsIgnoreCase(override))
		{
			final String configurationOptionStr = OptionConverter.getSystemProperty(LogManager.DEFAULT_CONFIGURATION_KEY, null);
			// final String configuratorClassName =
			// getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY);
			// if the user has not specified the log4j.configuration
			// property, we search first for the file "log4j.xml" and then
			// "log4j.properties"
			if (configurationOptionStr == null)
			{
				IConsumer2<String, Runnable> conf = (String resource, Runnable runnable) ->
				{
					configureLogOptions(Loader.java1, Loader.ignoreTCL, resource, runnable);
				};
				conf.accept(LogManager.DEFAULT_XML_CONFIGURATION_FILE, () ->
				{
					conf.accept(LogManager.DEFAULT_CONFIGURATION_FILE, () ->
					{
						debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Could not find resource: [", configurationOptionStr, "].");
					});
				});
			}
			else
			{
				try
				{
					final URL url = new URL(configurationOptionStr);
					debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
					try
					{
						OptionConverter.selectAndConfigure(url, OptionConverter.getSystemProperty(LogManager.CONFIGURATOR_CLASS_KEY, null), LogManager.getLoggerRepository());
					}
					catch (NoClassDefFoundError e)
					{
						LogLog.warn("Error during default initialization", e);
					}
				}
				catch (MalformedURLException ex)
				{
					// so, resource is not a URL:
					// attempt to get the resource from the class path
					configureLogOptions(Loader.java1, Loader.ignoreTCL, configurationOptionStr, () ->
					{
						debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Could not find resource: [", configurationOptionStr, "].");
					});
				}
			}
		}
		else
		{
			debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Default initialization of overridden by ", LogManager.DEFAULT_INIT_OVERRIDE_KEY, "property.");
		}
		if (LogManager.g_repositorySelector == null)
		{
			LogManager.g_repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
			LogManager.guard = null;
			Exception ex = new IllegalStateException("Class invariant violation");
			String msg = "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
			if (LogManager.isLikelySafeScenario(ex))
			{
				LogLog.debug(msg, ex);
			}
			else
			{
				LogLog.error(msg, ex);
			}
		}
		LoggerRepository loggerRepository = LogManager.g_repositorySelector.getLoggerRepository();
		loggerRepository.getRootLogger().setLevel(Level.WARN);
		final TitanGraph tg = TitanFactory.open(new ReadConfiguration()
		{
			@SuppressWarnings({ "unchecked", "null" })
			@Override
			public <O> O get(String key, Class<O> datatype)
			{
				switch (key)
				{
				case "storage.backend":
					return provideStringSetting(datatype, "berkeleyje");
				case "storage.read-only":
				case "storage.batch-loading":
				case "query.ignore-unknown-index-key":
				case "metrics.enabled":
				case "metrics.jmx.enabled":
				case "cache.db-cache":
				case "cluster.partition":
					return provideBooleanSetting(datatype, Boolean.FALSE);
				case "storage.transactions":
				case "graph.allow-stale-config":
				case "ids.flush":
				case "query.force-index":
				case "query.smart-limit":
				case "storage.parallel-backend-ops":
					return provideBooleanSetting(datatype, Boolean.TRUE);
				case "storage.directory":
					return provideStringSetting(datatype, db);
				case "storage.berkeleydb.cache-percentage":
					return provideIntegerSetting(datatype, 65);
				case "graph.timestamps":
					return provideSetting(datatype, Timestamps.class, Timestamps.MICRO);
				case "storage.setup-wait":
					return provideDurationSetting(datatype, new Duration()
					{
						@Override
						public int compareTo(Duration o)
						{
							throw unexpected();
						}

						@Override
						public long getLength(TimeUnit unit)
						{
							switch (unit)
							{
							case NANOSECONDS:
								return 60000000000L;
							case MILLISECONDS:
								return 60000000L;
							default:
								throw unexpected();
							}
						}

						@Override
						public boolean isZeroLength()
						{
							return false;
						}

						@Override
						public TimeUnit getNativeUnit()
						{
							throw unexpected();
						}

						@Override
						public Duration sub(Duration subtrahend)
						{
							throw unexpected();
						}

						@Override
						public Duration add(Duration addend)
						{
							throw unexpected();
						}

						@Override
						public Duration multiply(double multiplier)
						{
							throw unexpected();
						}
					});
				case "storage.lock.local-mediator-group":
				case "graph.unique-instance-id":
				case "metrics.csv.directory":
				case "metrics.ganglia.hostname":
				case "metrics.graphite.hostname":
					return provideStringSetting(datatype, null);
				case "storage.berkeleydb.isolation-level":
					return provideSetting(datatype, IsolationLevel.class, IsolationLevel.REPEATABLE_READ);
				case "storage.berkeleydb.lock-mode":
					return provideSetting(datatype, LockMode.class, LockMode.DEFAULT);
				case "graph.unique-instance-id-suffix":
					return provideSetting(datatype, Short.class, null);
				case "schema.default":
					return provideStringSetting(datatype, "none");
				case "log.tx.send-delay":
				case "log.titan.send-delay":
				case "metrics.console.interval":
				case "metrics.slf4j.interval":
					return provideDurationSetting(datatype, null);
				case "log.titan.key-consistent":
				case "query.fast-property":
				case "log.tx.key-consistent":
					return provideBooleanSetting(datatype, null);
				case "cache.tx-cache-size":
					return provideIntegerSetting(datatype, 20000);
				case "cache.tx-dirty-size":
				case "ids.authority.randomized-conflict-avoidance-retries":
				case "log.tx.send-batch-size":
				case "log.tx.read-threads":
				case "log.tx.read-batch-size":
				case "log.titan.send-batch-size":
				case "log.titan.read-threads":
				case "log.titan.read-batch-size":
					Assert.expect(datatype == Integer.class);
					return (O) null;
				case "metrics.prefix":
					Assert.expect(datatype == String.class);
					return (O) "com.thinkaurelius.titan";
				case "storage.buffer-size":
					Assert.expect(datatype == Integer.class);
					return (O) Integer.valueOf(1024);
				case "storage.write-time":
					Assert.expect(datatype == StandardDuration.class);
					return (O) new StandardDuration(100000, TimeUnit.MILLISECONDS);
				case "storage.read-time":
					Assert.expect(datatype == StandardDuration.class);
					return (O) new StandardDuration(10000, TimeUnit.MILLISECONDS);
				case "ids.authority.conflict-avoidance-mode":
					Assert.expect(datatype == ConflictAvoidanceMode.class);
					return (O) ConflictAvoidanceMode.NONE;
				case "ids.authority.conflict-avoidance-tag":
					Assert.expect(datatype == Integer.class);
					return (O) Integer.valueOf(0);
				case "log.tx.max-write-time":
				case "log.tx.read-lag-time":
				case "log.tx.max-read-time":
				case "log.titan.max-write-time":
				case "log.titan.read-lag-time":
				case "log.titan.max-read-time":
					Assert.expect(datatype == StandardDuration.class);
					return (O) null;
				case "log.tx.read-interval":
				case "log.titan.read-interval":
					Assert.expect(datatype == Duration.class);
					return (O) null;
				case "ids.renew-timeout":
					Assert.expect(datatype == Duration.class);
					return (O) new StandardDuration(120000, TimeUnit.MILLISECONDS);
				case "ids.renew-percentage":
					Assert.expect(datatype == Double.class);
					return (O) Double.valueOf(0.3);
				default:
					TitanCollector.fail();
					return null;
				}
			}

			@Override
			public Iterable<String> getKeys(String prefix)
			{
				switch (prefix)
				{
				case "":
					return Arrays.asList("storage.backend", "storage.read-only", "storage.batch-loading", "query.ignore-unknown-index-key", "metrics.enabled", "metrics.jmx.enabled", "storage.transactions", "graph.allow-stale-config", "ids.flush",
							"query.force-index", "query.smart-limit", "storage.directory", "storage.berkeleydb.cache-percentage", "graph.timestamps", "storage.setup-wait", "storage.berkeleydb.isolation-level", "storage.berkeleydb.lock-mode",
							"schema.default", "cache.tx-cache-size", "metrics.prefix", "cache.db-cache", "storage.buffer-size", "storage.write-time", "storage.read-time", "storage.parallel-backend-ops", "ids.authority.conflict-avoidance-mode",
							"ids.authority.conflict-avoidance-tag", "ids.renew-timeout", "ids.renew-percentage", "cluster.partition");
				case "index":
				case "attributes.custom":
					return () -> new Iterator<String>()
							{
						@Override
						public boolean hasNext()
						{
							return false;
						}

						@Override
						public String next()
						{
							throw new RuntimeException("not implemented!");
						}
							};
				default:
					throw new RuntimeException("not implemented!");
				}
			}

			@Override
			public void close()
			{
				throw new RuntimeException("not implemented!");
			}
		});
		try
		{
			if (primaryKey(new Key<>("systemId", Integer.class), tg))
			{
				final TitanManagement managementSystem = tg.getManagementSystem();
				try
				{
					TitanGraphs.defineAssociation("nextTask", LabelMakers.ONE_TO_ONE, managementSystem);
					TitanGraphs.makeEdgeLabel(Files.DIRECTORY_ENTRY, lm -> lm.multiplicity(Multiplicity.ONE2MANY), Files.NAME.makePropertyKey(managementSystem), managementSystem);
					final EnumKey<TaskType> TASK_TYPE_KEY = new EnumKey<>("task.type", TaskType.values());
					TASK_TYPE_KEY.makePropertyKey(tg);
					Resource.RECORDING.makeKey(tg);
					final EnumKey<Resource> RESOURCE_KIND = new EnumKey<>("resource.kind", Resource.values());
					RESOURCE_KIND.makePropertyKey(tg);
					Resource.RELEASE.makeKey(tg);
					TitanGraphs.defineAssociation(FILE_RECORDING, LabelMakers.MANY_TO_ONE, managementSystem);
					integer("systemVersion").makePropertyKey(tg);
					Resource.ARTIST.makeKey(tg);
					Resource.WORK.makeKey(tg);
					TitanCollector.primaryKey(Key.ACOUST_ID, tg);
					Resource.RELEASE_GROUP.makeKey(tg);
					Resource.AREA.makeKey(tg);
					Resource.URL.makeKey(tg);
					Resource.LABEL.makeKey(tg);
					Key.IS_COMPLETE.makePropertyKey(tg);
					TitanGraphs.defineAssociation("system.currentTask", LabelMakers.ONE_TO_ONE, managementSystem);
					managementSystem.commit();
					final Vertex system = newTask(tg, TaskType.ROOT);
					TitanCollector.set(system, integer("systemId"), 0);
					setVersion(system, 1);
					TitanCollector.setNextTask(system, system);
					system.addEdge("system.currentTask", system);
				}
				finally
				{
					if (managementSystem.isOpen())
					{
						managementSystem.rollback();
					}
				}
			}
			final Vertex system = getRoot(tg);
			switch (integer("systemVersion").get(system))
			{
			case 1:
				Resource.COLLECTION.makeKey(tg);
				//$FALL-THROUGH$
			case 2:
				Resource.ISRC.makeKey(tg);
				//$FALL-THROUGH$
			case 3:
				Resource.PLACE.makeKey(tg);
				//$FALL-THROUGH$
			case 4:
				Resource.DISC_ID.makeKey(tg);
				//$FALL-THROUGH$
			case 5:
			{
				final TitanManagement managementSystem = tg.getManagementSystem();
				try
				{
					TitanGraphs.makeEdgeLabel("playlist", LabelMakers.MANY_TO_ONE, null, managementSystem);
					managementSystem.commit();
				}
				finally
				{
					if (managementSystem.isOpen())
					{
						managementSystem.rollback();
					}
				}
			}
			//$FALL-THROUGH$
			case 6:
				final TitanManagement managementSystem = tg.getManagementSystem();
				try
				{
					TitanGraphs.makeEdgeLabel("rest", LabelMakers.MANY_TO_ONE, null, managementSystem);
					TitanGraphs.makeEdgeLabel("first", LabelMakers.MANY_TO_ONE, null, managementSystem);
					setVersion(system, 7);
					managementSystem.commit();
				}
				finally
				{
					if (managementSystem.isOpen())
					{
						managementSystem.rollback();
					}
				}
				//$FALL-THROUGH$
			case 7:
				Resource.SERIES.makeKey(tg);
				//$FALL-THROUGH$
			case 8:
				Resource.EVENT.makeKey(tg);
				setVersion(system, 9);
				//$FALL-THROUGH$
			case 9:
				break;
			default:
				TitanCollector.fail();
			}
			final WebService<MetaData> musicBrainz = new WebService<>(MetaData.class, 1000, "http://musicbrainz.org/ws/2");
			// t._musicBrainz = musicBrainz;
			final WebService<Response> acoustId = new WebService<>(Response.class, 334, "http://api.acoustid.org/v2");
			l1: for (;;)
			{
				final Vertex current = TitanCollector.getCurrent(tg);
				final EnumKey<TaskType> TASK_TYPE_KEY = new EnumKey<>("task.type", TaskType.values());
				switch (TASK_TYPE_KEY.get(current))
				{
				case FILESYSTEMOBJECT:
					final Vertex currentEntry = current;
					final File file = toFile(Files.getDirectoryEntryEdge(currentEntry));
					final String[] texts = { file.toString() };
					TitanCollector.log(texts, System.out);
					if (file.exists())
					{
						if (file.isDirectory())
						{
							final String[] entries = file.list();
							if (entries == null)
							{
								throw new RuntimeException("Access denied");
							}
							if (entries.length == 0)
							{
								Files.delete(file);
							}
							else
							{
								int iEntries = 0;
								final int lEntries = entries.length;
								for (;;)
								{
									if (iEntries == lEntries)
									{
										break;
									}
									final Vertex directory = current;
									final String name = entries[iEntries];
									if (include1(Files.NAME.has(current.query().direction(Direction.OUT).labels(Files.DIRECTORY_ENTRY), name).edges(), entryVertex -> Files.addDirectoryEntry(directory, entryVertex, name), TaskType.FILESYSTEMOBJECT, tg,
											"entry", name))
									{
										break;
									}
									iEntries++;
								}
							}
						}
						else
						{
							final String fileName = file.getName();
							if (NAMES_TO_DELETE.contains(fileName))
							{
								// Assert.fail();
								Files.delete(file);
							}
							else
							{
								final String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
								if (EXTENSIONS_TO_DELETE.contains(extension))
								{
									Files.delete(file);
								}
								else
								{
									if (!MEDIA_EXTENSION.contains(extension))
									{
										throw new RuntimeException("Unknown file type!");
									}
									try
									{
										final Tag tag = AudioFileIO.read(file).getTag();
										final String recordingId = tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID);
										if (recordingId.isEmpty())
										{
											throw new RuntimeException("Missing recording-id! Please tag file.");
										}
										final String releaseId = tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
										final Resource resource = Resource.RECORDING;
										final Pair<Vertex, Boolean> task = TitanCollector.includeMBEntity(resource, recordingId, tg);
										final Vertex current1 = TitanCollector.getCurrent(tg);
										final String fileProperty = "file." + resource.getName();
										Edges.removeReference(current1, fileProperty);
										current1.addEdge(fileProperty, task._first);
										final Pair<Vertex, Boolean> recordingTask = task;
										if (!recordingTask.second())
										{
											Assert.expect(!releaseId.isEmpty());
											final String earliestMBID = getEarliestRelease(musicBrainz, recordingId);
											if (!releaseId.equals(earliestMBID))
											{
												throw new RuntimeException("Not tagged to earliest release http://musicbrainz.org/release/" + earliestMBID);
											}
											final String acoustid = tag.getFirst(FieldKey.ACOUSTID_ID);
											// Assert.expect (acoustid !=
											// null);
											if ("".equals(acoustid))
											{
												throw new RuntimeException("Missing acoust-id! Please fingerprint file");
											}
											final LinkedList<AcoustId> tracks = getAcoustIds(acoustId, recordingId);
											final Iterator<AcoustId> itrack = tracks.iterator();
											while (itrack.hasNext())
											{
												final String id = itrack.next()._id;
												if (!id.equals(acoustid))
												{
													if (hasMultipleRecordings(acoustId, id))
													{
														throw new RuntimeException("Invalid acoustid '" + id + "' attached to recording!");
													}
												}
											}
										}
									}
									catch (final CannotReadException e)
									{
										throw new RuntimeException(e);
									}
									catch (final IOException e)
									{
										throw new RuntimeException(e);
									}
									catch (final TagException e)
									{
										throw new RuntimeException(e);
									}
									catch (final ReadOnlyFileException e)
									{
										throw new RuntimeException(e);
									}
									catch (final InvalidAudioFrameException e)
									{
										throw new RuntimeException(e);
									}
								}
							}
						}
						requeue(tg);
					}
					else
					{
						Files.getDirectoryEntryEdge(current).remove();
						removeCurrent(tg, "removing", file.toString());
						/*
						 * current.remove(); Log.logi("removing");
						 */
					}
					break;
				case MB_RESOURCE:
					// final Iterator<Edge> iEdge =
					// current.getEdges(Direction.OUT,
					// "playlist").iterator();
					final EnumKey<Resource> RESOURCE_KIND = new EnumKey<>("resource.kind", Resource.values());
					// final Iterator<Edge> iPlayList =
					// current.getEdges(Direction.OUT,
					// "playlist").iterator();
					switch (RESOURCE_KIND.get(current))
					{
					case RECORDING:
					{
						if (processResource(Resource.RECORDING, metaData -> metaData._recording, musicBrainz,
								Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels+isrcs"), (foreignId1, entity) ->
						{
							Parameter parameter = new Parameter("recording", foreignId1);
							return browseArtists(musicBrainz, parameter, tg) && browseReleases(musicBrainz, parameter, v ->
							{ // empty
							}, tg, mbid ->
							{ // empty
							}) && processRelations(entity, a -> a._relationLists, tg) && processAcoustIds(acoustId, checkAccuracy, tg, entity);
						}, tg, Recording::getTitle, Recording::getId))
						{
							break l1;
						}
						break;
					}
					case ARTIST:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.ARTIST, metaData -> metaData._artist, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							final TreeSet<String> recordings = new TreeSet<>((String s1, String s2) ->
							{
								if (s1.equals(s2))
								{
									return 0;
								}
								else
								{
									final Iterator<Release> ie1 = releases(musicBrainz, recordingParam(s1));
									Assert.expect(ie1.hasNext());
									Release release1 = ie1.next();
									YearMonthDay minDate1 = release1.getReleaseDate();
									// String earliestRelease2 =
									// release2.getId();
									while (ie1.hasNext())
									{
										final Release e = ie1.next();
										final YearMonthDay releaseDate = e.getReleaseDate();
										if (YearMonthDay.isSmaller(releaseDate, minDate1))
										{
											minDate1 = releaseDate;
											release1 = e;
										}
									}
									final Iterator<Release> ie2 = releases(musicBrainz, recordingParam(s2));
									Assert.expect(ie2.hasNext());
									Release release2 = ie2.next();
									YearMonthDay minDate2 = release2.getReleaseDate();
									// String earliestRelease2 =
									// release2.getId();
									while (ie2.hasNext())
									{
										final Release e = ie2.next();
										final YearMonthDay releaseDate = e.getReleaseDate();
										if (YearMonthDay.isSmaller(releaseDate, minDate2))
										{
											minDate2 = releaseDate;
											release2 = e;
										}
									}
									final String id = release1.getId();
									if (id.equals(release2.getId()))
									{
										final MetaData metaData = musicBrainz.query("release/" + id, Parameter.inc("recordings"));
										final Iterator<Medium> iterator = metaData._release._mediumList._mediums.iterator();
										for (;;)
										{
											Medium m = iterator.next();
											final Iterator<Track> iTrack = m._trackList._tracks.iterator();
											while (iTrack.hasNext())
											{
												Track track = iTrack.next();
												final String recordingId = track._recording._id;
												if (recordingId.equals(s1))
												{
													return -1;
												}
												if (recordingId.equals(s2))
												{
													return 1;
												}
												// Assert.fail();
												// return 0;
											}
										}
									}
									else
									{
										if (YearMonthDay.isSmaller(minDate1, minDate2))
										{
											return -1;
										}
										else
										{
											if (YearMonthDay.isSmaller(minDate2, minDate1))
											{
												return 1;
											}
											else
											{
												TitanCollector.fail();
												return 0;
											}
										}
									}
								}
							});
							final Parameter artist = new Parameter("artist", foreignId);
							if (capturePlayLists(TitanCollector::browseReleaseGroups, musicBrainz, artist, tg) && capturePlayLists((mb, parameter, checkVertex, t) ->
							{
								return browseReleases(mb, parameter, checkVertex, t, mbid ->
								{ // empty
								});
							}, musicBrainz, artist, tg) && browseReleases(musicBrainz, new Parameter("track_artist", foreignId), v ->
							{ // empty
							}, tg, releaseId ->
							{
								// System.out.println(releaseId);
								final MetaData metaData = musicBrainz.query("release/" + releaseId, new Parameter("inc", "artist-credits+recordings"));
								for (Medium m : metaData._release._mediumList._mediums)
								{
									for (final Track t : m._trackList._tracks)
									{
										final ArtistCredit artistCredit = t._artistCredit;
										if (artistCredit != null)
										{
											Assert.expect(artistCredit._nameCredits != null);
											final Iterator<NameCredit> inc = artistCredit._nameCredits.iterator();
											while (inc.hasNext())
											{
												if (inc.next()._artist._id.equals(foreignId))
												{
													recordings.add(t._recording._id);
													break;
												}
											}
										}
									}
								}
							}) && browseRecordings(musicBrainz, artist, tg, mbid ->
							{
								recordings.add(mbid);
							}) && processRelations(entity, a -> a._relationLists, tg) && processAreas(entity, tg))
							{
								final Iterator<RelationList> irl = entity._relationLists.iterator();
								while (irl.hasNext())
								{
									final RelationList rel = irl.next();
									switch (rel._targetType)
									{
									case WORK:
									{
										final Iterator<Relation> ir = rel._relations.iterator();
										while (ir.hasNext())
										{
											final Relation r = ir.next();
											switch (r._typeId)
											{
											case ARTIST_WORK_COMPOSER:
											case ARTIST_WORK_LYRICIST:
											case ARTIST_WORK_WRITER:
												final Iterator<Vertex> ivertex = tg.getVertices("work.mbid", r._work._id).iterator();
												final Iterator<Vertex> iPlayList = ivertex.next().getVertices(Direction.OUT, "playlist").iterator();
												if (iPlayList.hasNext())
												{
													Vertex playList = iPlayList.next();
													while (TASK_TYPE_KEY.get(playList) != TaskType.MB_RESOURCE)
													{
														recordings.add(playList.getVertices(Direction.OUT, "first").iterator().next().getProperty("recording.mbid"));
														playList = playList.getVertices(Direction.OUT, "rest").iterator().next();
													}
													recordings.add(playList.getProperty("recording.mbid"));
													// fail();
												}
												break;
											default:
												TitanCollector.fail();
											}
										}
									}
									break;
									case RECORDING:
									{
										final Iterator<Relation> ir = rel._relations.iterator();
										while (ir.hasNext())
										{
											final Relation r = ir.next();
											switch (r._typeId)
											{
											case ARTIST_RECORDING_VOCAL:
											case ARTIST_RECORDING_INSTRUMENT:
												break;
											default:
												TitanCollector.fail();
											}
											recordings.add(r._recording._id);
										}
										break;
									}
									case RELEASE:
									{
										final Iterator<Relation> ir = rel._relations.iterator();
										while (ir.hasNext())
										{
											final Relation r = ir.next();
											switch (r._typeId)
											{
											case ARTIST_RELEASE_DESIGN_ILLUSTRATION:
											case ARTIST_RELEASE_MISC:
											case ARTIST_RELEASE_PHOTOGRAPHY:
												final Iterator<Vertex> ivertex = tg.getVertices("release.mbid", r._release._id).iterator();
												Assert.expect(!ivertex.next().getVertices(Direction.OUT, "playlist").iterator().hasNext());
												Assert.expect(!ivertex.hasNext());
												break;
											default:
												TitanCollector.fail();
											}
										}
										break;
									}
									case URL:
										final Iterator<Relation> ir = rel._relations.iterator();
										while (ir.hasNext())
										{
											final Relation r = ir.next();
											switch (r._typeId)
											{
											case ARTIST_URL_DISCOGS:
												break;
											default:
												TitanCollector.fail();
											}
										}
										break;
									default:
										TitanCollector.fail();
										break;
									}
								}
								switch (recordings.size())
								{
								case 0:
									log(new String[] { "  playlist pending" }, System.out);
									return true;
								case 1:
								{
									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									if (iPlayList.hasNext())
									{
										iPlayList.next().remove();
									}
									final Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", recordings.iterator().next()).iterator();
									if (iRecordingVertex.hasNext())
									{
										current.addEdge("playlist", iRecordingVertex.next());
									}
								}
								break;
								default:
									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									if (iPlayList.hasNext())
									{
										iPlayList.next().remove();
									}
									Iterator<String> ird = recordings.descendingIterator();
									Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									Vertex playList = iRecordingVertex.next();
									l2: for (;;)
									{
										if (!ird.hasNext())
										{
											current.addEdge("playlist", playList);
											break;
										}
										String nextRecordId = ird.next();
										final Iterator<Vertex> iRest = playList.getVertices(Direction.IN, "rest").iterator();
										for (;;)
										{
											if (!iRest.hasNext())
											{
												for (;;)
												{
													final Iterator<Vertex> irec = tg.getVertices("recording.mbid", nextRecordId).iterator();
													if (irec.hasNext())
													{
														Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
														v.addEdge("rest", playList);
														v.addEdge("first", irec.next());
														playList = v;
													}
													if (!ird.hasNext())
													{
														break;
													}
													nextRecordId = ird.next();
												}
												current.addEdge("playlist", playList);
												break l2;
											}
											Vertex currentRest = iRest.next();
											if (currentRest.getVertices(Direction.OUT, "first").iterator().next().getProperty("recording.mbid").equals(nextRecordId))
											{
												playList = currentRest;
												break;
											}
										}
									}
									/*

									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									if (iPlayList.hasNext())
									{
										iPlayList.next().remove();
									}
									Assert.expect(!iPlayList.hasNext());
									Iterator<String> ird = recordings.descendingIterator();
									Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									Vertex playList = iRecordingVertex.next();
									final Iterator<Vertex> iExisting = playList.getVertices(Direction.IN, "rest").iterator();
									if (iExisting.hasNext())
									{
										Vertex existing = iExisting.next();
										Assert.expect(TASK_TYPE_KEY.get(existing) == TaskType.PLAYLIST);
										for (;;)
										{
											if (!ird.hasNext())
											{
												current.addEdge("playlist", existing);
												TitanCollector.fail();
												break;
											}
											final Iterator<Vertex> iExisting2 = existing.getVertices(Direction.IN, "rest").iterator();
											if (!iExisting2.hasNext())
											{
												Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
												v.addEdge("rest", existing);
												v.addEdge("first", tg.getVertices("recording.mbid", ird.next()).iterator().next());
												Assert.expect(!ird.hasNext());
												current.addEdge("playlist", v);
												break;
											}
											final Vertex next = iExisting2.next();
											String nextRecordId = ird.next();
											if (!next.getVertices(Direction.OUT, "first").iterator().next().getProperty("recording.mbid").equals(nextRecordId))
											{
												Assert.expect(!iExisting2.hasNext());
												for (;;)
												{
													Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
													v.addEdge("rest", existing);
													v.addEdge("first", tg.getVertices("recording.mbid", nextRecordId).iterator().next());
													if (!ird.hasNext())
													{
														current.addEdge("playlist", v);
														break;
													}
													nextRecordId = ird.next();
													existing = v;
												}
												break;
											}
											existing = next;
										}
									}
									else
									{
										while (ird.hasNext())
										{
											Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
											v.addEdge("rest", playList);
											iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
											Assert.expect(iRecordingVertex.hasNext());
											v.addEdge("first", iRecordingVertex.next());
											playList = v;
										}
										current.addEdge("playlist", playList);
									}
									 */
									// fail();
									break;
								}
							}
							return Boolean.FALSE;
						}, tg, Artist::getName, Artist::getId))
						{
							break l1;
						}
						break;
					}
					case RELEASE:
					{
						if (processResource(Resource.RELEASE, metaData -> metaData._release, musicBrainz,
								Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels+discids"), (foreignId, entity) ->
						{
							final Parameter release = new Parameter("release", foreignId);
							if (browseArtists(musicBrainz, release, tg) && browseReleaseGroups(musicBrainz, release, v ->
							{ // empty
							}, tg) && browseRecordings(musicBrainz, release, tg, mbid ->
							{ // empty
							}) && processRelations(entity, a -> a._relationLists, tg) && browse(musicBrainz, Resource.LABEL, release, metaData -> metaData._labelList, list -> list._labels, v ->
							{ // empty
							}, tg, Label::toString, mbid ->
							{ // empty
							}, Entity::getId) && processReleaseEvents(entity, tg))
							{
								final boolean added = false;
								/*
								 * for (Collection c : musicBrainz
								 * .query("collection", release,
								 * MetaData.PARAMETER_LIMIT,
								 * MetaData.PARAMETER_OFFSET
								 * )._collectionList ._collections) { if
								 * (addEntity (Resource.COLLECTION, c,
								 * Collection.NAME, tg)) { added = true;
								 * break; } }
								 */
								if (added)
								{
									return false;
								}
								else
								{
									final Iterator<Medium> im = entity._mediumList._mediums.iterator();
									Assert.expect(im.hasNext());
									final LinkedList<Disc> discs = im.next()._discs._discs;
									if (discs != null)
									{
										final Iterator<Disc> id = discs.iterator();
										if (id.hasNext())
										{
											final String discId = id.next()._id;
											Assert.expect(discId != null);
											Assert.expect(includeMBEntity(Resource.DISC_ID, discId, tg).second());
											return false;
										}
									}
									Assert.expect(!im.hasNext());
									final Boolean wasCompleted = Key.IS_COMPLETE.get(current);
									if (wasCompleted == null || wasCompleted == Boolean.FALSE)
									{
										if (isComplete(musicBrainz, release, tg))
										{
											completed(current, Resource.RELEASE);
											return true;
										}
										else
										{
											TitanCollector.set(current, Key.IS_COMPLETE, false);
											return false;
										}
									}
									else
									{
										Assert.expect(wasCompleted == Boolean.TRUE);
										Assert.expect(isComplete(musicBrainz, release, tg));
										return false;
									}
								}
							}
							else
							{
								return false;
							}
						}, tg, Release::getTitle, Release::getId))
						{
							break l1;
						}
						break;
					}
					case WORK:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.WORK, metaData -> metaData._work, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								Assert.expect(entity._iswcList == null);
								TreeSet<String> recordings = new TreeSet<>((String s1, String s2) ->
								{
									if (s1.equals(s2))
									{
										return 0;
									}
									else
									{
										final Iterator<Release> ie1 = releases(musicBrainz, recordingParam(s1));
										Assert.expect(ie1.hasNext());
										Release release1 = ie1.next();
										YearMonthDay minDate1 = release1.getReleaseDate();
										while (ie1.hasNext())
										{
											final Release e = ie1.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate1))
											{
												minDate1 = releaseDate;
												release1 = e;
											}
										}
										final Iterator<Release> ie2 = releases(musicBrainz, recordingParam(s2));
										Assert.expect(ie2.hasNext());
										Release release2 = ie2.next();
										YearMonthDay minDate2 = release2.getReleaseDate();
										while (ie2.hasNext())
										{
											final Release e = ie2.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate2))
											{
												minDate2 = releaseDate;
												release2 = e;
											}
										}
										Assert.expect(!release1.getId().equals(release2.getId()));
										if (YearMonthDay.isSmaller(release1.getReleaseDate(), release2.getReleaseDate()))
										{
											return -1;
										}
										else
										{
											if (YearMonthDay.isSmaller(release2.getReleaseDate(), release1.getReleaseDate()))
											{
												return 1;
											}
											else
											{
												throw unexpected();
											}
										}
									}
									/*
									 * (ie.hasNext()) { final Release e =
									 * ie.next(); final YearMonthDay releaseDate
									 * = e.getReleaseDate(); if
									 * (YearMonthDay.isSmaller(releaseDate ,
									 * minDate1)) releaseDate; // //
									 * earliestRelease = e.getId(); // } // }
									 * final Iterator<Release> ie2 =
									 * releases(musicBrainz,
									 * recordingParam(s2));
									 * Assert.expect(ie2.hasNext()); final
									 * Release release2 = ie2.next(); //
									 * YearMonthDay minDate1 = //
									 * release.getReleaseDate(); // String
									 * earliestRelease = // release.getId();
									 * Assert.expect(ie2.hasNext()); // while
									 * (ie.hasNext()) // { // final Release e =
									 * ie.next(); // final YearMonthDay
									 * releaseDate = // e.getReleaseDate(); //
									 * if // (YearMonthDay
									 * .isSmaller(releaseDate, // minDate1)) //
									 * { // minDate1 = releaseDate; // //
									 * earliestRelease = e.getId(); // } // }
									 * final YearMonthDay releaseDate1 =
									 * release1.getReleaseDate(); final
									 * YearMonthDay releaseDate2 =
									 * release2.getReleaseDate();
									 * Assert.expect(! YearMonthDay.isSmaller
									 * (releaseDate1, releaseDate2));
									 * Assert.expect(!YearMonthDay
									 * .isSmaller(releaseDate2, releaseDate1));
									 * Assert.fail(); return 0; /*
									 * Assert.expect(ie.hasNext()); final
									 * Release release = ie.next(); YearMonthDay
									 * minDate = release.getReleaseDate();
									 * String earliestRelease = release.getId();
									 * while (ie.hasNext()) { final Release e =
									 * ie.next(); final YearMonthDay releaseDate
									 * = e.getReleaseDate(); if
									 * (YearMonthDay.isSmaller(releaseDate ,
									 * minDate1)) { minDate = releaseDate;
									 * earliestRelease = e.getId(); } } //
									 * return earliestRelease;
									 */
									// throw new
									// RuntimeException("not implemented!");
								});
								for (RelationList rl : entity._relationLists)
								{
									switch (rl._targetType)
									{
									case ARTIST:
										for (Relation r : rl._relations)
										{
											switch (r._typeId)
											{
											case ARTIST_WORK_COMPOSER:
											case ARTIST_WORK_LIBRETTIST:
											case ARTIST_WORK_LYRICIST:
											case ARTIST_WORK_WRITER:
												break;
											default:
												TitanCollector.fail();
												break;
											}
										}
										break;
									case WORK:
										for (Relation r : rl._relations)
										{
											switch (r._typeId)
											{
											case WORK_PARTS:
											case WORK_WORK_OTHER_VERSION:
												if (r._direction == null)
												{
													final Iterator<Vertex> pli = tg.getVertices("work.mbid", r._work._id).iterator().next().getVertices(Direction.OUT, "playlist").iterator();
													Assert.expect(pli.hasNext());
													final Vertex pl = pli.next();
													Assert.expect(TASK_TYPE_KEY.get(pl) == TaskType.MB_RESOURCE);
													recordings.add(pl.getProperty("recording.mbid"));
												}
												else
												{
													Assert.expect(r._direction == rasuni.musicbrainz.Direction.BACKWARD);
												}
												break;
											default:
												TitanCollector.fail();
												break;
											}
										}
										break;
									case RECORDING:
										for (Relation relation : rl._relations)
										{
											switch (relation._typeId)
											{
											case WORK_PERFORMANCE:
												recordings.add(relation._recording._id);
												break;
											default:
												TitanCollector.fail();
											}
										}
										break;
									case URL:
										for (Relation relation : rl._relations)
										{
											switch (relation._typeId)
											{
											case URL_WORK_SCORE:
												break;
											default:
												TitanCollector.fail();
											}
										}
										break;
									case LABEL:
										for (Relation r : rl._relations)
										{
											switch (r._typeId)
											{
											case LABEL_PUBLISHED:
												break;
											default:
												TitanCollector.fail();
												break;
											}
										}
										break;
									default:
										TitanCollector.fail();
										break;
									}
								}
								switch (recordings.size())
								{
								case 0:
									log(new String[] { "  playlist pending" }, System.out);
									return true;
								case 1:
								{
									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									final Iterator<Edge> iEdge = iPlayList;
									if (iEdge.hasNext())
									{
										iEdge.next().remove();
									}
									final Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", recordings.iterator().next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									current.addEdge("playlist", iRecordingVertex.next());
									break;
								}
								default:
								{
									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									if (iPlayList.hasNext())
									{
										iPlayList.next().remove();
									}
									Iterator<String> ird = recordings.descendingIterator();
									Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									Vertex playList = iRecordingVertex.next();
									l2: for (;;)
									{
										if (!ird.hasNext())
										{
											current.addEdge("playlist", playList);
											break;
										}
										String nextRecordId = ird.next();
										final Iterator<Vertex> iRest = playList.getVertices(Direction.IN, "rest").iterator();
										for (;;)
										{
											if (!iRest.hasNext())
											{
												for (;;)
												{
													Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
													v.addEdge("rest", playList);
													v.addEdge("first", tg.getVertices("recording.mbid", nextRecordId).iterator().next());
													if (!ird.hasNext())
													{
														current.addEdge("playlist", v);
														break;
													}
													nextRecordId = ird.next();
													playList = v;
												}
												break l2;
											}
											Vertex currentRest = iRest.next();
											if (currentRest.getVertices(Direction.OUT, "first").iterator().next().getProperty("recording.mbid").equals(nextRecordId))
											{
												playList = currentRest;
												break;
											}
										}
									}
									break;
								}
								}
								// println("--> playlist added");
								// Assert.fail();
							}
							return false;
						}, tg, Work::getTitle, Work::getId))
						{
							break l1;
						}
						break;
					}
					case AREA:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.AREA, metaData -> metaData._area, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								TitanCollector.fail(); // must include area
								// relations
								return true;
							}
							return false;
						}, tg, Area::getName, Area::getId))
						{
							break l1;
						}
						break;
					}
					case RELEASE_GROUP:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.RELEASE_GROUP, metaData -> metaData._releaseGroup, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							final Parameter param = new Parameter("release-group", foreignId);
							if (browseArtists(musicBrainz, param, tg) && browseReleases(musicBrainz, param, v ->
							{ // empty
							}, tg, mbid ->
							{ // empty
							}) && processRelations(entity, a -> a._relationLists, tg))
							{
								final Iterator<Release> ie = releases(musicBrainz, param);
								if (ie.hasNext())
								{
									assertNotComplete(tg, Resource.RELEASE, ie.next(), Release::getId);
								}
								// Assert.fail(); // check for
								// completion;
							}
							return false;
						}, tg, ReleaseGroup::getTitle, ReleaseGroup::getId))
						{
							break l1;
						}
						break;
					}
					case URL:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.URL, metaData -> metaData._url, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								log(new String[] { "URL", "http://musicbrainz.org/url/" + entity._id }, System.out);
								return true;
							}
							else
							{
								return false;
							}
						}, tg, url -> url._resource, Url::getId))
						{
							break l1;
						}
						break;
					}
					case LABEL:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						if (processResource(Resource.LABEL, metaData -> metaData._label, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							if (browseReleases(musicBrainz, new Parameter("label", foreignId), v ->
							{ // empty
							}, tg, mbid ->
							{ // empty
							}) && processRelations(entity, a -> a._relationLists, tg) && !addArea(entity._area, System.out, tg))
							{
								final Iterator<RelationList> irl = entity._relationLists.iterator();
								Assert.expect(irl.hasNext());
								final RelationList nextRL = irl.next();
								final TreeSet<String> recordings = new TreeSet<>((String s1, String s2) ->
								{
									if (s1.equals(s2))
									{
										return 0;
									}
									else
									{
										final Iterator<Release> ie1 = releases(musicBrainz, recordingParam(s1));
										Assert.expect(ie1.hasNext());
										Release release1 = ie1.next();
										YearMonthDay minDate1 = release1.getReleaseDate();
										// String earliestRelease2 =
										// release2.getId();
										while (ie1.hasNext())
										{
											final Release e = ie1.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate1))
											{
												minDate1 = releaseDate;
												release1 = e;
											}
										}
										final Iterator<Release> ie2 = releases(musicBrainz, recordingParam(s2));
										Assert.expect(ie2.hasNext());
										Release release2 = ie2.next();
										YearMonthDay minDate2 = release2.getReleaseDate();
										// String earliestRelease2 =
										// release2.getId();
										while (ie2.hasNext())
										{
											final Release e = ie2.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate2))
											{
												minDate2 = releaseDate;
												release2 = e;
											}
										}
										final String id = release1.getId();
										if (id.equals(release2.getId()))
										{
											final MetaData metaData = musicBrainz.query("release/" + id, Parameter.inc("recordings"));
											final Iterator<Medium> iterator = metaData._release._mediumList._mediums.iterator();
											for (;;)
											{
												Medium m = iterator.next();
												final Iterator<Track> iTrack = m._trackList._tracks.iterator();
												while (iTrack.hasNext())
												{
													Track track = iTrack.next();
													final String recordingId = track._recording._id;
													if (recordingId.equals(s1))
													{
														return -1;
													}
													if (recordingId.equals(s2))
													{
														return 1;
													}
													// Assert.fail();
													// return 0;
												}
											}
										}
										else
										{
											if (YearMonthDay.isSmaller(minDate1, minDate2))
											{
												return -1;
											}
											else
											{
												if (YearMonthDay.isSmaller(minDate2, minDate1))
												{
													return 1;
												}
												else
												{
													TitanCollector.fail();
													return 0;
												}
											}
										}
									}
								});
								switch (nextRL._targetType)
								{
								case WORK:
								{
									final Iterator<Relation> iterator = nextRL._relations.iterator();
									Assert.expect(iterator.hasNext());
									final Iterator<Vertex> ivertex = tg.getVertices("work.mbid", iterator.next()._work._id).iterator();
									Assert.expect(!ivertex.next().getVertices(Direction.OUT, "playlist").iterator().hasNext());
									Assert.expect(!iterator.hasNext());
									break;
								}
								case RECORDING:
									final Iterator<Relation> iterator = nextRL._relations.iterator();
									while (iterator.hasNext())
									{
										recordings.add(iterator.next()._recording._id);
									}
									break;
								default:
									TitanCollector.fail();
									break;
								}
								Assert.expect(!irl.hasNext());
								switch (recordings.size())
								{
								case 0:
									log(new String[] { "  playlist pending" }, System.out);
									return true;
								case 1:
									TitanCollector.fail();
									return true;
								default:
									final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
									Assert.expect(!iPlayList.hasNext());
									Iterator<String> ird = recordings.descendingIterator();
									Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									Vertex playList = iRecordingVertex.next();
									final Iterator<Vertex> iExisting = playList.getVertices(Direction.IN, "rest").iterator();
									Assert.expect(!iExisting.hasNext());
									Assert.expect(ird.hasNext());
									Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
									v.addEdge("rest", playList);
									iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
									Assert.expect(iRecordingVertex.hasNext());
									v.addEdge("first", iRecordingVertex.next());
									playList = v;
									Assert.expect(!ird.hasNext());
									current.addEdge("playlist", playList);
									System.out.println("  updated playlist");
									return false;
								}
							}
							else
							{
								return false;
							}
						}, tg, Label::getName, Label::getId))
						{
							break l1;
						}
						break;
					}
					case COLLECTION:
					{
						final String foreignId = TitanCollector.string(Resource.COLLECTION.getMBID()).get(current);
						final String id = "collection/" + foreignId;
						final Collection entity = musicBrainz.query(id + "/releases")._collection;
						final String url = "http://musicbrainz.org/" + id;
						log(new String[] { url, entity._name }, System.out);
						final String entityId = entity.getId();
						Assert.expect(foreignId.equals(entityId));
						final ReleaseList list = entity._releaseList;
						final LinkedList<Release> entityList = list._releases;
						Assert.expect(list.isCount(entityList.size()));
						final Iterator<Release> ir = entityList.iterator();
						Assert.expect(ir.hasNext());
						if (!TitanCollector.addRelease(ir.next(), System.out, tg))
						{
							Assert.expect(!ir.hasNext());
						}
						requeue(tg);
						break;
					}
					case ISRC:
					{
						final String foreignId = TitanCollector.string(Resource.ISRC.getMBID()).get(current);
						final String id = "isrc/" + foreignId;
						log(new String[] { "http://musicbrainz.org/" + id }, System.out);
						final ISRC isrc = musicBrainz.query(id)._isrc;
						Assert.expect(foreignId.equals(isrc._id));
						final RecordingList recordingList = isrc._recordingList;
						if (recordingList.isCount(1))
						{
							Assert.expect(!addRecording(recordingList._recordings.getFirst(), System.out, tg));
							requeue(tg);
							break;
						}
						else
						{
							Assert.expect(!recordingList.isCount(0));
							log(new String[] { "Multiple recordings with same isrc!" }, System.out);
							requeue(tg);
							break l1;
						}
					}
					case PLACE:
					{
						final Parameter RELS_PARAMETER = Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels");
						processResource(Resource.PLACE, metaData -> metaData._place, musicBrainz, RELS_PARAMETER, (foreignId, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								TreeSet<String> recordings = new TreeSet<>((String s1, String s2) ->
								{
									if (s1.equals(s2))
									{
										return 0;
									}
									else
									{
										final Iterator<Release> ie1 = releases(musicBrainz, recordingParam(s1));
										Assert.expect(ie1.hasNext());
										Release release1 = ie1.next();
										YearMonthDay minDate1 = release1.getReleaseDate();
										while (ie1.hasNext())
										{
											final Release e = ie1.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate1))
											{
												minDate1 = releaseDate;
												release1 = e;
											}
										}
										final Iterator<Release> ie2 = releases(musicBrainz, recordingParam(s2));
										Assert.expect(ie2.hasNext());
										Release release2 = ie2.next();
										YearMonthDay minDate2 = release2.getReleaseDate();
										while (ie2.hasNext())
										{
											final Release e = ie2.next();
											final YearMonthDay releaseDate = e.getReleaseDate();
											if (YearMonthDay.isSmaller(releaseDate, minDate2))
											{
												minDate2 = releaseDate;
												release2 = e;
											}
										}
										final String id = release1.getId();
										if (id.equals(release2.getId()))
										{
											final MetaData metaData = musicBrainz.query("release/" + id, Parameter.inc("recordings"));
											final Iterator<Medium> iterator = metaData._release._mediumList._mediums.iterator();
											for (;;)
											{
												Medium m = iterator.next();
												final Iterator<Track> iTrack = m._trackList._tracks.iterator();
												while (iTrack.hasNext())
												{
													Track track = iTrack.next();
													final String recordingId = track._recording._id;
													if (recordingId.equals(s1))
													{
														return -1;
													}
													if (recordingId.equals(s2))
													{
														return 1;
													}
												}
											}
										}
										else
										{
											if (YearMonthDay.isSmaller(minDate1, minDate2))
											{
												return -1;
											}
											else
											{
												if (YearMonthDay.isSmaller(minDate2, minDate1))
												{
													return 1;
												}
												else
												{
													return s1.compareTo(s2);
												}
											}
										}
									}
								});
								final Iterator<RelationList> irl = entity._relationLists.iterator();
								for (;;)
								{
									if (!irl.hasNext())
									{
										switch (recordings.size())
										{
										case 0:
											TitanCollector.fail();
											break;
										case 1:
										{
											final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
											if (iPlayList.hasNext())
											{
												fail();
												iPlayList.next().remove();
											}
											final Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", recordings.iterator().next()).iterator();
											if (iRecordingVertex.hasNext())
											{
												current.addEdge("playlist", iRecordingVertex.next());
											}
										}
										break;
										default:
											final Iterator<Edge> iPlayList = current.getEdges(Direction.OUT, "playlist").iterator();
											if (iPlayList.hasNext())
											{
												iPlayList.next().remove();
											}
											Iterator<String> ird = recordings.descendingIterator();
											Iterator<Vertex> iRecordingVertex = tg.getVertices("recording.mbid", ird.next()).iterator();
											Assert.expect(iRecordingVertex.hasNext());
											Vertex playList = iRecordingVertex.next();
											l2: for (;;)
											{
												if (!ird.hasNext())
												{
													current.addEdge("playlist", playList);
													break;
												}
												String nextRecordId = ird.next();
												final Iterator<Vertex> iRest = playList.getVertices(Direction.IN, "rest").iterator();
												for (;;)
												{
													if (!iRest.hasNext())
													{
														for (;;)
														{
															Vertex v = enqueueNewTask(tg, TaskType.PLAYLIST);
															v.addEdge("rest", playList);
															v.addEdge("first", tg.getVertices("recording.mbid", nextRecordId).iterator().next());
															if (!ird.hasNext())
															{
																current.addEdge("playlist", v);
																break;
															}
															nextRecordId = ird.next();
															playList = v;
														}
														break l2;
													}
													Vertex currentRest = iRest.next();
													if (currentRest.getVertices(Direction.OUT, "first").iterator().next().getProperty("recording.mbid").equals(nextRecordId))
													{
														playList = currentRest;
														break;
													}
												}
											}
											break;
										}
										break;
									}
									final RelationList rl = irl.next();
									switch (rl._targetType)
									{
									case RECORDING:
									{
										final Iterator<Relation> r = rl._relations.iterator();
										while (r.hasNext())
										{
											final Relation relation = r.next();
											switch (relation._typeId)
											{
											case PLACE_RECORDED_AT:
											case PLACE_MIXED_AT:
												recordings.add(relation._recording._id);
												break;
											default:
												TitanCollector.fail();
												break;
											}
										}
									}
									break;
									case PLACE:
										final Iterator<Relation> r = rl._relations.iterator();
										Assert.expect(r.hasNext());
										final Relation relation = r.next();
										switch (relation._typeId)
										{
										case PLACE_PLACE_PARTS:
											Assert.expect(relation._direction == rasuni.musicbrainz.Direction.BACKWARD);
											break;
										default:
											TitanCollector.fail();
											break;
										}
										Assert.expect(!r.hasNext());
										break;
									default:
										TitanCollector.fail();
										break;
									}
								}
							}
							return false;
						}, tg, Place::getName, Place::getId);
						break;
					}
					case DISC_ID:
						final String foreignId = TitanCollector.string(Resource.DISC_ID.getMBID()).get(getCurrent(tg));
						if (foreignId == null)
						{
							System.out.println("Warning: fixing null discid!");
							removeCurrent(tg, "removed", Resource.DISC_ID.getName(), null);
						}
						else
						{
							final String id = "discid/" + foreignId;
							final MetaData metaData = musicBrainz.query(id);
							if (metaData == null)
							{
								removeCurrent(tg, "removed", Resource.DISC_ID.getName(), foreignId);
							}
							else
							{
								TitanCollector.fail();
								final Disc entity = metaData._disc;
								final String url = "http://musicbrainz.org/" + id;
								final String entityId = entity.getId();
								log(new String[] { url, entityId }, System.out);
								if (foreignId.equals(entityId))
								{
									TitanCollector.fail();
									requeue(tg);
								}
								else
								{
									includeEntity(Entity::getId, entity, Resource.DISC_ID, tg, System.out, Entity::getId, v ->
									{ // empty
									}, v -> null, v -> null);
									removeCurrent(tg, "merged", foreignId, entityId);
								}
							}
						}
						// Assert.fail();
						break;
					case SERIES:
						if (processResource(Resource.SERIES, metaData -> metaData._series, musicBrainz,
								Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels"), (fid, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								throw unexpected();
							}
							return false;
						}, tg, Series::getName, Series::getId))
						{
							fail();
							break l1;
						}
						break;
					case EVENT:
						if (processResource(Resource.EVENT, metaData -> metaData._event, musicBrainz,
								Parameter.inc("area-rels+artist-rels+event-rels+instrument-rels+label-rels+place-rels+recording-rels+release-rels+release-group-rels+series-rels+url-rels+work-rels"), (fid, entity) ->
						{
							if (processRelations(entity, a -> a._relationLists, tg))
							{
								throw unexpected();
							}
							return false;
						}, tg, Event::getName, Event::getId))
						{
							fail();
							break l1;
						}
						break;
					default:
						TitanCollector.fail();
						break;
					}
					break;
				case ACOUST_ID:
					final String foreignId = Key.ACOUST_ID.get(current);
					final String url = "http://acoustid.org/track/" + foreignId;
					log(new String[] { url }, System.out);
					if (hasMultipleRecordings(acoustId, foreignId))
					{
						requeue(tg);
						log(new String[] { "Multiple recordings with same acoust-id." }, System.out);
						break l1;
					}
					else
					{
						requeue(tg);
						break;
					}
				case ROOT:
					for (final Iterable<String> root : roots)
					{
						boolean added = false;
						Vertex next = system;
						for (final String entry : root)
						{
							final Iterator<Edge> iterator = Files.NAME.has(next.query().direction(Direction.OUT).labels(Files.DIRECTORY_ENTRY), entry).edges().iterator();
							String action;
							Vertex result;
							if (iterator.hasNext())
							{
								action = "already added";
								result = Edges.getHead(iterator.next());
							}
							else
							{
								action = "adding";
								result = TitanCollector.newTask(tg, TaskType.FILESYSTEMOBJECT);
								Files.addDirectoryEntry(next, result, entry);
								added = true;
							}
							logi(System.out, action + " entry " + entry);
							next = result;
						}
						if (added)
						{
							TitanCollector.enqueue(tg, next);
							break;
						}
					}
					requeue(tg);
					break;
				case PLAYLIST:
					System.out.print("playlist ");
					System.out.println(current.getId().toString());
					final Iterator<Vertex> iterator = current.getVertices(Direction.IN, "playlist").iterator();
					if (iterator.hasNext())
					{
						Vertex source = iterator.next();
						// println(" source: " + source.getId().toString());
						// println(" taskType:" + TASK_TYPE_KEY.get(source));
						failIf(TASK_TYPE_KEY.get(source) != TaskType.MB_RESOURCE);
						// println(" resource.kind: " + new
						// EnumKey<>("resource.kind",
						// Resource.values()).get(source));
						File file2 = null;
						for (final String entry1 : playListPath)
						{
							file2 = new File(file2, entry1);
						}
						System.out.print("  ");
						LinkedList<String> lines = new LinkedList<>();
						// boolean completed = true;
						switch (new EnumKey<>("resource.kind", Resource.values()).get(source))
						{
						case PLACE:
						{
							final String placeId = source.getProperty("place.mbid");
							MetaData md = musicBrainz.get("place/" + placeId);
							final File plDir = new File(new File(file2, "playlists"), "place");
							File plFile = new File(plDir, md._place.getName() + ".m3u8");
							System.out.println(plFile.toString());
							failIf(plFile.exists());
							System.out.print("  http://musicbrainz.org/place/");
							System.out.println(placeId);
							lines.add("# place/" + placeId);
							failIf(iterator.hasNext());
							Vertex playList = current;
							do
							{
								final Iterator<Vertex> iFirst = playList.getVertices(Direction.OUT, "first").iterator();
								failIf(!iFirst.hasNext());
								Vertex recording = iFirst.next();
								failIf(iFirst.hasNext());
								final Iterator<Vertex> ifile = recording.getVertices(Direction.IN, FILE_RECORDING).iterator();
								failIf(!ifile.hasNext());
								lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile.next())));
								playList = playList.getVertices(Direction.OUT, "rest").iterator().next();
							} while (TASK_TYPE_KEY.get(playList) == TaskType.PLAYLIST);
							final Iterator<Vertex> ifile2 = playList.getVertices(Direction.IN, FILE_RECORDING).iterator();
							failIf(!ifile2.hasNext());
							lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile2.next())));
							plDir.mkdirs();
							try
							{
								java.nio.file.Files.write(plFile.toPath(), lines, StandardCharsets.UTF_8);
							}
							catch (IOException e)
							{
								throw new RuntimeException(e);
							}
							requeue(tg);
							System.out.println("  complete!");
							break l1;
						}
						case ARTIST:
						{
							final String artistId = source.getProperty("artist.mbid");
							MetaData md = musicBrainz.get("artist/" + artistId);
							final File plDir = new File(new File(file2, "playlists"), "artist");
							File plFile = new File(plDir, md._artist.getName() + ".m3u8");
							System.out.println(plFile.toString());
							if (plFile.exists())
							{
								try
								{
									Iterator<String> iLine = java.nio.file.Files.readAllLines(plFile.toPath(), StandardCharsets.UTF_8).iterator();
									String line = iLine.next();
									Assert.expect(line.startsWith("# artist/"));
									Assert.expect(line.substring(9).equals(artistId));
								}
								catch (IOException e)
								{
									throw new RuntimeException(e);
								}
							}
							// failIf(plFile.exists());
							System.out.print("  http://musicbrainz.org/artist/");
							System.out.println(artistId);
							lines.add("# artist/" + artistId);
							failIf(iterator.hasNext());
							Vertex playList = current;
							boolean completed = true;
							do
							{
								final Iterator<Vertex> iFirst = playList.getVertices(Direction.OUT, "first").iterator();
								failIf(!iFirst.hasNext());
								Vertex recording = iFirst.next();
								failIf(iFirst.hasNext());
								final Iterator<Vertex> ifile = recording.getVertices(Direction.IN, FILE_RECORDING).iterator();
								if (ifile.hasNext())
								{
									lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile.next())));
								}
								else
								{
									completed = false;
								}
								playList = playList.getVertices(Direction.OUT, "rest").iterator().next();
							} while (TASK_TYPE_KEY.get(playList) == TaskType.PLAYLIST);
							final Iterator<Vertex> ifile2 = playList.getVertices(Direction.IN, FILE_RECORDING).iterator();
							if (ifile2.hasNext())
							{
								lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile2.next())));
							}
							else
							{
								completed = false;
							}
							plDir.mkdirs();
							try
							{
								java.nio.file.Files.write(plFile.toPath(), lines, StandardCharsets.UTF_8);
							}
							catch (IOException e)
							{
								throw new RuntimeException(e);
							}
							requeue(tg);
							if (completed)
							{
								System.out.println("  complete!");
								// fail();
								break l1;
							}
							break;
						}
						case WORK:
						{
							final String workId = source.getProperty("work.mbid");
							MetaData md = musicBrainz.get("work/" + workId);
							final File plDir = new File(new File(file2, "playlists"), "work");
							final String title = md._work.getTitle();
							int len = title.length();
							StringBuilder sb = new StringBuilder(len);
							for (int i = 0; i < len; i++)
							{
								char ch = title.charAt(i);
								if (ch == ':')
								{
									sb.append("%3A");
								}
								else
								{
									failIf(ch == '%');
									sb.append(ch);
								}
							}
							File plFile = new File(plDir, sb.toString() + ".m3u8");
							System.out.println(plFile.toString());
							if (plFile.exists())
							{
								try
								{
									Iterator<String> iLine = java.nio.file.Files.readAllLines(plFile.toPath(), StandardCharsets.UTF_8).iterator();
									String line = iLine.next();
									Assert.expect(line.startsWith("# work/"));
									Assert.expect(line.substring(7).equals(workId));
								}
								catch (IOException e)
								{
									throw new RuntimeException(e);
								}
							}
							// failIf(plFile.exists());
							System.out.print("  http://musicbrainz.org/work/");
							System.out.println(workId);
							lines.add("# work/" + workId);
							while (iterator.hasNext())
							{
								Vertex se = iterator.next();
								switch (new EnumKey<>("resource.kind", Resource.values()).get(se))
								{
								case ARTIST:
									final String eId = se.getProperty("artist.mbid");
									System.out.print("  http://musicbrainz.org/artist/");
									System.out.println(eId);
									lines.add("# artist/" + eId);
									break;
								default:
									fail();
								}
							}
							Vertex playList = current;
							for (;;)
							{
								final Iterator<Vertex> iFirst1 = playList.getVertices(Direction.OUT, "first").iterator();
								failIf(!iFirst1.hasNext());
								Vertex recording1 = iFirst1.next();
								failIf(iFirst1.hasNext());
								final Iterator<Vertex> ifile1 = recording1.getVertices(Direction.IN, FILE_RECORDING).iterator();
								if (!ifile1.hasNext())
								{
									for (;;)
									{
										playList = playList.getVertices(Direction.OUT, "rest").iterator().next();
										if (TASK_TYPE_KEY.get(playList) != TaskType.PLAYLIST)
										{
											break;
										}
										final Iterator<Vertex> iFirst = playList.getVertices(Direction.OUT, "first").iterator();
										failIf(!iFirst.hasNext());
										Vertex recording = iFirst.next();
										failIf(iFirst.hasNext());
										final Iterator<Vertex> ifile = recording.getVertices(Direction.IN, FILE_RECORDING).iterator();
										if (ifile.hasNext())
										{
											lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile.next())));
										}
									}
									final Iterator<Vertex> ifile2 = playList.getVertices(Direction.IN, FILE_RECORDING).iterator();
									failIf(ifile2.hasNext());
									// lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile2.next())));
									plDir.mkdirs();
									try
									{
										java.nio.file.Files.write(plFile.toPath(), lines, StandardCharsets.UTF_8);
									}
									catch (IOException e)
									{
										throw new RuntimeException(e);
									}
									requeue(tg);
									break;
								}
								lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile1.next())));
								playList = playList.getVertices(Direction.OUT, "rest").iterator().next();
								if (TASK_TYPE_KEY.get(playList) != TaskType.PLAYLIST)
								{
									final Iterator<Vertex> ifile2 = playList.getVertices(Direction.IN, FILE_RECORDING).iterator();
									failIf(!ifile2.hasNext());
									lines.add(toFilePath(Files.getDirectoryEntryEdge(ifile2.next())));
									plDir.mkdirs();
									try
									{
										java.nio.file.Files.write(plFile.toPath(), lines, StandardCharsets.UTF_8);
									}
									catch (IOException e)
									{
										throw new RuntimeException(e);
									}
									requeue(tg);
									System.out.println("  complete!");
									// fail();
									break l1;
								}
							}
							break;
						}
						default:
							fail();
						}
					}
					else
					{
						if (current.getVertices(Direction.IN, "rest").iterator().hasNext())
						{
							System.out.println("  in use by other playlist");
							requeue(tg);
						}
						else
						{
							removeCurrent(tg, "remove (unused)");
						}
					}
					// Assert.fail();
					break;
				default:
					TitanCollector.fail();
					break;
				}
				tg.commit();
				// break;
			}
			// Assert.fail();
			tg.commit();
		}
		finally
		{
			tg.rollback();
			tg.shutdown();
		}
	}

	/**
	 */
	public static void main_deprecated()
	{
		final LinkedList<Iterable<String>> roots = new LinkedList<>();
		roots.add(Arrays.asList("\\\\qnap", "Qmultimedia"));
		roots.add(Arrays.asList("D:", "ITunes", "Music"));
		roots.add(Arrays.asList("\\\\qnap", "music"));
		run("collection", roots, false, Arrays.asList("\\\\qnap", "Qmultimedia"));
	}

	private final static HashSet<String> NAMES_TO_DELETE = new HashSet<>();

	private static void nameToDelete(final String name)
	{
		NAMES_TO_DELETE.add(name);
	}

	private static void mediaExtension(final String extension)
	{
		MEDIA_EXTENSION.add(extension);
	}

	private final static HashSet<String> EXTENSIONS_TO_DELETE = new HashSet<>();

	private static void extensionToDelete(final String extension)
	{
		EXTENSIONS_TO_DELETE.add(extension);
	}

	private static final HashSet<String> MEDIA_EXTENSION = new HashSet<>();
	static
	{
		nameToDelete("infobrowser.opml");
		nameToDelete("Thumbs.db");
		nameToDelete("cover.gif");
		nameToDelete("folder.gif");
		mediaExtension("m4a");
		mediaExtension("flac");
		mediaExtension("m4p");
		mediaExtension("mp3");
		mediaExtension("wma");
		extensionToDelete("jpg");
		extensionToDelete("txt");
		extensionToDelete("ffp");
		extensionToDelete("md5");
		extensionToDelete("m4v");
		extensionToDelete("st5");
	}

	private final static String FILE_RECORDING = "file.recording";

	private static void setVersion(final Vertex system, final int version)
	{
		TitanCollector.set(system, integer("systemVersion"), version);
	}

	private static File toFile(final Edge startEntry)
	{
		Vertex currentEntry;
		final LinkedList<String> path = new LinkedList<>();
		Edge entry = startEntry;
		do
		{
			path.addFirst(Files.NAME.get(entry));
			currentEntry = TitanCollector.getTail(entry);
			entry = Files.getDirectoryEntryEdge(currentEntry);
		} while (entry != null);
		File file1 = null;
		for (final String entry1 : path)
		{
			file1 = new File(file1, entry1);
		}
		final File file = file1;
		return file;
	}

	private static <T> Boolean include1(final Iterable<T> iterable, final IConsumer<Vertex> initializer, final TaskType taskType, final TitanGraph tg, final String entryType, final String name)
	{
		return include(iterable, System.out, new String[] { entryType, name }, v ->
		{
			return false;
		}, v ->
		{
			initializer.accept(v);
			return true;
		}, tg, taskType);
	}

	private static void recordingLog(final String prefix, final Vertex current)
	{
		System.out.println(prefix + " http://musicbrainz.org/recording/" + string("recording.mbid").get(current));
	}

	private static Pair<Vertex, Boolean> includeMBEntity(final Resource resource, final String mbid, final TitanGraph tg)
	{
		return TitanCollector.includeMBEntity(resource, tg, mbid, System.out, t -> new String[] { t, mbid }, v -> new Pair<>(v, false), v -> new Pair<>(v, true));
	}

	private static boolean processRecordingFile(final boolean checkAccuracy, final TitanGraph tg, final Recording recording)
	{
		final Vertex current = TitanCollector.getCurrent(tg);
		final Iterator<Vertex> ifile = current.getVertices(Direction.IN, FILE_RECORDING).iterator();
		if (ifile.hasNext())
		{
			final Vertex currentEntry2 = ifile.next();
			final Edge entry = Files.getDirectoryEntryEdge(currentEntry2);
			if (Files.NAME.get(entry).endsWith(".flac") || !checkAccuracy)
			{
				if (ifile.hasNext())
				{
					System.out.println("Multiple files for recording:");
					System.out.println("  " + toFilePath(entry));
					System.out.println("  " + toFilePath(Files.getDirectoryEntryEdge(ifile.next())));
					return true;
				}
				else
				{
					Assert.expect(Edges.getReference(currentEntry2, "nextTask") != null);
					final ISRCList isrcList = recording._isrcList;
					if (isrcList != null)
					{
						Assert.expect(isrcList.isCount(1));
						TitanCollector.includeMBEntity(Resource.ISRC, isrcList._isrcs.getFirst()._id, tg);
					}
					return false;
				}
			}
			else
			{
				recordingLog(" ", current);
				System.out.println("File not accurate " + toFilePath(entry));
				return true;
			}
		}
		else
		{
			recordingLog("Recording not in collection", current);
			return true;
		}
	}

	/**
	 * @param entry
	 * @return
	 */
	private static String toFilePath(final Edge entry)
	{
		return toFile(entry).toString();
	}

	private static <T> boolean includeRelations(final RelationList rl, final IExpression2<Boolean, TitanGraph, T> include, final TitanGraph tg, final IExpression<T, Relation> entityFromRelation)
	{
		final Iterator<Relation> r = rl._relations.iterator();
		for (;;)
		{
			if (!r.hasNext())
			{
				return false;
			}
			if (include.apply(tg, entityFromRelation.apply(r.next())))
			{
				return true;
			}
		}
	}

	private static LinkedList<AcoustId> getAcoustIds(final WebService<Response> acoustId, final String recordingId)
	{
		return acoustId.get("track/list_by_mbid?format=xml&mbid=" + recordingId)._tracks._tracks;
	}

	private static boolean processAcoustIds(final WebService<Response> acoustId, final boolean checkAccuracy, final TitanGraph tg, final Recording entity)
	{
		final LinkedList<AcoustId> tracks = getAcoustIds(acoustId, entity._id);
		if (tracks != null)
		{
			final Iterator<AcoustId> it = tracks.iterator();
			for (;;)
			{
				if (!it.hasNext())
				{
					return processRecordingFile(checkAccuracy, tg, entity);
				}
				final AcoustId track = it.next();
				final String trackId = track._id;
				if (include1(tg.getVertices("acoust.id", trackId), newAcoustId -> TitanCollector.set(newAcoustId, Key.ACOUST_ID, trackId), TaskType.ACOUST_ID, tg, "acoustid", trackId))
				{
					return false;
				}
			}
		}
		else
		{
			return processRecordingFile(checkAccuracy, tg, entity);
		}
	}

	private static boolean addArea(final IExpression<Area, Artist> area, final Artist entity, final TitanGraph tg)
	{
		final Area e = area.apply(entity);
		return e == null || !TitanCollector.addArea(e, System.out, tg);
	}

	private static boolean processAreas(final Artist entity, final TitanGraph tg)
	{
		return addArea(artist -> artist._area, entity, tg) && addArea(artist -> artist._beginArea, entity, tg) && addArea(artist -> artist._endArea, entity, tg);
	}

	private static <EL extends EntityList, T> Iterator<T> iterator(final IExpression<EL, MetaData> entityList, final WebService<MetaData> musicBrainz, final Resource resource, final Parameter parameter, final IExpression<LinkedList<T>, EL> listExpr)
	{
		return new Iterator<T>()
		{
			private int _offset = 0;

			private EL _list;

			private Iterator<T> _ie;
			{
				fetch();
			}

			private LinkedList<T> entries()
			{
				return listExpr.apply(_list);
			}

			@Override
			public boolean hasNext()
			{
				return ifNull(_ie, false, () ->
				{
					if (_ie.hasNext())
					{
						return true;
					}
					else
					{
						_offset += entries().size();
						if (_list.isCount(_offset))
						{
							return false;
						}
						else
						{
							fetch();
							return true;
						}
					}
				});
			}

			private void fetch()
			{
				_list = entityList.apply(musicBrainz.query(resource.getName(), parameter, MetaData.PARAMETER_LIMIT, new Parameter("offset", String.valueOf(_offset))));
				_ie = ifNull(_list, null, () -> entries().iterator());
			}

			@Override
			public T next()
			{
				return _ie.next();
			}

			@Override
			public void remove()
			{
				_ie.remove();
			}
		};
	}

	private static <EL extends EntityList, T> boolean browse(final WebService<MetaData> musicBrainz, final Resource resource, final Parameter parameter, final IExpression<EL, MetaData> entityList, final IExpression<LinkedList<T>, EL> listExpr,
			IConsumer<Vertex> checkVertex, final TitanGraph tg, final IExpression<String, T> name, IConsumer<String> inspect, IExpression<String, T> id)
	{
		final Iterator<T> ie = iterator(entityList, musicBrainz, resource, parameter, listExpr);
		for (;;)
		{
			if (!ie.hasNext())
			{
				return true;
			}
			if (includeEntity(ie.next(), id, resource, tg, System.out, name, inspect, checkVertex))
			{
				return false;
			}
		}
	}

	private static <T> boolean processRelations(final T entity, IExpression<LinkedList<RelationList>, T> rle, final TitanGraph tg)
	{
		final LinkedList<RelationList> relationLists = rle.apply(entity);
		return ifNull(relationLists, true, () ->
		{
			final Iterator<RelationList> irl = relationLists.iterator();
			for (;;)
			{
				if (!irl.hasNext())
				{
					return true;
				}
				final RelationList rl = irl.next();
				switch (rl._targetType)
				{
				case ARTIST:
					if (includeRelations(rl, (tg1, artist) ->
					{
						return ifNull(artist, Boolean.FALSE, (IProvider<Boolean>) () ->
						{
							final String mbid = artist.getId();
							final IConsumer<String> l = (String s) ->
							{
								indent(System.out);
								printSpace(System.out, s);
								printSpace(System.out, "artist");
								printSpace(System.out, mbid);
								System.out.println(artist.getName());
							};
							return getFirst(tg1.getVertices("artist.mbid", mbid), (Vertex first) ->
							{
								l.accept("already added");
								return false;
							}, () ->
							{
								Vertex v = enqueueNewTask(tg1, TaskType.MB_RESOURCE);
								setProperty(v, "resource.kind", Resource.ARTIST);
								v.setProperty("artist.mbid", mbid);
								l.accept("adding");
								return true;
							});
						});
					}, tg, relation -> relation._artist))
					{
						return false;
					}
					break;
				case RECORDING:
					if (includeRelations(rl, (TitanGraph tg1, final Recording recording) -> addRecording(recording, System.out, tg1), tg, relation -> relation._recording))
					{
						return false;
					}
					break;
				case RELEASE:
					if (includeRelations(rl, (TitanGraph tg1, final Release release) -> addRelease(release, System.out, tg1), tg, relation -> relation._release))
					{
						return false;
					}
					break;
				case URL:
					if (includeRelations(rl, (tg1, target) ->
					{
						return includeEntity11(target1 -> target1._id, target, Resource.URL, tg1, System.out, target1 -> target1._target, v ->
						{ // empty
						}, v -> false, v -> true);
					}, tg, relation -> relation._target))
					{
						return false;
					}
					break;
				case WORK:
					if (includeRelations(rl, (tg1, work) ->
					{
						return ifNull(work, false, (IProvider<Boolean>) () ->
						{
							final String mbid = work.getId();
							final Consumer<String> l = (String s) ->
							{
								indent(System.out);
								printSpace(System.out, s);
								printSpace(System.out, "work");
								printSpace(System.out, mbid);
								System.out.println(work.getTitle());
							};
							return getFirst(tg1.getVertices("work.mbid", mbid), (Vertex first) ->
							{
								l.accept("already added");
								return false;
							}, () ->
							{
								Vertex v = enqueueNewTask(tg1, TaskType.MB_RESOURCE);
								setProperty(v, "resource.kind", Resource.WORK);
								v.setProperty("work.mbid", mbid);
								l.accept("adding");
								return true;
							});
						});
					}, tg, relation -> relation._work))
					{
						return false;
					}
					break;
				case LABEL:
					if (includeRelations(rl, (tg1, label) -> addEntity(label, Resource.LABEL, tg1, System.out), tg, relation -> relation._label))
					{
						return false;
					}
					break;
				case PLACE:
					if (includeRelations(rl, (tg1, p) -> addEntity(p, Resource.PLACE, tg1, System.out), tg, relation -> relation._place))
					{
						return false;
					}
					break;
				case AREA:
					if (includeRelations(rl, (tg1, area) -> addArea(area, System.out, tg1), tg, relation -> relation._area))
					{
						return false;
					}
					break;
				case RELEASE_GROUP:
					if (includeRelations(rl, (tg1, rg) -> addEntity(rg, Resource.RELEASE_GROUP, tg1, System.out), tg, relation -> relation._releaseGroup))
					{
						return false;
					}
					break;
				case SERIES:
					if (includeRelations(rl, (tg1, series) -> addResource(series, Series::getId, System.out, "series", Series::getName, tg, "series.mbid", Resource.SERIES), tg, relation -> relation._series))
					{
						return false;
					}
					break;
				case EVENT:
					if (includeRelations(rl, (tg1, event) -> addResource(event, Event::getId, System.out, "event", Event::getName, tg, "event.mbid", Resource.EVENT), tg, relation -> relation._event))
					{
						return false;
					}
					break;
				default:
					TitanCollector.fail();
					break;
				}
			}
		});
	}

	private static <T> Vertex lookup(final TitanGraph tg, final Resource resource, final T entity, IExpression<String, T> id)
	{
		return TitanCollector.getFirst(tg.getVertices(resource.getMBID(), id.apply(entity)));
	}

	private static boolean isRecordingMissing(final TitanGraph tg, final Recording r)
	{
		return !lookup(tg, Resource.RECORDING, r, Recording::getId).getEdges(Direction.IN, FILE_RECORDING).iterator().hasNext();
	}

	private static boolean isComplete(final WebService<MetaData> musicBrainz, final Parameter parameter, final TitanGraph tg)
	{
		// Assert.fail (); // must verify
		final Iterator<Recording> ie = iterator(MetaData.RECORDING_LIST, musicBrainz, Resource.RECORDING, parameter, RecordingList.LIST);
		for (;;)
		{
			if (!ie.hasNext())
			{
				return true;
			}
			if (isRecordingMissing(tg, ie.next()))
			{
				return false;
			}
		}
	}

	private static Parameter recordingParam(final String recordingMBID)
	{
		return new Parameter("recording", recordingMBID);
	}

	private static String getEarliestRelease(final WebService<MetaData> musicBrainz, final String recordingMBID)
	{
		final Iterator<Release> ie = releases(musicBrainz, recordingParam(recordingMBID));
		Assert.expect(ie.hasNext());
		final Release release = ie.next();
		YearMonthDay minDate = release.getReleaseDate();
		String earliestRelease = release.getId();
		while (ie.hasNext())
		{
			final Release e = ie.next();
			final YearMonthDay releaseDate = e.getReleaseDate();
			if (YearMonthDay.isSmaller(releaseDate, minDate))
			{
				minDate = releaseDate;
				earliestRelease = e.getId();
			}
		}
		return earliestRelease;
	}

	private static Iterator<Release> releases(final WebService<MetaData> musicBrainz, final Parameter param)
	{
		return iterator(MetaData.RELEASE_LIST, musicBrainz, Resource.RELEASE, param, ReleaseList.ENTRIES);
	}

	private static void requeue(final TitanGraph tg)
	{
		enqueue(tg, removeCurrent(tg));
	}

	private static Vertex removeCurrent(final TitanGraph tg)
	{
		final Vertex current = Edges.remove(TitanCollector.getCurrentTask(Vertex::getEdges, tg));
		final Vertex nextTask = Edges.removeReference(current, "nextTask");
		// reassign system.lastTask
		TitanCollector.replacePrevious(current, nextTask);
		getRoot(tg).addEdge("system.currentTask", nextTask);
		return current;
	}

	private static void removeCurrent(final TitanGraph tg, final String... logs)
	{
		removeCurrent(tg).remove();
		TitanCollector.logi(System.out, join(TitanCollector.sequence(logs, 0), t -> t, ' '));
	}

	private static <E> boolean processResource(final Resource resource, final IExpression<E, MetaData> entityExpr, final WebService<MetaData> musicBrainz, final Parameter inc, final IExpression2<Boolean, String, E> processEntity, final TitanGraph tg,
			final IExpression<String, E> name, final IExpression<String, E> idl)
	{
		final String foreignId = TitanCollector.string(resource.getMBID()).get(getCurrent(tg));
		final String resName = resource.getName();
		final String id = resName + "/" + foreignId;
		final MetaData metaData = musicBrainz.query(id, inc);
		if (metaData == null)
		{
			removeCurrent(tg, "removed", resName, foreignId);
			return false;
		}
		else
		{
			final E entity = entityExpr.apply(metaData);
			final String url = "http://musicbrainz.org/" + id;
			log(new String[] { url, name.apply(entity) }, System.out);
			final String entityId = idl.apply(entity);
			if (foreignId.equals(entityId))
			{
				final boolean res = processEntity.apply(foreignId, entity);
				requeue(tg);
				return res;
			}
			else
			{
				includeEntity(idl, entity, resource, tg, System.out, name, mbid ->
				{ // empty
				}, v -> null, v -> null);
				removeCurrent(tg, "merged", foreignId, entityId);
				return false;
			}
		}
	}

	private static boolean processReleaseEvents(final Release entity, final TitanGraph tg)
	{
		final ReleaseEventList _releaseEventList = entity._releaseEventList;
		return ifNull(_releaseEventList, Boolean.TRUE, () ->
		{
			final Iterator<ReleaseEvent> ire = _releaseEventList._releaseEvents.iterator();
			for (;;)
			{
				if (!ire.hasNext())
				{
					return true;
				}
				final Area area = ire.next()._area;
				if (area != null)
				{
					if (TitanCollector.addArea(area, System.out, tg))
					{
						return false;
					}
				}
			}
		});
	}

	private static boolean browseArtists(final WebService<MetaData> musicBrainz, final Parameter parameter, final TitanGraph tg)
	{
		return browse(musicBrainz, Resource.ARTIST, parameter, metaData -> metaData._artistList, artistList -> artistList._artists, v ->
		{ // empty
		}, tg, Artist::toString, mbid ->
		{ // empty
		}, Artist::getId);
	}

	private static boolean browseReleaseGroups(final WebService<MetaData> musicBrainz, final Parameter param, IConsumer<Vertex> checkVertex, final TitanGraph tg)
	{
		return browse(musicBrainz, Resource.RELEASE_GROUP, param, metaData -> metaData._releaseGroupList, releaseGroupList -> releaseGroupList._releaseGroups, checkVertex, tg, ReleaseGroup::toString, mbid ->
		{ // empty
		}, Entity::getId);
	}

	private static boolean browseRecordings(final WebService<MetaData> musicBrainz, final Parameter param, final TitanGraph tg, IConsumer<String> inspect)
	{
		return browse(musicBrainz, Resource.RECORDING, param, MetaData.RECORDING_LIST, RecordingList.LIST, v ->
		{ // empty
		}, tg, Recording::getTitle, inspect, Recording::getId);
	}

	private static boolean browseReleases(final WebService<MetaData> musicBrainz, final Parameter parameter, IConsumer<Vertex> checkVertex, final TitanGraph tg, IConsumer<String> inspect)
	{
		return browse(musicBrainz, Resource.RELEASE, parameter, MetaData.RELEASE_LIST, ReleaseList.ENTRIES, checkVertex, tg, Release::getTitle, inspect, Release::getId);
	}

	private static <T> void assertNotComplete(final TitanGraph tg, final Resource resource, final T rel, IExpression<String, T> getId)
	{
		Assert.expect(!Boolean.TRUE.equals(Key.IS_COMPLETE.get(lookup(tg, resource, rel, getId))));
	}

	private static boolean hasMultipleRecordings(final WebService<Response> acoustId, final String id)
	{
		final Response res = acoustId.get("lookup?format=xml&client=2enkIyWW&meta=recordingids+sources&trackid=" + id);
		final LinkedList<Result> results = res._results._results;
		Assert.expect(results.size() == 1);
		return results.getFirst()._recordings._recordings.size() != 1;
	}

	private static void completed(final Vertex current, final Resource resource)
	{
		TitanCollector.set(current, Key.IS_COMPLETE, true);
		final String name = resource.getName();
		log(new String[] { "  completed " + name + " http://musicbrainz.org/" + name + "/" + TitanCollector.string(resource.getMBID()).get(current) }, System.out);
	}

	private static boolean capturePlayLists(IExpression4<Boolean, WebService<MetaData>, Parameter, IConsumer<Vertex>, TitanGraph> browse, WebService<MetaData> musicBrainz, Parameter param, TitanGraph tg)
	{
		return browse.apply(musicBrainz, param, v ->
		{
			Assert.expect(!v.getVertices(Direction.OUT, "playlist").iterator().hasNext());
		}, tg);
	}

	/**
	 * Run the collector
	 *
	 * @param db
	 *            the database name
	 * @param checkAccuracy
	 *            check accuracy
	 * @param baseEntries
	 *            the base entries
	 */
	public static void run(final String db, final boolean checkAccuracy, String[] baseEntries)
	{
		run(db, Arrays.asList((Iterable<String>) Arrays.asList(baseEntries)), checkAccuracy, Arrays.asList(baseEntries));
	}
}