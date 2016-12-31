package rasuni.filesystemscanner.impl;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import rasuni.test.Classes;

public class FilesTest
{
	@Rule
	public final TemporaryFolder _folder = new TemporaryFolder();

	@Test
	public void containsNoEntries()
	{
		Assert.assertFalse(Files.containsEntries(_folder.getRoot()));
	}

	@Test
	public void containsEntries() throws IOException
	{
		_folder.newFolder();
		Assert.assertTrue(Files.containsEntries(_folder.getRoot()));
	}

	@SuppressWarnings("static-method")
	@Test
	public void construct()
	{
		Classes.construct(Files.class);
	}
}
