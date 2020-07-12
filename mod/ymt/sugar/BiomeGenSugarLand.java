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

import java.util.List;
import java.util.Random;
import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class BiomeGenSugarLand extends BiomeGenBase {
	private boolean initializedSpawnList = false;

	public BiomeGenSugarLand(int biomeId, int sugarBlockId) {
		super(biomeId);
		setBiomeName("SugarLand");
		this.topBlock = (byte) sugarBlockId;
		this.fillerBlock = (byte) sugarBlockId;
		this.color = 16777215;
		spawnableCreatureList.clear();
	}

	@Override
	public void decorate(World world, Random rand, int x, int z) {
		super.decorate(world, rand, x, z);
		// エメラルド生成
		int chance = 3 + rand.nextInt(6);
		for (int i = 0; i < chance; i++) {
			int posX = x + rand.nextInt(16);
			int posY = rand.nextInt(28) + 4;
			int posZ = z + rand.nextInt(16);
			if (world.getBlockId(posX, posY, posZ) == Block.stone.blockID) {
				world.setBlock(posX, posY, posZ, Block.oreEmerald.blockID, 0, 2);
			}
		}
	}

	@Override
	public List getSpawnableList(EnumCreatureType type) {
		synchronized (this) {
			if (type == EnumCreatureType.creature && !initializedSpawnList) {
				initializedSpawnList = true;
				SugarBiomeCore.getInstance().debugPrint("SpawnableList(Creature) initialize");
				// メイドさん追加
				for (Object obj: BiomeGenBase.plains.getSpawnableList(EnumCreatureType.creature)) {
					if (obj instanceof SpawnListEntry) {
						SpawnListEntry ent = (SpawnListEntry) obj;
						if ("LMM_EntityLittleMaid".equals(ent.entityClass.getSimpleName())) {
							SugarBiomeCore.getInstance().debugPrint("find littleMaidMob!");
							spawnableCreatureList.add(SugarBiomeCore.getInstance().copySpawnListEntry(ent));
						}
					}
				}
			}
		}
		return super.getSpawnableList(type);
	}

	@Override
	protected BiomeDecorator createBiomeDecorator() {
		return new BiomeSugarDecorator(this);
	}

	private static Class<?> findClassOrNull(String cls) {
		try {
			return Class.forName(cls);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}
}
