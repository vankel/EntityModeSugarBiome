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

import mod.ymt.sugar.cmn.ItemWithMetadata;
import mod.ymt.sugar.cmn.NekonoteCore;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author Yamato
 *
 */
public class SugarBiomeCore extends NekonoteCore {
	private static final SugarBiomeCore instance = new SugarBiomeCore();
	private Block sugarBlock = null;
	private int sugarBiomeId = 0;
	
	private SugarBiomeCore() {
		;
	}
	
	public int getSugarBiomeId() {
		return sugarBiomeId;
	}
	
	public Block getSugarBlock() {
		return sugarBlock;
	}
	
	@Override
	public void init() {
		// 砂糖ブロック
		this.sugarBlock = new BlockSugar().setBlockName("SugarBlock").setBlockTextureName("mod_ymt_sugar:sugar");
		GameRegistry.registerBlock(sugarBlock, ItemWithMetadata.class, "blockSugar");
		
		// 砂糖→砂糖ブロック(人工)
		GameRegistry.addRecipe(new ItemStack(sugarBlock, 1, 1), new Object[]{
			"XX", "XX", 'X', Items.sugar
		});
		// 砂糖ブロック→砂糖
		GameRegistry.addRecipe(new ItemStack(Items.sugar, 4, 0), new Object[]{
			"X", 'X', sugarBlock
		});
		
		if (sugarBlock != null && 0 < sugarBiomeId) {
			// 砂糖バイオーム
			BiomeGenSugarLand biome = new BiomeGenSugarLand(sugarBiomeId, sugarBlock);
			BiomeManager.desertBiomes.add(new BiomeEntry(biome, 10));
			BiomeManager.warmBiomes.add(new BiomeEntry(biome, 10));
		}
	}
	
	public void setSugarBiomeId(int sugarBiomeId) {
		this.sugarBiomeId = sugarBiomeId;
	}
	
	public static SugarBiomeCore getInstance() {
		return instance;
	}
}
