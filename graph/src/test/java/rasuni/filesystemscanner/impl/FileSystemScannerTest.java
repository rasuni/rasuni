package rasuni.filesystemscanner.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;
import org.pcollections.ConsPStack;
import rasuni.filesystemscanner.api.IFileSystemScanner;
import rasuni.functional.IConsumer;
import rasuni.graph.api.IGraphDatabase;
import rasuni.mock.MockTestCase;

public class FileSystemScannerTest extends MockTestCase
{
	private final Vertex _vertex1 = createStrictMock(Vertex.class);

	private final Runnable _runnable = createStrictMock(Runnable.class);

	@Rule
	public final TemporaryFolder _folder = new TemporaryFolder(new File("").getAbsoluteFile());

	@Rule
	public final SystemOutRule _systemOutRule = new SystemOutRule();

	public StringBuffer _out = new StringBuffer();

	interface IOutputSteam
	{
		void write(int b);
	}

	@SuppressWarnings("unchecked")
	private final IConsumer<IFileSystemScanner> _root = createStrictMock(IConsumer.class);

	@SuppressWarnings("unchecked")
	private final Iterator<Edge> _edgesIterator1 = createStrictMock(Iterator.class);

	private final Vertex _vertex2 = createStrictMock(Vertex.class);

	@SuppressWarnings("unchecked")
	private final Iterator<Edge> _edgesIterator2 = createStrictMock(Iterator.class);

	@SuppressWarnings("unchecked")
	private final Iterator<Edge> _edgesIterator3 = createStrictMock(Iterator.class);

	private final Vertex _vertex3 = createStrictMock(Vertex.class);

	@SuppressWarnings("unchecked")
	private final Iterator<Edge> _edgesIterator4 = createStrictMock(Iterator.class);

	private final Edge _edge = createStrictMock(Edge.class);

	@SuppressWarnings("static-method")
	@Test
	public void create()
	{
		Assert.assertNotNull(new FileSystemScanner(null, (IConsumer<IFileSystemScanner>) null));
	}

	@Test
	public void makeIntPropertyKey()
	{
		withScanner(scanner ->
		{
			scanner.makeIntPropertyKey("\000");
		});
	}

	@Test
	public void makeAssocManyToOne()
	{
		withScanner(scanner ->
		{
			scanner.makeAssocManyToOne("\000");
		});
	}

	@Test
	public void makeLongPropertyKey()
	{
		withScanner(scanner ->
		{
			scanner.makeLongPropertyKey("\000");
		});
	}

	@Test
	public void makeStringPropertyKey()
	{
		withScanner(scanner ->
		{
			scanner.makeStringPropertyKey("\000");
		});
	}

	@Test
	public void getSystem()
	{
		withScanner(scanner ->
		{
			Assert.assertNotNull(scanner.getSystem());
		});
	}

	@Test
	public void commit()
	{
		withScanner(scanner ->
		{
			scanner.commit();
		});
	}

	@Test
	public void getCurrentTask()
	{
		withScanner(scanner ->
		{
			Assert.assertNotNull(scanner.getCurrentTask());
		});
	}

	@Test
	public void getCurrentTaskType()
	{
		withScanner(scanner ->
		{
			Assert.assertEquals(IFileSystemScanner.TASK_TYPE_ROOT, scanner.getCurrentTaskType());
		});
	}

	@Test
	public void getCurrentTaskTypeFileSystem()
	{
		withScanner(scanner ->
		{
			scanner.enqueueNewTask(IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT);
			scanner.moveToNextTask();
			Assert.assertEquals(IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT, scanner.getCurrentTaskType());
		});
	}

	@Test
	public void getOut()
	{
		withScanner(scanner -> Assert.assertNotNull(scanner.getOut()));
	}

	@Test
	public void hasDirectoryEntry()
	{
		withScanner(scanner ->
		{
			Assert.assertFalse(scanner.hasDirectoryEntry(null));
		});
	}

	@Test
	public void getCurrentVertex()
	{
		withScanner(scanner ->
		{
			Assert.assertNotNull(scanner.getCurrentVertex());
		});
	}

	@Test
	public void enqueueNewTask()
	{
		withScanner((scanner) ->
		{
			Assert.assertNotNull(scanner.enqueueNewTask(0));
		});
	}

	@Test
	public void alreadyAdded()
	{
		println("already added null");
		withScanner(scanner ->
		{
			scanner.alreadyAdded((String) null);
		});
	}

