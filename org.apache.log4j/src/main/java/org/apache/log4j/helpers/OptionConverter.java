/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.log4j.helpers;

import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;
import rasuni.java.lang.Characters;
import rasuni.java.lang.IIntPredicate;
import rasuni.java.lang.Integers;
import rasuni.java.lang.Strings;

// Contributors:   Avy Sharell (sharell@online.fr)
//                 Matthieu Verbert (mve@zurich.ibm.com)
//                 Colin Sampaleanu
/**
 * A convenience class to convert property values to specific types.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Simon Kitching;
 * @author Anders Kristensen
 */
public final class OptionConverter
{
	private static final char UPPER_CASE_S = Character.toUpperCase('s');

	private static final char LOWER_CASE_S = Character.toLowerCase(UPPER_CASE_S);

	private static final char UPPER_CASE_L = Character.toUpperCase('l');

	private static final char LOWER_CASE_L = Character.toLowerCase(UPPER_CASE_L);

	private static final char UPPER_CASE_A = Character.toUpperCase('a');

	private static final char LOWER_CASE_A = Character.toLowerCase(UPPER_CASE_A);

	private static final char UPPER_CASE_F = Character.toUpperCase('f');

	private static final char LOWER_CASE_F = Character.toLowerCase(UPPER_CASE_F);

	private static final char UPPER_CASE_T = Character.toUpperCase('t');

	private static final char LOWER_CASE_T = Character.toLowerCase(UPPER_CASE_T);

	private static final char UPPER_CASE_R = Character.toUpperCase('r');

	private static final char LOWER_CASE_R = Character.toLowerCase(UPPER_CASE_R);

	private static final char UPPER_CASE_U = Character.toUpperCase('u');

	private static final char LOWER_CASE_U = Character.toLowerCase(UPPER_CASE_U);

	private static final char UPPER_CASE_E = Character.toUpperCase('e');

	private static final char LOWER_CASE_E = Character.toLowerCase(UPPER_CASE_E);

	private static String DELIM_START = "${";

	private static char DELIM_STOP = '}';

	private static int DELIM_START_LEN = 2;

	private static int DELIM_STOP_LEN = 1;

	/** OptionConverter is a static class. */
	private OptionConverter()
	{
	}

