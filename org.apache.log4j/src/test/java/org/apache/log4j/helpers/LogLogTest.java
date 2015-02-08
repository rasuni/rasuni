package org.apache.log4j.helpers;

import java.io.OutputStream;
import java.io.PrintStream;
import org.easymock.EasyMockSupport;
import org.junit.Test;

/**
 * Test case for the class {@link LogLog}
 *
 */
public class LogLogTest extends EasyMockSupport
{
	private interface IWriter
	{
		void write(int b);
	}

	private final IWriter _writer = createStrictMock(IWriter.class);

	private final PrintStream _printStream = new PrintStream(new OutputStream()
	{
		@Override
		public void write(int b)
		{
			_writer.write(b);
		}
	});

	/**
	 * Test the method {@link LogLog#debug(boolean, boolean, java.io.PrintStream, String)}. Debug is disabled.
	 */
	@Test
	public void debugDisabled()
	{
		replayAll();
		LogLog.debug(false, false, null, null);
		verifyAll();
	}

	/**
	 * Test the method {@link LogLog#debug(boolean, boolean, java.io.PrintStream, String)}. Debug is enabled.
	 */
	@Test
	public void debugEnabled()
	{
		_writer.write(108);
		_writer.write(111);
		_writer.write(103);
		_writer.write(52);
		_writer.write(106);
		_writer.write(58);
		_writer.write(32);
		_writer.write(110);
		_writer.write(117);
		_writer.write(108);
		_writer.write(108);
		_writer.write(13);
		_writer.write(10);
		replayAll();
		LogLog.debug(true, false, _printStream, null);
		verifyAll();
	}

	/**
	 * Test the method {@link LogLog#debug(boolean, boolean, java.io.PrintStream, String)}. Quiet mode.
	 */
	@Test
	public void debugQuiet()
	{
		replayAll();
		LogLog.debug(true, true, null, null);
		verifyAll();
	}
}
