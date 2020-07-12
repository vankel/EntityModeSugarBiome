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
package net.minecraft.src;

import java.util.Random;
import mod.ymt.sugar.SugarBiomeCore;

/**
 * @author Yamato
 *
 */
public class SBLMMEntityMode_SugarHunter extends LMM_EntityMode_Basic {
	public final int mmode_SugarHunter = 0x3201;
	private final Random rand = new Random();
	private boolean modeSearchChest = false;
	private int coolTime = 0;
	private boolean speedUp = false;
	private double lastdistance = 0;
	private int moveRetryCount = 0;

	static {
		SugarBiomeCore.getInstance().debugPrint("initializing SBLMMEntityMode_SugarHunter");
	}

	public SBLMMEntityMode_SugarHunter(LMM_EntityLittleMaid owner) {
		super(owner);
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		if (isModeEnable()) {
			EntityAITasks[] ltasks = new EntityAITasks[2];
			ltasks[0] = pDefaultMove;
			ltasks[1] = pDefaultTargeting;
			owner.addMaidMode(ltasks, "SugarHunter", mmode_SugarHunter);
		}
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		if (isModeEnable()) {
			ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
			if (litemstack != null) {
				if (litemstack.itemID == Item.reed.itemID) {
					owner.setMaidMode("SugarHunter");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			return super.checkBlock(pMode, px, py, pz);
		}
		// サトウキビの探索
		World w = owner.worldObj;
		int blockId = w.getBlockId(px, py, pz);
		if (blockId == Block.reed.blockID) {
			if (w.getBlockId(px, py - 1, pz) == Block.reed.blockID) {
				speedUp = true;
				SugarBiomeCore.getInstance().debugPrint("find reed %d, %d, %d", px, py, pz);
				return true;
			}
			// 伸びていなければ離れた場所に移動してみる
			if (rand.nextInt(800) == 0 && 5 * 5 < owner.getDistanceSq(px + 0.5, py + 0.5, pz + 0.5)) {
				SugarBiomeCore.getInstance().debugPrint("move far %d, %d, %d", px, py, pz);
				return true;
			}
		}
		// 植え付け可能場所の探索
		else if (blockId == Block.dirt.blockID || blockId == Block.grass.blockID || blockId == Block.sand.blockID) {
			Material mm = w.getBlockMaterial(px, py + 1, pz);
			if (mm == null || (mm.isReplaceable() && !mm.isLiquid())) {
				if (Block.reed.canPlaceBlockAt(w, px, py + 1, pz) && owner.maidInventory.hasItem(Item.reed.itemID)) {
					speedUp = 2 * 2 < owner.getDistanceSq(px + 0.5, py + 0.5, pz + 0.5);
					SugarBiomeCore.getInstance().debugPrint("find point %d, %d, %d", px, py, pz);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return true; // インベントリに空きがあればアイテムは拾いに行く。けど AICollectItem 内でも判定しているみたい
	}

	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			boolean result = super.executeBlock(pMode, px, py, pz);
			if (!result) {
				modeSearchChest = false;
			}
			return result;
		}
		World w = owner.worldObj;
		int blockId = w.getBlockId(px, py, pz);

		// さとうきびの刈り取り
		if (blockId == Block.reed.blockID && w.getBlockId(px, py - 1, pz) == Block.reed.blockID) {
			owner.setSwing(10, LMM_EnumSound.Null);
			Block.blocksList[blockId].dropBlockAsItem(w, px, py, pz, 0, 0);
			w.setBlockToAir(px, py, pz);
			w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Block.reed.stepSound.getPlaceSound(), (Block.reed.stepSound.getVolume() + 1.0F) / 2.0F,
					Block.reed.stepSound.getPitch() * 0.8F);
			coolTime = 10;
			speedUp = false;
		}
		// 植え付け
		else if (blockId == Block.dirt.blockID || blockId == Block.grass.blockID || blockId == Block.sand.blockID) {
			Material mm = w.getBlockMaterial(px, py + 1, pz);
			if (mm == null || mm.isReplaceable()) {
				if (Block.reed.canPlaceBlockAt(w, px, py + 1, pz) && owner.maidInventory.consumeInventoryItem(Item.reed.itemID)) {
					w.setBlock(px, py + 1, pz, Block.reed.blockID);
					owner.setSwing(10, LMM_EnumSound.Null);
					w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Block.reed.stepSound.getPlaceSound(), (Block.reed.stepSound.getVolume() + 1.0F) / 2.0F,
							Block.reed.stepSound.getPitch() * 0.8F);
				}
			}
		}
		return false;
	}

	@Override
	public int getNextEquipItem(int pMode) {
		if (pMode == mmode_SugarHunter) {
			for (int i = 0; i < owner.maidInventory.maxInventorySize; i++) {
				ItemStack item = owner.maidInventory.getStackInSlot(i);
				if (item != null && item.itemID == Item.reed.itemID) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void init() {
		if (isModeEnable()) {
			// 登録モードの名称追加
			ModLoader.addLocalization("littleMaidMob.mode.SugarHunter", "SugarHunter");
			ModLoader.addLocalization("littleMaidMob.mode.SugarHunter", "ja_JP", "シュガーハンター");
			ModLoader.addLocalization("littleMaidMob.mode.F-SugarHunter", "F-SugarHunter");
			ModLoader.addLocalization("littleMaidMob.mode.T-SugarHunter", "T-SugarHunter");
			ModLoader.addLocalization("littleMaidMob.mode.D-SugarHunter", "D-SugarHunter");
		}
	}

	@Override
	public boolean isSearchBlock() {
		if (owner.maidInventory.getFirstEmptyStack() == -1) {
			modeSearchChest = true; // アイテムいっぱいならチェスト探索モード
			return !super.shouldBlock(mmode_Escorter); // ほんとは mytile == null したいけど代用として
		}
		if (0 < coolTime) {
			return false; // クールタイム中は立ち止まる
		}
		return true; // サトウキビ探索
	}

	@Override
	public void onUpdate(int pMode) {
		super.onUpdate(pMode);
		if (0 < coolTime) {
			coolTime--;
		}
	}

	@Override
	public boolean outrangeBlock(int pMode, int pX, int pY, int pZ) {
		if (modeSearchChest) {
			return super.outrangeBlock(pMode, pX, pY, pZ);
		}
		boolean result = false;
		if (!owner.isMaidWaitEx()) {
			double distance = owner.getDistanceSq(pX + 0.5, pY + 0.5, pZ + 0.5);
			if (distance == lastdistance) {
				owner.updateWanderPath();
				SugarBiomeCore.getInstance().debugPrint("updateWanderPath(%s)", ++moveRetryCount);
				result = moveRetryCount < 40; // 40回までリトライ可能
			}
			else {
				result = owner.getNavigator().tryMoveToXYZ(pX, pY, pZ, speedUp ? 0.3F : 0.23F);
			}
			lastdistance = distance;
		}
		return result;
	}

	@Override
	public int priority() {
		return 5999;	// IC2だとサトウキビが燃えるようになるので、LMM_EntityMode_Cooking より優先度を上げる
	}

	@Override
	public void resetBlock(int pMode) {
		super.resetBlock(pMode);
		speedUp = false;
		moveRetryCount = 0;
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
			case mmode_SugarHunter:
				owner.setBloodsuck(false);
				owner.aiWander.setEnable(true);
				owner.aiJumpTo.setEnable(false);
				owner.aiFollow.setEnable(false);
				owner.aiAvoidPlayer.setEnable(false);
				return true;
		}
		return false;
	}

	@Override
	public boolean shouldBlock(int pMode) {
		if (modeSearchChest) {
			return super.shouldBlock(pMode);
		}
		return false;
	}

	private static boolean isModeEnable() {
		return SugarBiomeCore.getInstance().isRunning();
	}
}
