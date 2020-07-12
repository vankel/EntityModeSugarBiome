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

import java.util.Random;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.BlockSand;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MapColor;
import net.minecraft.src.Material;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class BlockSugar extends BlockSand {
	private static final boolean ENABLE_SPAWNMAID = false;
	private static final int ENTITY_LIMIT = 512; // 水に触れた際に砂糖を生成するかどうかのエンティティ限界値
	private static final Material sugarMaterial = new Material(MapColor.snowColor);

	private final SugarBiomeCore core = SugarBiomeCore.getInstance();

	public BlockSugar(int blockId) {
		super(blockId, sugarMaterial);
		setHardness(0.5F);
		setStepSound(soundSandFootstep);
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
					int id = idDropped(metadata, world.rand, fortune);
					if (0 < id) {
						dropBlockAsItem_do(world, x, y, z, new ItemStack(id, 1, this.damageDropped(metadata)));
					}
				}
			}
		}
	}

	@Override
	public boolean func_82506_l() { // scheduledUpdatesAreImmediate を禁止
		return false;
	}

	@Override
	public int idDropped(int metadata, Random rand, int fortune) {
		return Item.sugar.itemID;
	}

	public boolean isNatureSugar(int metadata) {
		return (metadata & 1) == 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int changedBlockId) {
		if (isTouchWater(world, x, y, z)) {
			if (Utils.isServerSide(world)) {
				if (world.loadedEntityList.size() < ENTITY_LIMIT) { // エンティティ数が一定以上の時には砂糖を生成しない
					int cnt = world.rand.nextInt(2);
					if (0 < cnt) {
						int meta = world.getBlockMetadata(x, y, z);
						int itemId = idDropped(meta, world.rand, 0);
						dropBlockAsItem_do(world, x, y, z, new ItemStack(itemId, cnt, this.damageDropped(meta)));
					}
				}
				world.setBlockToAir(x, y, z);
			}
			return;
		}
		if (world.getBlockId(x, y + 1, z) == Block.pumpkin.blockID) {
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
		super.onNeighborBlockChange(world, x, y, z, changedBlockId);
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

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("mod.ymt.sugar01");
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
		if (world.getBlockMaterial(x, y, z) != Material.water)
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
