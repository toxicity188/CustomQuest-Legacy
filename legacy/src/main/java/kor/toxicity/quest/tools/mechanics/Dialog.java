package kor.toxicity.quest.tools.mechanics;

import java.util.*;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import kor.toxicity.quest.QuestConfig;
import kor.toxicity.quest.events.DialogCompleteEvent;
import kor.toxicity.quest.events.DialogFailEvent;
import kor.toxicity.quest.events.DialogStartEvent;
import kor.toxicity.quest.tools.Tools;
import kor.toxicity.quest.util.CmdUtil;
import kor.toxicity.quest.util.EvtUtil;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.TextUtil;

public class Dialog extends Tools {
	
	String key;
	
	Map<Player,DialogSender> dialogexecutor;
	
	QuestConfig config;
		
	Map<String,List<String>> DialogString;
	Map<String,Integer> DialogInt;
	Map<String,Map<Integer,String>> DialogStringline;
	
	
	public Dialog(String key) {
		super();
		
		this.key = key;
			
		this.dialogexecutor = new HashMap<>();
		
		this.DialogString = new HashMap<>();
		this.DialogStringline = new HashMap<>();
		this.DialogInt = new HashMap<>();
		
		ConfigLoad();
	}

	public void clear() {
		dialogexecutor.clear();
		DialogString.clear();
		DialogStringline.clear();
		DialogInt.clear();
	}
	
	private void ConfigLoad() {
		String dialogkey = "Dialog." + key;
		this.config = pl.getConfig("dialog");
		for (String s : new String[] {
				"Talk",
				
				"LinkedDialog",
				"LinkedCommand",
				"LinkedQnA",
				"LinkedSubDialog",
				"LinkedPresent",
					
				"ConditionVars",
				"SetVars",
					
				"SetQuest",
				"CheckQuest",
			}) {
				if (config.get(dialogkey + "." + s) != null) DialogString.put(s, config.getStringList(dialogkey + "." + s));
			}
		for (String s : new String[] {
				"Sound",
				"Talker",
				"Interface",
				"Command",
				"TypingSound",
			}) {
				if (config.get(dialogkey + "." + s) == null) continue;
				Map<Integer,String> stringlist = new HashMap<>();
				for(String t : config.getKeys(dialogkey + "." + s)) {
					stringlist.put(Integer.parseInt(t)-1,config.getString(dialogkey + "." + s + "." + t,""));
				}
				DialogStringline.put(s, stringlist);
			}
				
	}
	
	private DialogSender getDialog(Player player) {
		if (!dialogexecutor.containsKey(player)) return null;
		return dialogexecutor.get(player);
	}
	
	public boolean isfinished(Player player) {
		return !dialogexecutor.containsKey(player);
	}
	public void start(Player player, String sender) {
		start(player, sender, null, null);
	}
	public void start(Player player, String sender, String sendersound) {
		start(player, sender, sendersound, null);
	}
	public void start(Player player, String sender, String sendersound, Inventory inv) {
		
		if (DialogString.containsKey("ConditionVars")) {
			for(int i = 0; i < DialogString.get("ConditionVars").size(); i++) {
			if (!pl.getVars(player).compare(DialogString.get("ConditionVars").get(i))) {
				String action = pl.getVars(player).getAction();
				if (action != null && pl.getGlobalDialog(action) != null) {pl.getGlobalDialog(action).start(player, sender, sendersound, inv); return;}
				Fail(player, sender, sendersound, inv); 
				return;
				}
			}
		}
		if (DialogString.containsKey("CheckQuest")) {
			for(int i = 0; i < DialogString.get("CheckQuest").size(); i++) {
				String[] key = TextUtil.split(DialogString.get("CheckQuest").get(i));
				switch((key.length >= 2) ? key[1].toLowerCase() : "complete") {
				default: {return;}
				case "complete": {
					if (!pl.getGlobalQuest(key[0]).isCompleted(player)) {Fail(player, sender, sendersound, inv); return;}
					break;
				}
				case "has": {
					if(!pl.getPlayerQuest(player).contains(key[0])) {Fail(player, sender, sendersound, inv); return;}
					break;
				}
				case "hasnot": {
					if(pl.getPlayerQuest(player).contains(key[0])) {Fail(player, sender, sendersound, inv); return;}
					break;
				}
				}
			}
		}

		if (!DialogString.containsKey("Talk")) {Success_load(player, sender, sendersound); return;}
		
		EvtUtil.call(new DialogStartEvent(player,this));
		dialogexecutor.put(player, new DialogSender(player, sender, sendersound, inv) );
	}
	private void Stop(Player player) {
		dialogexecutor.remove(player);
	}
	private void Success(Player player) {
		Success_load(player, getDialog(player).getSender(), getDialog(player).getSenderSound());
		Stop(player);
	}
	
