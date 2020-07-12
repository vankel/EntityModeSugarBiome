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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeSugarDecorator extends BiomeDecorator {
	private final WorldGenerator voidGen;
	private final WorldGenerator sugarGen;
	private final WorldGenerator cakeGen;
	private final SugarBiomeCore core = SugarBiomeCore.getInstance();
	private final int sugarBlockId = core.getSugarBlockId();
	private final int sugarBiomeId = core.getSugarBiomeId();

	public BiomeSugarDecorator(BiomeGenBase owner) {
		super(owner);
		this.voidGen = new WorldGenMinable(0, 10); // 空間生成
		this.sugarGen = new WorldGenMinable(sugarBlockId, 32); // 砂糖ブロック生成
		this.cakeGen = new WorldGenCakeForSugarLand();
		this.reedGen = new WorldGenReedForSugarLand();
		this.ironGen = new WorldGenMinableClipper(this.ironGen, 0, 48); // 鉄の生成範囲を 0 ～ 48 に制限
		this.waterlilyPerChunk = 4;
		this.treesPerChunk = -9999;
		this.flowersPerChunk = -9999;
		this.grassPerChunk = -9999;
		this.deadBushPerChunk = -9999;
		this.mushroomsPerChunk = -9999;
		this.reedsPerChunk = 80;
		this.cactiPerChunk = 0;
		this.sandPerChunk = 0;
		this.sandPerChunk2 = 0;
		this.clayPerChunk = 0;
	}

	@Override
	protected void decorate() {
		super.decorate();
		// 表層の石を砂糖に置き換え
		World world = this.currentWorld;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int posX = this.chunk_X + x;
				int posZ = this.chunk_Z + z;
				BiomeGenBase biome = world.getBiomeGenForCoords(posX, posZ);
				if (biome != null && biome.biomeID == sugarBiomeId) {
					int y = world.getTopSolidOrLiquidBlock(posX, posZ);
					int height = 24 + this.randomGenerator.nextInt(5);
					for (int i = 0; i < height && y > 0; i++, y--) {
						if (world.getBlockId(posX, y, posZ) == Block.stone.blockID) {
							world.setBlock(posX, y, posZ, sugarBlockId, 0, 2);
						}
					}
				}
			}
		}
		// ケーキ乱立
		for (int i = 0; i < 8; i++) {
			int x = this.chunk_X + this.randomGenerator.nextInt(16);
			int y = this.randomGenerator.nextInt(120) + 8;
			int z = this.chunk_Z + this.randomGenerator.nextInt(16);
			cakeGen.generate(this.currentWorld, this.randomGenerator, x, y, z);
		}
	}

	@Override
	protected void generateOres() {
		this.genStandardOre1(20, this.voidGen, 5, 128);
		this.genStandardOre1(40, this.sugarGen, 16, 128);
		super.generateOres();
	}
}
