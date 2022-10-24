package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class NavigationStartEvent extends QuestEvent {
	public static final HandlerList handlers = new HandlerList();

	public NavigationStartEvent(Player p) {
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
