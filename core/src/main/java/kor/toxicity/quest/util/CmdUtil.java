package kor.toxicity.quest.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdUtil {

	public static void cmd(String command) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public static void cmdOp(Player player, String command) {
		if (!player.isOp()) {
			player.setOp(true);
			Bukkit.getServer().dispatchCommand(player, command);
			player.setOp(false);
		} else {
			Bukkit.getServer().dispatchCommand(player, command);
		}
	}
}
