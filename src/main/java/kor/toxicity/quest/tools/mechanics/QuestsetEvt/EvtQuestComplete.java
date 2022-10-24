package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.event.EventHandler;

import kor.toxicity.quest.events.QuestCompleteEvent;

public class EvtQuestComplete extends EvtManager {
	
	@EventHandler
	public void EntityDeath(QuestCompleteEvent e) {
		if (e.getPlayer().equals(player)) {
			if (compare != null && ! e.getQuestSet().getKey().equals(compare)) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
}
