package kor.toxicity.quest.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EvtUtil {

	public static void call(Event e) {
		Bukkit.getPluginManager().callEvent(e);
	}
	
}
