package kor.toxicity.quest.tools.edittools;

import java.util.Set;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import kor.toxicity.quest.util.InvUtil;

public class ItemEditModule extends EditTools {

	Inventory inv;
	Class<? extends EditTools> ret;
	String itemkey;
	
	int guinum = 6;
	boolean sort = true;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);

		this.inv = getInv("Put your item in here!", guinum);
		IntStream.range(0,7).forEach((int i) -> {
			inv.setItem((guinum-1)*9+1 + i, InvUtil.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§f", null));
		});
		if (itemkey != null && config.isSet(key + "." + itemkey)) {
			Set<String> keyset = config.getConfigurationSection(key + "." + itemkey).getKeys(false);
			int loop = 0;
			for (String s : keyset) {
				inv.setItem((sort) ? loop : Integer.parseInt(s)-1, (ItemStack) config.get(key + "." + itemkey + "." + s));
				config.set(key + "." + itemkey + "." + s, null);
				loop ++;
			}
		}
		if (!sort) inv.setItem(center(), InvUtil.createItem(Material.ENCHANTED_BOOK, prefix + "Center", null));
		inv.setItem((guinum -1)*9, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Back without Save", null));
		inv.setItem(guinum * 9 - 1, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Save and Back", null));
		player.openInventory(inv);
		
		registerEvents();
	}
	public void setReturn(Class<? extends EditTools> ret) {
		this.ret = ret;
	}
	public void setItemKey(String key, boolean t, int guinum) {
		this.itemkey = key;
		this.guinum = guinum;
		this.sort = t;
	}

	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
			if (e.getSlot() >= (guinum-1)*9) e.setCancelled(true);
			if (e.getSlot() == center() && !sort) {e.setCancelled(true); return;}
			if (e.getSlot() == (guinum-1)*9) {
				Back();
				return;
			}
			if (e.getSlot() == guinum*9-1) {
				int loop = 0;
				for(int i = 0; i < (guinum-1)*9; i ++) {
					if (i == center() && !sort) continue;
					int thiskey = (sort) ? loop : i + 1;
					if (inv.getItem(i) != null) {loop ++; config.set(key + "." + itemkey + "." + thiskey, inv.getItem(i));}
				}
				configSave();
				Back();
				return;
			}
		}
	}
	int center() {
		if (Math.floorMod(guinum, 2) == 0) {
			return (guinum -2) * 9 /2 + 4;
		} else {return (guinum -1) * 9 /2 + 4;}
	}
	void Back() {
		unregisterEvents();
		if (ret != null && ret != ItemEditModule.class) {
			EditTools edit;
			try {
				edit = ret.getDeclaredConstructor().newInstance();
				edit.load(player, getType(), getFileName(), key);
			} catch (Exception e2) {}
		}
	}
	
	@EventHandler
	public void InvClose(InventoryCloseEvent e) {
		if (e.getPlayer().equals(player) && e.getInventory().equals(inv)) {
			unregisterEvents();
		}
	}
}
