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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;

/**
 * @author Yamato
 *
 */
public class CfgFile {
	private static File cfgdir = new File(getMcGameDir(), "/config/");
	private final File file;
	private final Properties props = new Properties();
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public CfgFile(File dir, String filename) {
		this.file = new File(dir, filename);
		load();
	}
	
	public CfgFile(String filename) {
		this(cfgdir, filename);
	}
	
	public boolean getBoolean(String key, boolean _default) {
		boolean result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			result = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("ok") || value.equalsIgnoreCase("yes");
		}
		if (value == null) {
			set(key, _default);
		}
		return result;
	}
	
	public double getDouble(String key, double _default) {
		double result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			try {
				result = Double.parseDouble(value);
			}
			catch (NumberFormatException e) {
				Trace.logWarning(logger, "CfgFile Format Error: file = %s, key = %s, value = %s", file, key, value);
				value = null;
			}
		}
		if (value == null) {
			set(key, _default);
		}
		return result;
	}
	
	public float getFloat(String key, float _default) {
		float result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			try {
				result = Float.parseFloat(value);
			}
			catch (NumberFormatException e) {
				Trace.logWarning(logger, "CfgFile Format Error: file = %s, key = %s, value = %s", file, key, value);
				value = null;
			}
		}
		if (value == null) {
			set(key, _default);
		}
		return result;
	}
	
	public int getInt(String key, int _default) {
		int result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			try {
				result = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				Trace.logWarning(logger, "CfgFile Format Error: file = %s, key = %s, value = %s", file, key, value);
				value = null;
			}
		}
		if (value == null) {
			set(key, _default);
		}
		return result;
	}
	
	public int getInt(String key, int _default, int min, int max) {
		int result = getInt(key, _default);
		if (result < min) {
			set(key, min);
			return min;
		}
		if (max < result) {
			set(key, max);
			return max;
		}
		return result;
	}
	
	public List<Integer> getIntList(String key, List<Integer> _default) {
		List<Integer> result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			result = parseIntList(value);
		}
		set(key, result); // CleanUp のため無条件で再設定
		return result;
	}
	
	public List<String> getStringList(String key, List<String> _default) {
		List<String> result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			result = parseStringList(value);
		}
		set(key, result); // CleanUp のため無条件で再設定
		return result;
	}
	
	public String getString(String key, String _default) {
		String result = _default;
		String value = props.getProperty(key);
		if (value != null) {
			result = value;
		}
		if (value == null) {
			set(key, _default);
		}
		return result;
	}
	
	public void save() {
		try {
			if (initFile() && file.canWrite()) {
				FileOutputStream fout = new FileOutputStream(file);
				try {
					props.store(fout, "Mod Configuration");
				}
				finally {
					fout.close();
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void set(String key, boolean value) {
		props.setProperty(key, value ? "true" : "false");
	}
	
	public void set(String key, double value) {
		props.setProperty(key, Double.toString(value));
	}
	
	public void set(String key, float value) {
		props.setProperty(key, Float.toString(value));
	}
	
	public void set(String key, int value) {
		props.setProperty(key, Integer.toString(value));
	}
	
	public void set(String key, List<?> value) {
		props.setProperty(key, toString(value));
	}
	
	public void set(String key, String value) {
		props.setProperty(key, value);
	}
	
	private boolean initFile() throws IOException {
		file.getParentFile().mkdirs();
		return file.exists() || file.createNewFile();
	}
	
	private void load() {
		try {
			if (initFile() && file.canRead()) {
				FileInputStream fin = new FileInputStream(file);
				try {
					// 読み込み
					props.load(fin);
					// ログ出力
					Trace.logFine(logger, "YMTLib-CfgFile load from: %s", file);
					for (String key: props.stringPropertyNames()) {
						Trace.logFine(logger, "    %s = %s", key, props.getProperty(key));
					}
				}
				finally {
					fin.close();
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private List<Integer> parseIntList(String text) {
		List<Integer> result = new ArrayList<Integer>();
		for(String value: parseStringList(text)) {
			try {
				result.add(Integer.parseInt(value));
			}
			catch (NumberFormatException ex) {
				Trace.logWarning(logger, "CfgFile IllegalNumberFormat Error: file = %s, value = %s", file, value);
			}
		}
		return result;
	}
	
	private List<String> parseStringList(String text) {
		List<String> result = new ArrayList<String>();
		if (text != null) {
			for (String value: text.split(",")) {
				value = value.trim();
				if (0 < value.length()) {
					result.add(value);
				}
			}
		}
		return result;
	}
	
	private static File getMcGameDir() {
		try {
			return Minecraft.getMinecraft().mcDataDir; // Minecraft があれば、mcDataDir を使用
		}
		catch (NoClassDefFoundError ex) {
			;
		}
		return new File("."); // なかったら暫定でカレントディレクトリ
	}

	private static String toString(List<?> value) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < value.size(); i++) {
			if (0 < i)
				result.append(", ");
			result.append(value.get(i));
		}
		return result.toString();
	}
}
