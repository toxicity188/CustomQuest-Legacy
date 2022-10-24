package kor.toxicity.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import kor.toxicity.quest.tools.PlayerGui;
import kor.toxicity.quest.tools.edittools.EditTools;
import kor.toxicity.quest.tools.edittools.DialogEdit;
import kor.toxicity.quest.tools.edittools.QuestSetEdit;
import kor.toxicity.quest.tools.edittools.ShopEdit;
import kor.toxicity.quest.tools.edittools.QnAEdit;

public class QuestCommand implements CommandExecutor, TabCompleter {

	Quest pl;
	Map<String,QuestConfig> config = new HashMap<>();
	
	public QuestCommand(Quest pl) {
		super();
		
		this.pl = pl;
	}
	
	public void reload() {
		config = pl.config;
	}

	void send(CommandSender sender, String m) {
		sender.sendMessage("§e§l[Quest] §f"+ m);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
		try {
			if (command.getName().toLowerCase().equals("quest")) {
				if (args.length == 0) {
					if (!(sender instanceof Player)) {send(sender,"this command is player only."); return true;}
					new PlayerGui((Player) sender);
					return true;
				}
				try {
					String t = cmdList.valueOf(args[0]).getErrorMsg(sender, args);
					if (t != null) {send(sender, t); return true;}
				} catch(Exception e) {send(sender, "unknown command."); return true;}
				
				switch(args[0].toLowerCase()) {
				case "reload":
					pl.load();
					send(sender,"reload completed.");
				case "edit":
					try {
						editorList list = editorList.valueOf(args[1].toLowerCase());
						EditTools edit = list.gui.getDeclaredConstructor().newInstance();
						edit.load((Player) sender, list.key, args[2], args[3]);
					} catch(Exception et) {}
				}
				
			}
		} catch(Exception e) {e.printStackTrace();}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (sender.isOp()) {
			if (args.length <= 1) {
				List<String> t = new ArrayList<>();
				for (cmdList s : cmdList.values()) {
					t.add(s.toString());
				}
				return t;
			}
			switch(args[0].toLowerCase()) {
			default: return null;
			case "edit":
				switch(args.length) {
				default: {return null;}
				case 2:
					List<String> t = new ArrayList<>();
					for (editorList s : editorList.values()) {
						t.add(s.toString());
					}
					return t;
				case 3:
					if (config.containsKey(args[1].toLowerCase())) return config.get(args[1].toLowerCase()).getFileList();
					return null;
				case 4:
					if (config.containsKey(args[1].toLowerCase())) return config.get(args[1].toLowerCase()).getValueList(args[2]);
					return null;
				}
			}
		} else {return null;}
	}

	enum editorList {
		dialog(DialogEdit.class,"Dialog"),
		questset(QuestSetEdit.class,"QuestSet"),
		qna(QnAEdit.class,"QnA"),
		shop(ShopEdit.class,"Shop"),
		;
		Class<? extends EditTools> gui;
		String key;
		
		editorList(Class<? extends EditTools> gui, String key) {
			this.gui = gui;
			this.key = key;
		}
	}
	enum cmdList {
		reload(null, 1, true, 
		null,null, "this is a Op only command."),
		edit("player", 4, true, 
		"this command is player only.","usage: /quest <edit> <mechanics> <filename> <QuestSet name>", "this is a Op only command."),
		;
		
		String senderlimit;
		int arglimit;
		boolean requireOp;
		String InvaildSenderError;
		String InvaildArgsError;
		String NoOpError;
		
		
		String getErrorMsg(CommandSender sender, String[] args) {
			if (this.senderlimit != null) {
				if (this.senderlimit.equals("player") && !(sender instanceof Player)) return this.InvaildSenderError;
				if (this.senderlimit.equals("console") && (sender instanceof Player)) return this.InvaildSenderError;
			}
			if (!sender.isOp() && this.requireOp) return this.NoOpError;
			if (this.arglimit > args.length) return this.InvaildArgsError;
			return null;
		}
		
		cmdList(String senderlimit, int arglimit, boolean requireOp, String ISE, String IAE, String NOE) {
			this.senderlimit = senderlimit;
			this.arglimit = arglimit;
			this.requireOp = requireOp;
			this.InvaildSenderError = ISE;
			this.InvaildArgsError = IAE;
			this.NoOpError = NOE;
		}
		
	}
}
