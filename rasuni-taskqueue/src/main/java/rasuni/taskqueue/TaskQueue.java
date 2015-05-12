package rasuni.taskqueue;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TaskQueue
 *
 */
public class TaskQueue implements IConnection, ITaskContext
{
	private final int _rootKind;

	private final SQLiteConnection _connection;

	private final ITaskColumn _additionalColum;

	private final static IStatementAction<Void> EXPECT_NO_DATA = statement ->
	{
		Assert.expect(!statement.step());
		return null;
	};

	/**
	 * the long type
	 */
	public static final IType<Long> LONG = new IType<Long>()
			{
		@Override
		public <T> T visit(ITypeVisitor<T> visitor)
		{
			return visitor.longType();
		}

		@Override
		public String toString()
		{
			return "long";
		}
			};

	/**
			 * next task column name
			 */
			private static final String REF_NEXTTASK = "nextTask";

	/**
			 * The next task column
			 */
			private static final Column<Long> COLUMN_SYSTEM_NEXTTASK = new Column<>(REF_NEXTTASK, LONG);

	/**
			 * name of system table
			 */
			private static final String TABLE_SYSTEM = "system";

	/**
			 * integer type
			 */
			private static final IType<Integer> INTEGER = new IType<Integer>()
					{
				@Override
				public <T> T visit(ITypeVisitor<T> visitor)
				{
					return visitor.integer();
				}

		@Override
				public String toString()
		{
					return "int";
				}
					};

	/**
					 * System version column
					 */
					private static final Column<Integer> COLUMN_SYSTEM_VERSION = new Column<>("version", INTEGER);

	/**
					 * Task
					 */
					public static final String TASK = "task";

	/**
					 * The task table
					 */
					public static final String TABLE_TASK = TASK;

	/**
					 * id field name
					 */
					public static final String ID_NAME = "id";

	/**
					 * the id column
					 */
					public static final Column<Integer> COLUMN_ID = new Column<>(ID_NAME, INTEGER);

	/**
					 * the id column
					 */
					public static final ColumnDef ID = new ColumnDef(COLUMN_ID, "PRIMARY KEY AUTOINCREMENT");

	/**
					 * The task kind name
					 */
					public static final String TASK_KIND = "kind";

	/**
					 * the task kind column
					 */
					public static final Column<Integer> COLUMN_TASK_KIND = new Column<>(TASK_KIND, INTEGER);

					private TaskQueue(SQLiteConnection connection, int rootKind, ITaskColumn additionalColumn)
					{
						_connection = connection;
						_rootKind = rootKind;
						_additionalColum = additionalColumn;
					}

