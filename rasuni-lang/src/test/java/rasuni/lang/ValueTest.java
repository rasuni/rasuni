package rasuni.lang;

import junit.framework.TestCase;

/**
 * Test case for the class {@link Value}
 *
 */
public class ValueTest extends TestCase
{
	
	private Value _value = new Value ();
	
	/**
	 * Test the method {@link Value#equals(Object)}
	 */
	public void testEquals () {
		assertFalse (_value.equals (null));
	}
	
	/**
	 * Test the method {@link Value#hashCode()}
	 */
	public void testHashCode () {
		assertEquals (17, _value.hashCode());
	}
	
	/**
	 * Test the method {@link Value#toString()}
	 */
	public void testToString () {
		assertEquals ("Value[]", _value.toString());
	}
}
