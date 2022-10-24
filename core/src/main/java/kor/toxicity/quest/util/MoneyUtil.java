package kor.toxicity.quest.util;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import net.milkbowl.vault.economy.Economy;

public class MoneyUtil{

	public static Economy getEconomy() {
		return Bukkit.getServicesManager().getRegistration(Economy.class) != null ? (Economy)Bukkit.getServicesManager().getRegistration(Economy.class).getProvider() : null;
	}
	public static double getMoney(OfflinePlayer p) {
		return getEconomy().getBalance(p);
	}
	public static void addMoney(OfflinePlayer p, double money) {
		try {
			if (!getEconomy().hasAccount(p)) getEconomy().createPlayerAccount(p);
			getEconomy().depositPlayer(p, money);
		} catch(Exception e) {}
	}
	
	public static String Comma(double d) {
		return new DecimalFormat().format(d);
	}
}
