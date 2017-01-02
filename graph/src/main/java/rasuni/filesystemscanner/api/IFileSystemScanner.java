package rasuni.filesystemscanner.api;

import java.io.File;
import java.util.function.Predicate;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.pcollections.PSequence;
import rasuni.filesystemscanner.impl.IIndentPrinter;
import rasuni.filesystemscanner.impl.Sequence;
import rasuni.graph.api.IGraphDatabase;
import rasuni.graph.api.IVertex;

public interface IFileSystemScanner
{
	int TASK_TYPE_FILESYSTEMOBJECT = 0;

	int TASK_TYPE_ROOT = 3;

	IGraphDatabase getDatabase();

	void makeIntPropertyKey(String name);

	void makeAssocManyToOne(String name);

	void makeLongPropertyKey(String name);

	void makeStringKey(String name);

	void makeIntKey(String name);

	void makeStringPropertyKey(String name);

	IVertex getSystem();

	void commit();

	IVertex getCurrentTask();

	int getCurrentTaskType();

	Vertex getSystemVertex();

	boolean processTask();

	void registerTaskType(int id, Predicate<IFileSystemScanner> taskType);

	IIndentPrinter getOut();

	void indent(String sectionName, Runnable runnable);

	boolean hasDirectoryEntry(String entryName);

	Vertex getCurrentVertex();

	Vertex enqueueNewTask(int taskType);

	void alreadyAdded(String itemName);

	boolean includeDirectoryEntry(String entryName);

	void includeDirectoryEntries(Iterable<String> entries);

	//void adding(String itemName);
	Vertex getNextTask();

	void setCurrentTask(Object taskId);

	void moveToNextTask();

	void processRoot(PSequence<String> rootEntries);

	Edge getDirectoryEntry(Object vertexId);

	Vertex addNewDirectoryEntryToCurrent(String name);

	Object getCurrentTaskId();

	PSequence<String> getCurrentPath();

	File getCurrentFile();

	String getCurrentFilePath();

	void currentFile(Runnable runnable);

	void deleteFile();

	void includeDirectoryEntries();

	Vertex getByPrimaryKey(String key, Object value);

	boolean includeTask(String key, Object id, int ordinal);

	void adding(Sequence<String> logs);

	void alreadyAdded(Sequence<String> logs);

	boolean include(String key, Object id, int taskType, Sequence<String> logs);

	void setPropertyByPrimaryKey(String key, Object keyValue, String propertyName, Object propertyValue);

	boolean put(String key, Object id, int taskType, String propertyName, Object propertyValue, Sequence<String> logs);

	boolean put(String key, Object id, int taskType, String propertyName, Object propertyValue, String log);

	void addEdgeToCurrent(String assocName, String primaryKey, Object keyValue);

	Vertex getByKeyAndPath(String primaryKey, Object keyValue, String assocName);

	<T> T getByKeyAndPath(String primaryKey, Object keyValue, String assocName, String propertyName);

	boolean isDirectory();

	String getFileName();

	void processDirectory();

	String getFileExtension();

	void clearCurrent();

	Vertex getSystemLabelVertex();
}
