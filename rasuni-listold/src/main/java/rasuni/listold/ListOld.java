package rasuni.listold;

import com.thinkaurelius.titan.core.TitanVertex;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import rasuni.filesystemscanner.api.IFileSystemScanner;
import rasuni.filesystemscanner.impl.FileSystemScanner;
import rasuni.graph.impl.Edges;
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
		final FileSystemScanner tg = new FileSystemScanner("listold", (IFileSystemScanner scanner) ->
		{
			registerRoot(scanner, "C:\\", () ->
			{
				return registerRoot(scanner, "D:\\", () ->
				{
					return registerRoot(scanner, "\\\\qnap\\backup", () ->
					{
						return registerRoot(scanner, "\\\\qnap\\Public", () ->
						{
							return registerRoot(scanner, "\\\\qnap\\Qmultimedia", () ->
							{
								return registerRoot(scanner, "\\\\qnap\\Qrecordings", () ->
								{
									Check.fail();
									return false;
								});
							});
						});
					});
				});
			});
		});
		try
		{
			tg.makeLongPropertyKey("fso.lastAccess");
			process(tg);
			tg.commit();
		}
		finally
		{
			tg.getDatabase().rollback();
			tg.getDatabase().shutdown();
		}
	}

	private static void process(IFileSystemScanner tg)
	{
		final Vertex system = tg.getSystemVertex();
		l1: for (;;)
		{
			Vertex current = tg.getCurrentVertex();
			switch (TaskType.values()[tg.getCurrentTaskType()])
			{
			case ROOT:
				//tg.indent("root", () -> tg.processRoot(TreePVector.from(Arrays.asList("C:\\", "D:\\", "\\\\qnap\\backup", "\\\\qnap\\Public", "\\\\qnap\\Qmultimedia"))));
				tg.getOut().println("root");
				tg.getOut().incrementLevel();
				try
				{
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
										return registerRoot(tg, "\\\\qnap\\music", () ->
										{
											return registerRoot(tg, "\\\\MUSIKSERVER\\Kunde", () ->
											{
												return registerRoot(tg, "\\\\MUSIKSERVER\\Lights-Out", () ->
												{
													return registerRoot(tg, "\\\\MUSIKSERVER\\Musik", () ->
													{
														return completeDirectory(tg);
													});
												});
											});
										});
									});
								});
							});
						});
					}))
					{
						break l1;
					}
				}
				finally
				{
					tg.getOut().decrementLevel();
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
										current.property("fso.lastAccess", lat);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
									}
									else
									{
										// creation time is min
										current.property("fso.lastAccess", ct);
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
										current.property("fso.lastAccess", lat);
										System.out.println("  set last access to '" + LocalDate.ofEpochDay(lat) + "'");
									}
									else
									{
										// last modification time is max
										current.property("fso.lastAccess", lmt);
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
								if (!((TitanVertex) current).query().direction(Direction.OUT).labels("directory.entry").has("name", name).edges().iterator().hasNext())
								{
									System.out.println("  adding " + name);
									Vertex newEntry = TitanCollector.newTask(tg.getDatabase(), TaskType.FILESYSTEMOBJECT);
									current.addEdge("directory.entry", newEntry).property("name", name);
									Edge eLastTask = getEdges(current, Direction.IN, "next.task").iterator().next();
									Vertex last = Edges.getTail(eLastTask);
									//Vertex last = eLastTask.getVertex(Direction.OUT);
									eLastTask.remove();
									FileSystemScanner.insert(last, newEntry, current);
									/*
									last.addEdge("next.task", newEntry);
									newEntry.addEdge("next.task", current);
									 */
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
									current.property("fso.lastAccess", lmt);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(lmt) + "'");
								}
								else
								{
									// last access time is max
									current.property("fso.lastAccess", lat);
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
									current.property("fso.lastAccess", ct);
									System.out.println("  set last access to '" + LocalDate.ofEpochDay(ct) + "'");
								}
								else
								{
									// last access time is max
									current.property("fso.lastAccess", lat);
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
					final Edge ePrevious = getEdges(current, Direction.IN, "next.task").iterator().next();
					Vertex previous = Edges.getTail(ePrevious);
					ePrevious.remove();
					final Edge eNext = getEdges(current, Direction.OUT, "next.task").iterator().next();
					Vertex next = eNext.inVertex();
					eNext.remove();
					previous.addEdge("next.task", next);
					current.remove();
					system.addEdge("system.currentTask", next);
					tg.getDatabase().commit();
				}
				break;
			default:
				Check.fail();
			}
		}
	}

	private static <T> T getProperty(Vertex vertex, String name)
	{
		final Property<T> property = vertex.property(name);
		return property.isPresent() ? property.value() : null;
	}

	private static Iterable<Edge> getEdges(Vertex vertex, Direction direction, String label)
	{
		return () -> vertex.edges(direction, label);
	}

	private static boolean completeDirectory(IFileSystemScanner fs)
	{
		final Vertex system = getVertices(fs.getSystemLabelVertex(), Direction.OUT, "system").iterator().next();
		final Iterator<Edge> iterator = getEdges(system, Direction.OUT, "system.currentTask").iterator();
		Vertex current;
		if (iterator.hasNext())
		{
			Edge currentEdge = iterator.next();
			current = Edges.getHead(currentEdge);
		}
		else
		{
			current = system;
		}
		Iterator<Vertex> iEntries2 = getVertices(current, Direction.OUT, "directory.entry").iterator();
		for (;;)
		{
			if (!iEntries2.hasNext())
			{
				removeProperty(current, "fso.lastAccess");
				System.out.println("  clear last access");
				complete(fs);
				return false;
			}
			final Long lat1 = getProperty(iEntries2.next(), "fso.lastAccess");
			if (lat1 != null)
			{
				long minTime = lat1.longValue();
				while (iEntries2.hasNext())
				{
					final Long lat2 = getProperty(iEntries2.next(), "fso.lastAccess");
					if (lat2 != null)
					{
						long lat2v = lat2.longValue();
						if (lat2v < minTime)
						{
							minTime = lat2v;
						}
					}
				}
				current.property("fso.lastAccess", minTime);
				System.out.println("  set last access to '" + LocalDate.ofEpochDay(minTime) + "'");
				Long smt = getProperty(system, "fso.lastAccess");
				if (smt == null)
				{
					complete(fs);
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
						complete(fs);
						return false;
					}
					else
					{
						System.out.println();
						for (String line : lines)
						{
							System.out.println(line);
						}
						complete(fs);
						return true;
					}
				}
			}
		}
	}

	private static void removeProperty(Vertex vertex, String name)
	{
		vertex.property(name).remove();
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
			path.addFirst(getProperty(entry, "name"));
			currentEntry = Edges.getTail(entry);
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

	@SuppressWarnings("unchecked")
	private static String getProperty(Edge entry, String name)
	{
		return ((Property<String>) (Property<?>) entry.property(name)).value();
	}

	private static void dump(final Vertex parent, long minTime, LinkedList<String> lines)
	{
		Iterator<Edge> iEntries = getEdges(parent, Direction.OUT, "directory.entry").iterator();
		if (iEntries.hasNext())
		{
			for (;;)
			{
				Edge ce = iEntries.next();
				Vertex fso = Edges.getHead(ce);
				Long la = getProperty(fso, "fso.lastAccess");
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
					if (rasuni.filesystemscanner.impl.Files.containsEntries(file))
					{
						Check.fail();
					}
					else
					{
						lines.add("RD \"" + file.toString() + "\"");
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
								lines.add("ATTRIB -H \"" + file.toString() + "\"");
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

	private static boolean registerRoot(IFileSystemScanner tg, String root, BooleanSupplier next)
	{
		if (tg.hasDirectoryEntry(root))
		{
			tg.alreadyAdded(root);
			return next.getAsBoolean();
		}
		else
		{
			Vertex newEntry = tg.addNewDirectoryEntryToCurrent(root);
			Vertex current = tg.getCurrentVertex();
			final Iterator<Edge> previousTaskEdgesIterator = FileSystemScanner.getPreviousTaskEdgesIterator(current);
			FileSystemScanner.insert(previousTaskEdgesIterator.hasNext() ? Edges.getTailFromNext(previousTaskEdgesIterator) : current, newEntry, current);
			return completeDirectory(tg);
		}
	}

	private static void complete(IFileSystemScanner fs)
	{
		fs.moveToNextTask();
		/*
		final Vertex system = getVertices(tg.getSystemLabel().getVertex(), Direction.OUT, "system").iterator().next();
		Edge currentEdge = getEdges(system, Direction.OUT, "system.currentTask").iterator().next();
		Vertex current = Edges.getHead(currentEdge);
		currentEdge.remove();
		system.addEdge("system.currentTask", getVertices(current, Direction.OUT, "next.task").iterator().next());
		 */
		fs.commit();
	}

	private static Iterable<Vertex> getVertices(Vertex vertex, Direction direction, String label)
	{
		return () -> vertex.vertices(direction, label);
	}
}
