package kor.toxicity.quest.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import io.chazza.advancementapi.AdvancementAPI;
import io.chazza.advancementapi.FrameType;
import kor.toxicity.quest.Quest;

public class AdvancementsUtil {
	
	public static AdvancementAPI getAdvancement(String lore) {
		AdvancementAPI parent = AdvancementAPI.builder(new NamespacedKey(Quest.pl, Integer.toString(lore.hashCode())))
	            .title(lore)
                .description("")
	            .icon("minecraft:writable_book")
	            .hidden(false)
	            .toast(true)
	            .background("minecraft:textures/gui/advancements/backgrounds/stone.png")
	            .frame(FrameType.GOAL)
	            .build();
		return parent;
	}
	
	public static void showAdvancement(Player player, String key) {
		if (!Quest.basic.getBoolean("config.use-show-advancements", true)) return;
		AdvancementAPI api = getAdvancement(key).add();
		Advancement adv = api.getAdvancement();
		grant(player,adv);
		Bukkit.getScheduler().runTaskLater(Quest.pl, () -> {revoke(player,adv); api.remove();}, 2L);
	}
	
    private static void grant(Player player, Advancement advancement) {
        if (!player.getAdvancementProgress(advancement).isDone()) {
	        Collection<String> remainingCriteria = player.getAdvancementProgress(advancement).getRemainingCriteria();
	        for (String remainingCriterion : remainingCriteria)
	        	player.getAdvancementProgress(advancement)
	        	.awardCriteria(remainingCriterion);
        }
    }

    private static void revoke(Player player, Advancement advancement) {
        if (player.getAdvancementProgress(advancement).isDone()) {
        	Collection<String> awardedCriteria = player.getAdvancementProgress(advancement).getAwardedCriteria();
        	for (String awardedCriterion : awardedCriteria)
        		player.getAdvancementProgress(advancement)
        		.revokeCriteria(awardedCriterion);
        }
    }
}
