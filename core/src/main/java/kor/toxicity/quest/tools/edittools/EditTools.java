package kor.toxicity.quest.tools.edittools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import kor.toxicity.quest.tools.Tools;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.TextUtil;

public abstract class EditTools extends Tools {

	Player player;

	String 
	type,
	key,
	filename;
	
	File file;
	YamlConfiguration config = new YamlConfiguration();
	
	public void load(Player player, String type, String file, String key) {}
	
	protected void FileLoad(Player player, String type, String file, String key) {
		this.player = player;
		this.type = type;
		this.filename = file;
		this.file = new File(pl.getDataFolder().getAbsolutePath() + "\\" + type + "\\" + file + ".yml");
		this.key = key;
		configLoad();
	}
	
	protected Inventory getInv(String name, int rows) {
		return InvUtil.getInventory(prefix + TextUtil.colored(name), rows);
	}
	
	protected String getType() {return type;}
	protected String getFileName() {return filename;}
	protected YamlConfiguration getYaml() {return config;}
	protected String getKey() {return key;}
	
	protected String[] getString(String key) {
		if (config.isConfigurationSection(this.key + "." + key)) return none("§7(Click - Set Value)");
		if (config.getString(this.key + "." + key) == null) return none("§7(Click - Set Value)");
		return new String[] {
				" §6- " + TextUtil.colored(config.getString(this.key + "." + key)),
				"",
				"§7(Click - Set Value)",
				};
	}
	protected String[] getStringList(String key) {
		if (config.isConfigurationSection(this.key + "." + key)) return none("§7(Left - Add Text, Right - Delete Last)");
		if (!config.contains(this.key + "." + key)) return none("§7(Left - Add Text, Right - Delete Last)");
		List<String> list = config.getStringList(this.key + "." + key);
		String[] ret = new String[list.size() +2];
		IntStream.range(0,list.size()).forEach((int i) -> {
			ret[i] = " §6- " + TextUtil.colored(list.get(i));
		});
		ret[list.size()] = "";
		ret[list.size() +1] = "§7(Left - Add Text, Right - Delete Last)";
		return ret;
	}
	protected String[] getItemStack(String key) {
		if (!config.contains(this.key + "." + key)) return none("§7(Click - Open item Editor)");
		Set<String> list = config.getConfigurationSection(this.key + "." + key).getKeys(false);
		String[] ret = new String[list.size() + 2];
		int i = 0;
		for (String s : list) {
			ret[i] = " §6- " + TextUtil.colored(TextUtil.itemName((ItemStack) config.get(this.key + "." + key + "." + s)));
			i++;
		}
		ret[list.size()] = "";
		ret[list.size() +1] = "§7(Click - Open item Editor)";
		return ret;
	}
	protected String[] getLocation(String key) {
		if (!config.contains(this.key + "." + key)) return none("§7(Click - Add your location)");
		Set<String> list = config.getConfigurationSection(this.key + "." + key).getKeys(false);
		String[] ret = new String[list.size() + 2];
		int i = 0;
		for (String s : list) {
			ret[i] = " §6- " + TextUtil.colored(TextUtil.LocationName((Location) config.get(this.key + "." + key + "." + s)));
			i++;
		}
		ret[list.size()] = "";
		ret[list.size() +1] = "§7(Click - Add your location)";
		return ret;
	}
	String[] none(String s) {
		return new String[] {"§7 - <none> - ", "", s};
	}
	
	protected Map<String,String> getStringKeySet(String key) {
		Map<String,String> ret = new WeakHashMap<>();
		if (!config.contains(this.key + "." + key)) return ret;
		Set<String> list = config.getConfigurationSection(this.key + "." + key).getKeys(false);
		for (String s : list) {
			ret.put(s, " §6- " + TextUtil.colored(config.getString(this.key + "." + key + "." + s)));
		}
		return ret;
	}
	
	protected void configLoad() {
		try {
			if (!file.exists()) file.createNewFile();
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	protected void configSave() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void itemEdit(String t, boolean b, int i) {
		unregisterEvents();
		ItemEditModule edit = new ItemEditModule();
		edit.setItemKey(t, b, i);
		edit.setReturn(this.getClass());
		edit.load(player, getType(), getFileName(), key);
	}
	protected void ChatEdit(String t, String mode) {
		unregisterEvents();
		ChatModule edit = new ChatModule();
		edit.setChatkey(t);
		edit.setChatMode(mode);
		edit.setReturn(this.getClass());
		edit.load(player, getType(), getFileName(), key);
	}
	protected void LastDelete(String t) {
		List<String> get = config.getStringList(key + "." + t);
		if (get == null) return;
		switch(get.size()) {
		default: {
			get.remove(get.size()-1);
			config.set(key + "." + t, get);
			break;
		}
		case 1:
			config.set(key + "." + t, null);
			break;
		}
	}
}