	@Test
	public void includeDirectoryEntry()
	{
		withScanner((scanner) ->
		{
			Assert.assertTrue(scanner.includeDirectoryEntry(""));
			Assert.assertFalse(scanner.includeDirectoryEntry(""));
		});
	}

	@Test
	public void includeDirectoryEntries()
	{
		println("adding ");
		withScanner((scanner) ->
		{
			scanner.includeDirectoryEntries(Arrays.asList(""));
		});
	}

	@Test
	public void includeDirectoryEntriesMultiple()
	{
		println("adding ");
		println("already added ");
		withScanner((scanner) ->
		{
			scanner.includeDirectoryEntries(Arrays.asList(""));
			scanner.includeDirectoryEntries(Arrays.asList(""));
		});
	}

	@Test
	public void getNextTask()
	{
		withScanner(scanner ->
		{
			Assert.assertNull(scanner.getNextTask());
		});
	}

	@Test
	public void processRoot()
	{
		withScanner(scanner ->
		{
			scanner.processRoot(ConsPStack.empty());
		});
	}

	@Test
	public void createRoot()
	{
		println("root");
		Capture<IFileSystemScanner> sc = Capture.newInstance();
		_root.accept(EasyMock.capture(sc));
		withScanner(scanner ->
		{
			scanner.processTask();
			Assert.assertSame(scanner, sc.getValue());
		});
	}

	private void enableLog()
	{
		_systemOutRule.mute();
		_systemOutRule.enableLog();
	}

	private void withScanner(Consumer<FileSystemScanner> consumer)
	{
		enableLog();
		final FileSystemScanner fileSystemScanner = new FileSystemScanner(_folder.getRoot().getName(), _root);
		try
		{
			replay(() -> consumer.accept(fileSystemScanner));
			Assert.assertEquals(_out.toString(), _systemOutRule.getLog());
		}
		finally
		{
			fileSystemScanner.getDatabase().shutdown();
		}
	}

	@Test
	public void addNewDirectoryEntryToCurrent()
	{
		println("adding ");
		withScanner(fileSystemScanner -> Assert.assertNotNull(fileSystemScanner.addNewDirectoryEntryToCurrent("")));
	}

	@Test
	public void getDirectoryEntry()
	{
		println("adding ");
		withScanner(fileSystemScanner -> Assert.assertNotNull(fileSystemScanner.getDirectoryEntry(fileSystemScanner.addNewDirectoryEntryToCurrent("").id())));
	}

	@Test
	public void getCurrentPathEmpty()
	{
		withScanner(fileSystemScanner -> Assert.assertEquals(ConsPStack.empty(), fileSystemScanner.getCurrentPath()));
	}

