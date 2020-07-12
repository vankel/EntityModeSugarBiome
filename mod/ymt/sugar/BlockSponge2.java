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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import mod.ymt.cmn.Coord3D;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * @author Yamato
 *
 */
public class BlockSponge2 extends BlockSponge {
	public BlockSponge2(int blockId) {
		super(blockId);
		setHardness(0.6F);
		setStepSound(soundClothFootstep);
		setUnlocalizedName("sponge");
		setTextureName("sponge");
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		world.scheduleBlockUpdate(x, y, z, blockID, tickRate(world));
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {
		world.scheduleBlockUpdate(x, y, z, blockID, tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		eraseLiquid(world, x, y, z);
	}

	private void addLiquidSource(Set<Coord3D> result, World world, Coord3D pos, Coord3D base, Set<Coord3D> route) {
		if (route.contains(pos) || !pos.nearFrom(5, base) || pos.y < 0 || 255 < pos.y) {
			return;
		}
		route.add(pos);
		if (isLiquid(world, pos.x, pos.y, pos.z)) {
			if (world.getBlockMetadata(pos.x, pos.y, pos.z) == 0) {
				result.add(pos);
			}
			for (Coord3D newPos: pos.getNeighbor()) {
				addLiquidSource(result, world, newPos, base, route);
			}
		}
	}

	private void eraseLiquid(World world, int x, int y, int z) {
		Set<Coord3D> waterSource = new HashSet<Coord3D>();
		Set<Coord3D> route = new HashSet<Coord3D>();
		Coord3D base = new Coord3D(x, y, z);
		for (Coord3D pos: base.getNeighbor()) {
			addLiquidSource(waterSource, world, pos, base, route);
		}
		for (Coord3D pos: waterSource) {
			world.setBlockToAir(pos.x, pos.y, pos.z);
		}
	}

	private boolean isLiquid(World world, int x, int y, int z) {
		Material material = world.getBlockMaterial(x, y, z);
		return material != null && material.isLiquid() && material == Material.water; // マグマは吸水できない
	}
}
