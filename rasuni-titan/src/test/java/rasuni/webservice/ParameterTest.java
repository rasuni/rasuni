package rasuni.webservice;

import junit.framework.TestCase;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * @author Ralph Sigrist
 *
 * Test case for the class {@link Parameter}
 *
 */
public class ParameterTest extends TestCase
{
	/**
	 * Test the method {@link Parameter#inc(String)}
	 */
	@SuppressWarnings("static-method")
	public void testInc()
	{
		assertTrue(EqualsBuilder.reflectionEquals(Parameter.inc(null), new Parameter("inc", null)));
	}
}
