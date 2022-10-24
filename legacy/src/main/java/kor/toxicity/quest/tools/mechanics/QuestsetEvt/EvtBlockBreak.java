package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import kor.toxicity.quest.util.TextUtil;

public class EvtBlockBreak extends EvtManager {

	@EventHandler(priority=EventPriority.MONITOR)
	public void Blockbreak(BlockBreakEvent e) {
		if (e.getPlayer().equals(player) && !e.isCancelled()) {
			if (compare != null && ! TextUtil.uncolored(e.getBlock().getType().toString()).equals(compare.replaceAll("\\_", " "))) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
	
}
