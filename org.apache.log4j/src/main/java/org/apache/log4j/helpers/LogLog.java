/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.helpers;

import java.io.PrintStream;

/**
   This class used to output log statements from within the log4j package.

   <p>Log4j components cannot make log4j logging calls. However, it is
   sometimes useful for the user to learn about what log4j is
   doing. You can enable log4j internal logging by defining the
   <b>log4j.configDebug</b> variable.

   <p>All log4j internal debug calls go to <code>System.out</code>
   where as internal error messages are sent to
   <code>System.err</code>. All internal messages are prepended with
   the string "log4j: ".

   @since 0.8.2
   @author Ceki G&uuml;lc&uuml;
 */
public class LogLog
{
	/**
	   Defining this value makes log4j components print log4j-internal
	   debug statements to <code>System.out</code>.

	  <p> The value of this string is <b>log4j.configDebug</b>.

	  <p>Note that the search for all option names is case sensitive.

	 */
	@Deprecated
	public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";

	private static final String ERR_PREFIX = "log4j:ERROR ";

	private static final String WARN_PREFIX = "log4j:WARN ";

	/**
	   Allows to enable/disable log4j internal logging.
	 */
	static public void setInternalDebugging(@SuppressWarnings("javadoc") boolean enabled)
	{
		LogLog.g_debugEnabled = enabled;
	}

	/**
	   This method is used to output log4j internal debug
	   statements. Output goes to <code>System.out</code>.
	 */
	@SuppressWarnings("javadoc")
	public static void debug(String msg, Throwable t)
	{
		if (LogLog.g_debugEnabled && !LogLog.g_quietMode)
		{
			System.out.println("log4j: " + msg);
			if (t != null)
			{
				t.printStackTrace(System.out);
			}
		}
	}

	/**
	   This method is used to output log4j internal error
	   statements. There is no way to disable error statements.
	   Output goes to <code>System.err</code>.
	 */
	@SuppressWarnings("javadoc")
	public static void error(String msg)
	{
		if (LogLog.g_quietMode)
		{
			return;
		}
		System.err.println(ERR_PREFIX + msg);
	}

	/**
	   This method is used to output log4j internal error
	   statements. There is no way to disable error statements.
	   Output goes to <code>System.err</code>.
	 */
	@SuppressWarnings("javadoc")
	public static void error(String msg, Throwable t)
	{
		if (LogLog.g_quietMode)
		{
			return;
		}
		System.err.println(ERR_PREFIX + msg);
		if (t != null)
		{
			t.printStackTrace();
		}
	}

	/**
	   In quite mode no LogLog generates strictly no output, not even
	   for errors.

	   @param quietMode A true for not
	 */
	public static void setQuietMode(boolean quietMode)
	{
		LogLog.g_quietMode = quietMode;
	}

	/**
	   This method is used to output log4j internal warning
	   statements. There is no way to disable warning statements.
	   Output goes to <code>System.err</code>.  */
	@SuppressWarnings("javadoc")
	public static void warn(String msg)
	{
		if (LogLog.g_quietMode)
		{
			return;
		}
		System.err.println(WARN_PREFIX + msg);
	}

	/**
	   This method is used to output log4j internal warnings. There is
	   no way to disable warning statements.  Output goes to
	   <code>System.err</code>.  */
	@SuppressWarnings("javadoc")
	public static void warn(String msg, Throwable t)
	{
		if (LogLog.g_quietMode)
		{
			return;
		}
		System.err.println(WARN_PREFIX + msg);
		if (t != null)
		{
			t.printStackTrace();
		}
	}

	/**
	This method is used to output log4j internal debug
	statements. Output goes to <code>System.out</code>.
	 * @param msg the message
	 */
	public static void debug(String msg)
	{
		debug(g_debugEnabled, g_quietMode, System.out, msg);
	}

	/**
	   This method is used to output log4j internal debug
	   statements. Output goes to <code>System.out</code>.
	 * @param debugEnabled true if debug is enabled
	 * @param quietMode true if quiet mode
	 * @param out the out print stream
	 * @param msg the message
	 */
	public static void debug(boolean debugEnabled, boolean quietMode, PrintStream out, String msg)
	{
		if (debugEnabled && !quietMode)
		{
			out.println("log4j: " + msg);
		}
	}

	/**
	 * true if debug is enabled
	 */
	public static boolean g_debugEnabled = false;

	/**
	   In quietMode not even errors generate any output.
	 */
	public static boolean g_quietMode = false;
}
