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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/**
 * @author Yamato
 *
 */
public class Reflection {
	private static final Logger logger = Logger.getLogger(Reflection.class.getName());
	private static UnsafeAccessor accessor = null;

	static {
		try {
			accessor = UnsafeAccessorImpl.newInstance();
		}
		catch (NoClassDefFoundError e) {
			Trace.logWarningThrow(logger, e, "UnsafeAccessor unsupported");
		}
		catch (UnsupportedOperationException e) {
			Trace.logWarningThrow(logger, e, "UnsafeAccessor unsupported");
		}
	}

	public static <T> List<T> getFieldValues(Class<?> cls, Object obj, Class<T> type) throws IllegalArgumentException, IllegalAccessException {
		List<T> result = new ArrayList<T>();
		for (Field f: cls.getDeclaredFields()) {
			int mod = f.getModifiers();
			if (!Modifier.isStatic(mod) && obj == null) {
				continue;
			}
			f.setAccessible(true);
			Object value = f.get(obj);
			if (value != null && type.isInstance(value)) {
				result.add(type.cast(value));
			}
		}
		return result;
	}

	public static boolean replaceFieldValues(Class<?> cls, Object obj, Object before, Object after) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		boolean result = false;
		for (Field f: cls.getDeclaredFields()) {
			int mod = f.getModifiers();
			if (!Modifier.isStatic(mod) && obj == null) {
				continue;
			}
			f.setAccessible(true);
			if (f.get(obj) == before) {
				if (setFieldValue(f, obj, after)) {
					Trace.logFine(logger, "replace field(%s): %s -> %s", f, before, after);
					result = true;
				}
			}
		}
		return result;
	}

	public static boolean setFieldValue(Class<?> cls, Object obj, Object after, Class<?> fieldType, String... fields) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		boolean result = false;
		Set<String> fieldNames = new HashSet<String>(Arrays.asList(fields));
		for (Field f: cls.getDeclaredFields()) {
			if (f.getType() != fieldType) {
				continue;
			}
			if (!Modifier.isStatic(f.getModifiers()) && obj == null) {
				continue;
			}
			if (fieldNames.contains(f.getName())) {
				f.setAccessible(true);
				if (setFieldValue(f, obj, after)) {
					Trace.logFine(logger, "set field(%s): -> %s", f, after);
					result = true;
				}
			}
		}
		return result;
	}

	public static boolean setFieldValue(Field f, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		int mod = f.getModifiers();
		if (Modifier.isFinal(mod)) {
			if (accessor == null) {
				Trace.logFine(logger, "ignore field(%s) because UnsafeAccessor unsupported", f);
				return false;
			}
			if (f.getType().isPrimitive()) {
				Trace.logFine(logger, "ignore field(%s) because field is primitive", f);
				return false;
			}
			accessor.putObjectVolatile(obj, f, value);
		}
		else {
			f.set(obj, value);
		}
		return true;
	}

	private interface UnsafeAccessor {
		public void putObjectVolatile(Object obj, Field f, Object value);
	}

	private static class UnsafeAccessorImpl implements UnsafeAccessor {
		/**
		 * 黒魔術
		 */
		private Unsafe unsafe;

		private UnsafeAccessorImpl() {
			try {
				Field f = Unsafe.class.getDeclaredField("theUnsafe");
				f.setAccessible(true);
				unsafe = (Unsafe) f.get(null);
			}
			catch (NoSuchFieldException e) {
				throw new UnsupportedOperationException(e);
			}
			catch (SecurityException e) {
				throw new UnsupportedOperationException(e);
			}
			catch (IllegalArgumentException e) {
				throw new UnsupportedOperationException(e);
			}
			catch (IllegalAccessException e) {
				throw new UnsupportedOperationException(e);
			}
		}

		@Override
		public void putObjectVolatile(Object obj, Field f, Object value) {
			boolean isStatic = Modifier.isStatic(f.getModifiers());
			obj = isStatic ? unsafe.staticFieldBase(f) : obj;
			if (obj == null) {
				throw new NullPointerException("object is null");
			}
			long offset = isStatic ? unsafe.staticFieldOffset(f) : unsafe.objectFieldOffset(f);
			unsafe.putObjectVolatile(obj, offset, value);
		}

		public static UnsafeAccessor newInstance() {
			return new UnsafeAccessorImpl();
		}
	}
}