					/**
					 * Start the task queue application
					 *
					 * @param name
					 *            the data base name
					 * @param application
					 *            the application
					 * @param additionalColumn
					 *            an optional additional task column
					 * @param taskIndex
					 *            an optional index for the task table
					 * @param additionalTable
					 *            an optional additional table
					 * @param rootKind
					 *            the root kind
					 * @param updaters
					 *            the schema updaters
					 *
					 */
					public static void start(String name, ITaskHandler application, final ITaskColumn additionalColumn, IIndex taskIndex, ITableDef additionalTable, int rootKind, IDatabaseApplication[] updaters)
					{
						Logger.getLogger("").setLevel(Level.WARNING);
						SQLiteConnection connection = new SQLiteConnection(new File(name + ".db"));
						try
						{
							final TaskQueue tq = new TaskQueue(connection, rootKind, additionalColumn);
							if (connection.getDatabaseFile().exists())
							{
								tq.open(false);
							}
							else
							{
								tq.open(true);
								tq.exec("PRAGMA FOREIGN_KEYS=ON");
								LinkedList<IColumnDef> columns = new LinkedList<>();
								columns.add(ID);
								columns.add(ColumnDef.notNull(COLUMN_TASK_KIND));
								if (additionalColumn != null)
								{
									columns.add(visitor -> visitor.definition(additionalColumn.getColumnName(), ColumnDef.STRING, additionalColumn.getConstraint()));
								}
								LinkedList<IIndex> indexes = new LinkedList<>();
								if (taskIndex != null)
								{
									indexes.add(taskIndex);
								}
								tq.createTable(TABLE_TASK, columns, indexes);
								tq.createTable(TABLE_SYSTEM, Arrays.asList(new Reference(REF_NEXTTASK, true, TaskQueue.TABLE_TASK), ColumnDef.notNull(COLUMN_SYSTEM_VERSION)), Arrays.asList(new IIndex[] {}));
								if (additionalTable != null)
								{
									tq.createTable(additionalTable.getName(), additionalTable.getColumns(), additionalTable.getIndexes());
								}
								tq.insert(TaskQueue.TABLE_SYSTEM, Arrays.asList(new IColumnValue[] { ColumnValue.create(COLUMN_SYSTEM_NEXTTASK, tq.addRoot()), ColumnValue.create(TaskQueue.COLUMN_SYSTEM_VERSION, 0) }));
								tq.commit();
							}
							try
							{
								int idx = tq.selectOneColumnFromSystem(TaskQueue.COLUMN_SYSTEM_VERSION);
								while (idx != updaters.length)
								{
									updaters[idx].run(tq);
									idx++;
									tq.exec("UPDATE system SET version=?", Arrays.asList((IValue) new LongValue(idx)));
									tq.commit();
								}
								for (;;)
				{
									final long taskId = tq.selectOneColumnFromSystem(TaskQueue.COLUMN_SYSTEM_NEXTTASK);
									final IValue taskid = new LongValue(taskId);
									LinkedList<Column<?>> columns = new LinkedList<>();
									columns.add(COLUMN_TASK_KIND);
									Column<String> column = null;
									if (additionalColumn != null)
					{
										column = Column.text(additionalColumn.getColumnName());
										columns.add(column);
									}
									IRow row = tq.selectOne(columns, TABLE_TASK, "id=?", Arrays.asList(taskid));
									//TaskContext taskContext = new TaskContext(tq, taskId);
									int kindo = row.get(COLUMN_TASK_KIND).intValue();
									String foreignId = column == null ? null : row.get(column);
									HandlerResult res = application.handle(tq, taskId, kindo, foreignId);
									tq.exec("UPDATE system SET nexttask=?", Arrays.asList((IValue) new LongValue(taskId + 1)));
									tq.delete(TABLE_TASK, TaskQueue.ID_NAME, new LongValue(taskId));
									if (res.isRequeue())
					{
										tq.addTask(kindo, foreignId);
									}
									//taskContext.checkIncrement();
									tq.commit();
									if (!res.isNext())
									{
										break;
									}
				}
							}
							catch (Throwable t)
							{
								tq.exec("ROLLBACK");
								t.printStackTrace();
							}
						}
						finally
						{
							connection.dispose();
						}
					}

					private void exec(String sql)
					{
						try
						{
							_connection.exec(sql);
						}
						catch (SQLiteException e)
						{
							throw new RuntimeException(e);
						}
					}

					private long getLastInsertId()
					{
						try
						{
							return _connection.getLastInsertId();
						}
						catch (SQLiteException e)
						{
							throw new RuntimeException(e);
						}
					}

					private void open(boolean allowCreate)
					{
						try
						{
							_connection.open(allowCreate);
						}
						catch (SQLiteException e)
						{
							throw new RuntimeException(e);
						}
						exec("BEGIN");
					}

					private SQLiteStatement prepare(String sql)
					{
						try
						{
							return _connection.prepare(sql);
						}
						catch (SQLiteException e)
						{
							throw new RuntimeException(e);
						}
					}

					@Override
					public void createTable(String tableName, Iterable<IColumnDef> columns, Iterable<IIndex> indexes)
					{
						final StringBuffer sql = new StringBuffer("CREATE TABLE ");
						sql.append(tableName);
						sql.append(" (");
						Iterator<IColumnDef> itr = columns.iterator();
						Assert.expect(itr.hasNext());
						final StringBuffer constraints = new StringBuffer();
						IColumnVisitor visitor = new IColumnVisitor()
						{
							@Override
							public void definition(String name, String typeName, String constraint)
							{
								sql.append(name);
								sql.append(" ");
								sql.append(typeName);
								if (constraint != null)
								{
									sql.append(" ");
									sql.append(constraint);
								}
							}

							@Override
							public void tableConstraint(String columnName, String referencedTable)
							{
								constraints.append(", FOREIGN KEY (");
								constraints.append(columnName);
								constraints.append(") REFERENCES ");
								constraints.append(referencedTable);
								constraints.append("(id)");
							}
						};
						itr.next().visit(visitor);
						while (itr.hasNext())
						{
							sql.append(", ");
							itr.next().visit(visitor);
						}
						sql.append(constraints);
						sql.append(")");
						exec(sql.toString());
						for (IIndex index : indexes)
						{
							exec(index.getCreateSql(tableName));
						}
					}

