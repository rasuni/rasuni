package rasuni.listold;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import rasuni.filesystemscanner.FileSystemScanner;
import rasuni.filesystemscanner.ICommander;
import rasuni.filesystemscanner.IDeleteProcessingContext;
import rasuni.filesystemscanner.IFileProcessingContext;
import rasuni.filesystemscanner.IFileSystemScanner;
import rasuni.filesystemscanner.IRootRegistry;
import rasuni.taskqueue.Column;
import rasuni.taskqueue.ColumnDef;
import rasuni.taskqueue.ColumnValue;
import rasuni.taskqueue.IColumnDef;
import rasuni.taskqueue.IColumnValue;
import rasuni.taskqueue.IConnection;
import rasuni.taskqueue.IDatabaseApplication;
import rasuni.taskqueue.IIndex;
import rasuni.taskqueue.Index;
import rasuni.taskqueue.Reference;
import rasuni.taskqueue.TaskQueue;

/**
 * @author Ralph Sigrist Lists old files
 */
public class ListOld implements ICommander
{
	private static final String LAST_ACCCESS_TIME = "lastAcccessTime";

	private static final String FILE = "file";

	private static final Column<Long> COLUMN_LAST_ACCESS_TIME = new Column<>(LAST_ACCCESS_TIME, TaskQueue.LONG);

	private int _scanCount = 0;

	private Requirements _requirements;

	/**
	 * The main entry point
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		FileSystemScanner.start(new ListOld(), "listold", new IDatabaseApplication[] { new IDatabaseApplication()
		{
			@Override
			public void run(IConnection context)
			{
				context.createTable(FILE, Arrays.asList(new IColumnDef[] { ColumnDef.notNull(COLUMN_LAST_ACCESS_TIME), new Reference(FileSystemScanner.TABLE_FILESYSTEMOBJECT, true, FileSystemScanner.TABLE_FILESYSTEMOBJECT) }),
						Arrays.asList((IIndex) new Index(false, Arrays.asList(LAST_ACCCESS_TIME))));
			}
		} });
	}

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
}
