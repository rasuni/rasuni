package rasuni.filesystemscanner;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import rasuni.taskqueue.Assert;
import rasuni.taskqueue.Column;
import rasuni.taskqueue.ColumnDef;
import rasuni.taskqueue.ColumnValue;
import rasuni.taskqueue.HandlerResult;
import rasuni.taskqueue.IColumnValue;
import rasuni.taskqueue.IDatabaseApplication;
import rasuni.taskqueue.IIndex;
import rasuni.taskqueue.IRow;
import rasuni.taskqueue.ITaskContext;
import rasuni.taskqueue.ITaskHandler;
import rasuni.taskqueue.IValue;
import rasuni.taskqueue.IValueVisitor;
import rasuni.taskqueue.Index;
import rasuni.taskqueue.LongValue;
import rasuni.taskqueue.Reference;
import rasuni.taskqueue.TaskQueue;

/**
 * Filesystem scanner
 * 
 */
public class FileSystemScanner implements ITaskHandler
{
	private static final String NAME = "name";
	private static final String REF_PARENT = "parent";
	private static final Column<Long> COLUMN_FILESYSTEMOBJECT_TASK = new Column<>(TaskQueue.TASK, TaskQueue.LONG);
	private static final Column<String> COLUMN_FILESYSTEMOBJECT_NAME = Column.text(NAME);
	private static final Column<Long> COLUMN_FILESYSTEMOBJECT_PARENT = new Column<>(REF_PARENT, TaskQueue.LONG);
	/**
	 */
	public static final String TABLE_FILESYSTEMOBJECT = "filesystemobject";
	private static final int TASKKIND_FILESYSTEMOBJECT = 0;
	private static final int TASKKIND_ROOT = 1;
	private ICommander _commander;

