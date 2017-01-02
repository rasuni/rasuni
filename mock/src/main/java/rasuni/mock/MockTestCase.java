package rasuni.mock;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;

/**
 * Mock test case helper
 *
 */
public class MockTestCase extends EasyMockSupport
{
	protected static <T> void expectAndReturn(T ignore, T result)
	{
		EasyMock.expect(ignore).andReturn(result);
	}

	protected static <T> void expectAndThrow(T ignore, Throwable throwable)
	{
		EasyMock.expect(ignore).andThrow(throwable);
	}

	protected void replay(Runnable runnable)
	{
		replayAll();
		runnable.run();
		verifyAll();
	}
}
