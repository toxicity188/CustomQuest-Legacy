package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import kor.toxicity.quest.util.TextUtil;

public class EvtEntityDeath extends EvtManager {
	
	@EventHandler
	public void EntityDeath(EntityDeathEvent e) {
		if (e.getEntity().getKiller() != null && e.getEntity().getKiller().equals(player)) {
			if (compare != null && !TextUtil.entityName(e.getEntity()).equals(compare.replaceAll("\\_", " "))) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
}
