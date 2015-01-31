package rasuni.webservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import rasuni.titan.TitanCollector;

/**
 * @author Ralph Sigrist
 *
 * @param <T>
 *            result class
 */
public final class WebService<T>
{
	private final Unmarshaller _unmarshaller;

	private Long _lastQueryTime = null;

	private final String _address;

	private final int _waitTime;

	private final DefaultHttpClient httpclient = new DefaultHttpClient();

	private final HashMap<String, T> _map = new HashMap<>();

	/**
	 * Constructor
	 *
	 * @param cls
	 *            result class
	 * @param waitTime
	 *            wait time
	 * @param address
	 *            the address
	 */
	public WebService(Class<T> cls, int waitTime, String address)
	{
		httpclient.setCredentialsProvider(new CredentialsProvider()
		{
			/**
			 * The one and only instance
			 */
			private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			private Credentials _credentials;

			private String readConsole()
			{
				try
				{
					return in.readLine();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}

			@Override
			public void setCredentials(AuthScope authscope, Credentials credentials)
			{
				throw new RuntimeException("not implemented!");
			}

			@Override
			public Credentials getCredentials(AuthScope authscope)
			{
				if (_credentials == null)
				{
					System.out.println(authscope.getHost() + ":" + authscope.getPort() + " requires authentication with the realm '" + authscope.getRealm() + "'");
					System.out.print("Enter username: ");
					String user = readConsole();
					System.out.print("Enter password: ");
					String password = readConsole();
					_credentials = new UsernamePasswordCredentials(user, password);
				}
				return _credentials;
			}

			@Override
			public void clear()
			{
				throw new RuntimeException("not implemented!");
			}
		});
		try
		{
			_unmarshaller = JAXBContext.newInstance(cls).createUnmarshaller();
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(e);
		}
		_waitTime = waitTime;
		_address = address;
	}

	private static void sleep(long l)
	{
		try
		{
			Thread.sleep(l);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get resource
	 *
	 * @param param
	 *            parameter
	 * @return the result
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public T get(String param)
	{
		T res = _map.get(param);
		if (res == null)
		{
			int retryCount = 0;
			for (;;)
			{
				try
				{
					long currentTime = System.currentTimeMillis();
					if (_lastQueryTime != null)
					{
						long diff = currentTime - _lastQueryTime.longValue();
						if (diff < 1000)
						{
							long l = _waitTime - diff;
							sleep(l < 0 ? 0 : l);
						}
					}
					_lastQueryTime = new Long(currentTime);
					String url = _address + "/" + param;
					// System.out.println(url);
					try
					{
						try
						{
							final HttpResponse response = httpclient.execute(new HttpGet(url));
							final HttpEntity entity = response.getEntity();
							try
							{
								if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND)
								{
									return null;
								}
								else
								{
									res = (T) _unmarshaller.unmarshal(entity.getContent());
									_map.put(param, res);
									return res;
								}
							}
							finally
							{
								EntityUtils.consume(entity);
							}
						}
						catch (IOException e)
						{
							throw new RuntimeException(e);
						}
					}
					catch (UnmarshalException ue)
					{
						final Throwable cause = ue.getCause();
						if (cause instanceof FileNotFoundException)
						{
							return null;
						}
						if (cause != null)
						{
							throw ue;
						}
						if (retryCount == 4)
						{
							throw ue;
						}
						retryCount++;
						sleep(retryCount * 1000);
						System.out.println("retrying " + url);
					}
				}
				catch (JAXBException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		else
		{
			return res;
		}
	}

	/**
	 * Query a resource with parameter
	 *
	 * @param resource
	 *            the resource
	 * @param parameters
	 *            the parameters
	 * @return the query result
	 */
	public T query(String resource, Parameter... parameters)
	{
		String params = TitanCollector.join(TitanCollector.sequence(parameters, 0), parameter -> parameter._name + '=' + parameter._value, '&');
		return get(params.isEmpty() ? resource : resource + '?' + params);
	}
}