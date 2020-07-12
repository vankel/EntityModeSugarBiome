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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenReed;

public class WorldGenReedForSugarLand extends WorldGenReed {
	private final SugarBiomeCore core = SugarBiomeCore.getInstance();
	private final int sugarBlockId = core.getSugarBlockId();

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		for (int i = 0; i < 20; ++i) {
			int posX = x + rand.nextInt(4) - rand.nextInt(4);
			int posY = y;
			int posZ = z + rand.nextInt(4) - rand.nextInt(4);

			if (world.isAirBlock(posX, posY, posZ) && isWaterSide(world, posX, posY, posZ)) {
				if (world.getBlockId(posX, posY - 1, posZ) == sugarBlockId) {
					world.setBlock(posX, posY - 1, posZ, Block.grass.blockID, 0, 2);
				}
				int height = 2 + rand.nextInt(rand.nextInt(3) + 1);
				for (int j = 0; j < height; j++) {
					if (Block.reed.canBlockStay(world, posX, posY + j, posZ)) {
						world.setBlock(posX, posY + j, posZ, Block.reed.blockID, 0, 2);
					}
				}
			}
		}
		return true;
	}

	private boolean isWaterSide(World world, int posX, int y, int posZ) {
		if (world.getBlockMaterial(posX - 1, y - 1, posZ) == Material.water)
			return true;
		if (world.getBlockMaterial(posX + 1, y - 1, posZ) == Material.water)
			return true;
		if (world.getBlockMaterial(posX, y - 1, posZ - 1) == Material.water)
			return true;
		if (world.getBlockMaterial(posX, y - 1, posZ + 1) == Material.water)
			return true;
		return false;
	}
}