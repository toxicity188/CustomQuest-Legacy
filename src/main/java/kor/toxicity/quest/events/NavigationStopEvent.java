package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class NavigationStopEvent extends QuestEvent {
	public static final HandlerList handlers = new HandlerList();

	public NavigationStopEvent(Player p) {
		super(p);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
