package rasuni.titan;

import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.SchemaManager;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import rasuni.acoustid.Response;
import rasuni.acoustid.Result;
import rasuni.filesystemscanner.impl.FileSystemScanner;
import rasuni.functional.IConsumer;
import rasuni.functional.IExpression3;
import rasuni.functional.IFunction;
import rasuni.functional.IFunction2;
import rasuni.functional.IProvider;
import rasuni.graph.Key;
import rasuni.graph.api.IGraphDatabase;
import rasuni.graph.impl.Edges;
import rasuni.musicbrainz.Area;
import rasuni.musicbrainz.MetaData;
import rasuni.musicbrainz.Release;
import rasuni.musicbrainz.ReleaseEvent;
import rasuni.musicbrainz.ReleaseEventList;
import rasuni.musicbrainz.Resource;
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
	private static Key<String> string(String name)
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
	private static <T> T getReferenced(IExpression3<Iterable<T>, Vertex, Direction, String> expression, Vertex vertex, String label)
	{
		return getSingle(expression, vertex, Direction.OUT, label);
	}

	private static <T> T getCurrentTask(final IExpression3<Iterable<T>, Vertex, Direction, String> expression, final IGraphDatabase tg)
	{
		return getReferenced(expression, getRoot(tg), "system.currentTask");
	}

	private static Vertex getCurrent(final IGraphDatabase tg)
	{
		return getCurrentTask(TitanCollector::getVertices, tg);
	}

	private static Iterable<Vertex> getVertices(Vertex vertex, Direction direction, String label)
	{
		return () -> vertex.vertices(direction, label);
	}

	private static Iterable<Edge> getEdges(Vertex vertex, Direction direction, String label)
	{
		return () ->
		{
			return vertex.edges(direction, label);
		};
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
		return getSingle(TitanCollector::getEdges, vertex, Direction.IN, label);
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
		Vertex last = Edges.getTail(eLastTask);
		eLastTask.remove();
		FileSystemScanner.setNextTask(last, newNext);
	}

	private static void enqueue(final IGraphDatabase tg, final Vertex vEntry)
	{
		final Vertex current = getCurrent(tg);
		replacePrevious(current, vEntry);
		FileSystemScanner.setNextTask(vEntry, current);
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

	private static void setProperty(Element element, String name, Enum<?> value)
	{
		element.property(name, value.ordinal());
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
		Vertex v = tg.getVertexForId(tg.addVertex());
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
			v.property("resource.kind", resource);
			v.property(idProperty, mbid);
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
				v.property("resource.kind", resource);
				v.property(key, mbid);
				l.accept("adding");
				return Boolean.TRUE;
			});
		});
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
		final String name = k._name;
		return graph.containsPropertyKey(name) ? existing.provide() : created.apply(graph.makePropertyKey(name).cardinality(Cardinality.SINGLE).dataType(k._type).make());
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

	private static void requeue(final IGraphDatabase tg)
	{
		enqueue(tg, removeCurrent(tg));
	}

	/**
	 * Get first outgoing edge for specified label
	 *
	 * @param vertex
	 *            the source vertex
	 * @param label
	 *            the label
	 * @return the first outgoing edge
	 */
	private static Edge getReference(Vertex vertex, String label)
	{
		return getReferenced(TitanCollector::getEdges, vertex, label);
	}

	/**
	 * Remove a reference from a vertex
	 *
	 * @param vertex
	 *            the vertex
	 * @param label
	 *            the reference label
	 * @return the previously referenced vertex
	 */
	private static Vertex removeReference(Vertex vertex, String label)
	{
		Edge edge = getReference(vertex, label);
		return edge == null ? null : Edges.remove(edge);
	}

	private static Vertex removeCurrent(final IGraphDatabase tg)
	{
		final Vertex current = Edges.remove(TitanCollector.getCurrentTask(TitanCollector::getEdges, tg));
		final Vertex nextTask = removeReference(current, "nextTask");
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
		Key<String> r = TitanCollector.string(resName + ".mbid");
		final String foreignId = r.getProperty(getCurrent(tg), r._name);
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
			final Iterator<ReleaseEvent> ire = _releaseEventList._list.iterator();
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
}