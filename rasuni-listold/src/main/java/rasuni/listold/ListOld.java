package rasuni.listold;

import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import rasuni.graph.api.IGraphDatabase;
import rasuni.graph.api.IVertex;
import rasuni.graph.impl.GraphDatabase;
import rasuni.titan.Edges;
import rasuni.titan.TaskType;
import rasuni.titan.TitanCollector;

/**
 * Lists old files
 */
public final class ListOld // NO_UCD (unused code)
{
	/**
	 * The new main method
	 *
	 * @param args
	 *            the argznebts
	 */
	public static void main(String[] args)
	{
		final IGraphDatabase tg = new GraphDatabase(TitanFactory.open("berkeleyje:" + "listold"));
		try
		{
			tg.makeVertexLabel("system");
			tg.makeIntPropertyKey("task.type");
			tg.makeStringPropertyKey("name");
			tg.makeAssocOneToMany("directory.entry");
			tg.makeEdgeIndex("directory.entry", "byName", "name");
			tg.makeAssocOneToOne("next.task");
			tg.makeLongPropertyKey("fso.lastAccess");
			tg.makeAssocOneToOne("system");
			tg.makeAssocOneToOne("system.currentTask");
			final IVertex vertexLabel = tg.getVertexLabel("system");
			if (vertexLabel.hasOutVertices("system"))
			{
				process(tg);
			}
			else
			{
				final Vertex system = tg.addVertex();
				vertexLabel.getVertex().addEdge("system", system);
				system.addEdge("system.currentTask", system);
				system.setProperty("task.type", TaskType.ROOT.ordinal());
				system.addEdge("next.task", system);
				process(tg);
			}
			tg.commit();
		}
		finally
		{
			tg.rollback();
			tg.shutdown();
		}
	}

