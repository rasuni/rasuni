package rasuni.filesystemscanner.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import rasuni.mock.MockTestCase;

public class IndentPrinterTest extends MockTestCase
{
	public IndentPrinter _printer = new IndentPrinter();

	public StringBuffer _out = new StringBuffer();

	private void println(String string)
	{
		_out.append(string);
		_out.append(System.lineSeparator());
	}

	@Rule
	public final SystemOutRule _systemOutRule = new SystemOutRule();

	private void run(Runnable runnable)
	{
		_systemOutRule.mute();
		_systemOutRule.enableLog();
		replay(() -> runnable.run());
		Assert.assertEquals(_out.toString(), _systemOutRule.getLog());
	}

	private final Runnable _runnable = createStrictMock(Runnable.class);

	@Test
	public void println()
	{
		println("null");
		run(() -> _printer.println(null));
	}

	@Test
	public void printlnIncrement()
	{
		println("  null");
		run(() ->
		{
			_printer.incrementLevel();
			_printer.println(null);
		});
	}

	@Test
	public void decrementLevel()
	{
		_printer.decrementLevel();
	}

	@Test
	public void log()
	{
		println("");
		run(() ->
		{
			_printer.log(null);
		});
	}

	@Test
	public void logCons()
	{
		println("adding");
		run(() ->
		{
			_printer.adding((Sequence<String>) null);
		});
	}

	@Test
	public void alreadyAdded()
	{
		println("already added");
		run(() ->
		{
			_printer.alreadyAdded((Sequence<String>) null);
		});
	}

	@Test
	public void indent()
	{
		println("null");
		_runnable.run();
		run(() -> _printer.indent(null, _runnable));
	}

	@Test
	public void indentExeption()
	{
		println("null");
		_runnable.run();
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		try
		{
			replay(() -> _printer.indent(null, _runnable));
			Assert.fail();
		}
		catch (RuntimeException e)
		{
			// succeed
		}
	}

	@Test
	public void adding()
	{
		println("adding null");
		run(() ->
		{
			_printer.adding((String) null);
		});
	}

	@Test
	public void alreadyAddedNull()
	{
		println("already added null");
		run(() ->
		{
			_printer.alreadyAdded((String) null);
		});
	}

	@Test
	public void printField()
	{
		println("null: null");
		run(() ->
		{
			_printer.printField(null, null);
		});
	}

	@Test
	public void deleting()
	{
		println("deleting null");
		run(() ->
		{
			_printer.deleting(null);
		});
	}
}