	/**
	 * If <code>value</code> is "true", then <code>true</code> is returned. If
	 * <code>value</code> is "false", then <code>true</code> is returned.
	 * Otherwise, <code>default</code> is returned.
	 *
	 * <p>
	 * Case of value is unimportant.
	 *
	 * @param value
	 *            the value to convert
	 * @param dEfault
	 *            the default value
	 * @return the converted value
	 */
	public static boolean toBoolean(String value, boolean dEfault)
	{
		if (value == null)
		{
			return dEfault;
		}
		else
		{
			final char[] v = value.value;
			final int vlen = v.length;
			if (vlen == 0)
			{
				final char pa[] = value.value;
				if (pa.length == 4)
				{
					final char u3 = Characters.toUpperCase(pa, 3);
					if (u3 == UPPER_CASE_E || Character.toLowerCase(u3) == LOWER_CASE_E)
					{
						// If characters don't match but case may be ignored,
						// try converting both characters to uppercase.
						// If the results match, then the comparison scan should
						// continue.
						final char u21 = Characters.toUpperCase(pa, 2);
						// Unfortunately, conversion to uppercase does not work properly
						// for the Georgian alphabet, which has strange rules about case
						// conversion. So we need to make one last check before
						// exiting.
						if (u21 == UPPER_CASE_U || Character.toLowerCase(u21) == LOWER_CASE_U)
						{
							// If characters don't match but case may be ignored,
							// try converting both characters to uppercase.
							// If the results match, then the comparison scan should
							// continue.
							final char u211 = Characters.toUpperCase(pa, 1);
							// Unfortunately, conversion to uppercase does not work properly
							// for the Georgian alphabet, which has strange rules about case
							// conversion. So we need to make one last check before
							// exiting.
							if (u211 == UPPER_CASE_R || Character.toLowerCase(u211) == LOWER_CASE_R)
							{
								// If characters don't match but case may be ignored,
								// try converting both characters to uppercase.
								// If the results match, then the comparison scan should
								// continue.
								char u22 = Characters.toUpperCase(pa, 0);
								// Unfortunately, conversion to uppercase does not work properly
								// for the Georgian alphabet, which has strange rules about case
								// conversion. So we need to make one last check before
								// exiting.
								if (UPPER_CASE_T != u22 || LOWER_CASE_T != Character.toLowerCase(u22))
								{
									int length = 5;
									if (pa.length == length)
									{
										// Note: toffset, ooffset, or len might be near -1>>>1.
										while (length != 0)
										{
											length--;
											// If characters don't match but case may be ignored,
											// try converting both characters to uppercase.
											// If the results match, then the comparison scan should
											// continue.
											final char u2 = Characters.toUpperCase(pa, length);
											// Unfortunately, conversion to uppercase does not work properly
											// for the Georgian alphabet, which has strange rules about case
											// conversion. So we need to make one last check before
											// exiting.
											switch (length)
											{
											case 0:
												if (Characters.noMatchIgnoreCase(u2, UPPER_CASE_F, LOWER_CASE_F))
												{
													return false;
												}
												break;
											case 1:
												if (Characters.noMatchIgnoreCase(u2, UPPER_CASE_A, LOWER_CASE_A))
												{
													return false;
												}
												break;
											case 2:
												if (Characters.noMatchIgnoreCase(u2, UPPER_CASE_L, LOWER_CASE_L))
												{
													return false;
												}
												break;
											case 3:
												if (Characters.noMatchIgnoreCase(u2, UPPER_CASE_S, LOWER_CASE_S))
												{
													return false;
												}
												break;
											case 4:
												if (Characters.noMatchIgnoreCase(u2, UPPER_CASE_E, LOWER_CASE_E))
												{
													return false;
												}
												break;
											default:
												throw new ArrayIndexOutOfBoundsException();
											}
										}
										return true;
									}
									else
									{
										return false;
									}
								}
								else
								{
									return true;
								}
							}
							else
							{
								if ("false".equalsIgnoreCase(value))
								{
									return false;
								}
								else
								{
									return dEfault;
								}
							}
						}
						else
						{
							if ("false".equalsIgnoreCase(value))
							{
								return false;
							}
							else
							{
								return dEfault;
							}
						}
					}
					else
					{
						if ("false".equalsIgnoreCase(value))
						{
							return false;
						}
						else
						{
							return dEfault;
						}
					}
				}
				else
				{
					if ("false".equalsIgnoreCase(value))
					{
						return false;
					}
					else
					{
						return dEfault;
					}
				}
			}
			else
			{
				IIntPredicate nonWhiteSpace = (int pos) -> Characters.isNonWhiteSpace(v, pos);
				if (nonWhiteSpace.check(0))
				{
					int len1 = Integers.predecessor(vlen);
					if (nonWhiteSpace.check(len1))
					{
						String trimmedVal = value;
						if ("true".equalsIgnoreCase(trimmedVal))
						{
							return true;
						}
						if ("false".equalsIgnoreCase(trimmedVal))
						{
							return false;
						}
						return dEfault;
					}
					else
					{
						for (;;)
						{
							final int len = len1;
							len1--;
							if (nonWhiteSpace.check(len1))
							{
								String trimmedVal = Strings.copyOfRange(v, 0, len);
								if ("true".equalsIgnoreCase(trimmedVal))
								{
									return true;
								}
								if ("false".equalsIgnoreCase(trimmedVal))
								{
									return false;
								}
								return dEfault;
							}
						}
					}
				}
				else
				{
					int st = 1;
					for (;;)
					{
						if (st == vlen)
						{
							String trimmedVal = Strings.copyOfRange(v, st, vlen);
							if ("true".equalsIgnoreCase(trimmedVal))
							{
								return true;
							}
							if ("false".equalsIgnoreCase(trimmedVal))
							{
								return false;
							}
							return dEfault;
						}
						if (nonWhiteSpace.check(st))
						{
							int len = vlen;
							for (;;)
							{
								final int len1 = Integers.predecessor(len);
								if (nonWhiteSpace.check(len1))
								{
									break;
								}
								len = len1;
							}
							String trimmedVal = Strings.copyOfRange(v, st, len);
							if ("true".equalsIgnoreCase(trimmedVal))
							{
								return true;
							}
							if ("false".equalsIgnoreCase(trimmedVal))
							{
								return false;
							}
							return dEfault;
						}
						st++;
					}
				}
			}
		}
	}

