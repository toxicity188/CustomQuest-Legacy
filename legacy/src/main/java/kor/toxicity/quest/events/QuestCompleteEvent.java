package kor.toxicity.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import kor.toxicity.quest.tools.mechanics.QuestSet;

public class QuestCompleteEvent extends QuestEvent {

	public static final HandlerList handlers = new HandlerList();

	QuestSet quest;
	int money;
	int exp;
	
	public QuestCompleteEvent(Player p, QuestSet q, int money, int exp) {
		super(p);
		this.quest = q;
		this.money = money;
		this.exp = exp;
	}

	public void setMoney(double q) {money = (int) Math.round(q * money);}
	public void setExp(double q) {exp = (int) Math.round(q * exp);}
	
	public int getMoney() {return money;}
	public int getExp() {return exp;}
	public QuestSet getQuestSet() {return quest;}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
