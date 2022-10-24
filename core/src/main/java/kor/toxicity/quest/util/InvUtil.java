package kor.toxicity.quest.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvUtil {
	
	
	public static Inventory getInventory(String name, int rows) {
		return Bukkit.createInventory(null, rows * 9, name);
	}
	public static int getItemAmountInPlayerInv(Player player,ItemStack item) {
		int ret = 0;
		List<ItemStack> items = Arrays.asList(player.getInventory().getContents());
		for(int i = 0; i < items.size(); i++) {
			if (items.get(i) != null) {
				if (items.get(i).clone().isSimilar(item.clone())) ret += items.get(i).getAmount();
			}
		}
		return ret;
	}
	
	public static ItemStack ShinyItem(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 4, true);
		meta.addItemFlags(ItemFlag.values());
		item.setItemMeta(meta);
		return item;
	}
	
	public static void give(Player player, ItemStack item) {
		player.getInventory().addItem(item);
	}
	public static void take(Player player, ItemStack item) {
		List<ItemStack> items = Arrays.asList(player.getInventory().getContents());
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) != null && items.get(i).isSimilar(item)) {
				if (items.get(i).getAmount() > item.getAmount()) {
					items.get(i).setAmount(items.get(i).getAmount() - item.getAmount());
				} else {
					player.getInventory().remove(item);
				}
				return;
			}
		}
	}
	public static void giveAll(Player player, List<ItemStack> item) {
		IntStream.range(0,item.size()).forEach(i -> {give(player,item.get(i));});
	}
	public static void takeAll(Player player, List<ItemStack> item) {
		IntStream.range(0,item.size()).forEach(i -> {take(player,item.get(i));});
	}
    public static ItemStack createItem(Material material, String name, String[] lore) {
    	return createItem(material, name, lore, 1, (short) 0, false, false);
    }
    public static ItemStack createItem(Material material, String name, String[] lore, int damage) {
    	return createItem(material, name, lore, 1, damage, false, false);
    }
    public static ItemStack createItem(Material material, String name, String[] lore, int amount, int damage, boolean unbreakable, boolean flaghidden) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setCustomModelData(damage);
        meta.setDisplayName(name);

        // Set the lore of the item
        if (lore != null) meta.setLore(Arrays.asList(lore));
        
        meta.setUnbreakable(unbreakable);
        if (flaghidden) meta.addItemFlags(ItemFlag.values());
        
        item.setItemMeta(meta);
        
        
        return item;
    }
}
