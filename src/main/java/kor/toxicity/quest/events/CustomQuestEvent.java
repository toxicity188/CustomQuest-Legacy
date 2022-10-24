package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import kor.toxicity.quest.tools.mechanics.QuestSet;

public class CustomQuestEvent extends QuestEvent {

	public static final HandlerList handlers = new HandlerList();

	QuestSet quest;
	String val;
	
	public CustomQuestEvent(Player p, String val) {
		super(p);
		this.val = val;
	}

	public String getVal() {
		return val;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
