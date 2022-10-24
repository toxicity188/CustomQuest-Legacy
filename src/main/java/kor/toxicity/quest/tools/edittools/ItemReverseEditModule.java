package kor.toxicity.quest.tools.edittools;

import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import kor.toxicity.quest.util.InvUtil;

public class ItemReverseEditModule extends ItemEditModule {

	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);

		this.inv = getInv("Put your item in here!", guinum);
		IntStream.range(0,7).forEach((int i) -> {
			inv.setItem((guinum-1)*9+1 + i, InvUtil.createItem(Material.STAINED_GLASS_PANE, "§f", null));
		});
		if (itemkey != null && config.isSet(key + "." + itemkey)) {
			for (String s : config.getConfigurationSection(key + "." + itemkey).getKeys(false)) {
				if (config.isSet(key + "." + itemkey + "." + s + ".Item")) {
					inv.setItem(Integer.parseInt(s)-1, (ItemStack) config.get(key + "." + itemkey + "." + s + ".Item"));
					config.set(key + "." + itemkey + "." + s + ".Item", null);
				}
			}
		}
		inv.setItem((guinum -1)*9, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Back without Save", null));
		inv.setItem(guinum * 9 - 1, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Save and Back", null));
		player.openInventory(inv);
		
		registerEvents();
	}
	
	@Override
	void Save() {
		int loop = 0;
		for(int i = 0; i < (guinum-1)*9; i ++) {
			int thiskey = (sort) ? loop : i + 1;
			String executekey = key + "." + itemkey + "." + thiskey;
			if (inv.getItem(i) != null) {loop ++; config.set(executekey + ".Item", inv.getItem(i));}
			if (config.isSet(executekey) && config.getConfigurationSection(executekey).getKeys(false).size() == 0) config.set(executekey, null);
		}
		if (config.isSet(key + "." + itemkey) && config.getConfigurationSection(key + "." + itemkey).getKeys(false).size() == 0) config.set(key + "." + itemkey, null);
		configSave();
		Back();
	}
	
}
