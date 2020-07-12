/**
 * Copyright 2013 Yamato
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
package net.minecraft.src;

import mod.ymt.cmn.CfgFile;
import mod.ymt.sugar.SugarBiomeCore;

/**
 * @author Yamato
 *
 */
public class mod_SugarBiome extends BaseMod {
	@Override
	public String getPriorities() {
		return "required-after:mod_YMTLib";
	}

	@Override
	public String getVersion() {
		return "162v2";
	}

	@Override
	public void load() {
		CfgFile cfgFile = new CfgFile("mod_SugarBiome.txt");
		int sugarBlockId = cfgFile.getInt("sugarBlockId", 203, 0, 255);
		int sugarBiomeId = cfgFile.getInt("sugarBiomeId", 55, 0, 127);
		boolean replaceCake = cfgFile.getBoolean("replaceCake", false);
		boolean replaceSponge = cfgFile.getBoolean("replaceSponge", false);
		boolean enable = cfgFile.getBoolean("enable", true);
		cfgFile.save();

		if (enable) {
			SugarBiomeCore core = SugarBiomeCore.getInstance();
			core.setSugarBlockId(sugarBlockId);
			core.setSugarBiomeId(sugarBiomeId);
			core.setReplaceCake(replaceCake);
			core.setReplaceSponge(replaceSponge);
			core.run();
		}
	}

	public static SpawnListEntry copySpawnListEntry(SpawnListEntry ent) {
		return new SpawnListEntry(ent.entityClass, ent.itemWeight, ent.minGroupCount, ent.maxGroupCount);
	}
}