					private <T> T statement(String sql, Iterable<IValue> parameterValues, IStatementAction<T> action)
					{
						final SQLiteStatement statement = prepare(sql);
						try
						{
							bind(parameterValues, statement);
							return action.execute(new IStatement()
							{
								@Override
								public int columnInt(int column)
								{
									try
									{
										return statement.columnInt(column);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public boolean step()
								{
									try
									{
										return statement.step();
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public long columnLong(int column)
								{
									try
									{
										return statement.columnLong(column);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public boolean columnNull(int column)
								{
									try
									{
										return statement.columnNull(column);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public String columnString(int column)
								{
									try
									{
										return statement.columnString(column);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public void rebind(List<IValue> parameterValues1)
								{
									try
									{
										statement.reset();
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
									bind(parameterValues1, statement);
								}
							});
						}
						finally
						{
							statement.dispose();
						}
					}

					@Override
					public void exec(String sql, Iterable<IValue> parameterValues)
					{
						statement(sql, parameterValues, EXPECT_NO_DATA);
					}

					@Override
					public long insert(String tableName, final Iterable<IColumnValue> columnValues)
					{
						StringBuffer sql = new StringBuffer();
						sql.append("INSERT INTO ");
						sql.append(tableName);
						sql.append(" (");
						boolean needsComma = false;
						StringBuffer questionMarks = new StringBuffer();
						for (IColumnValue cv : columnValues)
						{
							if (needsComma)
							{
								sql.append(", ");
								questionMarks.append(", ");
							}
							else
							{
								needsComma = true;
							}
							sql.append(cv.getColumnName());
							questionMarks.append("?");
						}
						sql.append(") VALUES (");
						sql.append(questionMarks);
						sql.append(")");
						exec(sql.toString(), new ValueIterableAdapter(columnValues));
						return getLastInsertId();
					}

					private long addTask(int taskKind, IColumnValue additional)
					{
						LinkedList<IColumnValue> values = new LinkedList<>();
						values.add(ColumnValue.create(COLUMN_TASK_KIND, taskKind));
						if (additional != null)
						{
							values.add(additional);
						}
						return insert(TABLE_TASK, values);
					}

					@Override
					public long addRoot()
					{
						return addTask(_rootKind, _additionalColum == null ? null : new ColumnValue<>(Column.text(_additionalColum.getColumnName()), _additionalColum.getValue()));
					}

					private void commit()
					{
						exec("COMMIT");
						exec("BEGIN");
					}

					/**
					 * Generate select sql statements
					 *
					 * @param resultColumns
					 *            the columns
					 * @param joinSource
					 *            the join source
					 * @param whereExpression
					 *            the where expression
					 * @param limit
					 *            the limit
					 * @param orderColumn
					 *            the order column
					 * @return the sql
					 */
					public static String selectSql(final String resultColumns, final String joinSource, final String whereExpression, final boolean limit, final String orderColumn)
					{
						StringBuffer sql = new StringBuffer();
						sql.append("SELECT ");
						sql.append(resultColumns);
						if (joinSource != null)
						{
							sql.append(" FROM ");
							sql.append(joinSource);
						}
						if (whereExpression != null)
						{
							sql.append(" WHERE ");
							sql.append(whereExpression);
						}
						if (orderColumn != null)
						{
							sql.append(" ORDER BY ");
							sql.append(orderColumn);
							sql.append(" ASC");
						}
						if (limit)
						{
							sql.append(" LIMIT 1");
						}
						return sql.toString();
					}

					/**
					 * Select query
					 *
					 * @param resultColumns
					 *            the result columns
					 * @param joinSource
					 *            the join source
					 * @param whereExpression
					 *            the qhere expression
					 * @param parameterValues
					 *            the parameter values
					 * @param limit
					 *            the limit
					 * @param orderColumn
					 *            the order column
					 * @return the resulting rows
					 */
	@Override
					public Iterable<IRow> select(final Iterable<Column<?>> resultColumns, String joinSource, String whereExpression, Iterable<IValue> parameterValues, boolean limit, String orderColumn)
					{
						StringBuffer sql = new StringBuffer();
						boolean needsComma = false;
						final HashMap<Column<?>, Integer> idForColumn = new HashMap<>();
						int index = 0;
						for (Column<?> resultColumn : resultColumns)
						{
							if (needsComma)
							{
								sql.append(", ");
							}
							else
							{
								needsComma = true;
							}
							sql.append(resultColumn.getName());
							idForColumn.put(resultColumn, Integer.valueOf(index));
							index++;
						}
						final int count = index;
						return statement(selectSql(sql.toString(), joinSource, whereExpression, limit, orderColumn), parameterValues, statement ->
						{
							LinkedList<IRow> result = new LinkedList<>();
							while (statement.step())
							{
								Object[] slots = new Object[count];
								Row row = new Row(slots, column -> idForColumn.get(column));
								result.add(row);
								int pos = 0;
								for (Column<?> resultColumn : resultColumns)
								{
									final int column = pos;
									slots[pos] = statement.columnNull(pos) ? null : resultColumn.visitType(new ITypeVisitor<Object>()
											{
										@Override
										public Integer integer()
										{
											return statement.columnInt(column);
										}

										@Override
										public Long longType()
										{
											return statement.columnLong(column);
										}

										@Override
										public String string()
										{
											return statement.columnString(column);
										}
											});
									pos++;
								}
							}
							return result;
						});
					}

					@Override
					public IRow selectAtMostOne(Iterable<Column<?>> resultColumns, String joinSource, String whereExpression, Iterable<IValue> parameterValues, String orderColumn)
					{
						Iterator<IRow> i = select(resultColumns, joinSource, whereExpression, parameterValues, true, orderColumn).iterator();
						return i.hasNext() ? i.next() : null;
					}

					@Override
					public IRow selectOne(Iterable<Column<?>> resultColumns, String joinSource, String whereExpression, Iterable<IValue> parameterValues)
					{
						IRow row = selectAtMostOne(resultColumns, joinSource, whereExpression, parameterValues, null);
						if (row == null)
		{
							System.out.println("Warning: No row found!");
						}
						//Assert.expect(row != null);
						return row;
					}

					private <T> T selectOneColumnFromSystem(final Column<T> column)
					{
						return selectOne(Arrays.asList(new Column<?>[] { column }), TABLE_SYSTEM, null, new LinkedList<IValue>()).get(column);
					}

					private static void bind(Iterable<IValue> parameterValues, final SQLiteStatement statement)
					{
						int index = 1;
						for (IValue value : parameterValues)
						{
							final int fIndex = index;
							value.visit(new IValueVisitor()
							{
								@Override
								public void text(String value1)
								{
									try
									{
										statement.bind(fIndex, value1);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public void nullValue()
								{
									try
									{
										statement.bindNull(fIndex);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public void longValue(long value1)
								{
									try
									{
										statement.bind(fIndex, value1);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}

								@Override
								public void integer(int value1)
								{
									try
									{
										statement.bind(fIndex, value1);
									}
									catch (SQLiteException e)
									{
										throw new RuntimeException(e);
									}
								}
							});
							index++;
						}
					}

	@Override
					public long addTask(int taskKind, String foreignKey)
					{
						return addTask(taskKind, _additionalColum == null ? null : new ColumnValue<>(Column.text(_additionalColum.getColumnName()), foreignKey));
					}

					@Override
					public void delete(String table, String criteriaColumn, IValue criteriaValue)
					{
						exec("DELETE FROM " + table + " WHERE " + criteriaColumn + "=?", Arrays.asList(criteriaValue));
					}

					@Override
					public boolean exists(String table, Iterable<IColumnValue> condition)
					{
						StringBuffer whereExpression = new StringBuffer();
						boolean needsAnd = false;
						for (IColumnValue cv : condition)
						{
							if (needsAnd)
							{
								whereExpression.append(" AND ");
							}
							else
							{
								needsAnd = true;
							}
							whereExpression.append("(");
							whereExpression.append(cv.getColumnName());
							whereExpression.append("=?)");
						}
						final Column<Integer> column = new Column<>("EXISTS (" + selectSql("1", table, whereExpression.toString(), true, null) + ")", INTEGER);
						return selectOne(Arrays.asList(new Column<?>[] { column }), null, null, new ValueIterableAdapter(condition)).get(column) == 1;
					}
}
