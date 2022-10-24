package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import kor.toxicity.quest.tools.mechanics.QuestSet;

public abstract class QuestEvent extends Event {

	protected Player player;
	protected QuestSet quest;
	
	public QuestEvent(Player p) {
		player = p;
	}
	
	public Player getPlayer() {
		return player;
	}
}