	@Override
	public HandlerResult handle(final ITaskContext connection, final long taskId, int taskKind, String foreignId)
	{
		final IValue taskid = new LongValue(taskId);
		boolean requeue;
		switch (taskKind /*row.get(TaskQueue.COLUMN_TASK_KIND).intValue()*/)
		{
		case TASKKIND_FILESYSTEMOBJECT:
			final IRow stmtfs = connection.selectOne(Arrays.asList(new Column<?>[] { COLUMN_FILESYSTEMOBJECT_NAME, COLUMN_FILESYSTEMOBJECT_PARENT, TaskQueue.COLUMN_ID }), TABLE_FILESYSTEMOBJECT, "task=?", Arrays.asList(new IValue [] {taskid}));
			Long parent = stmtfs.get(COLUMN_FILESYSTEMOBJECT_PARENT);
			Long currentParent = parent;
			IRow newStmtfs = stmtfs;
			LinkedList<String> names = new LinkedList<>();
			for (;;)
			{
				names.addFirst(newStmtfs.get(COLUMN_FILESYSTEMOBJECT_NAME));
				if (currentParent == null)
				{
					break;
				}
				newStmtfs = connection.selectOne(Arrays.asList(new Column<?>[] { COLUMN_FILESYSTEMOBJECT_NAME, COLUMN_FILESYSTEMOBJECT_PARENT }), TABLE_FILESYSTEMOBJECT, "id=?", Arrays.asList((IValue) new LongValue(currentParent)));
				currentParent = newStmtfs.get(COLUMN_FILESYSTEMOBJECT_PARENT);
			}
			Iterator<String> n = names.iterator();
			File file = new File(n.next());
			while (n.hasNext())
			{
				file = new File(file, n.next());
			}
			System.out.println("Processing: " + file);
			if (file.exists())
			{
				final long id = stmtfs.get(TaskQueue.COLUMN_ID).intValue();
				if (file.isDirectory())
				{
					String[] fnames = file.list();
					if (fnames == null)
					{
						throw new RuntimeException("access denied");
					}
					if (fnames.length == 0)
					{
						if (names.size() == 1)
						{
							System.out.println("  empty root");
						}
						else
						{
							System.out.println("  deleting");
							Assert.expect(file.delete());
						}
					}
					else
					{
						for (String name : fnames)
						{
							if (fileSystemObjectExists(Arrays.asList(new IColumnValue[] { ColumnValue.create(COLUMN_FILESYSTEMOBJECT_PARENT, id), new ColumnValue<>(COLUMN_FILESYSTEMOBJECT_NAME, name) }), connection))
							{
								System.out.println("  already added " + name);
							}
							else
							{
								System.out.println("  adding entry " + name);
								addDirectoryEntryTask(id, name, connection);
							}
						}
					}
				}
				else
				{
					_commander.visit(file, new IFileProcessingContext()
					{
						@Override
						public void createOrUpdate(String table, final ColumnValue<?> columnValue)
						{
							ColumnValue<Long> fso = new ColumnValue<>(new Column<>(TABLE_FILESYSTEMOBJECT, TaskQueue.LONG), id);
							if (connection.exists(table, Arrays.asList(new IColumnValue[] { fso })))
							{
								connection.exec("UPDATE " + table + " SET " + columnValue.getColumnName() + "=? WHERE filesystemobject=?", Arrays.asList(new IValue[] { new IValue()
								{
									@Override
									public void visit(IValueVisitor visitor)
									{
										columnValue.visitValue(visitor);
									}
								}, new LongValue(id) }));
							}
							else
							{
								connection.insert(table, Arrays.asList(new IColumnValue[] { fso, columnValue }));
							}
						}
					});
				}
				connection.exec("UPDATE filesystemobject SET task=? WHERE ID=?", Arrays.asList((IValue) new LongValue(addFileSystemObjectTask(connection)), new LongValue(id)));
			}
			else
			{
				IValue parentValue = new LongValue(parent);
				Assert.expect(fileSystemObjectExists(Arrays.asList(new IColumnValue[] { new ColumnValue<>(COLUMN_FILESYSTEMOBJECT_PARENT, parent) }), connection));
				_commander.fileDeleted(new IDeleteProcessingContext()
				{
					@Override
					public void delete(String table)
					{
						connection.delete(table, TABLE_FILESYSTEMOBJECT, new LongValue(stmtfs.get(TaskQueue.COLUMN_ID).intValue()));
					}
				});
				System.out.println("  removing");
				connection.delete(TABLE_FILESYSTEMOBJECT, TaskQueue.ID_NAME, new LongValue(stmtfs.get(TaskQueue.COLUMN_ID).intValue()));
				IRow task = connection.selectOne(Arrays.asList(new Column<?>[] { COLUMN_FILESYSTEMOBJECT_TASK }), TABLE_FILESYSTEMOBJECT, "id=?", Arrays.asList(parentValue));
				Assert.expect(task.get(COLUMN_FILESYSTEMOBJECT_TASK) != null);
			}
			requeue = false;
			break;
		case TASKKIND_ROOT:
			System.out.println("Query root entries");
			_commander.provideRootEntries(new IRootRegistry()
			{
				@Override
				public void register(Iterable<String> pathEntries)
				{
					FileSystemScanner.register(pathEntries, connection);
				}
			});
			requeue = true;
			break;
		default:
			throw new RuntimeException("unexpected");
		}
		boolean next = _commander.execute(new IFileSystemScanner()
		{
			@Override
			public <T> T getMin(Column<T> column, String table)
			{
				return FileSystemScanner.getMin(column, table, connection);
			}

			@Override
			public Iterable<File> select(String table, IColumnValue columnValue)
			{
				return FileSystemScanner.select(table, columnValue, connection);
			}

			@Override
			public void delete(String table, IColumnValue criteria)
			{
				FileSystemScanner.delete(table, criteria, connection);
			}
		});
		//connection.commit();
		return new HandlerResult (next, requeue);
	}

	private static boolean fileSystemObjectExists(Iterable<IColumnValue> condition, ITaskContext _db)
	{
		return _db.exists(TABLE_FILESYSTEMOBJECT, condition);
	}

	private static long addFileSystemObjectTask(Long parent, String name, ITaskContext _db)
	{
		long taskId = addFileSystemObjectTask(_db);
		addFileSystemObject(parent, name, taskId, _db);
		return taskId;
	}

	private static long addDirectoryEntryTask(long parent, String name, ITaskContext _db)
	{
		return addFileSystemObjectTask(parent, name, _db);
	}

	private static long addFileSystemObjectTask(ITaskContext _db)
	{
		return _db.addTask(TASKKIND_FILESYSTEMOBJECT, null); //_db.insert(TaskQueue.TABLE_TASK, Arrays.asList(new IColumnValue[] { ColumnValue.create(TaskQueue.COLUMN_TASK_KIND, TASKKIND_FILESYSTEMOBJECT) }));
	}

	private static long addFileSystemObject(Long parent, String name, Long task, ITaskContext _db)
	{
		return _db
				.insert(TABLE_FILESYSTEMOBJECT, Arrays.asList(new IColumnValue[] { new ColumnValue<>(COLUMN_FILESYSTEMOBJECT_PARENT, parent), new ColumnValue<>(COLUMN_FILESYSTEMOBJECT_NAME, name), new ColumnValue<>(COLUMN_FILESYSTEMOBJECT_TASK, task) }));
	}

	private FileSystemScanner(ICommander commander)
	{
		_commander = commander;
		// _updaters = updaters;
	}