	private String RandomKey(List<String> map) {
		if (map.size() == 1) return map.get(0);
		return map.get(ThreadLocalRandom.current().nextInt(map.size()));
	}

	private void Fail(Player player, String sender, String sendersound, Inventory inv) {
		if (DialogString.containsKey("LinkedSubDialog")) {
			Dialog d = pl.getGlobalDialog(RandomKey(DialogString.get("LinkedSubDialog")));
			if (d != null) d.start(player, sender, sendersound, inv);
			return;
		}
	}
	private void Success_load(Player player, String sender, String sendersound) {

		String[] key;

		if (DialogString.containsKey("SetVars")) {
			DialogString.get("SetVars").forEach(s -> pl.getVars(player).setParameter(s));
		}	
		if (DialogString.containsKey("SetQuest")) {
			for (int i = 0; i < DialogString.get("SetQuest").size(); i ++) {
				key = TextUtil.split(DialogString.get("SetQuest").get(i));
				QuestSet quest = pl.getGlobalQuest(key[0]);
				if (quest == null) continue;
				switch ((key.length >= 2) ? key[1].toLowerCase() : "give") {
				default: {continue;}
				case "give": {
					quest.start(player);
					break;
				}
				case "surrender": {
					if (pl.getPlayerQuest(player).contains(key[0])) quest.surrender(player);
					break;
				}
				case "complete": {
					quest.complete(player);
					break;
				}
				}
			}
		}
		if (DialogString.containsKey("LinkedCommand")) {CmdUtil.cmdOp(player, RandomKey(DialogString.get("LinkedCommand")).replaceAll("<player>",player.getName()));}
		if (DialogString.containsKey("LinkedDialog")) {
			Dialog d = pl.getGlobalDialog(RandomKey(DialogString.get("LinkedDialog")));
			if (d != null) d.start(player, sender, sendersound, (getDialog(player) != null) ? getDialog(player).getInventory() : null);
			return;
			}
		if (DialogString.containsKey("LinkedQnA")) {
			new QnA(player,RandomKey(DialogString.get("LinkedQnA")),sender,sendersound,(getDialog(player) != null) ? getDialog(player).getInventory() : null); 
			return;
			}
		player.closeInventory();
		EvtUtil.call(new DialogCompleteEvent(player,this));
	}
	
	private class DialogSender implements Listener {

		Player player;
		String sender;
		String sendersound;
		String defsound;
		Inventory inv;
		
		String sendway;
		Map<String,String> typingsound;
		
		DialogExecutor executor;

		int reader = 0;
		int length = DialogString.get("Talk").size();
		String currentSender;
		
		long speed = def.getInt("config.dialog-tickspeed-default", 2);

		DialogSender(Player player, String sender, String sendersound, Inventory inv) {
			this.player = player;
			this.sender = sender;
			this.sendersound = sendersound;
			this.defsound = def.getString("config.default-typing-sound", "block.stone_button.click_on 0.2 0.7");
			
			this.sendway = "gui";
			
			typingsound =  new WeakHashMap<>();
			typingsound.put(player.getName(), defsound);
			if (sendersound != null) typingsound.put(sender, sendersound);

			Bukkit.getPluginManager().registerEvents(this,pl);
			if (inv == null) {
				this.inv = InvUtil.getInventory(prefix + getGeneral("dialog-inventory-name").replaceAll("<sender>", sender), 5);
				player.openInventory(this.inv);
			} else {
				this.inv = inv;
			}
			load();
			
		}
		private void Success() {
			Stop();
			Dialog.this.Success(player);
		}
		private void Stop() {
			HandlerList.unregisterAll(this);
			EvtUtil.call(new DialogFailEvent(player,Dialog.this));
			executor.kill();
		}
		
