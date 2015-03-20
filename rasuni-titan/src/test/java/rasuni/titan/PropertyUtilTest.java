package rasuni.titan;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import rasuni.mock.MockTestCase;
import rasuni.test.Classes;

/**
 * Test case for the class {@link PropertyUtil}.
 */
public class PropertyUtilTest extends MockTestCase
{
	interface IProperties
	{
		String getProperty(String key, String defaultValue);
	}

	private final IProperties _properties = createStrictMock(IProperties.class);

	/**
	 * Test the method {@link PropertyUtil#get(Properties, String)}
	 */
	@Test
	public void get()
	{
		expectAndReturn(_properties.getProperty(null, null), null);
		replay(() ->
		{
			Assert.assertNull(PropertyUtil.get(new Properties()
			{
				@Override
				public String getProperty(String key, String defaultValue)
				{
					return _properties.getProperty(key, defaultValue);
				}
			}, null));
		});
	}

	/**
	 * Test full coverage
	 */
	@Test
	public void coverage()
	{
		replay(() ->
		{
			Classes.construct(PropertyUtil.class);
		});
	}
}
