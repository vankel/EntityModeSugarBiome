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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * @author Yamato
 *
 */
public class WorldGenMinableClipper extends WorldGenerator {
	private final WorldGenerator parent;
	private final int min, max;

	public WorldGenMinableClipper(WorldGenerator parent, int min, int max) {
		this.parent = parent;
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (min <= y && y <= max) {
			parent.generate(world, rand, x, y, z);
		}
		return true;
	}
}