	/*
		@Override
		public boolean execute(IFileSystemScanner scanner)
		{
		Long lastAccessTime = scanner.getMin(COLUMN_LAST_ACCESS_TIME, FILE);
		if (lastAccessTime != null && (_requirements == null || _requirements.fulfilled(lastAccessTime, _scanCount)))
		{
			LinkedList<String> commands = new LinkedList<>();
			IColumnValue criteria = new ColumnValue<>(COLUMN_LAST_ACCESS_TIME, lastAccessTime);
			int count = 0;
			for (File file : scanner.select(FILE, criteria))
			{
				if (file != null)
				{
					if (file.isHidden())
					{
						commands.add("ATTRIB -S -H \"" + file + '"');
					}
					commands.add("DEL \"" + file + '"');
				}
				count++;
			}
			int requiredCount = Math.max(count * 3 / 2, 2);
			if (requiredCount <= _scanCount)
			{
				System.out.println("Scan-count: " + _scanCount);
				for (String cmd : commands)
				{
					System.out.println(cmd);
				}
				scanner.delete(FILE, criteria);
				return false;
			}
			else
			{
				System.out.println("Required-count: " + requiredCount);
				_requirements = new Requirements(lastAccessTime, requiredCount);
				return true;
			}
		}
		else
		{
			return true;
		}
		}

		@Override
		public void visit(File file, IFileProcessingContext context)
		{
		try
		{
			BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			context.createOrUpdate(FILE, new ColumnValue<>(COLUMN_LAST_ACCESS_TIME, Math.max(Math.max(toDays(attrs.lastAccessTime()), toDays(attrs.creationTime())), toDays(attrs.lastModifiedTime()))));
			_scanCount++;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		}

		private static long toDays(FileTime time)
		{
		return time.to(TimeUnit.DAYS);
		}

		@Override
		public void provideRootEntries(IRootRegistry registry)
		{
		registry.register(Arrays.asList("C:\\"));
		registry.register(Arrays.asList("D:\\"));
		registry.register(Arrays.asList("\\\\qnap\\backup"));
		registry.register(Arrays.asList("\\\\qnap\\Network Recycle Bin 1"));
		registry.register(Arrays.asList("\\\\qnap\\Public"));
		registry.register(Arrays.asList("\\\\qnap\\Qdownload"));
		registry.register(Arrays.asList("\\\\qnap\\Qmultimedia"));
		registry.register(Arrays.asList("\\\\qnap\\Qrecordings"));
		registry.register(Arrays.asList("\\\\qnap\\Qusb"));
		registry.register(Arrays.asList("\\\\qnap\\Qweb"));
		registry.register(Arrays.asList("\\\\qnap\\music"));
		registry.register(Arrays.asList("\\\\qnap\\itunes"));
		registry.register(Arrays.asList("\\\\MUSIKSERVER\\Kunde"));
		registry.register(Arrays.asList("\\\\MUSIKSERVER\\Lights-Out"));
		registry.register(Arrays.asList("\\\\MUSIKSERVER\\Musik"));
		}

		@Override
		public void fileDeleted(IDeleteProcessingContext context)
		{
		context.delete(FILE);
		}
	 */
	private static void process(IGraphDatabase tg)
	{
		final Vertex system = tg.getOutVertices("system", "system").iterator().next();
		// LinkedList<String> lines = new LinkedList<>();
		l1: for (;;)
		{
			Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
			Vertex current = Edges.getHead(currentEdge);
			switch (TaskType.values()[(Integer) current.getProperty("task.type")])
			{
			case ROOT:
				System.out.println("root");
				if (registerRoot(tg, "C:\\", () ->
				{
					return registerRoot(tg, "D:\\", () ->
					{
						return registerRoot(tg, "\\\\qnap\\backup", () ->
						{
							return registerRoot(tg, "\\\\qnap\\Public", () ->
							{
								return registerRoot(tg, "\\\\qnap\\Qmultimedia", () ->
								{
									return registerRoot(tg, "\\\\qnap\\Qrecordings", () ->
									{
										Check.fail();
										return false;
									});
								});
							});
						});
					});
				}))
				{
					break l1;
				}
				// TitanCollector.fail();
				break;
			case FILESYSTEMOBJECT:
				final File file = toFile(current);
				System.out.println(file.toString());
				if (file.exists())
				{
					if (file.isDirectory())
					{
						final String[] entries = file.list();
						if (entries == null)
						{
							throw new RuntimeException("Access denied");
						}
						final int lEntries = entries.length;
						if (lEntries == 0)
						{
							try
							{
								BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
								long ct = attrs.creationTime().to(TimeUnit.DAYS);
								long lmt = attrs.lastModifiedTime().to(TimeUnit.DAYS);
								if (ct < lmt)
								{
									// creation time is min
									long lat = attrs.lastAccessTime().to(TimeUnit.DAYS);
									if (lat < ct)
									{
										// last access time is min
										current.setProperty("fso.lastAccess", lat);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
									}
									else
									{
										// creation time is min
										current.setProperty("fso.lastAccess", ct);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(ct) + "'");
									}
								}
								else
								{
									// last modification time is min
									long lat = attrs.lastAccessTime().to(TimeUnit.DAYS);
									if (lat < ct)
									{
										// last access time is min
										current.setProperty("fso.lastAccess", lat);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
									}
									else
									{
										// last modification time is max
										current.setProperty("fso.lastAccess", lmt);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(lmt) + "'");
									}
								}
							}
							catch (IOException e)
							{
								throw new RuntimeException(e);
							}
							complete(tg);
						}
						else
						{
							int iEntries = 0;
							for (;;)
							{
								final String name = entries[iEntries];
								if (!current.query().direction(Direction.OUT).labels("directory.entry").has("name", name).edges().iterator().hasNext())
								{
									System.out.println("  adding " + name);
									Vertex newEntry = TitanCollector.newTask(tg, TaskType.FILESYSTEMOBJECT);
									current.addEdge("directory.entry", newEntry).setProperty("name", name);
									Edge eLastTask = current.getEdges(Direction.IN, "next.task").iterator().next();
									Vertex last = eLastTask.getVertex(Direction.OUT);
									eLastTask.remove();
									last.addEdge("next.task", newEntry);
									newEntry.addEdge("next.task", current);
									break;
								}
								System.out.println("  already added " + name);
								iEntries++;
								if (iEntries == lEntries)
								{
									break;
								}
							}
							if (completeDirectory(tg))
							{
								break l1;
							}
						}
					}
					else
					{
						try
						{
							BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
							long ct = attrs.creationTime().to(TimeUnit.DAYS);
							long lmt = attrs.lastModifiedTime().to(TimeUnit.DAYS);
							if (ct < lmt)
							{
								// last modification time is max
								long lat = attrs.lastAccessTime().to(TimeUnit.DAYS);
								if (lat < lmt)
								{
									// last modified time is max
									current.setProperty("fso.lastAccess", lmt);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(lmt) + "'");
								}
								else
								{
									// last access time is max
									current.setProperty("fso.lastAccess", lat);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
								}
							}
							else
							{
								// creation time is max
								long lat = attrs.lastAccessTime().to(TimeUnit.DAYS);
								if (lat < ct)
								{
									// creation time is max
									current.setProperty("fso.lastAccess", ct);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(ct) + "'");
								}
								else
								{
									// last access time is max
									current.setProperty("fso.lastAccess", lat);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
								}
							}
						}
						catch (IOException e)
						{
							throw new RuntimeException(e);
						}
						complete(tg);
					}
				}
				else
				{
					System.out.println("  removing");
					final Edge ePrevious = current.getEdges(Direction.IN, "next.task").iterator().next();
					Vertex previous = ePrevious.getVertex(Direction.OUT);
					ePrevious.remove();
					final Edge eNext = current.getEdges(Direction.OUT, "next.task").iterator().next();
					Vertex next = eNext.getVertex(Direction.IN);
					eNext.remove();
					previous.addEdge("next.task", next);
					current.remove();
					system.addEdge("system.currentTask", next);
					tg.commit();
				}
				break;
			default:
				Check.fail();
			}
		}
	}

	private static boolean completeDirectory(IGraphDatabase tg)
	{
		final Vertex system = tg.getVertexLabel("system").getVertex().getVertices(Direction.OUT, "system").iterator().next();
		Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
		Vertex current = Edges.getHead(currentEdge);
		Iterator<Vertex> iEntries2 = current.getVertices(Direction.OUT, "directory.entry").iterator();
		for (;;)
		{
			if (!iEntries2.hasNext())
			{
				current.removeProperty("fso.lastAccess");
				System.out.println("  clear last access");
				complete(tg);
				return false;
			}
			final Long lat1 = iEntries2.next().getProperty("fso.lastAccess");
			if (lat1 != null)
			{
				long minTime = lat1.longValue();
				while (iEntries2.hasNext())
				{
					final Long lat2 = iEntries2.next().getProperty("fso.lastAccess");
					if (lat2 != null)
					{
						long lat2v = lat2.longValue();
						if (lat2v < minTime)
						{
							minTime = lat2v;
						}
					}
				}
				current.setProperty("fso.lastAccess", minTime);
				System.out.println("  set last access to '" + LocalDate.ofEpochDay(minTime) + "'");
				Long smt = system.getProperty("fso.lastAccess");
				if (smt == null)
				{
					complete(tg);
					return false;
				}
				else
				{
					LinkedList<String> lines = new LinkedList<>();
					dump(system, smt.longValue(), lines);
					if (lines.isEmpty())
					{
						//TitanCollector.fail();
						System.out.println("Info: dump empty!");
						complete(tg);
						return false;
					}
					else
					{
						System.out.println();
						for (String line : lines)
						{
							System.out.println(line);
						}
						complete(tg);
						return true;
					}
				}
			}
		}
	}

	/**
	 * @param current
	 * @return
	 */
	private static File toFile(Vertex current)
	{
		final LinkedList<String> path = new LinkedList<>();
		Vertex currentEntry = current;
		Edge entry = TitanCollector.getSingleIncoming(currentEntry, "directory.entry");
		do
		{
			path.addFirst(entry.getProperty("name"));
			currentEntry = TitanCollector.getTail(entry);
			entry = TitanCollector.getSingleIncoming(currentEntry, "directory.entry");
		} while (entry != null);
		File file1 = null;
		for (final String entry1 : path)
		{
			file1 = new File(file1, entry1);
		}
		final File file = file1;
		return file;
	}

	private static void dump(final Vertex parent, long minTime, LinkedList<String> lines)
	{
		Iterator<Edge> iEntries = parent.getEdges(Direction.OUT, "directory.entry").iterator();
		if (iEntries.hasNext())
		{
			for (;;)
			{
				Edge ce = iEntries.next();
				Vertex fso = Edges.getHead(ce);
				Long la = fso.getProperty("fso.lastAccess");
				if (la != null)
				{
					if (la.longValue() == minTime)
					{
						dump(fso, minTime, lines);
					}
					else
					{
						// TitanCollector.fail();
					}
				}
				if (!iEntries.hasNext())
				{
					break;
				}
			}
		}
		else
		{
			File file = toFile(parent);
			if (file.exists())
			{
				if (file.isDirectory())
				{
					if (file.list().length == 0)
					{
						lines.add("RD \"" + file.toString() + "\"");
					}
					else
					{
						Check.fail();
					}
				}
				else
				{
					try
					{
						DosFileAttributes attr = Files.readAttributes(file.toPath(), DosFileAttributes.class);
						if (attr.isHidden())
						{
							if (attr.isSystem())
							{
								lines.add("ATTRIB -H -S \"" + file.toString() + "\"");
							}
							else
							{
								Check.fail();
							}
						}
						else
						{
							if (attr.isReadOnly())
							{
								lines.add("ATTRIB -R \"" + file.toString() + "\"");
								//TitanCollector.fail();
							}
						}
					}
					catch (IOException e)
					{
						throw new RuntimeException(e);
					}
					lines.add("DEL \"" + file.toString() + "\"");
				}
			}
			else
			{
				System.out.println("dump - Warning: file '" + file + "' does not exists!");
			}
		}
	}

	private static boolean registerRoot(IGraphDatabase tg, String root, IRunnable next)
	{
		final Vertex system = tg.getVertexLabel("system").getVertex().getVertices(Direction.OUT, "system").iterator().next();
		Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
		Vertex current = Edges.getHead(currentEdge);
		if (current.query().direction(Direction.OUT).labels("directory.entry").has("name", root).edges().iterator().hasNext())
		{
			System.out.println("  already added " + root);
			return next.run();
		}
		else
		{
			System.out.println("  adding " + root);
			Vertex newEntry = TitanCollector.newTask(tg, TaskType.FILESYSTEMOBJECT);
			current.addEdge("directory.entry", newEntry).setProperty("name", root);
			Edge eLastTask = current.getEdges(Direction.IN, "next.task").iterator().next();
			Vertex last = eLastTask.getVertex(Direction.OUT);
			eLastTask.remove();
			last.addEdge("next.task", newEntry);
			newEntry.addEdge("next.task", current);
			return completeDirectory(tg);
		}
	}

	private static void complete(IGraphDatabase tg)
	{
		final Vertex system = tg.getVertexLabel("system").getVertex().getVertices(Direction.OUT, "system").iterator().next();
		Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
		Vertex current = Edges.getHead(currentEdge);
		currentEdge.remove();
		system.addEdge("system.currentTask", current.getVertices(Direction.OUT, "next.task").iterator().next());
		tg.commit();
	}
}
