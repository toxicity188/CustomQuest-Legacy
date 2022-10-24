package kor.toxicity.quest.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import kor.toxicity.quest.tools.mechanics.QuestSet;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.TextUtil;

public class NavGui extends Tools implements Nav {

	Player player;
	
	Inventory inv;
	Map<String,Location> loc;
	List<String> list = new ArrayList<>();
	
	public NavGui(Player player, QuestSet quest) {
		this.player = player;

		loc = quest.getLocationList();
		
		inv = InvUtil.getInventory(super.prefix + getGeneral("navgui-name"), (loc.size()/9 > 4) ? 6 : loc.size()/9+3);
		int loop = 0;
		for (String s : loc.keySet()) {
			inv.setItem(9 + loop, InvUtil.createItem(Material.BEACON, super.prefix + TextUtil.colored(s), new String[] {"",getGeneral("navgui-prefix") + TextUtil.LocationName(loc.get(s))}));
			list.add(s);
			loop ++;
		}
		player.openInventory(inv);
		registerEvents();
	}
	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.BEACON)) {
				NavStart(player,loc.get(list.get(e.getSlot()-9)),list.get(e.getSlot()-9)); 
			}
		}
	}
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
	private void End() {
		list.clear();
		unregisterEvents();
	}
}
