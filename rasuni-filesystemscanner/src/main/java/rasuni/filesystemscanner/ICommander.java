package rasuni.filesystemscanner;

import java.io.File;

/**
 * The commander for a file system scannner
 *
 */
public interface ICommander
{

	/**
	 * Command the scanner
	 * @param scanner the scanner
	 * @return true if another execution is required, false if stop
	 * 
	 */
	boolean execute(IFileSystemScanner scanner);

	/**
	 *Process a file
	 * @param file the file
	 * @param context the file processing context
	 */
	void visit(File file,  IFileProcessingContext context);

	/**
	 * Provide root entries
	 * @param registry the registry
	 */
	void provideRootEntries(IRootRegistry registry);

	/**
	 * File has been deleted
	 * @param context the context
	 */
	void fileDeleted(IDeleteProcessingContext context);

}
