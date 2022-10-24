package kor.toxicity.quest.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import kor.toxicity.quest.tools.Tools;

public class PlayerUtil extends Tools {

	public static void NavStart(Player player, Location loc, String msg) {
		player.sendMessage(msg);
		player.setCompassTarget(loc);
	}
	public static void NavEnd(Player player, String msg) {
		player.sendMessage(msg);
		player.setCompassTarget(player.getCompassTarget().zero());
	}
}