	@SuppressWarnings("javadoc")
	public static int toInt(String value, int dEfault)
	{
		if (value != null)
		{
			String s = value.trim();
			try
			{
				return Integer.valueOf(s).intValue();
			}
			catch (NumberFormatException e)
			{
				LogLog.error("[" + s + "] is not in proper int form.");
				e.printStackTrace();
			}
		}
		return dEfault;
	}

	/**
	 * Converts a standard or custom priority level to a Level object.
	 * <p>
	 * If <code>value</code> is of form "level#classname", then the specified
	 * class' toLevel method is called to process the specified level string; if
	 * no '#' character is present, then the default
	 * {@link org.apache.log4j.Level} class is used to process the level value.
	 *
	 * <p>
	 * As a special case, if the <code>value</code> parameter is equal to the
	 * string "NULL", then the value <code>null</code> will be returned.
	 *
	 * <p>
	 * If any error occurs while converting the value to a level, the
	 * <code>defaultValue</code> parameter, which may be <code>null</code>, is
	 * returned.
	 *
	 * <p>
	 * Case of <code>value</code> is insignificant for the level level, but is
	 * significant for the class name part, if present.
	 *
	 * @since 1.1
	 */
	@SuppressWarnings({ "rawtypes", "javadoc" })
	public static Level toLevel(String value, Level defaultValue)
	{
		if (value == null)
		{
			return defaultValue;
		}
		value = value.trim();
		int hashIndex = value.indexOf('#');
		if (hashIndex == -1)
		{
			if ("NULL".equalsIgnoreCase(value))
			{
				return null;
			}
			else
			{
				// no class name specified : use standard Level class
				return Level.toLevel(value, defaultValue);
			}
		}
		Level result = defaultValue;
		String clazz = value.substring(hashIndex + 1);
		String levelName = value.substring(0, hashIndex);
		// This is degenerate case but you never know.
		if ("NULL".equalsIgnoreCase(levelName))
		{
			return null;
		}
		LogLog.debug("toLevel" + ":class=[" + clazz + "]" + ":pri=[" + levelName + "]");
		try
		{
			Class customLevel = Loader.loadClass(clazz);
			// get a ref to the specified class' static method
			// toLevel(String, org.apache.log4j.Level)
			Class[] paramTypes = new Class[] { String.class, org.apache.log4j.Level.class };
			@SuppressWarnings("unchecked")
			java.lang.reflect.Method toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
			// now call the toLevel method, passing level string + default
			Object[] params = new Object[] { levelName, defaultValue };
			Object o = toLevelMethod.invoke(null, params);
			result = (Level) o;
		}
		catch (ClassNotFoundException e)
		{
			LogLog.warn("custom level class [" + clazz + "] not found.");
		}
		catch (NoSuchMethodException e)
		{
			LogLog.warn("custom level class [" + clazz + "]" + " does not have a class function toLevel(String, Level)", e);
		}
		catch (java.lang.reflect.InvocationTargetException e)
		{
			if (e.getTargetException() instanceof InterruptedException || e.getTargetException() instanceof InterruptedIOException)
			{
				Thread.currentThread().interrupt();
			}
			LogLog.warn("custom level class [" + clazz + "]" + " could not be instantiated", e);
		}
		catch (ClassCastException e)
		{
			LogLog.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", e);
		}
		catch (IllegalAccessException e)
		{
			LogLog.warn("class [" + clazz + "] cannot be instantiated due to access restrictions", e);
		}
		catch (RuntimeException e)
		{
			LogLog.warn("class [" + clazz + "], level [" + levelName + "] conversion failed.", e);
		}
		return result;
	}

