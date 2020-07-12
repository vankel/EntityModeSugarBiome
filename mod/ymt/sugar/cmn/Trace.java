/**
 * Copyright 2015 Yamato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mod.ymt.sugar.cmn;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Trace {
	public static void log(Logger logger, Level level, String format, Object... args) {
		if (logger.isLoggable(level)) {
			logger.log(level, format(format, args));
		}
		//		System.err.println(format(format, args));
	}
	
	public static void logConfig(Logger logger, String format, Object... args) {
		log(logger, Level.CONFIG, format, args);
	}
	
	public static void logFine(Logger logger, String format, Object... args) {
		log(logger, Level.FINE, format, args);
	}
	
	public static void logFineThrow(Logger logger, Throwable th, String format, Object... args) {
		if (logger.isLoggable(Level.FINE)) {
			LogRecord record = newRecord(Level.FINE, format, args);
			record.setThrown(th);
			logger.log(record);
		}
	}
	
	public static void logInfo(Logger logger, String format, Object... args) {
		log(logger, Level.INFO, format, args);
	}
	
	public static void logSevere(Logger logger, String format, Object... args) {
		log(logger, Level.SEVERE, format, args);
	}
	
	public static void logWarning(Logger logger, String format, Object... args) {
		log(logger, Level.WARNING, format, args);
	}
	
	public static void logWarningThrow(Logger logger, Throwable th, String format, Object... args) {
		if (logger.isLoggable(Level.WARNING)) {
			LogRecord record = newRecord(Level.WARNING, format, args);
			record.setThrown(th);
			logger.log(record);
		}
	}
	
	private static String format(String format, Object... args) {
		if (args == null || args.length == 0)
			return format;
		else
			return String.format(format, args);
	}
	
	private static LogRecord newRecord(Level level, String format, Object... args) {
		return new LogRecord(level, format(format, args));
	}
}
