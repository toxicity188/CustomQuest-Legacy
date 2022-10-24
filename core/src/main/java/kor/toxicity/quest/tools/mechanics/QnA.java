package kor.toxicity.quest.tools.mechanics;

import kor.toxicity.quest.QuestConfig;
import kor.toxicity.quest.tools.Tools;
import kor.toxicity.quest.util.InvUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class QnA extends Tools {

	Player player;
	String sender;
	String sendersound;
	
	Map<String,ItemStack> item;
	Map<String,String> LinkedDialog;
	
	Inventory inv;

	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
				String t = Integer.toString(e.getSlot() + 1);
				if (item.containsKey(t)) {
					try {
						pl.getGlobalDialog(LinkedDialog.get(t)).start(player, sender, sendersound);
					} catch(Exception h) {player.closeInventory();}
					clear();
				}
			}
		}
	}
	@EventHandler
	public void InvClick(InventoryCloseEvent e) {
		if (e.getInventory().equals(inv)) {
			clear();
		}
	}
	
	private void clear() {
		item.clear();
		LinkedDialog.clear();
		unregisterEvents();
	}
	
	public QnA(Player player, String key, String sender, String sendersound, Inventory getInv) {
		this.player = player;
		this.sender = sender;
		this.sendersound = sendersound;

		item = new HashMap<>();
		LinkedDialog = new HashMap<>();
		
		QuestConfig config = pl.getConfig("qna");
		for(String cal : config.getKeys("QnA." + key + ".Gui")) {
			item.put(cal, (ItemStack) config.get("QnA." + key + ".Gui." + cal));
		}
		for(String cal : config.getKeys("QnA." + key + ".LinkedDialog")) {
			LinkedDialog.put(cal, config.getString("QnA." + key + ".LinkedDialog." + cal,"Error"));
		}
		
		inv = InvUtil.getInventory(prefix +getGeneral("qna-inventory-name").replaceAll("<sender>", sender), 3);
		
		registerEvents();
		try {
			inv.setItem(13, getInv.getItem(22));
		} catch(Exception e) {}
		for(String i : item.keySet()) {
			try {
				inv.setItem(Integer.parseInt(i) - 1,item.get(i));
			} catch(Exception e) {}
		}
		
		player.openInventory(inv);
	}
	
}
