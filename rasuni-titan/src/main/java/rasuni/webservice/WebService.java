package rasuni.webservice;

import fj.data.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import org.xml.sax.SAXParseException;

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

	private final int _waitTime;

	private final DefaultHttpClient httpclient = new DefaultHttpClient();

	private final String _address;

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
		_address = address;
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
	 * @param resource
	 *            the resource
	 *
	 * @return the result
	 */
	@SuppressWarnings({ "unchecked" })
	public T get(String resource)
	{
		T res = _map.get(resource);
		if (res == null)
		{
			int retryCount = 0;
			String uri = _address + "/" + resource;
			for (;;)
			{
				try
				{
					long currentTime = System.currentTimeMillis();
					if (_lastQueryTime != null)
					{
						long diff = currentTime - _lastQueryTime.longValue();
						long sleepTime = _waitTime - diff;
						if (sleepTime > 0)
						{
							sleep(sleepTime);
							currentTime += sleepTime;
						}
					}
					_lastQueryTime = new Long(currentTime);
					try
					{
						try
						{
							final HttpResponse response = httpclient.execute(new HttpGet(uri));
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
									_map.put(resource, res);
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
						if (cause != null && !(cause instanceof SAXParseException))
						{
							throw ue;
						}
						if (retryCount == 4)
						{
							throw ue;
						}
						retryCount++;
						sleep(retryCount * 1000);
						System.out.println("retrying " + uri);
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
		List<String> pn = List.list(parameters).map(parameter -> parameter._name + '=' + encode(parameter._value));
		return get(pn.isEmpty() ? resource : resource + '?' + pn.tail().foldLeft((params, param) -> params + '&' + param, pn.head()));
	}

	private static String encode(String value)
	{
		try
		{
			return URLEncoder.encode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}