	@Test
	public void getCurrentPath()
	{
		println("adding ");
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent("");
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertEquals(ConsPStack.singleton(""), fileSystemScanner.getCurrentPath());
		});
	}

	@Test
	public void getCurrentFile()
	{
		println("adding ");
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent("");
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertEquals(new File(""), fileSystemScanner.getCurrentFile());
		});
	}

	@Test
	public void getCurrentFilePath()
	{
		println("adding ");
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent("");
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertEquals("", fileSystemScanner.getCurrentFilePath());
		});
	}

	@Test
	public void currentFile()
	{
		_runnable.run();
		println("adding ");
		println("");
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent("");
			fileSystemScanner.setCurrentTask(newEntry);
			fileSystemScanner.currentFile(_runnable);
		});
	}

	@Test
	public void deleteFile() throws IOException
	{
		File temp = File.createTempFile("file", null, _folder.getRoot());
		final String absolutePath = temp.getAbsolutePath();
		println("adding " + absolutePath);
		println("deleting " + absolutePath);
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(absolutePath);
			fileSystemScanner.setCurrentTask(newEntry);
			fileSystemScanner.deleteFile();
		});
		Assert.assertFalse(temp.exists());
	}

	@Test
	public void includeDirectoryEntriesForCurrent()
	{
		enableLog();
		final File root = _folder.getRoot();
		File dir = new File(root, "dir");
		dir.mkdir();
		final String absolutePath = dir.getAbsolutePath();
		println("adding " + absolutePath);
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(absolutePath);
			fileSystemScanner.setCurrentTask(newEntry);
			fileSystemScanner.includeDirectoryEntries();
		});
	}

	@Test
	public void getByPrimaryKey()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeStringKey("\000");
			Assert.assertNull(fileSystemScanner.getByPrimaryKey("\000", ""));
		});
	}

	@Test
	public void getPreviousTaskEdgesIterator()
	{
		expectAndReturn(_vertex1.edges(Direction.IN, new String[] { "next.task" }), null);
		replay(() -> Assert.assertNull(FileSystemScanner.getPreviousTaskEdgesIterator(_vertex1)));
	}

	@Test
	public void includeTask_exists()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			Assert.assertFalse(fileSystemScanner.includeTask("\000", 0, IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT));
		});
	}

	@Test
	public void includeTask_notExists()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			Assert.assertTrue(fileSystemScanner.includeTask("\000", 0, IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT));
		});
	}

	@Test
	public void addingSequence()
	{
		println("adding");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.adding((Sequence<String>) null);
		});
	}

	private void println(String string)
	{
		_out.append(string);
		_out.append(System.lineSeparator());
	}

	@Test
	public void setNextTask()
	{
		expectAndReturn(_vertex1.edges(Direction.OUT, "next.task"), _edgesIterator1);
		expectAndReturn(_edgesIterator1.hasNext(), false);
		expectAndReturn(_vertex2.edges(Direction.IN, "next.task"), _edgesIterator2);
		expectAndReturn(_edgesIterator2.hasNext(), false);
		expectAndReturn(_vertex1.addEdge("next.task", _vertex2), null);
		replay(() -> FileSystemScanner.setNextTask(_vertex1, _vertex2));
	}

	@Test
	public void alreadyAddedSequence()
	{
		println("already added");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.alreadyAdded((Sequence<String>) null);
		});
	}

	@Test
	public void include_adding()
	{
		println("adding");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			Assert.assertTrue(fileSystemScanner.include("\000", 0, IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT, null));
		});
	}

	@Test
	public void include_alreadyAdded()
	{
		println("already added");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			Assert.assertFalse(fileSystemScanner.include("\000", 0, IFileSystemScanner.TASK_TYPE_FILESYSTEMOBJECT, null));
		});
	}

	@SuppressWarnings("static-method")
	@Test
	public void create2()
	{
		Assert.assertNotNull(FileSystemScanner.create(null, null, null));
	}

	@Test
	public void processRoot2()
	{
		enableLog();
		final IFileSystemScanner fileSystemScanner = FileSystemScanner.create(_folder.getRoot().getName(), ConsPStack.empty(), null);
		try
		{
			println("root");
			Assert.assertFalse(fileSystemScanner.processTask());
			Assert.assertEquals(_out.toString(), _systemOutRule.getLog());
		}
		finally
		{
			fileSystemScanner.getDatabase().shutdown();
		}
	}

	@Test
	public void setPropertyByPrimaryKey()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			fileSystemScanner.setPropertyByPrimaryKey("\000", 0, "\000", 0);
		});
	}

	@Test
	public void putAlreadyAdded()
	{
		println("already added");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			fileSystemScanner.put("\000", 0, 0, "\000", 0, (Sequence<String>) null);
		});
	}

	@Test
	public void putAdding()
	{
		println("adding");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			fileSystemScanner.put("\000", 0, 0, "\000", 0, (Sequence<String>) null);
		});
	}

	@Test
	public void putStringAlreadyAdded()
	{
		println("already added null");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			fileSystemScanner.put("\000", 0, 0, "\000", 0, (String) null);
		});
	}

	@Test
	public void putStringAdding()
	{
		println("adding null");
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			fileSystemScanner.put("\000", 0, 0, "\000", 0, (String) null);
		});
	}

	@Test
	public void removeExistintNextTask()
	{
		expectAndReturn(_vertex1.edges(Direction.OUT, "next.task"), _edgesIterator1);
		expectAndReturn(_edgesIterator1.hasNext(), true);
		expectAndReturn(_edgesIterator1.next(), _edge);
		_edge.remove();
		expectAndReturn(_vertex2.edges(Direction.IN, "next.task"), _edgesIterator2);
		expectAndReturn(_edgesIterator2.hasNext(), false);
		expectAndReturn(_vertex1.addEdge("next.task", _vertex2), null);
		replay(() -> FileSystemScanner.setNextTask(_vertex1, _vertex2));
	}

	@Test
	public void addEdgeToCurrentPK()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			fileSystemScanner.addEdgeToCurrent("next.task", "\000", 0);
		});
	}

	@Test
	public void getByKeyAndPath()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			Assert.assertNull(fileSystemScanner.getByKeyAndPath("\000", 0, "next.task"));
		});
	}

	@Test
	public void getByKeyAndPathPropertyNotFound()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			database.setProperty(database.addVertex(), "\000", 0);
			Assert.assertNull(fileSystemScanner.getByKeyAndPath("\000", 0, "next.task", "\000"));
		});
	}

	@Test
	public void getByKeyAndPathPropertyFound()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.makeIntKey("\000");
			final IGraphDatabase database = fileSystemScanner.getDatabase();
			Object vertex = database.addVertex();
			database.addEdge(vertex, "next.task", vertex);
			database.setProperty(vertex, "\000", 0);
			Assert.assertEquals(0, ((Integer) fileSystemScanner.getByKeyAndPath("\000", 0, "next.task", "\000")).intValue());
		});
	}

	@Test
	public void insert()
	{
		expectAndReturn(_vertex1.edges(Direction.OUT, "next.task"), _edgesIterator1);
		expectAndReturn(_edgesIterator1.hasNext(), false);
		expectAndReturn(_vertex2.edges(Direction.IN, "next.task"), _edgesIterator2);
		expectAndReturn(_edgesIterator2.hasNext(), false);
		expectAndReturn(_vertex1.addEdge("next.task", _vertex2), null);
		expectAndReturn(_vertex2.edges(Direction.OUT, "next.task"), _edgesIterator3);
		expectAndReturn(_edgesIterator3.hasNext(), false);
		expectAndReturn(_vertex3.edges(Direction.IN, "next.task"), _edgesIterator4);
		expectAndReturn(_edgesIterator4.hasNext(), false);
		expectAndReturn(_vertex2.addEdge("next.task", _vertex3), null);
		replay(() -> FileSystemScanner.insert(_vertex1, _vertex2, _vertex3));
	}

	@Test
	public void isDirectory() throws IOException
	{
		File temp = File.createTempFile("file", null, _folder.getRoot());
		final String absolutePath = temp.getAbsolutePath();
		println("adding " + absolutePath);
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(absolutePath);
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertFalse(fileSystemScanner.isDirectory());
		});
	}

	@Test
	public void getFileName() throws IOException
	{
		File temp = File.createTempFile("file", null, _folder.getRoot());
		final String absolutePath = temp.getAbsolutePath();
		println("adding " + absolutePath);
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(absolutePath);
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertEquals(absolutePath, fileSystemScanner.getFileName());
		});
	}

	@Test
	public void processDirectoryAddEntry() throws IOException
	{
		final File directory = _folder.newFolder("test");
		File temp = File.createTempFile("file", null, directory);
		println("adding " + directory.getAbsolutePath());
		println("adding " + temp.getName());
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(directory.getAbsolutePath());
			fileSystemScanner.setCurrentTask(newEntry);
			fileSystemScanner.processDirectory();
		});
	}

	@Test
	public void processDirectoryRemove() throws IOException
	{
		final File directory = _folder.newFolder("test");
		final String path = directory.getAbsolutePath();
		println("adding " + path);
		println("deleting " + path);
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.setCurrentTask(fileSystemScanner.addNewDirectoryEntryToCurrent(path));
			fileSystemScanner.processDirectory();
		});
	}

	@Test
	public void getFileExtension() throws IOException
	{
		File temp = File.createTempFile("file", ".temp", _folder.getRoot());
		final String absolutePath = temp.getAbsolutePath();
		println("adding " + absolutePath);
		withScanner(fileSystemScanner ->
		{
			Vertex newEntry = fileSystemScanner.addNewDirectoryEntryToCurrent(absolutePath);
			fileSystemScanner.setCurrentTask(newEntry);
			Assert.assertEquals("temp", fileSystemScanner.getFileExtension());
		});
	}

	@Test
	public void clearCurrent()
	{
		withScanner(scanner ->
		{
			scanner.clearCurrent();
		});
	}

	@Test
	public void getSystemLabelVertex()
	{
		withScanner(fileSystemScanner ->
		{
			Assert.assertNotNull(fileSystemScanner.getSystemLabelVertex());
		});
	}

	@Test
	public void registerTaskType()
	{
		withScanner(fileSystemScanner ->
		{
			fileSystemScanner.registerTaskType(128, null);
			fileSystemScanner.registerTaskType(128, null);
			//fileSystemScanner.registerTaskType(16, null);
		});
	}
}
