package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import kor.toxicity.quest.Quest;
import kor.toxicity.quest.tools.mechanics.QuestSet;
import kor.toxicity.quest.util.AdvancementsUtil;

public abstract class EvtManager implements Listener {

	QuestSet questset;
	Player player;
	String val;
	String lore;
	String compare;
	int add;
	
	boolean isCompleted = false;
	
	public EvtManager() {
	}
	
	public void registerEvents(Player player, QuestSet q, String lore, String val, int add, String compare) {
		this.questset = q;
		this.player = player;
		this.val = val;

		this.compare = compare;
		this.add = add;
		
		String t = (compare != null) ? lore.replaceAll("<entity>", this.compare.replaceAll("_", " ")).replaceAll("<amount>", Integer.toString(add)) : lore.replaceAll("<amount>", Integer.toString(add));
		this.lore = t;
		
		questset.getEvtList(player).add(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, Quest.pl);
	}
	public void unregisterEvents() {
		this.isCompleted = true;
        HandlerList.unregisterAll(this);
	}
	protected void complete() {
		unregisterEvents();
		AdvancementsUtil.showAdvancement(player, lore);
	}
	
	public boolean isCompleted() {return isCompleted;}
	
	public String getLore() {return lore;}
	public String getVal() {return val;}
	public int getInt() {return add;}
	public String getCompare() {return compare;}
}
