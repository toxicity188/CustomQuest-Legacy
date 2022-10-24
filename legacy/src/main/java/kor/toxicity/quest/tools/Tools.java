package kor.toxicity.quest.tools;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.HandlerList;

import kor.toxicity.quest.Quest;
import kor.toxicity.quest.QuestConfig;
import kor.toxicity.quest.util.TextUtil;

public abstract class Tools implements Listener {
	
	protected Quest pl;
	protected Map<String,String> general;
	protected String prefix;
	protected QuestConfig def;
	protected String compass;
	
	public Tools() {
		this.pl = Quest.pl;
		this.general = Quest.lore;
		this.def = Quest.basic;
		
		this.prefix = getGeneral("prefix");
	}

	protected String getGeneral(String s) {
		if (!general.containsKey(s)) return "";
		return TextUtil.colored(general.get(s));
	}
	protected String getGeneral_Uncolored(String s) {
		if (!general.containsKey(s)) return "";
		return TextUtil.uncolored(general.get(s));
	}
	
	protected void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Quest.pl);
	}
	protected void unregisterEvents() {
        HandlerList.unregisterAll(this);
	}	
}
