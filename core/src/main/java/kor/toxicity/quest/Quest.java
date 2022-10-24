package kor.toxicity.quest;

import kor.toxicity.quest.tools.mechanics.Dialog;
import kor.toxicity.quest.tools.mechanics.QuestSet;
import kor.toxicity.quest.tools.vars.Vars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest extends JavaPlugin {

	public static Quest pl;
	public static Map<String,String> lore;
	public static QuestConfig basic;
	
	Map<String,QuestConfig> config;
	
	
	Map<String,Dialog> globaldialog;
	Map<String,QuestSet> globalquest;
	
	Map<Player,Vars> playervars;
	Map<Player,List<String>> playerquest;
	
	QuestPlayerListener listener;
	QuestCommand command;
	
	@Override
	public void onEnable() {
		pl = this;
		lore = new HashMap<>();
		
		this.config = new HashMap<>();
		
		this.playervars = new HashMap<>();
		this.globaldialog = new HashMap<>();
		this.globalquest = new HashMap<>();
		this.playerquest = new HashMap<>();
		
		File folder = this.getDataFolder();
		this.getDataFolder().mkdir();
		new File(folder.getAbsolutePath() + "\\User").mkdir();
		
		try {
			if (!new File(folder,"lore.yml").exists()) saveResource("lore.yml", false);
			if (!new File(folder,"config.yml").exists()) saveResource("config.yml", false);
			basic = new QuestConfig(pl,"config.yml","config");
			QuestConfig general = new QuestConfig(pl,"lore.yml","general");
			for (String s : general.getKeys("general")) {
				lore.put(s, general.getString("general." + s, "Error!").replaceAll("&", "§"));
			}
		} catch (IOException | InvalidConfigurationException e1) {e1.printStackTrace();}

		listener = new QuestPlayerListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
		command = new QuestCommand(this);
		try {load();} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		getCommand("quest").setExecutor(command);

		for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
			if (player.isOnline()) {
				try {
					userLoad((Player) player);
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		
		send("플러그인이 활성화 되었습니다.");
	}
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);

		for (Player player : playervars.keySet()) {
			try {
				userSave(player, true);
			} catch (Exception e) {e.printStackTrace();}
		}
		for (String k : globaldialog.keySet()) {
			globaldialog.get(k).clear();
		}
		for (String k : globalquest.keySet()) {
			globalquest.get(k).clear();
		}
		
		lore.clear();
		
		this.config.clear();
		
		this.globaldialog.clear();
		this.globalquest.clear();
		this.playerquest.clear();
		this.playervars.clear();
		
		send("플러그인이 비활성화 되었습니다.");
	}
	
	void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
		for (String cfg : config.keySet()) {
			config.get(cfg).clear();
		}
		config.clear();
		File folder = pl.getDataFolder();
		for (String f : new String[] {"Dialog", "QnA" , "QuestSet", "NPC" , "Shop"}) {
			config.put(f.toLowerCase(), new QuestConfig(new File(folder + "\\" + f),f));
		}

		for (String k : globaldialog.keySet()) {
			globaldialog.get(k).clear();
		}
		globaldialog.clear();
		if (config.get("dialog").IsSet("Dialog")) {
			for (String keys : config.get("dialog").getKeys("Dialog")) {
				globaldialog.put(keys, new Dialog(keys));
			}
			
		}
		if (config.get("questset").IsSet("QuestSet")) {
			for (String keys : config.get("questset").getKeys("QuestSet")) {
				if (globalquest.containsKey(keys)) {
					globalquest.get(keys).reload();
				} else {
					globalquest.put(keys, new QuestSet(keys));
				}
			}
		}
		listener.reload();
		command.reload();
	}
	
	void send(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Quest] " + s);
	}
	
	public QuestConfig getConfig(String key) {
		return this.config.get(key);
	}
	public Vars getVars(Player player) {
		return this.playervars.get(player);
	}
	public QuestSet getGlobalQuest(String key) {
		if (!this.globalquest.containsKey(key)) return null;
		return this.globalquest.get(key);
	}
	public Dialog getGlobalDialog(String key) {
		if (!this.globaldialog.containsKey(key)) return null;
		return this.globaldialog.get(key);
	}
	public List<String> getPlayerQuest(Player p) {
		if (!this.playerquest.containsKey(p)) this.playerquest.put(p, new ArrayList<>());
		return this.playerquest.get(p);
	}
	
	void userLoad(Player player) throws IOException, InvalidConfigurationException {
		File file = new File(pl.getDataFolder().getAbsolutePath() + "\\User");
		String name = player.getUniqueId() + ".yml";
		if (!new File(file,name).exists()) new File(file,name).createNewFile();
		QuestConfig config = new QuestConfig(file, name, "User");
		Vars var = new Vars(player);
		if (config.getKeys("User.Vars") != null) {
			for (String s : config.getKeys("User.Vars")) {
				var.set(s,config.getInt("User.Vars." + s, 0));
			}
		}
		playervars.put(player, var);
		if (config.getKeys("User.Quests") != null) {
			for (String key : config.getKeys("User.Quests")) {
				if (globalquest.containsKey(key)) {
					Vars v = new Vars(player);
					if (config.getString("User.Quests." + key,"") == null) {
						for (String s : config.getKeys("User.Quests." + key)) {
							v.set(s,config.getInt("User.Quests." + key + "." + s, 0));
						}
						globalquest.get(key).LoadVars(player, v);
					}
					globalquest.get(key).load(player);
				}
			}
		}
	}
	void userSave(Player player, boolean stopping) {
		try {
			YamlConfiguration t = new YamlConfiguration();
			for (String s : getVars(player).getValueList().keySet()) {
				if (playervars.get(player).getValue(s) != 0) {
					t.set("Vars." + s, playervars.get(player).getValue(s));
				}
			}
			getPlayerQuest(player).forEach(z -> {
				QuestSet q = getGlobalQuest(z);
				if (q.getVars(player).getValueList().size() == 0) {t.set("Quests." + q.getKey(), "none");}
				for (String s : q.getVars(player).getValueList().keySet()) {
					t.set("Quests." + q.getKey() + "." + s, q.getVars(player).getValue(s));
				}});
			
			if (stopping) {
				getPlayerQuest(player).forEach(z -> getGlobalQuest(z).stop(player));
				playervars.get(player).clear();
				playervars.remove(player);
				if (playerquest.containsKey(player)) playerquest.remove(player);
			}
			t.save(new File(this.getDataFolder().getAbsolutePath() + "\\User", player.getUniqueId() + ".yml"));
		} catch (Exception e) {e.printStackTrace();}
	}
	
}
