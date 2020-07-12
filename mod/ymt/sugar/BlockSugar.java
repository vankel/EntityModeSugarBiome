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

import java.util.Random;
import mod.ymt.sugar.cmn.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Yamato
 *
 */
public class BlockSugar extends BlockFalling {
	private static final boolean ENABLE_SPAWNMAID = false;
	private static final int ENTITY_LIMIT = 512; // 水に触れた際に砂糖を生成するかどうかのエンティティ限界値
	private static final Material sugarMaterial = new Material(MapColor.snowColor);
	
	public BlockSugar() {
		super(sugarMaterial);
		setHardness(0.5F);
		setStepSound(soundTypeSand);
		setHarvestLevel("shovel", 0);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public int damageDropped(int metadata) {
		return 0; // Item.sugar の metadata = 0
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float probability, int fortune) {
		if (!world.isRemote) {
			int quantity = quantityDroppedWithBonus(fortune, world.rand);
			if (!isNatureSugar(metadata)) {
				quantity = Math.min(quantity, 4); // 人工砂糖は最大ドロップ数4個
			}
			for (int i = 0; i < quantity; i++) {
				if (world.rand.nextFloat() <= probability) {
					Item item = getItemDropped(metadata, world.rand, fortune);
					if (item != null) {
						dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, this.damageDropped(metadata)));
					}
				}
			}
		}
	}
	
	@Override
	public boolean func_149698_L() { // scheduledUpdatesAreImmediate を禁止
		return false;
	}
	
	@Override
	public Item getItemDropped(int metadata, Random rand, int fortune) {
		return Items.sugar;
	}
	
	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}
	
	public boolean isNatureSugar(int metadata) {
		return (metadata & 1) == 0;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block changedBlock) {
		if (isTouchWater(world, x, y, z)) {
			if (Utils.isServerSide(world)) {
				if (world.loadedEntityList.size() < ENTITY_LIMIT) { // エンティティ数が一定以上の時には砂糖を生成しない
					int cnt = world.rand.nextInt(2);
					if (0 < cnt) {
						int metadata = world.getBlockMetadata(x, y, z);
						Item item = getItemDropped(metadata, world.rand, 0);
						if (item != null) {
							dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, this.damageDropped(metadata)));
						}
					}
				}
				world.setBlockToAir(x, y, z);
			}
			return;
		}
		if (world.getBlock(x, y + 1, z) == Blocks.pumpkin) {
			if (ENABLE_SPAWNMAID) {
				if (Utils.isServerSide(world)) {
					Entity ent = EntityList.createEntityByName("LittleMaid", world);
					if (ent instanceof EntityLiving) {
						// メイドさんスポーン
						EntityLiving living = (EntityLiving) ent;
						living.setLocationAndAngles(x + 0.5, y + 0.5, z + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
						if (world.spawnEntityInWorld(living)) {
							// ブロック消去
							world.setBlockToAir(x, y, z);
							world.setBlockToAir(x, y + 1, z);
						}
					}
				}
				return;
			}
		}
		super.onNeighborBlockChange(world, x, y, z, changedBlock);
	}
	
	@Override
	public int quantityDropped(Random rand) {
		return 4;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand) {
		int result = quantityDropped(rand);
		if (0 < fortune) {
			result += rand.nextInt(fortune);
		}
		return result;
	}
	
	private boolean isTouchWater(World world, int x, int y, int z) {
		if (isWater(world, x, y + 1, z, false))
			return true;
		if (isWater(world, x - 1, y, z, true))
			return true;
		if (isWater(world, x + 1, y, z, true))
			return true;
		if (isWater(world, x, y, z - 1, true))
			return true;
		if (isWater(world, x, y, z + 1, true))
			return true;
		return false;
	}
	
	private boolean isWater(World world, int x, int y, int z, boolean ignoreUnderFlow) {
		if (y < 0)
			return false;
		if (world.getBlock(x, y, z).getMaterial() != Material.water)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		if (ignoreUnderFlow && 8 <= meta)
			return false; // ブロック横の下向き水流はセーフ
		if (meta == 0)
			return false; // 水源はセーフ
		if (meta == 7)
			return false; // 長さ 7 の隣はセーフ
		return true;
	}
}
