package rasuni.lang;

import junit.framework.TestCase;

/**
 * Test case for the class {@link Objects}
 */
public class ObjectsTest extends TestCase
{
	
	private Object _object = new Object ();

	/**
	 * Test the method {@link Objects#isNull(Object)}
	 */
	@SuppressWarnings("static-method")
	public void testIsNull () {
		assertTrue (Objects.isNull(null));
	}

	/**
	 * Test the method {@link Objects#isNull(Object)}
	 */
	public void testIsNullFallse () {
		assertFalse (Objects.isNull(_object));
	}

	/**
	 * Test the method {@link Objects#notNull(Object)}
	 */
	@SuppressWarnings("static-method")
	public void testNotNull () {
		assertFalse (Objects.notNull(null));
	}


	/**
	 * Test the method {@link Objects#notNull(Object)}
	 */
	public void testNotNullTrue () {
		assertTrue (Objects.notNull(_object));
	}

}
