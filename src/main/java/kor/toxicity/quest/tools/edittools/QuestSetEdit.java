package kor.toxicity.quest.tools.edittools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import kor.toxicity.quest.util.InvUtil;

public class QuestSetEdit extends EditTools {

	Inventory inv;
	String filename;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);
		this.inv = getInv("&b&l&o" + type + ": &f" + key, 5);
		reload();
		player.openInventory(inv);
		
		registerEvents();
	}
	
	private void reload() {

		inv.setItem(9, InvUtil.createItem(Material.WOOL, prefix + "§fTitle:", getString("Title")));
		inv.setItem(18, InvUtil.createItem(Material.WOOL, prefix + "§fLore:", getStringList("Lore")));
		inv.setItem(27, InvUtil.createItem(Material.WOOL, prefix + "§fRecommend:", getStringList("Recommend"), (short)1));
		inv.setItem(11, InvUtil.createItem(Material.WOOL, prefix + "§fCondition(Vars):", getStringList("ConditionVars"), (short)4));
		inv.setItem(12, InvUtil.createItem(Material.WOOL, prefix + "§fCondition(Items):", getItemStack("ConditionItem"), (short)4));
		inv.setItem(13, InvUtil.createItem(Material.WOOL, prefix + "§fCondition(Custom):", getStringList("ConditionCustom"), (short)4));
		inv.setItem(29, InvUtil.createItem(Material.WOOL, prefix + "§fRewards(Money):", getString("rewardsMoney"), (short)5));
		inv.setItem(30, InvUtil.createItem(Material.WOOL, prefix + "§fRewards(Exp):", getString("rewardsExp"), (short)5));
		inv.setItem(31, InvUtil.createItem(Material.WOOL, prefix + "§fRewards(Items):", getItemStack("rewardsItem"), (short)5));
		inv.setItem(24, InvUtil.createItem(Material.WOOL, prefix + "§fVars(Complete):", getStringList("CompleteVars"), (short)14));
		inv.setItem(25, InvUtil.createItem(Material.WOOL, prefix + "§fVars(Surrender):", getStringList("SurrenderVars"), (short)14));
		inv.setItem(26, InvUtil.createItem(Material.WOOL, prefix + "§fLocation:", getLocation("Location"), (short)3));
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
			if (e.isLeftClick()) {
				switch (e.getSlot()) {
				case 9: {ChatEdit("Title","string"); break;}
				case 18: {ChatEdit("Lore","list"); break;}
				case 27: {ChatEdit("Recommend","list"); break;}
				case 11: {ChatEdit("ConditionVars","list"); break;}
				case 12: {itemEdit("ConditionItem",true,6); break;}
				case 13: {ChatEdit("ConditionCustom","list"); break;}
				case 29: {ChatEdit("rewardsMoney","int"); break;}
				case 30: {ChatEdit("rewardsExp","int"); break;}
				case 31: {itemEdit("rewardsItem",true,6); break;}
				case 24: {ChatEdit("CompleteVars","list"); break;}
				case 25: {ChatEdit("SurrenderVars","list"); break;}
				case 26: {ChatEdit("Location","location"); break;}
				}
			}
			if (e.isRightClick()) {
				switch (e.getSlot()) {
				case 18: {delete("Lore"); break;}
				case 27: {delete("Recommend"); break;}
				case 11: {delete("ConditionVars"); break;}
				case 13: {delete("ConditionCustom"); break;}
				case 24: {delete("CompleteVars"); break;}
				case 25: {delete("SurrenderVars"); break;}
				}
			}
		}
	}
	private void delete(String s) {
		LastDelete(s);
		configSave();
		reload();
	}
}
