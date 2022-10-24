package kor.toxicity.quest.tools.mechanics.QuestsetEvt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import kor.toxicity.quest.util.TextUtil;

public class EvtChat extends EvtManager {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void PlayerChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().equals(player) && !e.isCancelled()) {
			if (compare != null && !TextUtil.uncolored(e.getMessage()).equals(compare.replaceAll("\\_", " "))) return;
			questset.getVars(player).add(val, 1);
			if (questset.getVars(player).getValue(val) >= add) {
				complete();
			}
		}
	}
}