		@EventHandler
		public void onDeath(EntityDeathEvent e) {
			if (e.getEntity().equals(player)) {
				Stop();
				dialogexecutor.remove(player);
				cancelMessage("dialog-cancel-death");
			}
		}
		@EventHandler
		public void PlayerQuit(PlayerQuitEvent e) {
			if (e.getPlayer().equals(player)) {
				dialogexecutor.remove(player);
				Stop();
			}
		}
		@EventHandler
		public void InvClose(InventoryCloseEvent e) {
			if (e.getPlayer().equals(player) && e.getInventory().equals(inv) && sendway.equals("gui")) {
				Stop();
				dialogexecutor.remove(player);
				cancelMessage("dialog-cancel-invclose");
			}
		}
		@EventHandler
		public void cmdSend(PlayerCommandPreprocessEvent e) {
			if (e.getPlayer().equals(player)) {
				e.setCancelled(true);
				cancelMessage("dialog-block-command");
			}
		}
		@EventHandler
		public void InvClick(InventoryClickEvent e) {
			if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
				e.setCancelled(true);
				if (e.getCurrentItem() == null) return;
				if (e.getSlot() == 22) {
					if (e.getClick().isLeftClick()) executor.addSpeed(1);
					if (e.getClick().isRightClick()) executor.addSpeed(-1);
				}
			}
		}
		private void cancelMessage(String s) {
			String t = getGeneral(s);
			if (!t.equals("")) player.sendMessage(prefix + t);
		}
		private void load() {
			if (reader == length) {Success(); return;}
			String talk = DialogString.get("Talk").get(reader).replaceAll("&", "§").replaceAll("<player>",player.getName());
			if (talk.contains("%")) talk = TextUtil.VarConvert(player, talk);
			try {
				currentSender = (DialogStringline.get("Talker").get(reader).equals("player")) ? player.getName() : DialogStringline.get("Talker").get(reader);
			} catch(Exception e) {currentSender = sender;}
			try {
				sendway = DialogStringline.get("Interface").get(reader).toLowerCase();
				switch (sendway) {
				default: {sendway = "gui";}
				case "gui": {player.openInventory(inv); break;}
				case "title": {player.closeInventory(); break;}
				}
			} catch(Exception ignored) {}
			try {soundPlay(player,DialogStringline.get("Sound").get(reader));} catch(Exception ignored) {}
			try {CmdUtil.cmdOp(player,DialogStringline.get("Command").get(reader).replaceAll("<player>",player.getName()));} catch(Exception ignored) {}
			try {
				if (DialogStringline.get("TypingSound").get(reader) != null) typingsound.put(currentSender,DialogStringline.get("TypingSound").get(reader));
			} catch(Exception ignored) {}
			reader ++;
			inv.clear();
			String guiName = "Dialog." + key + ".Item." + reader;
			if (config.getKeys(guiName) != null) {
				for (String s : config.getKeys(guiName)) {
					inv.setItem(Integer.parseInt(s) - 1, (ItemStack) config.get(guiName + "." + s));
				}
			}
			if (!typingsound.containsKey(currentSender)) typingsound.put(currentSender, defsound);
			executor = new DialogExecutor(this, currentSender, sendway, talk, speed, typingsound.get(currentSender));
		}
		
		String getSenderSound() {return sendersound;}
		String getSender() {return sender;}
		Player getPlayer() {return player;}
		Inventory getInventory() {return inv;}
		
	}
	private class DialogExecutor implements Runnable {

		DialogSender dig;
		
		Inventory inv;
		Player player;
		
		String sender,
		text,
		sendway,
		typingsound;
		
		int textlen = 0;
		BukkitTask taskId;
		int textspeed = 1;
		
		DialogExecutor(DialogSender dig, String sender, String sendway, String text, long speed, String typingsound) {
			this.dig = dig;
			this.inv = dig.getInventory();
			this.player = dig.getPlayer();
			this.sender = sender;
			this.text = text;
			this.sendway = sendway;
			this.typingsound = typingsound;

			taskId = Bukkit.getScheduler().runTaskTimer(pl, this, 0, speed);
			
		}
		private void addSpeed(int i) {
			textspeed += i;
			if (textspeed < 1) textspeed = 1;
			if (textspeed > 4) textspeed = 4;
		}
		@Override
		public void run() {
			textlen += textspeed;
			if (textlen < text.length() + textspeed) {
				if (textlen > text.length()) textlen = text.length();
				String ret = Sub(text);
				if (!Last(ret,1).equals("*")) {
					soundPlay(player,typingsound);
					switch(sendway) {
					case "gui": {
						inv.setItem(22, InvUtil.createItem(Material.ENCHANTED_BOOK, prefix + "§f" + sender + ":", new String[] {"§f"+ret.replaceAll("\\*", "")}));
						player.updateInventory();
						break;
					}
					case "title" : {
						player.sendTitle("§e§l" + sender, ret.replaceAll("\\*", ""), 0, 30, 10);
						break;
					}
					}
				}
			} else {Return();
			}
		}
		private void kill() {
			taskId.cancel();
		}
		private void Return() {
			kill();
			taskId = Bukkit.getScheduler().runTaskLater(pl,() -> dig.load(), 20);
		}
		String Sub(String full) {
			String substring = full.substring(0, textlen);
			while (((substring.length() >= 2) ? Last(substring,2) : substring).contains("§")) {textlen += 2; substring = full.substring(0,textlen);}
			return substring;
		}
		String Last(String text, int key) {
			return text.substring(text.length() - key,text.length());
		}
	}
	
	private void soundPlay(Player player,String s) {
		if (Objects.equals(s, "")) return;
		List<String> sound = new ArrayList<>(Arrays.asList(TextUtil.split(s)));
		while (sound.size() < 3) sound.add("1");
		try {
			player.playSound(player.getLocation(), sound.get(0), Float.parseFloat(sound.get(1)), Float.parseFloat(sound.get(2)));
		} catch(Exception ignored) {}
	}
}