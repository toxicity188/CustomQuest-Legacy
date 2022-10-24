package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import kor.toxicity.quest.tools.mechanics.Dialog;

public class DialogStartEvent extends QuestEvent {
	public static final HandlerList handlers = new HandlerList();

	Dialog dialog;
	boolean cancelled = false;
	
	public DialogStartEvent(Player p, Dialog q) {
		super(p);
		this.dialog = q;
	}

	public Dialog getDialog() {return dialog;}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