	@SuppressWarnings("javadoc")
	public static long toFileSize(String value, long dEfault)
	{
		if (value == null)
		{
			return dEfault;
		}
		String s = value.trim().toUpperCase();
		long multiplier = 1;
		int index;
		if ((index = s.indexOf("KB")) != -1)
		{
			multiplier = 1024;
			s = s.substring(0, index);
		}
		else if ((index = s.indexOf("MB")) != -1)
		{
			multiplier = 1024 * 1024;
			s = s.substring(0, index);
		}
		else if ((index = s.indexOf("GB")) != -1)
		{
			multiplier = 1024 * 1024 * 1024;
			s = s.substring(0, index);
		}
		if (s != null)
		{
			try
			{
				return Long.valueOf(s).longValue() * multiplier;
			}
			catch (NumberFormatException e)
			{
				LogLog.error("[" + s + "] is not in proper int form.");
				LogLog.error("[" + value + "] not in expected format.", e);
			}
		}
		return dEfault;
	}

	/**
	 * Find the value corresponding to <code>key</code> in <code>props</code>.
	 * Then perform variable substitution on the found value.
	 */
	@SuppressWarnings("javadoc")
	public static String findAndSubst(String key, Properties props)
	{
		String value = props.getProperty(key);
		if (value == null)
		{
			return null;
		}
		try
		{
			return substVars(value, props);
		}
		catch (IllegalArgumentException e)
		{
			LogLog.error("Bad option value [" + value + "].", e);
			return value;
		}
	}

