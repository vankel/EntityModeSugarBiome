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
package mod.ymt.sugar;

import mod.ymt.sugar.cmn.CfgFile;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * @author Yamato
 *
 */
@Mod(modid = "mod.ymt.sugar.EntityModeSugarBiome", name = "EntityModeSugarBiome", version = "17Av1", dependencies="after:lmmx")
public class SugarBiome {
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CfgFile cfgFile = new CfgFile("mod_SugarBiome.txt");
		int sugarBiomeId = cfgFile.getInt("sugarBiomeId", 55, 0, 127);
		cfgFile.save();
		
		SugarBiomeCore core = SugarBiomeCore.getInstance();
		core.setSugarBiomeId(sugarBiomeId);
		core.run();
	}
}
