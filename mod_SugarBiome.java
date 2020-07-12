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

import mod.ymt.sugar.SugarBiomeCore;

/**
 * @author Yamato
 *
 */
public class mod_SugarBiome extends BaseMod {
	@MLProp(min = 0, max = 255)
	public static int sugarBlockId = 203;
	@MLProp(min = 0, max = 127)
	public static int sugarBiomeId = 55;
	@MLProp
	public static boolean replaceCake = false;
	@MLProp
	public static boolean replaceSponge = false;

	@Override
	public String getPriorities() {
		return "required-after:mod_YMTLib";
	}

	@Override
	public String getVersion() {
		return "151v4";
	}

	@Override
	public void load() {
		try {
			SugarBiomeCore core = SugarBiomeCore.getInstance();
			core.setSugarBlockId(sugarBlockId);
			core.setSugarBiomeId(sugarBiomeId);
			core.setReplaceCake(replaceCake);
			core.setReplaceSponge(replaceSponge);
			core.run();
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}
	}

	public static SpawnListEntry copySpawnListEntry(SpawnListEntry ent) {
		return new SpawnListEntry(ent.entityClass, ent.itemWeight, ent.minGroupCount, ent.maxGroupCount);
	}
}
