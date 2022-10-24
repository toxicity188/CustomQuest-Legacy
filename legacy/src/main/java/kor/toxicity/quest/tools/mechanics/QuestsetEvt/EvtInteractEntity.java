package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import kor.toxicity.quest.util.TextUtil;

public class EvtInteractEntity extends EvtManager {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void InteractEntity(PlayerInteractEntityEvent e) {
		if (e.getPlayer().equals(player) && !e.isCancelled()) {
			Entity t = e.getRightClicked();
			if (compare != null && !TextUtil.uncolored(TextUtil.entityName(t)).equals(compare.replaceAll("\\_", " "))) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
}
