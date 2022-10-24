package kor.toxicity.quest.tools.edittools;

import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import kor.toxicity.quest.util.InvUtil;

public class ShopEdit extends EditTools {

	Inventory inv;
	String status, key;
	
	int line;
	int page = 1;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);

		this.key = key;
		this.status = "global";
		this.player = player;

		reloadAll();
	}
	private void reloadAll() {
		line = config.getInt(key + ".line", 1);

		this.inv = getInv("&b&l&o" + type + ": &f" + key, line + 1);
		
		reload();
		
		player.openInventory(inv);
		registerEvents();
	}
	private void reload() {
		inv.clear();
		IntStream.range(0,2).forEach(t -> {
		IntStream.range(2 + 3*t,4 + 3*t).forEach(i -> {
		inv.setItem(line*9 + i, InvUtil.createItem(Material.STAINED_GLASS_PANE, "§f", null));});
		});
		inv.setItem(line*9 + 4, InvUtil.createItem(Material.ENCHANTED_BOOK, prefix + "§fEdit GUI line", new String[] {"","§7(Left - Add line, Right - Subtract line)"}));
		inv.setItem(line*9, InvUtil.createItem(Material.PAPER, prefix + "§fAdd Item", new String[] {"","§7(Click - Add Item)"}));
		
		if (config.isSet(key + ".Page." + page)) {
			for (String s : config.getConfigurationSection(key + ".Page." + page).getKeys(false)) {
				inv.setItem(Integer.parseInt(s)-1, config.getItemStack(key + ".Page." + page + "." + s + ".Item"));
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
			switch (status) {
			case "global": {
				switch (e.getSlot() - line*9) {
				case 0:
					itemEdit("Page." + Integer.toString(page), false, line + 1, true);
				case 4:
					if (e.isLeftClick()) setLine(1);
					if (e.isRightClick()) setLine(-1);
					break;
				}
			}
			break;
			}
		}
	}
	private void setLine(int i) {
		if ((i < 0 && line + i < 1) || (i > 0 && line + i > 5)) return;
		line += i;
		config.set(key + ".line", line);
		configSave();
		reloadAll();
	}
}
