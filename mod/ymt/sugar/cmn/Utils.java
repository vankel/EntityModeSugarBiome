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
package mod.ymt.sugar.cmn;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Yamato
 *
 */
public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	private Utils() {
		;
	}
	
	@Deprecated
	public static void addLocalization(String key, String en_name, String ja_name) {
		LanguageRegistry.instance().injectLanguage("en_US", toHashMap(key, en_name));
		LanguageRegistry.instance().injectLanguage("ja_JP", toHashMap(key, ja_name));
	}
	
	@Deprecated
	public static void addName(Object obj, String en_name, String ja_name) {
		addLocalization(toUnlocalizedName(obj), en_name, ja_name);
	}
	
	public static int compare(int x, int y) {
		if (x < y)
			return -1;
		if (x > y)
			return 1;
		return 0;
	}
	
	public static String ensureNamespaced(String name) {
		return name.indexOf(':') < 0 ? "minecraft:" + name : name;
	}
	
	public static Block getBlock(int blockId) {
		if (blockId != 0) {
			Block block = Block.getBlockById(blockId);
			if (block == Blocks.air) {
				return null;
			}
			if (block != null) {
				return block;
			}
			log.fine(String.format("Tool#getBlock - UnknownBlockID[%s]", blockId));
		}
		return null;
	}
	
	public static int getUnusedEntityID() {
		int result = -1;
		if (isForge()) {
			try {
				Class entityRegistry = Class.forName("cpw.mods.fml.common.registry.EntityRegistry");
				result = (Integer) entityRegistry.getMethod("findGlobalUniqueEntityId").invoke(null);
			}
			catch (Exception e) {
				throw new RuntimeException("EntityID cant generated", e);
			}
		}
		else {
			for (int i = 1; i <= Byte.MAX_VALUE; i++) {
				if (EntityList.getClassFromID(i) == null) {
					result = i;
					break;
				}
			}
		}
		if (result < 0) {
			throw new RuntimeException("EntityID cant generated");
		}
		log.fine(String.format("Tool#getUnusedEntityID - NewEntityID[%s]", result));
		return result;
	}
	
	public static boolean hasString(String text) {
		return text != null && 0 < text.trim().length();
	}
	
	public static boolean isClientSide(World world) {
		return world.isRemote;
	}
	
	public static boolean isForge() {
		return true; // Loader.isModLoaded("Forge");
	}
	
	public static boolean isServerSide(World world) {
		return !world.isRemote;
	}
	
	@Deprecated
	public static boolean isValidTileEntity(TileEntity ent) {
		TileEntity ent2 = ent.getWorldObj().getTileEntity(ent.xCoord, ent.yCoord, ent.zCoord);
		if (ent2 != null && ent != ent2) { // 頻繁に迷い込んでくるので、もしやってきた場合は容赦なく invalidate
			ent.invalidate();
			log.fine(String.format("Tool#isValidTileEntity - InvalidateTileEntity[%s] %d, %d, %d", ent, ent.xCoord, ent.yCoord, ent.zCoord));
		}
		return !ent.isInvalid();
	}
	
	public static void replaceBlockRegistry(String name, Block oldBlock, Block newBlock) {
		// blockRegistry 置換
		try {
			for (ObjectIntIdentityMap map: Reflection.getFieldValues(RegistryNamespaced.class, Block.blockRegistry, ObjectIntIdentityMap.class)) {
				map.func_148746_a(newBlock, Block.getIdFromBlock(oldBlock));
			}
			Block.blockRegistry.putObject(ensureNamespaced(name), newBlock);
			Reflection.replaceFieldValues(Blocks.class, null, oldBlock, newBlock);
		}
		catch (Exception e) {
			log.severe(e.toString());
		}
		
	}
	
	public static void replaceItemRegistry(String name, Item oldItem, Item newItem) {
		// itemRegistry 置換
		try {
			for (ObjectIntIdentityMap map: Reflection.getFieldValues(RegistryNamespaced.class, Item.itemRegistry, ObjectIntIdentityMap.class)) {
				map.func_148746_a(newItem, Item.getIdFromItem(oldItem));
			}
			Item.itemRegistry.putObject(ensureNamespaced(name), newItem);
			Reflection.replaceFieldValues(Items.class, null, oldItem, newItem);
		}
		catch (Exception e) {
			log.severe(e.toString());
		}
	}
	
	public static void showMessage(EntityPlayer player, String msg) {
		if (player != null && msg != null) {
			msg = msg.trim();
			if (0 < msg.length()) {
				player.addChatMessage(new ChatComponentText(msg));
			}
		}
	}
	
	public static void showMessage(World world, String msg) {
		for (Object ent: world.loadedEntityList) {
			if (ent instanceof EntityPlayer) {
				showMessage((EntityPlayer) ent, msg);
			}
		}
	}
	
	public static void spawnExplodeParticle(World world, double x, double y, double z) {
		Random rand = world.rand;
		for (int j = 0; j < 20; j++) {
			double d = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			world.spawnParticle("explode", x + rand.nextFloat() * 2.0 - 1.0, y + rand.nextFloat(), z + rand.nextFloat() * 2.0 - 1.0, d, d1, d2);
		}
	}
	
	public static void spawnPortalParticle(World world, double x, double y, double z) {
		Random rand = world.rand;
		for (int i = 0; i < 3; i++) {
			double px = x + (rand.nextDouble() - 0.5D);
			double py = y + rand.nextDouble();
			double pz = z + (rand.nextDouble() - 0.5D);
			double velx = (rand.nextDouble() - 0.5D) * 2.0D;
			double vely = -rand.nextDouble();
			double velz = (rand.nextDouble() - 0.5D) * 2.0D;
			world.spawnParticle("portal", px, py, pz, velx, vely, velz);
		}
	}
	
	public static boolean tryUseItems(EntityPlayer player, Item item, boolean consumed) {
		return tryUseItems(player, item, -1, consumed);
	}
	
	public static boolean tryUseItems(EntityPlayer player, Item item, int damage, boolean consumed) {
		ItemStack curItem = player.getCurrentEquippedItem();
		if (curItem != null && curItem.getItem() == item && (damage < 0 || curItem.getItemDamage() == damage)) {
			if (consumed && !player.capabilities.isCreativeMode) {
				curItem.stackSize--;
				if (curItem.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
			}
			return true;
		}
		return false;
	}
	
	private static HashMap<String, String> toHashMap(String key, String value) {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(key, value);
		return result;
	}
	
	private static String toUnlocalizedName(Object obj) {
		String result;
		if (obj instanceof Item) {
			result = ((Item) obj).getUnlocalizedName();
		}
		else if (obj instanceof Block) {
			result = ((Block) obj).getUnlocalizedName();
		}
		else if (obj instanceof ItemStack) {
			result = ((ItemStack) obj).getItem().getUnlocalizedName((ItemStack) obj);
		}
		else {
			throw new IllegalArgumentException(String.format("Illegal object for naming %s", obj));
		}
		result += ".name";
		return result;
	}
}
