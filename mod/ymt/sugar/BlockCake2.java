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

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Yamato
 *
 */
public class BlockCake2 extends BlockCake {
	public BlockCake2(int blockId) {
		super(blockId);
		setUnlocalizedName("cake");
		setHardness(0.5F);
		setStepSound(soundClothFootstep);
		disableStats();
		setTextureName("cake");
	}

	@Override
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
		// 何もしない
		// 食べて消失するのと同時に回収すると  InventoryPlayer.canHarvestBlock で NullPointerException が出てしまうので
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	protected ItemStack createStackedBlock(int metadata) {
		if (metadata == 0)
			return new ItemStack(Item.cake, 1, 0);
		else
			return new ItemStack(Item.itemsList[Block.cake.blockID], 1, metadata);
	}
}
