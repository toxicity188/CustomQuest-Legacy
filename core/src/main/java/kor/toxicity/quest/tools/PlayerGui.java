package kor.toxicity.quest.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import kor.toxicity.quest.tools.mechanics.QuestSet;
import kor.toxicity.quest.util.InvUtil;

public class PlayerGui extends Tools implements Nav {

	Map<Integer,QuestSet> itemquest;
	
	String compass;
	
	Player player;
	Inventory inv;
	
	List<Boolean> swit = new ArrayList<>();
	List<String> locnum = new ArrayList<>();
	
	@EventHandler
	public void InvClose(InventoryCloseEvent e) {
		if (e.getPlayer().equals(player) && e.getInventory().equals(inv)) {
			End();
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer().equals(player)) {
			End();
		}
	}
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity().equals(player)) {
			End();
		}
	}
	
	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
			e.setCancelled(true);
			int slot = e.getSlot();
			if (itemquest.containsKey(slot-9)) {
				if (e.getClick().isLeftClick() && !e.getClick().isShiftClick()) {
					locnum.clear();
					for (String s : itemquest.get(slot-9).getLocationList().keySet()) {
						locnum.add(s);
					}
					switch (locnum.size()) {
					case 0: 
						player.sendMessage(super.prefix + getGeneral("playergui-navigation-fail")); 
						break;
					case 1: 
						NavStart(player, itemquest.get(slot-9).getLocationList().get(locnum.get(0)),locnum.get(0)); 
						break;
					default:
						new NavGui(player, itemquest.get(slot-9));
						break;
					}
				}
				if (e.getClick().isRightClick() && !e.getClick().isShiftClick()) {
					NavEnd(player);
				}
				if (e.getClick().isLeftClick() && e.getClick().isShiftClick()) {
					swit.set(slot-9, !swit.get(slot-9));
					inv.setItem(slot, itemquest.get(slot-9).getItem(player,swit.get(slot-9)));
				}
				if (e.getClick().isRightClick() && e.getClick().isShiftClick()) {
					if (itemquest.get(slot-9).isCancellable()) {
						itemquest.get(slot-9).surrender(player);
						player.closeInventory();
					} else {
						player.sendMessage(super.prefix + getGeneral("playergui-surrender-fail"));
					}
				}
			}
		}
	}
	private void End() {
		swit.clear();
		locnum.clear();
		itemquest.clear();
		unregisterEvents();
	}
	
	public PlayerGui(Player player) {
		
		inv = InvUtil.getInventory(super.prefix + getGeneral("playergui-name"), 3);
		itemquest = new WeakHashMap<>();
		
		this.player = player;
		
		int loop = 0;
		
		for (int i =0; i < pl.getPlayerQuest(player).size(); i++) {
			String t = pl.getPlayerQuest(player).get(i);
			if (pl.getGlobalQuest(t) != null) {
				QuestSet q = pl.getGlobalQuest(t);
				swit.add(loop, false);
				itemquest.put(loop, q);
				inv.setItem(loop + 9, q.getItem(player, false));
				loop ++;
			}
		}
		
		player.openInventory(inv);
		registerEvents();
	}
	
}