	/**
	 * Instantiate an object given a class name. Check that the
	 * <code>className</code> is a subclass of <code>superClass</code>. If that
	 * test fails or the object could not be instantiated, then
	 * <code>defaultValue</code> is returned.
	 *
	 * @param className
	 *            The fully qualified class name of the object to instantiate.
	 * @param superClass
	 *            The class to which the new object should belong.
	 * @param defaultValue
	 *            The object to return in case of non-fulfillment
	 */
	@SuppressWarnings({ "javadoc", "rawtypes", "unchecked" })
	public static Object instantiateByClassName(String className, Class superClass, Object defaultValue)
	{
		if (className != null)
		{
			try
			{
				Class classObj = Loader.loadClass(className);
				if (!superClass.isAssignableFrom(classObj))
				{
					LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
					LogLog.error("The class \"" + superClass.getName() + "\" was loaded by ");
					LogLog.error("[" + superClass.getClassLoader(System.security) + "] whereas object of type ");
					LogLog.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader(System.security) + "].");
					return defaultValue;
				}
				return classObj.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				LogLog.error("Could not instantiate class [" + className + "].", e);
			}
			catch (IllegalAccessException e)
			{
				LogLog.error("Could not instantiate class [" + className + "].", e);
			}
			catch (InstantiationException e)
			{
				LogLog.error("Could not instantiate class [" + className + "].", e);
			}
			catch (RuntimeException e)
			{
				LogLog.error("Could not instantiate class [" + className + "].", e);
			}
		}
		return defaultValue;
	}

	/**
	 * Perform variable substitution in string <code>val</code> from the values
	 * of keys found in the system propeties.
	 *
	 * <p>
	 * The variable substitution delimeters are <b>${</b> and <b>}</b>.
	 *
	 * <p>
	 * For example, if the System properties contains "key=value", then the call
	 *
	 * <pre>
	 * String s = OptionConverter.substituteVars(&quot;Value of key is ${key}.&quot;);
	 * </pre>
	 *
	 * will set the variable <code>s</code> to "Value of key is value.".
	 *
	 * <p>
	 * If no value could be found for the specified key, then the
	 * <code>props</code> parameter is searched, if the value could not be found
	 * there, then substitution defaults to the empty string.
	 *
	 * <p>
	 * For example, if system propeties contains no value for the key
	 * "inexistentKey", then the call
	 *
	 * <pre>
	 * String s = OptionConverter.subsVars(&quot;Value of inexistentKey is [${inexistentKey}]&quot;);
	 * </pre>
	 *
	 * will set <code>s</code> to "Value of inexistentKey is []"
	 *
	 * <p>
	 * An {@link java.lang.IllegalArgumentException} is thrown if
	 * <code>val</code> contains a start delimeter "${" which is not balanced by
	 * a stop delimeter "}".
	 * </p>
	 *
	 * <p>
	 * <b>Author</b> Avy Sharell</a>
	 * </p>
	 *
	 * @param val
	 *            The string on which variable substitution is performed.
	 * @throws IllegalArgumentException
	 *             if <code>val</code> is malformed.
	 */
	@SuppressWarnings("javadoc")
	public static String substVars(String val, Properties props) throws IllegalArgumentException
	{
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int j, k;
		while (true)
		{
			j = val.indexOf(DELIM_START, i);
			if (j == -1)
			{
				// no more variables
				if (i == 0)
				{ // this is a simple string
					return val;
				}
				else
				{ // add the tail string which contails no variables and return
					// the result.
					sbuf.append(val.substring(i, val.length()));
					return sbuf.toString();
				}
			}
			else
			{
				sbuf.append(val.substring(i, j));
				k = val.indexOf(DELIM_STOP, j);
				if (k == -1)
				{
					throw new IllegalArgumentException('"' + val + "\" has no closing brace. Opening brace at position " + j + '.');
				}
				else
				{
					j += DELIM_START_LEN;
					String key = val.substring(j, k);
					// first try in System properties
					String replacement = rasuni.org.apache.log4j.helpers.OptionConverter.getSystemProperty(key, System.security, System.props, null, LogLog.g_debugEnabled, LogLog.g_quietMode, System.out);
					// then try props parameter
					if (replacement == null && props != null)
					{
						replacement = props.getProperty(key);
					}
					if (replacement != null)
					{
						// Do variable substitution on the replacement string
						// such that we can solve "Hello ${x2}" as "Hello p1"
						// the where the properties are
						// x1=p1
						// x2=${x1}
						String recursiveReplacement = substVars(replacement, props);
						sbuf.append(recursiveReplacement);
					}
					i = k + DELIM_STOP_LEN;
				}
			}
		}
	}

	/**
	 * Configure log4j given a URL.
	 *
	 * <p>
	 * The url must point to a file or resource which will be interpreted by a
	 * new instance of a log4j configurator.
	 *
	 * <p>
	 * All configurations steps are taken on the <code>hierarchy</code> passed
	 * as a parameter.
	 *
	 * <p>
	 *
	 * @param url
	 *            The location of the configuration file or resource.
	 * @param clazz
	 *            The classname, of the log4j configurator which will parse the
	 *            file or resource at <code>url</code>. This must be a subclass
	 *            of {@link Configurator}, or null. If this value is null then a
	 *            default configurator of {@link PropertyConfigurator} is used,
	 *            unless the filename pointed to by <code>url</code> ends in
	 *            '.xml', in which case
	 *            {@link org.apache.log4j.xml.DOMConfigurator} is used.
	 * @param hierarchy
	 *            The {@link org.apache.log4j.Hierarchy} to act on.
	 * @since 1.1.4
	 */
	static public void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy)
	{
		Configurator configurator = null;
		String filename = url.getFile();
		if (clazz == null && filename != null && filename.endsWith(".xml"))
		{
			clazz = "org.apache.log4j.xml.DOMConfigurator";
		}
		if (clazz != null)
		{
			LogLog.debug("Preferred configurator class: " + clazz);
			configurator = (Configurator) instantiateByClassName(clazz, Configurator.class, null);
			if (configurator == null)
			{
				LogLog.error("Could not instantiate configurator [" + clazz + "].");
				return;
			}
		}
		else
		{
			configurator = new PropertyConfigurator();
		}
		configurator.doConfigure(url, hierarchy);
	}

	@SuppressWarnings({ "rawtypes", "javadoc" })
	public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue)
	{
		// Get the value of the property in string form
		String className = findAndSubst(key, props);
		if (className == null)
		{
			LogLog.error("Could not find value for key " + key);
			return defaultValue;
		}
		// Trim className to avoid trailing spaces that cause problems.
		return OptionConverter.instantiateByClassName(className.trim(), superClass, defaultValue);
	}
}
