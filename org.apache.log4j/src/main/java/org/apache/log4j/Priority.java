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
// Contributors:  Kitching Simon <Simon.Kitching@orange.ch>
package org.apache.log4j;

/**
   <font color="#AA4444">Refrain from using this class directly, use
   the {@link Level} class instead</font>.

   @author Ceki G&uuml;lc&uuml; */
public class Priority
{
	int level;

	String _levelStr;

	int _syslogEquivalent;

	@SuppressWarnings("javadoc")
	public final static int OFF_INT = Integer.MAX_VALUE;

	@SuppressWarnings("javadoc")
	public final static int FATAL_INT = 50000;

	@SuppressWarnings("javadoc")
	public final static int ERROR_INT = 40000;

	@SuppressWarnings("javadoc")
	public final static int WARN_INT = 30000;

	@SuppressWarnings("javadoc")
	public final static int INFO_INT = 20000;

	@SuppressWarnings("javadoc")
	public final static int DEBUG_INT = 10000;

	// public final static int FINE_INT = DEBUG_INT;
	@SuppressWarnings("javadoc")
	public final static int ALL_INT = Integer.MIN_VALUE;

	/**
	 * @deprecated Use {@link Level#FATAL} instead.
	 */
	@Deprecated
	final static public Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

	/**
	 * @deprecated Use {@link Level#ERROR} instead.
	 */
	@Deprecated
	final static public Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

	/**
	 * @deprecated Use {@link Level#WARN} instead.
	 */
	@Deprecated
	final static public Priority WARN = new Level(WARN_INT, "WARN", 4);

	/**
	 * @deprecated Use {@link Level#INFO} instead.
	 */
	@Deprecated
	final static public Priority INFO = new Level(INFO_INT, "INFO", 6);

	/**
	 * @deprecated Use {@link Level#DEBUG} instead.
	 */
	@Deprecated
	final static public Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

	/**
	 * Default constructor for deserialization.
	 */
	Priority()
	{
		level = DEBUG_INT;
		_levelStr = "DEBUG";
		_syslogEquivalent = 7;
	}

	/**
	   Instantiate a level object.
	 * @param plevel the level
	 * @param levelStr the level string
	 * @param syslogEquivalent  the system log equivalent
	 */
	public Priority(int plevel, String levelStr, int syslogEquivalent)
	{
		level = plevel;
		_levelStr = levelStr;
		_syslogEquivalent = syslogEquivalent;
	}

	/**
	   Two priorities are equal if their level fields are equal.
	   @since 1.2
	 */
	@Override
	public final boolean equals(Object o)
	{
		if (o instanceof Priority)
		{
			Priority r = (Priority) o;
			return level == r.level;
		}
		else
		{
			return false;
		}
	}

	/**
	   Return the syslog equivalent of this priority as an integer.
	 */
	@SuppressWarnings("javadoc")
	public final int getSyslogEquivalent()
	{
		return _syslogEquivalent;
	}

	/**
	   Returns <code>true</code> if this level has a higher or equal
	   level than the level passed as argument, <code>false</code>
	   otherwise.

	   <p>You should think twice before overriding the default
	   implementation of <code>isGreaterOrEqual</code> method.

	 */
	@SuppressWarnings("javadoc")
	public final boolean isGreaterOrEqual(Priority r)
	{
		return level >= r.level;
	}

	/**
	   Return all possible priorities as an array of Level objects in
	   descending order.

	   @deprecated This method will be removed with no replacement.
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public static Priority[] getAllPossiblePriorities()
	{
		return new Priority[] { Priority.FATAL, Priority.ERROR, Level.WARN, Priority.INFO, Priority.DEBUG };
	}

	/**
	   Returns the string representation of this priority.
	 */
	@Override
	public final String toString()
	{
		return _levelStr;
	}

	/**
	   Returns the integer representation of this level.
	 */
	@SuppressWarnings("javadoc")
	public final int toInt()
	{
		return level;
	}

	@Override
	public final int hashCode()
	{
		throw new RuntimeException("not implemented!");
	}

	/**
	 * @deprecated Please use the {@link Level#toLevel(String)} method instead.
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public static Priority toPriority(String sArg)
	{
		return Level.toLevel(sArg);
	}

	/**
	 * @deprecated Please use the {@link Level#toLevel(int)} method instead.
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public static Priority toPriority(int val)
	{
		return toPriority(val, Level.DEBUG);
	}

	/**
	 * @deprecated Please use the {@link Level#toLevel(int, Level)} method instead.
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public static Priority toPriority(int val, Level defaultPriority)
	{
		return Level.toLevel(val, defaultPriority);
	}

	/**
	 * @deprecated Please use the {@link Level#toLevel(String, Level)} method instead.
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public static Priority toPriority(String sArg, Level defaultPriority)
	{
		return Level.toLevel(sArg, defaultPriority);
	}
}