	/**
	 * @param processor
	 *            the file system scanner processor
	 * @param name
	 *            the database file name
	 * @param updaters
	 *            the updaters to apply depending on the current version
	 * 
	 */
	public static void start(ICommander processor, String name, IDatabaseApplication[] updaters)
	{
		TaskQueue.start(
				name,
				new FileSystemScanner(processor),
				null,
				null,
				new TableDef(TABLE_FILESYSTEMOBJECT, Arrays.asList(TaskQueue.ID, new Reference(REF_PARENT, false, TABLE_FILESYSTEMOBJECT), ColumnDef.notNull(COLUMN_FILESYSTEMOBJECT_NAME), new Reference(TaskQueue.TASK, false, TaskQueue.TABLE_TASK)), Arrays
						.asList(new IIndex[] { new Index(true, Arrays.asList(REF_PARENT, NAME)), new Index(true, Arrays.asList(TaskQueue.TASK)) })), TASKKIND_ROOT, updaters);
		/*
		 * FileSystemScanner db = new FileSystemScanner(processor, new
		 * SQLiteConnection(new File(name + ".db"))); try { db.run(updaters); }
		 * finally { db.close(); }
		 */
	}

	// @Override
	private static void register(Iterable<String> pathEntries, ITaskContext _db)
	{
		final Iterator<String> i = pathEntries.iterator();
		Assert.expect(i.hasNext());
		final Long parent = null; // Null.INSTANCE;
		final String name = i.next();
		if (_db.selectAtMostOne(Arrays.asList(new Column<?>[] { TaskQueue.COLUMN_ID }), TABLE_FILESYSTEMOBJECT, "name=? AND parent=?", Arrays.asList(new Text(name), Null.INSTANCE), null) != null)
		{
			throw new RuntimeException("not implemented!");
		}
		Long parent1 = parent;
		String name1 = name;
		while (i.hasNext())
		{
			parent1 = addFileSystemObject(parent1, name1, null, _db);
			name1 = i.next();
		}
		addFileSystemObjectTask(parent1, name1, _db);
	}

	@SuppressWarnings("null")
	private static <T> T getMin(Column<T> column, String table, ITaskContext _db)
	{
		IRow row = _db.selectAtMostOne(Arrays.asList(new Column<?>[] { column }), table, null, Arrays.asList(new IValue[] {}), column.getName());
		return row == null ? null : row.get(column);
	}

	private static Iterable<File> select(String table, final IColumnValue columnValue, final ITaskContext _db)
	{
		final Column<Long> fso = new Column<>(TABLE_FILESYSTEMOBJECT, TaskQueue.LONG);
		final Iterable<IRow> rows = _db.select(Arrays.asList(new Column<?>[] { fso }), table, columnValue.getColumnName() + "=?", Arrays.asList(new IValue[] { new IValue()
		{
			@Override
			public void visit(IValueVisitor visitor)
			{
				columnValue.visitValue(visitor);
			}
		} }), false, null);
		return new Iterable<File>()
		{
			@Override
			public Iterator<File> iterator()
			{
				final Iterator<IRow> rowi = rows.iterator();
				return new Iterator<File>()
				{
					@Override
					public boolean hasNext()
					{
						return rowi.hasNext();
					}

					@Override
					public File next()
					{
						LinkedList<String> names = new LinkedList<>();
						Long fsoid = rowi.next().get(fso);
						while (fsoid != null)
						{
							IRow newStmtfs = _db.selectOne(Arrays.asList(new Column<?>[] { COLUMN_FILESYSTEMOBJECT_NAME, COLUMN_FILESYSTEMOBJECT_PARENT, TaskQueue.COLUMN_ID }), TABLE_FILESYSTEMOBJECT, "id=?",
									Arrays.asList(new IValue[] { new LongValue(fsoid) }));
							if (newStmtfs == null) {
								return null;
							}
							fsoid = newStmtfs.get(COLUMN_FILESYSTEMOBJECT_PARENT);
							names.addFirst(newStmtfs.get(COLUMN_FILESYSTEMOBJECT_NAME));
						}
						Iterator<String> n = names.iterator();
						File file = new File(n.next());
						while (n.hasNext())
						{
							file = new File(file, n.next());
						}
						return file;
					}

					@Override
					public void remove()
					{
						throw new RuntimeException("not implemented!");
					}
				};
			}
		};
	}

	private static void delete(String table, final IColumnValue criteria, ITaskContext _db)
	{
		_db.delete(table, criteria.getColumnName(), new IValue()
		{
			@Override
			public void visit(IValueVisitor visitor)
			{
				criteria.visitValue(visitor);
			}
		});
	}
}
