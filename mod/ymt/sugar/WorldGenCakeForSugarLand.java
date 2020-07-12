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
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCakeForSugarLand extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		final Block cake = Blocks.cake;
		final Block sugar = SugarBiomeCore.getInstance().getSugarBlock();
		for (; 0 < y; y--) {
			Block id = world.getBlock(x, y, z);
			if (id == sugar || id == cake) {
				if (world.isAirBlock(x, y + 1, z) && cake.canBlockStay(world, x, y + 1, z)) {
					world.setBlock(x, y + 1, z, cake, 0, 2);
					return true;
				}
			}
		}
		return true;
	}
}
