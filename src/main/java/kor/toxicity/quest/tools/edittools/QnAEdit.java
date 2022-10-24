package kor.toxicity.quest.tools.edittools;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kor.toxicity.quest.util.InvUtil;

public class QnAEdit extends EditTools {

	Inventory inv;
	String filename, mode;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);
		filename = file;
		
		inv = getInv("&b&l&o" + type + ": &f" + key, 4);
		reload();
		
		player.openInventory(inv);
		registerEvents();
	}
	
	private void reload() {
		mode = "global";
		inv.clear();
		IntStream.range(0,9).forEach((int q) -> {
			inv.setItem(27 + q, InvUtil.createItem(Material.STAINED_GLASS_PANE, "§f", null));
		});
		inv.setItem(11, InvUtil.createItem(Material.CHEST, prefix + "§fGui", getItemStack("Gui")));
		inv.setItem(15, InvUtil.createItem(Material.PAINTING, prefix + "§fLink(Dialog)", new String[] {"","§7(Click - Link Dialog)"}));
	}
	private void LinkDialog() {
		mode = "link";
		inv.clear();
		for (int q = 0; q < 8; q++) {
			inv.setItem(27 + q, InvUtil.createItem(Material.STAINED_GLASS_PANE, "§f", null));
		}
		inv.setItem(35, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Back", null));
		inv.setItem(13, InvUtil.createItem(Material.ENCHANTED_BOOK, prefix + "Center", null));
		if (config.isSet(key + ".Gui")) {
			for (String s : config.getConfigurationSection(key + ".Gui").getKeys(false)) {
				ItemStack item = (ItemStack) config.get(key + ".Gui." + s);
				ItemMeta meta = item.getItemMeta();
				meta.setLore(Arrays.asList(new String[] {"","§7(Click - try link Dialog in this item)"}));
				item.setItemMeta(meta);
				inv.setItem(Integer.parseInt(s)-1, item);
			}
		}
	}
	
	@EventHandler
	public void InvClose(InventoryCloseEvent e) {
		if (e.getPlayer().equals(player) && e.getInventory().equals(inv)) {
			unregisterEvents();
		}
	}

	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
			e.setCancelled(true);
			switch(mode) {
			case "global":
				if (e.getSlot() == 11) {itemEdit("Gui",false,4); return;}
				if (e.getSlot() == 15) {LinkDialog(); return;}
				break;
			case "link":
				if (e.getSlot() == 13) return;
				if (e.getSlot() < 27) {ChatEdit("LinkedDialog." + Integer.toString(e.getSlot() + 1),"string"); return;};
				if (e.getSlot() == 35) {reload(); return;}
				break;
			}
		}
	}
}
