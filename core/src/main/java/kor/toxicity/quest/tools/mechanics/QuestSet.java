package kor.toxicity.quest.tools.mechanics;

import kor.toxicity.quest.QuestConfig;
import kor.toxicity.quest.events.QuestCompleteEvent;
import kor.toxicity.quest.tools.Tools;
import kor.toxicity.quest.tools.mechanics.QuestsetEvt.*;
import kor.toxicity.quest.tools.vars.Vars;
import kor.toxicity.quest.util.EvtUtil;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.MoneyUtil;
import kor.toxicity.quest.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class QuestSet extends Tools {
	
	boolean cancellable, takeitem;
	
	Map<String,List<String>> QuestSetStringList;
	Map<String,List<ItemStack>> QuestSetItem;
	Map<String,Location> QuestSetLocation;

	int rewardsMoney, rewardsExp;

	Map<Player,Vars> vars;
	Map<Player,List<EvtManager>> EvtList;
	
	QuestConfig config;
	
	Material questitem;
	short durability;
	
	String
	title,
	key,
	type,
	
	pRecommend
	,pCondition
	,pRewards
	,pMoney
	,pExp
	,sMoney
	,sExp
	,sComplete
	
	,itemlore
	
	,qRecommend
	,qCondition
	,qRewards
	,qComplete
	,qRead
	,qGive;

	public void clear() {
		QuestSetStringList.clear();
		QuestSetItem.clear();
		QuestSetLocation.clear();
	}
	
	public QuestSet(String key) {
		super();
		
		this.key = key;
		
		this.vars = new HashMap<>();
		
		this.QuestSetStringList = new HashMap<>();
		this.QuestSetLocation = new HashMap<>();
		this.QuestSetItem = new HashMap<>();
		
		this.EvtList = new HashMap<>();

		pCondition = getGeneral("prefix-condition");
		pRewards = getGeneral("prefix-rewards");
		pMoney = getGeneral("prefix-money");
		pExp = getGeneral("prefix-exp");
		sMoney = getGeneral("suffix-money");
		sExp = getGeneral("suffix-exp");
		sComplete = getGeneral("suffix-complete");
		
		qRecommend = getGeneral("quest-recommend");
		qCondition = getGeneral("quest-condition");
		qRewards = getGeneral("quest-rewards");
		qComplete = getGeneral("quest-complete");
		qRead = getGeneral("quest-read");
		qGive = getGeneral("quest-give");
		
		itemlore = getGeneral("event-take-item");
		
		try {
			questitem = Material.valueOf(getGeneral_Uncolored("quest-item"));
			durability = (short) Integer.parseInt(getGeneral_Uncolored("quest-durability"));
		} catch(Exception e) {
			questitem = Material.PAPER;
			durability = (short) 0;
		}
		
		configLoad();
	}
	private void configLoad() {
		this.config = pl.getConfig("questset");
		
		QuestSetStringList.clear();
		QuestSetLocation.clear();
		QuestSetItem.clear();
		
		String questkey = "QuestSet." + key;
		this.title = TextUtil.colored(config.getString(questkey + ".Title","Error"));
		this.type = config.getString(questkey + "." + "Type", "Main");
		
		this.cancellable = config.getBoolean(questkey + "." + "Cancellable", false);
		this.takeitem = config.getBoolean(questkey + "." + "TakeItem", false);
		
		if (config.getKeys(questkey + ".Location") != null) {
			for (String s : config.getKeys(questkey + ".Location")) {
				QuestSetLocation.put(s , (Location) config.get(questkey+ ".Location." + s));
			}
		}		
		for (String s : new String[] {
			"Lore",
			"Recommend",
			"ConditionVars",
			"ConditionCustom",
			
			"SurrenderVars",
			"CompleteVars",
		}) {
			if (config.get(questkey + "." + s) != null) QuestSetStringList.put(s, config.getStringList(questkey + "." + s));
		}
		for (String s : new String[] {
			"ConditionItem",
			"rewardsItem",
		}) {
			if (config.get(questkey + "." + s) == null) continue;
			List<ItemStack> itemlist = new ArrayList<>();
			for(String t : config.getKeys(questkey + "." + s)) {
				itemlist.add((ItemStack) config.get(questkey + "." + s + "." + t));
			}
			QuestSetItem.put(s, itemlist);
		}
		
		rewardsExp = config.getInt(questkey + ".rewardsExp",0);
		rewardsMoney = config.getInt(questkey + ".rewardsMoney",0);
		
	}

	public void load(Player player) {
		if (pl.getPlayerQuest(player).contains(key)) return;
		pl.getPlayerQuest(player).add(key);
		load_register(player);
	}
	private void load_register(Player player) {
		if (!vars.containsKey(player)) vars.put(player,new Vars(player));
		if (QuestSetStringList.containsKey("ConditionVars")) {
			QuestSetStringList.get("ConditionVars").forEach(s -> {
			String[] keys = TextUtil.split(s);
			if (keys.length >= 2) {
				try {
					
					EventList elist = EventList.valueOf(keys[0]);
					
					String compare = (keys.length >= 3) ? keys[2] : null;
					String valname = (compare != null) ? keys[0] + "_" + compare : keys[0];
					String lore;
					
					if (keys.length < 4) {
						lore = getGeneral((compare != null) ? elist.withcompare : elist.withoutcompare);
					} else {
						int customlore = 0;
						for(int t2 = 0; t2 < 3; t2 ++) {customlore += keys[t2].length() + 1;}
						lore = s.substring(customlore);
					}
					
					EvtManager evt = elist.evt.getDeclaredConstructor().newInstance();
					evt.registerEvents(player, this, lore, valname, Integer.parseInt(keys[1]), compare);
					if (getVars(player).getValue(valname) >= Integer.parseInt(keys[1])) {
						evt.unregisterEvents();
					}
				} catch (Exception e) {e.printStackTrace();}
			}
			});
		}
		
	}
	public void stop(Player player) {
		if (!pl.getPlayerQuest(player).contains(key)) return;
		pl.getPlayerQuest(player).remove(key);
		vars.get(player).clear();
		vars.remove(player);
		stop_unregister(player);
	}
	private void stop_unregister(Player player) {
		if (getEvtList(player).size() > 0) {
			getEvtList(player).forEach(EvtManager::unregisterEvents);
		}
		EvtList.remove(player);
	}
	
	public void start(Player player) {
		player.playSound(player.getLocation(),"entity.experience_orb.pickup", 1.0F,1.0F);
		player.sendMessage(prefix + qGive + title);
		load(player);
	}
	public void complete(Player player) {
		QuestCompleteEvent event = new QuestCompleteEvent(player, this, rewardsMoney, rewardsExp);
		EvtUtil.call(event);
		VarAdd(player,"CompleteVars");
		player.sendTitle(title, qComplete + pMoney + "§f- " + MoneyUtil.Comma(event.getMoney()) + ", " + pExp + "§f- " + MoneyUtil.Comma(event.getExp()), 10, 60, 10);
		player.playSound(player.getLocation(), "ui.toast.challenge_complete", 1, 1);
		MoneyUtil.addMoney(player, event.getMoney());
		if (QuestSetItem.containsKey("ConditionItem") && takeitem) InvUtil.takeAll(player, QuestSetItem.get("ConditionItem"));
		if (QuestSetItem.containsKey("rewardsItem")) InvUtil.giveAll(player, QuestSetItem.get("rewardsItem"));
		stop(player);
	}
	public void surrender(Player player) {
		VarAdd(player,"SurrenderVars");
		player.sendMessage(prefix + getGeneral("quest-surrender") + title);
		stop(player);
	}
	
	public void reload() {
		configLoad();
		for(Player p : vars.keySet()) {
			stop_unregister(p);
			load_register(p);
		}
	}
	private void VarAdd(Player player, String s) {
		if (QuestSetStringList.containsKey(s)) {
			IntStream.range(0, QuestSetStringList.get(s).size()).forEach((int i) -> pl.getVars(player).setParameter(QuestSetStringList.get(s).get(i)));
		}	
	}
	
	public Map<String,Location> getLocationList() {return this.QuestSetLocation;}
	public boolean isCancellable() {return this.cancellable;}
	public String getKey() {return this.key;}
	public Vars getVars(Player player) {return this.vars.get(player);}
	public void LoadVars(Player player, Vars v) {this.vars.put(player,v);}
	public List<EvtManager> getEvtList(Player player) {if(!EvtList.containsKey(player)) EvtList.put(player, new ArrayList<>()); return this.EvtList.get(player);}
	
	
	
	public ItemStack getItem(Player player, boolean expose) {
		List<String> itemlore = new ArrayList<>();
		List<String> t;
		
		if (QuestSetStringList.containsKey("Lore")) itemlore.addAll(QuestSetStringList.get("Lore"));
		if (expose) {
			t = Recommend(player,qRecommend);
			if (t != null) itemlore.addAll(t);
		}
		t = Condition(player,qCondition);
		if (t != null) itemlore.addAll(t);
		if (expose) {
			t = rewards(player,qRewards);
			if (t != null) itemlore.addAll(t);
		}
		if (!expose) itemlore.addAll(Arrays.asList(new String[] {"",qRead}));
		
		String[] loreget = new String[itemlore.size()];
		IntStream.range(0, loreget.length).forEach((int i) -> loreget[i] = TextUtil.colored(itemlore.get(i)));
		
		ItemStack item = InvUtil.createItem(questitem, title, loreget, 1, durability, true, true);
		
		if (isCompleted(player)) item = InvUtil.ShinyItem(item);
		return item;
	}
	private List<String> Recommend(Player player, String title) {
		List<String> ret = new ArrayList<>(Arrays.asList(new String[]{"", title}));
		if (QuestSetStringList.containsKey("Recommend")) ret.addAll(QuestSetStringList.get("Recommend"));
		if (ret.size() <= 2) return null;
		return ret;
	}
	private List<String> Condition(Player player, String title) {
		List<String> ret = new ArrayList<>(Arrays.asList(new String[]{"", title}));
		if (getEvtList(player).size() > 0) {
			getEvtList(player).forEach(s -> {
				String addlore = s.getLore() + " &7&o(" + getVars(player).getValueComma(s.getVal()) + "/" + MoneyUtil.Comma(s.getInt())+")";
				if (!s.isCompleted()) {
					ret.add("&e&l└ &f" + addlore);
				} else {
					ret.add("&e&l└ &f&m" + TextUtil.uncolored(addlore.replaceAll("&", "§")) + sComplete );
				}
			});
		}
		if (QuestSetItem.containsKey("ConditionItem")) {
			QuestSetItem.get("ConditionItem").forEach(s -> {
				ItemStack t = s.clone();
				int amount = InvUtil.getItemAmountInPlayerInv(player, t);
				int amount2 = t.getAmount();
				
				String addlore = itemlore.replaceAll("<entity>", TextUtil.itemName(t)).replaceAll("<amount>", MoneyUtil.Comma(amount2)) + " &7&o(" + MoneyUtil.Comma(amount)+ "/" + MoneyUtil.Comma(amount2)+")";
				if (amount < amount2) {
					ret.add("&e&l└ &f" + addlore);
				} else {
					ret.add("&e&l└ &f&m" + TextUtil.uncolored(addlore.replaceAll("&", "§")) + sComplete);
				}
			});
		}
		if (QuestSetStringList.containsKey("ConditionCustom")) {
			QuestSetStringList.get("ConditionCustom").forEach(s -> ret.add("&e&l└ &f" + s));
		}
		if (ret.size() <= 2) return null;
		return ret;
	}
	private List<String> rewards(Player player,String title) {
		List<String> ret = new ArrayList<>(Arrays.asList(new String[]{"", title}));
		ret.add(pRewards + pMoney + MoneyUtil.Comma(rewardsMoney)+ sMoney);
		ret.add(pRewards + pExp + MoneyUtil.Comma(rewardsExp)+ sExp);
		if (QuestSetItem.containsKey("rewardsItem")) {
			QuestSetItem.get("rewardsItem").forEach(s -> ret.add(pRewards + "&f" + TextUtil.itemName(s) + " " + MoneyUtil.Comma(s.getAmount()) + "개"));
		}
		if (ret.size() <= 2) return null;
		return ret;
	}
	public boolean isCompleted(Player player) {
		if (!vars.containsKey(player)) return false;
		if (QuestSetStringList.containsKey("ConditionCustom")) return false;
		for (int i = 0; i < getEvtList(player).size(); i ++) {
			if (!getEvtList(player).get(i).isCompleted()) return false;
		}
		if (QuestSetItem.containsKey("ConditionItem")) {
			for (int i = 0; i < QuestSetItem.get("ConditionItem").size(); i++) {
				ItemStack t = QuestSetItem.get("ConditionItem").get(i);
				if (InvUtil.getItemAmountInPlayerInv(player, t) < t.getAmount()) return false;
			}
		}	
		return true;
	}
	
	private enum EventList {
		HUNT(EvtEntityDeath.class,"event-hunt-withoutvar","event-hunt-withvar"),
		CHAT(EvtChat.class,"event-chat-withoutvar","event-chat-withvar"),
		COMPLETE(EvtQuestComplete.class,"event-complete-withvar","event-complete-withvar"),
		BREAK(EvtBlockBreak.class,"event-break-withoutvar","event-break-withvar"),
		CUSTOM(EvtCustom.class,"event-custom-withoutvar","event-custom-withvar"),
		INTERACTENTITY(EvtInteractEntity.class,"event-interactentity-withoutvar","event-interactentity-withvar"),
		;
		
		final Class<? extends EvtManager> evt;
		final String withoutcompare;
		final String withcompare;
		
		
		EventList(Class<? extends EvtManager> evt, String woc, String wc) {
			this.evt = evt;
			this.withoutcompare = woc;
			this.withcompare = wc;
		}
	}
}
