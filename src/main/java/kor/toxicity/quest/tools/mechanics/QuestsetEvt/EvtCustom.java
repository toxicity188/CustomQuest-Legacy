package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.event.EventHandler;
import kor.toxicity.quest.events.CustomQuestEvent;

public class EvtCustom extends EvtManager {

	@EventHandler
	public void CustomEvt(CustomQuestEvent e) {
		if (e.getPlayer().equals(player)) {
			if (compare != null && ! e.getVal().equals(compare)) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
}
