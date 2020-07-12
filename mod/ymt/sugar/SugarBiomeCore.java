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
package mod.ymt.sugar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mod.ymt.cmn.ItemWithMetadata;
import mod.ymt.cmn.NekonoteCore;
import mod.ymt.cmn.Reflection;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemSpade;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.ModLoader;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.mod_SugarBiome;

/**
 * @author Yamato
 *
 */
public class SugarBiomeCore extends NekonoteCore {
	private static final SugarBiomeCore instance = new SugarBiomeCore();
	private int sugarBlockId = 0;
	private int sugarBiomeId = 0;
	private boolean replaceCake = true;
	private boolean replaceSponge = true;

	private SugarBiomeCore() {
		;
	}

	public SpawnListEntry copySpawnListEntry(SpawnListEntry ent) {
		return mod_SugarBiome.copySpawnListEntry(ent);
	}

	public int getSugarBiomeId() {
		return sugarBiomeId;
	}

	public int getSugarBlockId() {
		return sugarBlockId;
	}

	@Override
	public void init() {
		Block sugarBlock = null;
		if (0 < sugarBlockId) {
			// �����u���b�N
			sugarBlock = new BlockSugar(sugarBlockId).setUnlocalizedName("SugarBlock");
			ModLoader.registerBlock(sugarBlock, ItemWithMetadata.class);
			Utils.addName(sugarBlock, "SugarBlock", "�����u���b�N");
			Utils.addName(new ItemStack(sugarBlock, 0, 0), "SugarBlock", "�����u���b�N"); // �V�R�����u���b�N
			Utils.addName(new ItemStack(sugarBlock, 0, 1), "SugarBlock", "�����u���b�N"); // �l�H�����u���b�N

			// �����������u���b�N(�l�H)
			ModLoader.addRecipe(new ItemStack(sugarBlock, 1, 1), new Object[]{
				"XX", "XX", 'X', Item.sugar
			});
			// �����u���b�N������
			ModLoader.addRecipe(new ItemStack(Item.sugar, 4, 0), new Object[]{
				"X", 'X', sugarBlock
			});

			// �����u���b�N�̓X�R�b�v�ŉ󂵂₷��
			try {
				Block[] old_blocks = (Block[]) ModLoader.getPrivateValue(ItemSpade.class, null, 0);
				List<Block> list = new ArrayList<Block>(Arrays.asList(old_blocks));
				list.add(sugarBlock);
				Block[] new_blocks = list.toArray(new Block[0]);
				Reflection.replaceFieldValues(ItemSpade.class, null, old_blocks, new_blocks);
				for (ItemSpade spade: Reflection.getFieldValues(Item.class, null, ItemSpade.class)) {
					Reflection.replaceFieldValues(ItemTool.class, spade, old_blocks, new_blocks);
				}
			}
			catch (Exception ex) {
				debugPrint(ex, "blocksEffectiveAgainst set failed");
			}
		}
		if (sugarBlock != null && 0 < sugarBiomeId) {
			// �����o�C�I�[��
			BiomeGenSugarLand biome = new BiomeGenSugarLand(sugarBiomeId, sugarBlock.blockID);
			ModLoader.addBiome(biome);
		}

		// �P�[�L�����ւ�
		if (replaceCake) {
			Block.blocksList[Block.cake.blockID] = null;
			Block newCake = new BlockCake2(Block.cake.blockID);
			ModLoader.registerBlock(newCake, ItemWithMetadata.class);
			Utils.addName(newCake, "Cake", "�P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 0), "Cake", "�P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 1), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 2), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 3), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 4), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 5), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			Utils.addName(new ItemStack(newCake, 0, 6), "half-eaten Cake", "�H�ׂ����̃P�[�L");
			try {
				Reflection.replaceFieldValues(Block.class, null, Block.cake, newCake);
				// Item.cake �͒u�������Ȃ�
			}
			catch (Exception e) {
				debugPrint(e, "replaceFieldValues cake");
			}
		}

		// �X�|���W�����ւ�
		if (replaceSponge) {
			Block.blocksList[Block.sponge.blockID] = null;
			Block newSponge = new BlockSponge2(Block.sponge.blockID);
			ModLoader.registerBlock(newSponge);
			Utils.addName(newSponge, "Sponge", "�X�|���W");
			Utils.addName(new ItemStack(newSponge, 0, 0), "Sponge", "�X�|���W");
			try {
				Reflection.replaceFieldValues(Block.class, null, Block.sponge, newSponge);
			}
			catch (Exception e) {
				debugPrint(e, "replaceFieldValues sponge");
			}
			ModLoader.addRecipe(new ItemStack(Block.sponge, 1, 0), new Object[]{
				"X X", " X ", "X X", 'X', Block.cloth
			});
		}
	}

	public boolean isReplaceCake() {
		return replaceCake;
	}

	public boolean isReplaceSponge() {
		return replaceSponge;
	}

	public void setReplaceCake(boolean replaceCake) {
		this.replaceCake = replaceCake;
	}

	public void setReplaceSponge(boolean replaceSponge) {
		this.replaceSponge = replaceSponge;
	}

	public void setSugarBiomeId(int sugarBiomeId) {
		this.sugarBiomeId = sugarBiomeId;
	}

	public void setSugarBlockId(int sugarBlockId) {
		this.sugarBlockId = sugarBlockId;
	}

	public static SugarBiomeCore getInstance() {
		return instance;
	}
}
