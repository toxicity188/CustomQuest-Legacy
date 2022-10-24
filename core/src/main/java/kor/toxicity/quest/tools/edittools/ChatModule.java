package kor.toxicity.quest.tools.edittools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatModule extends EditTools {

	Class<? extends EditTools> ret;
	String itemkey;
	String chatkey;
	String chatmode;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);
		this.chatkey = key;
		player.closeInventory();
		player.sendMessage(prefix + "§fChat the message you want to set.");
		player.sendMessage(prefix + "§fChat 'back' to stop this task and back.");
		registerEvents();
	}
	
	public void setReturn(Class<? extends EditTools> ret) {
		this.ret = ret;
	}
	public void setChatkey(String key) {
		this.itemkey = key;
	}
	public void setChatMode(String mode) {
		this.chatmode = mode;
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer().equals(player)) {
			unregisterEvents();
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().equals(player)) {
			e.setCancelled(true);
			if (e.getMessage().equals("back")) {
				Back();
				player.sendMessage(prefix + "§fCancelled Successfully.");
			} else {
				switch(chatmode.toLowerCase()) {
				default: {chatmode = "list";}
				case "list":
					List<String> get = config.getStringList(chatkey + "." + itemkey);
					if (get == null) get = new ArrayList<>();
					get.add(e.getMessage().replaceAll("&", "§"));
					config.set(chatkey + "." + itemkey, get);
					break;
				case "listset":
					String[] converter = itemkey.split("\\.");
					List<String> get2 = config.getStringList(chatkey + "." + converter[0]);
					if (get2 == null) get2 = new ArrayList<>();
					get2.set(Integer.parseInt(converter[1]),e.getMessage().replaceAll("&", "§"));
					config.set(chatkey + "." + converter[0], get2);
					break;
				case "string":
					config.set(chatkey + "." + itemkey, e.getMessage().replaceAll("&", "§"));
					break;
				case "int":
					config.set(chatkey + "." + itemkey, Integer.parseInt(e.getMessage()));
					break;
				case "location":
					config.set(chatkey + "." + itemkey + "." + e.getMessage(), player.getLocation().clone());
					break;
				}
				player.sendMessage(prefix + "§fSaved Successfully.");
				configSave();
				Back();
			}
		}
	}
	
	void Back() {
		unregisterEvents();
		if (ret != null && ret != ChatModule.class) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(pl,() -> {
			try {
				EditTools edit = ret.getDeclaredConstructor().newInstance();
				edit.load(player, getType(), getFileName(), key);
			} catch (Exception e2) {}},5);
		}
	}
	
}
