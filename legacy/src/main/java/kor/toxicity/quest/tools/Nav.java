package kor.toxicity.quest.tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import kor.toxicity.quest.Quest;
import kor.toxicity.quest.events.NavigationStartEvent;
import kor.toxicity.quest.events.NavigationStopEvent;
import kor.toxicity.quest.util.EvtUtil;
import kor.toxicity.quest.util.InvUtil;
import kor.toxicity.quest.util.PlayerUtil;
import kor.toxicity.quest.util.TextUtil;

public interface Nav {

	ItemStack compass = InvUtil.createItem(Material.COMPASS,TextUtil.colored(Quest.basic.getString("config.navigation-compass-name", "&e&l[Quest] &fNavigation")),null);
	boolean nav = Quest.basic.getBoolean("config.activate-navigation", true);
	boolean override = Quest.basic.getBoolean("config.navigation-override", false);
	boolean give = Quest.basic.getBoolean("config.navigation-give", true);
	
	default void NavStart(Player player, Location loc, String name) {
		if (!nav) return;
		EvtUtil.call(new NavigationStartEvent(player));
		player.closeInventory();
		if (player.getInventory().getItem(40) != null && !player.getInventory().getItem(40).equals(compass) && !override) InvUtil.give(player, player.getInventory().getItem(40));
		if (give) player.getInventory().setItem(40, compass);
		String msg = TextUtil.colored(Quest.basic.getString("config.navigation-start", ""));
		if (!msg.equals("")) PlayerUtil.NavStart(player, loc, msg + name);
	}
	default void NavEnd(Player player) {
		if (!nav) return;
		EvtUtil.call(new NavigationStopEvent(player));
		player.closeInventory();
		if (give && player.getInventory().getItem(40) != null && player.getInventory().getItem(40).equals(compass)) player.getInventory().setItem(40, new ItemStack(Material.AIR, 1, (short)0));
		String msg = TextUtil.colored(Quest.basic.getString("config.navigation-stop", ""));
		if (!msg.equals("")) PlayerUtil.NavEnd(player, msg);
	}
}
