package rasuni.titan;

import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.SchemaManager;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import rasuni.acoustid.AcoustId;
import rasuni.acoustid.Response;
import rasuni.acoustid.Result;
import rasuni.functional.IConsumer;
import rasuni.functional.IExpression3;
import rasuni.functional.IExpression4;
import rasuni.functional.IFunction;
import rasuni.functional.IFunction2;
import rasuni.functional.IProvider;
import rasuni.graph.Key;
import rasuni.graph.api.IGraphDatabase;
import rasuni.musicbrainz.Area;
import rasuni.musicbrainz.Artist;
import rasuni.musicbrainz.Entity;
import rasuni.musicbrainz.Event;
import rasuni.musicbrainz.ISRCList;
import rasuni.musicbrainz.MetaData;
import rasuni.musicbrainz.Recording;
import rasuni.musicbrainz.Relation;
import rasuni.musicbrainz.RelationList;
import rasuni.musicbrainz.Release;
import rasuni.musicbrainz.ReleaseEvent;
import rasuni.musicbrainz.ReleaseEventList;
import rasuni.musicbrainz.Resource;
import rasuni.musicbrainz.Series;
import rasuni.webservice.Parameter;
import rasuni.webservice.WebService;

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
	 *
	 * @return the new exception instance
	 */
	private static RuntimeException unexpected()
	{
		return new RuntimeException("Unexpected");
	}

	/**
	 * Unconditional fail
	 */
	private static void fail()
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
	private static <R, T> R getFirst(Iterable<T> iterable, IFunction<R, T> first, IProvider<R> empty)
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

	private static Vertex getRoot(final IGraphDatabase tg)
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

	private static <T> T getCurrentTask(final IExpression3<Iterable<T>, Vertex, Direction, String> expression, final IGraphDatabase tg)
	{
		return getReferenced(expression, getRoot(tg), "system.currentTask");
	}

	private static Vertex getCurrent(final IGraphDatabase tg)
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

	private static void enqueue(final IGraphDatabase tg, final Vertex vEntry)
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
	private static String join(ISequence<String> members, char separator)
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

	private static <T> ISequence<String> map(ISequence<T> sequence, IFunction<String, T> toString)
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
	private static <T> String join(ISequence<T> members, IFunction<String, T> toString, char separator)
	{
		return join(map(members, toString), separator);
	}

	private static void space(PrintStream out)
	{
		out.print(' ');
	}

	/**
	 * Create a sequence from an array
	 *
	 * @param <T>
	 *            the member type
	 * @param array
	 *            the array
	 * @param pos
	 *            the start index
	 * @return the corresponding sequence
	 */
	private static <T> ISequence<T> sequence(T[] array, int pos)
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
	private static void log(String[] texts, PrintStream out)
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

	private static <R, I> R include(PrintStream out, String addLog, final String[] logs, IFunction<R, I> expr, I i)
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
	 * Set the value to the element
	 *
	 * @param element
	 *            the element
	 * @param key
	 *            the key to set
	 * @param value
	 *            the new value
	 */
	private static void set(Element element, Key<?> key, Object value)
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
	 * @param tt
	 *            the task type
	 * @return the task vertex
	 */
	public static Vertex newTask(IGraphDatabase tg, TaskType tt)
	{
		Vertex v = tg.addVertex();
		setProperty(v, "task.type", tt);
		return v;
	}

	private static Vertex enqueueNewTask(IGraphDatabase tg, TaskType taskType)
	{
		Vertex v = newTask(tg, taskType);
		enqueue(tg, v);
		return v;
	}

	private static <R, I> R include(final Iterable<I> iterable, PrintStream out, final String[] logs, IFunction<R, I> found, IFunction<R, Vertex> added, final IGraphDatabase tg, final TaskType taskType)
	{
		return getFirst(iterable, (I first) ->
		{
			return include(out, "  already added ", logs, found, first);
		}, () ->
		{
			return include(out, "  adding ", logs, added, enqueueNewTask(tg, taskType));
		});
	}

	private static String getName(Resource resource)
	{
		switch (resource)
		{
		default:
			fail();
			return null;
		}
	}

	private static <R> R includeMBEntity(Resource resource, final IGraphDatabase tg, final String mbid, PrintStream out, final IFunction<String[], String> description, IFunction<R, Vertex> found, IFunction<R, Vertex> added)
	{
		final String resourceName = getName(resource);
		final String idProperty = resourceName + ".mbid";
		return include(tg.getVertices(idProperty, mbid), out, description.apply(resourceName), found, (Vertex v) ->
		{
			setProperty(v, "resource.kind", resource);
			v.setProperty(idProperty, mbid);
			return added.apply(v);
		}, tg, TaskType.MB_RESOURCE);
	}

	private static <R, E> R includeEntity11(final IFunction<String, E> id, final E entity, final Resource resource, final IGraphDatabase tg, PrintStream out, final IFunction<String, E> description, IConsumer<String> inspect, IFunction<R, Vertex> found,
			IFunction<R, Vertex> added)
	{
		final String mbid = id.apply(entity);
		return includeMBEntity(resource, tg, mbid, out, (String resourceName) -> new String[] { resourceName, mbid, description.apply(entity) }, (Vertex v) ->
		{
			inspect.accept(mbid);
			return found.apply(v);
		}, added);
	}

	private static <R, E> R includeEntity(IFunction<String, E> id, final E entity, final Resource resource, final IGraphDatabase tg, PrintStream out, final IFunction<String, E> description, IConsumer<String> inspect, IFunction<R, Vertex> found,
			IFunction<R, Vertex> added)
	{
		return includeEntity11(id, entity, resource, tg, out, description, inspect, found, added);
	}

	private static <E> boolean includeEntity(final E entity, IFunction<String, E> id, Resource resource, final IGraphDatabase tg, PrintStream out, final IFunction<String, E> description, IConsumer<String> inspect, IConsumer<Vertex> checkVertex)
	{
		return entity != null && includeEntity(id, entity, resource, tg, out, description, inspect, (Vertex v) ->
		{
			checkVertex.accept(v);
			return false;
		}, (Vertex v) -> true);
	}

	private static <E extends Entity> boolean addEntity(final E entity, final Resource resource, final IGraphDatabase tg, PrintStream out)
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

	private static <T> boolean addResource(T entity, IFunction<String, T> getId, PrintStream out, String resourceName, IFunction<String, T> getName, IGraphDatabase tg, String key, Resource resource)
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

	private static boolean addRecording(final Recording recording, PrintStream out, final IGraphDatabase tg)
	{
		return addResource(recording, Recording::getId, out, "recording", r -> r._title, tg, "recording.mbid", Resource.RECORDING);
	}

	private static boolean addRelease(final Release release, PrintStream out, final IGraphDatabase tg)
	{
		return addResource(release, Release::getId, out, "release", Release::getTitle, tg, "release.mbid", Resource.RELEASE);
	}

	private static boolean addArea(final Area area, PrintStream out, final IGraphDatabase tg)
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
	private static <R> R definePropertyKey(Key<?> k, SchemaManager graph, IProvider<R> existing, IFunction<R, PropertyKey> created)
	{
		final String name = k.getName();
		return graph.containsPropertyKey(name) ? existing.provide() : created.apply(graph.makePropertyKey(name).cardinality(Cardinality.SINGLE).dataType(k._type).make());
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
	private static void failIf(boolean condition)
	{
		if (condition)
		{
			fail();
		}
	}

	public static EdgeLabel makeLink(TitanManagement tm, String name, Multiplicity multiplicity)
	{
		return tm.makeEdgeLabel(name).multiplicity(multiplicity).make();
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

	public static void setVersion(final Vertex system, final int version)
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
			path.addFirst(NAME.get(entry));
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

	private static <T> Boolean include1(final Iterable<T> iterable, final IConsumer<Vertex> initializer, final TaskType taskType, final IGraphDatabase tg, final String entryType, final String name)
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

	private static Pair<Vertex, Boolean> includeMBEntity(final Resource resource, final String mbid, final IGraphDatabase tg)
	{
		return TitanCollector.includeMBEntity(resource, tg, mbid, System.out, t -> new String[] { t, mbid }, v -> new Pair<>(v, false), v -> new Pair<>(v, true));
	}

	private static boolean processRecordingFile(final boolean checkAccuracy, final IGraphDatabase tg, final Recording recording)
	{
		final Vertex current = TitanCollector.getCurrent(tg);
		final Iterator<Vertex> ifile = current.getVertices(Direction.IN, FILE_RECORDING).iterator();
		if (ifile.hasNext())
		{
			final Vertex currentEntry2 = ifile.next();
			final Edge entry = Files.getDirectoryEntryEdge(currentEntry2);
			if (NAME.get(entry).endsWith(".flac") || !checkAccuracy)
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
					expect(Edges.getReference(currentEntry2, "nextTask") != null);
					final ISRCList isrcList = recording._isrcList;
					if (isrcList != null)
					{
						expect(isrcList.isCount(1));
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

	private static <T> boolean includeRelations(final RelationList rl, final IFunction2<Boolean, IGraphDatabase, T> include, final IGraphDatabase tg, final IFunction<T, Relation> entityFromRelation)
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

	public static boolean processAcoustIds(final WebService<Response> acoustId, final boolean checkAccuracy, final IGraphDatabase tg, final Recording entity)
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

	private static boolean addArea(final IFunction<Area, Artist> area, final Artist entity, final IGraphDatabase tg)
	{
		final Area e = area.apply(entity);
		return e == null || !TitanCollector.addArea(e, System.out, tg);
	}

	public static boolean processAreas(final Artist entity, final IGraphDatabase tg)
	{
		return addArea(artist -> artist._area, entity, tg) && addArea(artist -> artist._beginArea, entity, tg) && addArea(artist -> artist._endArea, entity, tg);
	}

	public static <T> boolean processRelations(final T entity, IFunction<LinkedList<RelationList>, T> rle, final IGraphDatabase tg)
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
							final String mbid = artist._id;
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
					if (includeRelations(rl, (IGraphDatabase tg1, final Recording recording) -> addRecording(recording, System.out, tg1), tg, relation -> relation._recording))
					{
						return false;
					}
					break;
				case RELEASE:
					if (includeRelations(rl, (IGraphDatabase tg1, final Release release) -> addRelease(release, System.out, tg1), tg, relation -> relation._release))
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

	private static void requeue(final IGraphDatabase tg)
	{
		enqueue(tg, removeCurrent(tg));
	}

	private static Vertex removeCurrent(final IGraphDatabase tg)
	{
		final Vertex current = Edges.remove(TitanCollector.getCurrentTask(Vertex::getEdges, tg));
		final Vertex nextTask = Edges.removeReference(current, "nextTask");
		// reassign system.lastTask
		TitanCollector.replacePrevious(current, nextTask);
		getRoot(tg).addEdge("system.currentTask", nextTask);
		return current;
	}

	private static void removeCurrent(final IGraphDatabase tg, final String... logs)
	{
		removeCurrent(tg).remove();
		TitanCollector.logi(System.out, join(TitanCollector.sequence(logs, 0), t -> t, ' '));
	}

	public static <E> boolean processResource(final Resource resource, final IFunction<E, MetaData> entityExpr, final WebService<MetaData> musicBrainz, final Parameter inc, final IFunction2<Boolean, String, E> processEntity, final IGraphDatabase tg,
			final IFunction<String, E> name, final IFunction<String, E> idl)
	{
		final String resName = getName(resource);
		final String foreignId = TitanCollector.string(resName + ".mbid").get(getCurrent(tg));
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

	public static boolean processReleaseEvents(final Release entity, final IGraphDatabase tg)
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

	public static boolean hasMultipleRecordings(final WebService<Response> acoustId, final String id)
	{
		final Response res = acoustId.get("lookup?format=xml&client=2enkIyWW&meta=recordingids+sources&trackid=" + id);
		final LinkedList<Result> results = res._results._results;
		expect(results.size() == 1);
		return results.getFirst()._recordings._recordings.size() != 1;
	}

	public static void completed(final Vertex current, final String resourceName)
	{
		TitanCollector.set(current, Key.IS_COMPLETE, true);
		log(new String[] { "  completed " + resourceName + " http://musicbrainz.org/" + resourceName + "/" + TitanCollector.string(resourceName + ".mbid").get(current) }, System.out);
	}

	public static boolean capturePlayLists(IExpression4<Boolean, WebService<MetaData>, Parameter, IConsumer<Vertex>, TitanGraph> browse, WebService<MetaData> musicBrainz, Parameter param, TitanGraph tg)
	{
		return browse.apply(musicBrainz, param, v ->
		{
			expect(!v.getVertices(Direction.OUT, "playlist").iterator().hasNext());
		}, tg);
	}

	/**
	 * Register a property key
	 *
	 * @param key
	 *            the key
	 *
	 * @param tg
	 *            the titan graph
	 * @return the titan key
	 */
	public static PropertyKey makePropertyKey(Key<?> key, SchemaManager tg)
	{
		return TitanCollector.definePropertyKey(key, tg, () -> null, tk -> tk);
	}

	/**
	 * Register an association
	 *
	 * @param name
	 *            the association name
	 * @param uniqueDirection
	 *            the unique direction expression
	 * @param tg
	 *            the titan graph
	 */
	public static void defineAssociation(String name, IFunction<EdgeLabelMaker, EdgeLabelMaker> uniqueDirection, TitanManagement tg)
	{
		makeEdgeLabel(name, uniqueDirection, null, tg);
	}

	/**
	 * Register an edge label
	 *
	 * @param name
	 *            the name
	 * @param uniqueDirection
	 *            the unique direction expression
	 * @param primaryKey
	 *            the label key
	 * @param tg
	 *            the titan graph
	 */
	private static void makeEdgeLabel(String name, IFunction<EdgeLabelMaker, EdgeLabelMaker> uniqueDirection, PropertyKey primaryKey, TitanManagement managementSystem)
	{
		EdgeLabelMaker tm = uniqueDirection.apply(managementSystem.makeEdgeLabel(name));
		EdgeLabel em = tm.make();
		if (primaryKey != null)
		{
			managementSystem.buildEdgeIndex(em, name + ".index", Direction.OUT, primaryKey);
		}
	}

	/**
	 * Expect a true condition
	 *
	 * @param expected
	 *            expected to be true
	 */
	private static void expect(boolean expected)
	{
		TitanCollector.failIf(!expected);
	}

	/**
	 * Delete a file and log the deletion
	 *
	 * @param file
	 *            the file to delete
	 */
	public static void delete(File file)
	{
		log(new String[] { "  deleting" }, System.out);
		expect(file.delete());
	}

	/**
	 * The name key
	 */
	private final static Key<String> NAME = TitanCollector.string("name");
}