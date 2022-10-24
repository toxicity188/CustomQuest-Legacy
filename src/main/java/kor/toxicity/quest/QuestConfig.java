package kor.toxicity.quest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestConfig {
	
	Quest plugin;
	YamlConfiguration main = new YamlConfiguration();
	
	List<String> fileList = new ArrayList<>();
	Map<String,List<String>> keyList = new HashMap<>();
	
	public QuestConfig(Quest pl, String file, String key) throws FileNotFoundException, IOException, InvalidConfigurationException {
		QuestConfig_Load(pl.getDataFolder(),file,key);
	}
	public QuestConfig(File folder, String key) throws FileNotFoundException, IOException, InvalidConfigurationException {
		if (!folder.exists()) folder.mkdir();
		for (File MarketConfig : folder.listFiles()) {
			QuestConfig_Load(folder,MarketConfig.getName(),key);
		}
	}
	public QuestConfig(File folder, String file, String key) throws FileNotFoundException, IOException, InvalidConfigurationException {
		QuestConfig_Load(folder, file, key);
	}
	public void QuestConfig_Load(File folder, String file, String key) throws FileNotFoundException, IOException, InvalidConfigurationException {
		File config = new File(folder, file);
		if (config.exists()) {
			if (file.contains(".yml")) fileList.add(file.replaceAll(".yml",""));
			if (!this.main.contains(key)) this.main.createSection(key);
			YamlConfiguration t = new YamlConfiguration();
			t.load(config);
			List<String> listget = new ArrayList<>();
			for (String i : t.getKeys(true)) {
				if (!i.contains(".")) listget.add(i);
				if (t.get(i).getClass() == MemorySection.class) { 
					this.main.createSection(key + "." + i);
				} else {
					this.main.set(key + "." + i, t.get(i));
				}
			}
			keyList.put(file.replaceAll(".yml",""), listget);
		}
	}

	void clear() {
		keyList.clear();
		fileList.clear();
	}
	List<String> getFileList() {return this.fileList;}
	List<String> getValueList(String s) {return this.keyList.get(s);}


	public boolean IsSet(String key) {
		if (!this.main.contains(key)) return false;
		return true;
	}
	public Object get(String key) {
		if (!this.main.contains(key)) return null;
		return this.main.get(key);
	}
	public Set<String> getKeys(String key) {
		if (!this.main.contains(key) && !this.main.isConfigurationSection(key)) return null;
		return this.main.getConfigurationSection(key).getKeys(false);
	}
	public List<String> getStringList(String key) {
		if (!this.main.contains(key) && !this.main.isConfigurationSection(key)) return null;
		return this.main.getStringList(key);
	}
	public String getString(String key,String def) {
		if (this.main.isConfigurationSection(key)) return null;
		if (!this.main.contains(key)) return def;
		return this.main.getString(key);
	}
	public float getFloat(String key,float def) {
		if (!this.main.contains(key)) return def;
		return (float) this.main.getDouble(key);
	}
	public double getDouble(String key,double def) {
		if (!this.main.contains(key)) return def;
		return this.main.getDouble(key);
	}
	public int getInt(String key,int def) {
		if (!this.main.contains(key)) return def;
		return this.main.getInt(key);
	}
	public boolean getBoolean(String key,boolean def) {
		if (!this.main.contains(key)) return def;
		return this.main.getBoolean(key);
	}
}
