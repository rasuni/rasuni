package rasuni.java.lang;

import java.util.Properties;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

/**
 * Test the class {@link System}
 *
 */
public class SystemTest extends EasyMockSupport
{
	private interface ICheckPropertyAccess
	{
		void checkPropertyAccess(String key);
	}

	private final ICheckPropertyAccess _checkPropertyAccess = createStrictMock(ICheckPropertyAccess.class);

	private final SecurityManager _securityManager = new SecurityManager()
	{
		@Override
		public void checkPropertyAccess(String key)
		{
			_checkPropertyAccess.checkPropertyAccess(key);
		}
	};

	private interface IGetProperty
	{
		String getProperty(String key);
	}

	private final IGetProperty _getProperty = createStrictMock(IGetProperty.class);

	private final Properties _properties = new Properties()
	{
		@Override
		public String getProperty(String key)
		{
			return _getProperty.getProperty(key);
		}
	};

	void replay(Runnable runnable)
	{
		replayAll();
		runnable.run();
		verifyAll();
	}

	/**
	 * Test the method {@link System#checkKey(String)}. Null Key.
	 */
	@Test
	public void checkKeyNull()
	{
		replay(() ->
		{
			try
			{
				System.checkKey(null);
				Assert.fail();
			}
			catch (NullPointerException e1)
			{
				// success
			}
		});
	}

	/**
	 * Test the method {@link System#checkKey(String)}. Illegal argument
	 */
	@Test
	public void checkKeyIllegal()
	{
		replay(() ->
		{
			try
			{
				System.checkKey("");
				Assert.fail();
			}
			catch (IllegalArgumentException e1)
			{
				// success
			}
		});
	}

	/**
	 * Test the method {@link System#checkKey(String)}. Success.
	 */
	@Test
	public void checkKeySuccess()
	{
		replay(() -> System.checkKey("\0"));
	}

	/**
	 * Test full coverage
	 */
	@Test
	public void coverage()
	{
		replay(() -> Classes.construct(System.class));
	}

	private void expectGetProperty()
	{
		EasyMock.expect(_getProperty.getProperty("\0")).andReturn(null);
	}

	/**
	 * Test the method {@link System#getProperty(String, SecurityManager, Properties, String)}
	 */
	@Test
	public void getProperty()
	{
		_checkPropertyAccess.checkPropertyAccess("\0");
		expectGetProperty();
		replay(() ->
		{
			Assert.assertNull(System.getProperty("\0", _securityManager, _properties, null));
		});
	}

	/**
	 * Test the method {@link System#getProperty(String, SecurityManager, Properties, String)}. No security manager
	 */
	@Test
	public void getPropertyNoSecurityManager()
	{
		expectGetProperty();
		replay(() ->
		{
			Assert.assertNull(System.getProperty("\0", null, _properties, null));
		});
	}
}
