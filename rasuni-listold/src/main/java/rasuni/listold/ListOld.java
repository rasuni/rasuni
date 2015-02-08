package rasuni.listold;

import com.sleepycat.je.LockMode;
import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.VertexLabel;
import com.thinkaurelius.titan.core.attribute.Duration;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager.IsolationLevel;
import com.thinkaurelius.titan.diskstorage.configuration.ReadConfiguration;
import com.thinkaurelius.titan.diskstorage.idmanagement.ConflictAvoidanceMode;
import com.thinkaurelius.titan.diskstorage.util.time.StandardDuration;
import com.thinkaurelius.titan.diskstorage.util.time.Timestamps;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
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
import rasuni.check.Assert;
import rasuni.functional.IConsumer2;
import rasuni.titan.Edges;
import rasuni.titan.TaskType;
import rasuni.titan.TitanCollector;

/**
 * Lists old files
 */
public final class ListOld
{
	/**
	 * The main entry point
	 *
	 * @param args
	 *            the arguments
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
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
					TitanCollector.configureLogOptions(Loader.java1, Loader.ignoreTCL, resource, runnable);
				};
				conf.accept(LogManager.DEFAULT_XML_CONFIGURATION_FILE, () ->
				{
					conf.accept(LogManager.DEFAULT_CONFIGURATION_FILE, () ->
					{
						TitanCollector.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Could not find resource: [", configurationOptionStr, "].");
					});
				});
			}
			else
			{
				try
				{
					final URL url = new URL(configurationOptionStr);
					TitanCollector.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Using URL [", url, "] for automatic log4j configuration.");
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
					TitanCollector.configureLogOptions(Loader.java1, Loader.ignoreTCL, configurationOptionStr, () ->
					{
						TitanCollector.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Could not find resource: [", configurationOptionStr, "].");
					});
				}
			}
		}
		else
		{
			TitanCollector.debug(LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "Default initialization of overridden by ", LogManager.DEFAULT_INIT_OVERRIDE_KEY, "property.");
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
					return TitanCollector.provideStringSetting(datatype, "berkeleyje");
				case "storage.read-only":
				case "storage.batch-loading":
				case "query.ignore-unknown-index-key":
				case "metrics.enabled":
				case "metrics.jmx.enabled":
				case "cache.db-cache":
				case "cluster.partition":
					return TitanCollector.provideBooleanSetting(datatype, Boolean.FALSE);
				case "storage.transactions":
				case "graph.allow-stale-config":
				case "ids.flush":
				case "query.force-index":
				case "query.smart-limit":
				case "storage.parallel-backend-ops":
					return TitanCollector.provideBooleanSetting(datatype, Boolean.TRUE);
				case "storage.directory":
					return TitanCollector.provideStringSetting(datatype, "listold");
				case "storage.berkeleydb.cache-percentage":
					return TitanCollector.provideIntegerSetting(datatype, 65);
				case "graph.timestamps":
					return TitanCollector.provideSetting(datatype, Timestamps.class, Timestamps.MICRO);
				case "storage.setup-wait":
					return TitanCollector.provideDurationSetting(datatype, new StandardDuration(60L, TimeUnit.SECONDS));
				case "storage.lock.local-mediator-group":
				case "graph.unique-instance-id":
				case "metrics.csv.directory":
				case "metrics.ganglia.hostname":
				case "metrics.graphite.hostname":
					return TitanCollector.provideStringSetting(datatype, null);
				case "storage.berkeleydb.isolation-level":
					return TitanCollector.provideSetting(datatype, IsolationLevel.class, IsolationLevel.REPEATABLE_READ);
				case "storage.berkeleydb.lock-mode":
					return TitanCollector.provideSetting(datatype, LockMode.class, LockMode.DEFAULT);
				case "graph.unique-instance-id-suffix":
					return TitanCollector.provideSetting(datatype, Short.class, null);
				case "schema.default":
					return TitanCollector.provideStringSetting(datatype, "none");
				case "log.tx.send-delay":
				case "log.titan.send-delay":
				case "metrics.console.interval":
				case "metrics.slf4j.interval":
					return TitanCollector.provideDurationSetting(datatype, null);
				case "log.titan.key-consistent":
				case "query.fast-property":
				case "log.tx.key-consistent":
					return TitanCollector.provideBooleanSetting(datatype, null);
				case "cache.tx-cache-size":
					return TitanCollector.provideIntegerSetting(datatype, 20000);
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
			TitanManagement tm = tg.getManagementSystem();
			try
			{
				if (!tm.containsVertexLabel("system"))
				{
					tm.makeVertexLabel("system").make();
					tm.makeEdgeLabel("system").multiplicity(Multiplicity.ONE2ONE).make();
					tm.makeEdgeLabel("system.currentTask").multiplicity(Multiplicity.ONE2ONE).make();
					tm.makePropertyKey("task.type").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
					PropertyKey name = tm.makePropertyKey("name").dataType(String.class).cardinality(Cardinality.SINGLE).make();
					EdgeLabel directoryEntry = tm.makeEdgeLabel("directory.entry").multiplicity(Multiplicity.ONE2MANY).make();
					tm.buildEdgeIndex(directoryEntry, "byName", Direction.OUT, Order.DEFAULT, name);
					tm.makeEdgeLabel("next.task").multiplicity(Multiplicity.ONE2ONE).make();
					tm.makePropertyKey("fso.lastAccess").dataType(Long.class).cardinality(Cardinality.SINGLE).make();
				}
			}
			finally
			{
				tm.commit();
			}
			final VertexLabel vertexLabel = tg.getVertexLabel("system");
			if (vertexLabel.getVertices(Direction.OUT, "system").iterator().hasNext())
			{
				process(tg);
			}
			else
			{
				final TitanVertex system = tg.addVertex();
				vertexLabel.addEdge("system", system);
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
				registry.register(Arrays.asList("\\\\MUSIKSERVER\\Lighs-Out"));
				registry.register(Arrays.asList("\\\\MUSIKSERVER\\Musik"));
			}

			@Override
			public void fileDeleted(IDeleteProcessingContext context)
			{
				context.delete(FILE);
			}
	 */
	private static void process(TitanGraph tg)
	{
		final Vertex system = tg.getVertexLabel("system").getVertices(Direction.OUT, "system").iterator().next();
		l1: for (;;)
		{
			Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
			Vertex current = Edges.getHead(currentEdge);
			switch (TaskType.values()[(Integer) current.getProperty("task.type")])
			{
			case ROOT:
				System.out.println("root");
				registerRoot(tg, "C:\\", () ->
				{
					registerRoot(tg, "D:\\", () ->
					{
						registerRoot(tg, "\\\\qnap\\backup", () ->
						{
							registerRoot(tg, "\\\\qnap\\Network Recycle Bin 1", () ->
							{
								TitanCollector.fail();
							});
						});
					});
				});
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
							TitanCollector.fail();
						}
						else
						{
							int iEntries = 0;
							for (;;)
							{
								final String name = entries[iEntries];
								if (current.query().direction(Direction.OUT).labels("directory.entry").has("name", name).edges().iterator().hasNext())
								{
									System.out.println("  already added " + name);
									iEntries++;
								}
								else
								{
									System.out.println("  adding " + name);
									Vertex newEntry = TitanCollector.newTask(tg, TaskType.FILESYSTEMOBJECT);
									current.addEdge("directory.entry", newEntry).setProperty("name", name);
									Edge eLastTask = current.getEdges(Direction.IN, "next.task").iterator().next();
									Vertex last = eLastTask.getVertex(Direction.OUT);
									eLastTask.remove();
									last.addEdge("next.task", newEntry);
									newEntry.addEdge("next.task", current);
									currentEdge.remove();
									system.addEdge("system.currentTask", current.getVertices(Direction.OUT, "next.task").iterator().next());
									tg.commit();
									break;
									// TitanCollector.fail();
								}
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
								TitanCollector.fail();
							}
							else
							{
								long lat = attrs.lastAccessTime().to(TimeUnit.DAYS);
								if (lat < lmt)
								{
									TitanCollector.fail();
								}
								else
								{
									Vertex currentPEntry = current;
									long minTime = lat;
									for (;;)
									{
										Long currentLat = currentPEntry.getProperty("fso.lastAccess");
										if (currentLat == null)
										{
											currentPEntry.setProperty("fso.lastAccess", minTime);
											Iterator<Vertex> iParent = currentPEntry.getVertices(Direction.IN, "directory.entry").iterator();
											if (iParent.hasNext())
											{
												currentPEntry = iParent.next();
												Iterator<Vertex> iEntries = currentPEntry.getVertices(Direction.OUT, "directory.entry").iterator();
												if (iEntries.hasNext())
												{
													final Long lat1 = iEntries.next().getProperty("fso.lastAccess");
													if (lat1 == null)
													{
														TitanCollector.fail();
													}
													else
													{
														minTime = lat1.longValue();
														for (;;)
														{
															if (!iEntries.hasNext())
															{
																break;
															}
															final Long lat2 = iEntries.next().getProperty("fso.lastAccess");
															if (lat2 != null)
															{
																TitanCollector.fail();
															}
														}
													}
												}
												else
												{
													TitanCollector.fail();
												}
											}
											else
											{
												LinkedList<String> lines = new LinkedList<>();
												dump(system, minTime, lines);
												Iterator<String> iline = lines.iterator();
												if (iline.hasNext())
												{
													System.out.println();
													for (;;)
													{
														System.out.println(iline.next());
														if (!iline.hasNext())
														{
															break;
														}
													}
													currentEdge.remove();
													system.addEdge("system.currentTask", current.getVertices(Direction.OUT, "next.task").iterator().next());
													tg.commit();
													break l1;
												}
												else
												{
													TitanCollector.fail();
												}
											}
										}
										else
										{
											TitanCollector.fail();
										}
									}
								}
							}
						}
						catch (IOException e)
						{
							throw new RuntimeException(e);
						}
					}
				}
				else
				{
					TitanCollector.fail();
				}
				break;
			default:
				TitanCollector.fail();
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

	/**
	 * @param system
	 * @param minTime
	 */
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
						TitanCollector.fail();
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
					TitanCollector.fail();
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
								TitanCollector.fail();
							}
						}
						else
						{
							TitanCollector.fail();
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
				TitanCollector.fail();
			}
		}
	}

	private static void registerRoot(TitanGraph tg, String root, Runnable next)
	{
		final Vertex system = tg.getVertexLabel("system").getVertices(Direction.OUT, "system").iterator().next();
		Edge currentEdge = system.getEdges(Direction.OUT, "system.currentTask").iterator().next();
		Vertex current = Edges.getHead(currentEdge);
		if (current.query().direction(Direction.OUT).labels("directory.entry").has("name", root).edges().iterator().hasNext())
		{
			System.out.println("  already added " + root);
			next.run();
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
			currentEdge.remove();
			system.addEdge("system.currentTask", current.getVertices(Direction.OUT, "next.task").iterator().next());
			tg.commit();
		}
	}
}
