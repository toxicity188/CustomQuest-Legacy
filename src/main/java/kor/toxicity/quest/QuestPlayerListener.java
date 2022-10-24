package kor.toxicity.quest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import kor.toxicity.quest.events.DialogCompleteEvent;
import kor.toxicity.quest.events.DialogFailEvent;
import kor.toxicity.quest.events.DialogStartEvent;
import kor.toxicity.quest.tools.Nav;
import kor.toxicity.quest.tools.mechanics.Dialog;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.TextUtil;

public class QuestPlayerListener implements Listener, Nav {
	
	QuestConfig npclist;
	QuestConfig basic;
	Quest pl;
	
	List<Player> ActiveCd;
	List<Player> DialogActive;
	Map<Player,AutoSave> autosave;
	
	public QuestPlayerListener(Quest plugin) {
		this.pl = plugin;
		this.basic = Quest.basic;
		
		this.ActiveCd = new ArrayList<>();
		this.DialogActive = new ArrayList<>();
		this.autosave = new HashMap<>();
	}
	
	public void reload() {
		ActiveCd.clear();
		this.npclist = pl.getConfig("npc");
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (e.isCancelled() || !ActCooldown(e.getPlayer()) || DialogActive.contains(e.getPlayer()) ) return;
		Player player = e.getPlayer();
		Entity entity = e.getRightClicked();
		String name = (entity.getName() == null) ? entity.getType().toString() : TextUtil.uncolored(entity.getName());
		String npcvar = name.replaceAll(" ","\\_");
		List<String> dialog = npclist.getStringList("NPC." + npcvar + ".Dialog");
		if (dialog != null) {
			try {
				e.setCancelled(true);
				String dialogvar = npclist.getString("NPC."+npcvar+".DialogVar", null);
				int dialognum =  (dialogvar != null) ? pl.getVars(player).getValue(dialogvar) : 0;
				Dialog dir = pl.getGlobalDialog(dialog.get(dialognum));
				dir.start(player, name, npclist.getString("NPC."+npcvar+".TypingSound", null));
				
			} catch(Exception ignored) {}
		}
	}
	@EventHandler
	public void DialogStart(DialogStartEvent e) {
		if (!DialogActive.contains(e.getPlayer())) DialogActive.add(e.getPlayer());
		
	}
	@EventHandler
	public void DialogComplete(DialogCompleteEvent e) {
		if (DialogActive.contains(e.getPlayer())) DialogActive.remove(e.getPlayer());
		
	}
	@EventHandler
	public void DialogFail(DialogFailEvent e) {
		if (DialogActive.contains(e.getPlayer())) DialogActive.remove(e.getPlayer());
		
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws FileNotFoundException, IOException, InvalidConfigurationException {
		pl.userLoad(e.getPlayer());
		int i = basic.getInt("config.autosave", 300);
		if (i > 0) autosave.put(e.getPlayer(), new AutoSave(e.getPlayer(),i, basic.getString("config.autosave-message", "&e&l[Quest] &fData Saved.").replaceAll("&","§")));
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Player p = e.getPlayer();
		if (autosave.containsKey(p)) autosave.get(p).stop();
		pl.userSave(p, true);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null 
			&& e.getClickedInventory().equals(player.getOpenInventory().getBottomInventory()) 
			&& e.getSlot() == 40
			&& e.getCurrentItem() != null 
			&& e.getCurrentItem().equals(compass)) {
			e.setCancelled(true);
			InvUtil.take(player, compass);
			NavEnd(player);
		}
	}
	@EventHandler
	public void onItemSwap(PlayerSwapHandItemsEvent e) {
		if (e.getMainHandItem() != null && e.getMainHandItem().equals(compass)) {
			e.setCancelled(true);
		}
	}
	
	private boolean ActCooldown(Player uuid) {
		if (ActiveCd.contains(uuid)) {
			return false;
		} else {
			int i = basic.getInt("config.interact-delay", 30);
			if (i > 0) {
				ActiveCd.add(uuid);
				Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> {if (ActiveCd.contains(uuid)) ActiveCd.remove(uuid);}, i);
			}
			return true;
		}
	}
	private class AutoSave implements Runnable {
		Player player;
		int taskId;
		String msg;
		
		AutoSave(Player player, int cooldown, String msg) {
			this.player = player;
			this.msg = msg;
			
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, this ,cooldown * 20, cooldown * 20);
		}
		@Override
		public void run() {
			if (autosave.containsKey(player) && player.isOnline()) {
				pl.userSave(player,false);
				if (!msg.equals("")) player.sendMessage(msg);
			} else {return;}
		}
		
		public void stop() {
			Bukkit.getScheduler().cancelTask(taskId); 
			autosave.remove(player); 
			return;
		}
	}
}
