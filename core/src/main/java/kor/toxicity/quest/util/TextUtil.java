package kor.toxicity.quest.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kor.toxicity.quest.Quest;

public class TextUtil {

	public static String entityName(Entity entity) {
		return (entity.getName() == null) ? entity.getType().toString() : uncolored(entity.getName());
	}
	public static String itemName(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		return (meta.getDisplayName() != null && meta.getDisplayName() != "") ? meta.getDisplayName() : item.getType().toString().toLowerCase();
	}
	public static String VarConvert(Player player, String s) {
		String ret = "";
		String[] t = s.split("%");
		if (t.length < 3) return s; 
		for(int i = 0; i < t.length; i ++) {
			if (Math.floorMod(i + 1, 2) == 0) {
				ret += Quest.pl.getVars(player).getValueComma(t[i]);
			} else {
				ret += t[i];
			}
		}
		return ret;
	}
	
	public static String LocationName(Location loc) {
		return "X: " + (int) loc.getX() + ", Y: " + (int) loc.getY() + ", Z: " + (int) loc.getZ();
	}
	
	public static String uncolored(String s) {
		String t = "";
		for (int i = 1; i < s.length() + 1; i ++) {
			while (s.substring(i - 1, i).equals("§")) {i += 2;}
			String last = s.substring(i - 1, i);
			t += last;
		}
		return t;
	}
	public static String colored(String s) {return "§f" + s.replaceAll("&", "§");}
	public static String[] split(String s) {return (s.contains(" ")) ? s.split(" ") : new String[] {s};}
}
