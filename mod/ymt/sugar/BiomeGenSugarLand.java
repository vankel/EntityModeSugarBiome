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

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * @author Yamato
 *
 */
public class BiomeGenSugarLand extends BiomeGenBase {
	private boolean initializedSpawnList = false;

	public BiomeGenSugarLand(int biomeId, Block sugarBlock) {
		super(biomeId);
		setBiomeName("SugarLand");
		this.topBlock = sugarBlock;
		this.fillerBlock = sugarBlock;
		this.color = 16777215;
		setDisableRain();
		setHeight(height_LowPlains);
		spawnableCreatureList.clear();
	}

	@Override
	public void decorate(World world, Random rand, int x, int z) {
		super.decorate(world, rand, x, z);
		SugarBiomeCore.getInstance().logFine("decorate: x = " + x + ", z = " + z);
		// エメラルド生成
		int chance = 3 + rand.nextInt(6);
		for (int i = 0; i < chance; i++) {
			int posX = x + rand.nextInt(16);
			int posY = rand.nextInt(28) + 4;
			int posZ = z + rand.nextInt(16);
			if (world.getBlock(posX, posY, posZ) == Blocks.stone) {
				world.setBlock(posX, posY, posZ, Blocks.emerald_ore, 0, 2);
			}
		}
	}

	@Override
	public List getSpawnableList(EnumCreatureType type) {
		synchronized (this) {
			if (type == EnumCreatureType.creature && !initializedSpawnList) {
				initializedSpawnList = true;
				SugarBiomeCore.getInstance().logFine("SpawnableList(Creature) initialize");
				// メイドさん追加
				for (Object obj: BiomeGenBase.plains.getSpawnableList(EnumCreatureType.creature)) {
					if (obj instanceof SpawnListEntry) {
						SpawnListEntry ent = (SpawnListEntry) obj;
						if ("LMM_EntityLittleMaid".equals(ent.entityClass.getSimpleName())) {
							SugarBiomeCore.getInstance().logFine("find littleMaidMob!");
							spawnableCreatureList.add(new SpawnListEntry(ent.entityClass, ent.itemWeight, ent.minGroupCount, ent.maxGroupCount));
						}
					}
				}
			}
		}
		return super.getSpawnableList(type);
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new BiomeSugarDecorator(this);
	}
}
