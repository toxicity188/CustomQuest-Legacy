package kor.toxicity.quest.tools.vars;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;

import kor.toxicity.quest.util.MoneyUtil;
import kor.toxicity.quest.util.TextUtil;

public class Vars {

	Player player;
	Map<String, Integer> value = new HashMap<>();
	String action = null;
	
	public Vars(Player player) {
		this.player = player;
	}
	
	public void clear() {
		value.clear();
	}
	
	
	public String getAction() {return action;}
	public int getValue(String key) {
		switch (key.toLowerCase()) {
		case "money": return (int) MoneyUtil.getMoney(player);
		case "level": return (int) player.getLevel();
		}
		if (!this.value.containsKey(key)) this.value.put(key, 0);
		return this.value.get(key);
	}
	public String getValueComma(String key) {return MoneyUtil.Comma(getValue(key));}
	public Map<String, Integer> getValueList() {return this.value;}
	
	public void set(String name, int val) {
		if (val > 3000) val = 3000;
		if (val < 0) val = 0;
		value.put(name, val);
	}
	public void add(String name, int val) {set(name, val + getValue(name));}
	public void subtract(String name, int val) {set(name, val - getValue(name));}
	
	public void setParameter(String s) {
		
		String[] parameters = TextUtil.split(s);
		int val;
		if (parameters.length <= 1) return;
		try {
			if (parameters[1].contains("to")) {
				String[] rand = parameters[1].split("to");
				val = ThreadLocalRandom.current().nextInt(Integer.parseInt(rand[1])) + Integer.parseInt(rand[0]);
			} else {
				val = Integer.parseInt(parameters[1]);
			}
		} catch(Exception e) {return;}
		switch ((parameters.length >= 3) ? parameters[2].toLowerCase() : "add") {
		case "add": {add(parameters[0], val); return;}
		case "set": {set(parameters[0], val); return;}
		case "subtract": {subtract(parameters[0], val); return;}
		default: {return;}
		}
	}
	
	public boolean compare(String s) {
		
		String[] compare = TextUtil.split(s);
		int val;
		int comp = getValue(compare[0]);
		boolean t = true;
		try {
			val = Integer.parseInt(compare[1]);
			if (compare.length >= 4) {
				t = false;
				action = compare[3];
			}
		} catch(Exception e) {return false;}
		switch ((compare.length >= 3) ? compare[2].toLowerCase() : "equals") {
		case "more": {return (val < comp) == t;}
		case "less": {return (val > comp) == t;}
		case "moreorequals": {return (val <= comp) == t;}
		case "lessorequals": {return (val >= comp) == t;}
		case "equals": {return (val == comp) == t;}
		default: {return false;}
		}
	}
	
}
