package kor.toxicity.quest.tools.edittools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import kor.toxicity.quest.util.InvUtil;

public class DialogEdit extends EditTools {

	Inventory inv;
	String status, key;
	List<String> talklist;
	
	int tasknum;
	
	@Override
	public void load(Player player, String type, String file, String key) {
		FileLoad(player, type, file, key);

		this.inv = getInv("&b&l&o" + type + ": &f" + key, 6);
		this.key = key;
		reload(0);

		player.openInventory(inv);
		registerEvents();
	}
	private void reload(int line) {
		status = "global";
		
		if (config.isSet(key + "." + "Talk")) {
			talklist = config.getStringList(key + "." + "Talk");
			if (talklist == null) talklist = new ArrayList<>();
		} else {talklist = new ArrayList<>();}
		
		String soundget, talkerget, interfget, commandget;
		
		Map<String,String> sound = getStringKeySet("Sound");
		Map<String,String> talker = getStringKeySet("Talker");
		Map<String,String> interf = getStringKeySet("interface");
		Map<String,String> command = getStringKeySet("Command");
		
		inv.clear();
		int loop = 0;
		for (String s : config.getStringList(key + "." + "Talk")) {
			String t = Integer.toString(loop + 1);
			
			soundget = (sound.containsKey(t)) ? sound.get(t) : "§f - <none> - ";
			talkerget = (talker.containsKey(t)) ? talker.get(t) : "§f - <none> - ";
			interfget = (interf.containsKey(t)) ? interf.get(t) : "§f - <none> - ";
			commandget = (command.containsKey(t)) ? command.get(t) : "§f - <none> - ";
			
			inv.setItem(loop, InvUtil.createItem(Material.PAPER, prefix + "§fTalk. " + Integer.toString(loop + 1), new String[] {
					"§f" + s,
					"",
					"§6[!] §fTalker : " + talkerget,
					"§6[!] §fSound : " + soundget,
					"§6[!] §fInterface : " + interfget,
					"§6[!] §fCommand : " + commandget,
					"",
					"§7(Left - Edit Talk, Right - Open Advanced Editor)",
					"§7(Shift+Left - Create new Talk in front, Shift+Right - Remove Talk)",
					}));
			loop ++;
		}
		if (loop <= 45) inv.setItem(loop, InvUtil.createItem(Material.BOOK, prefix + "Add Talk", new String[] {"","§6[!] §fClick this to add Talk."}));
		
		
		inv.setItem(45, InvUtil.createItem(Material.WOOL, prefix + "§fLinkedDialog:", getStringList("LinkedDialog"), (short)11));
		inv.setItem(46, InvUtil.createItem(Material.WOOL, prefix + "§fLinkedSubDialog:", getStringList("LinkedSubDialog"), (short)11));
		inv.setItem(47, InvUtil.createItem(Material.WOOL, prefix + "§fVars(Condition):", getStringList("ConditionVars"), (short)4));
		inv.setItem(48, InvUtil.createItem(Material.WOOL, prefix + "§fVars(Set):", getStringList("SetVars"), (short)4));
		inv.setItem(49, InvUtil.createItem(Material.WOOL, prefix + "§fQuest(Check):", getStringList("CheckQuest"), (short)5));
		inv.setItem(50, InvUtil.createItem(Material.WOOL, prefix + "§fQuest(Set):", getStringList("SetQuest"), (short)5));
		inv.setItem(51, InvUtil.createItem(Material.WOOL, prefix + "§fLinkedQnA", getStringList("LinkedQnA"), (short)2));
		inv.setItem(52, InvUtil.createItem(Material.WOOL, prefix + "§fLinkedPresent", getStringList("LinkedPresent"), (short)2));
		inv.setItem(53, InvUtil.createItem(Material.WOOL, prefix + "§fLinkedCommand", getStringList("LinkedCommand"), (short)14));
	}
	private void TalkGui(int t) {
		inv.clear();
		this.tasknum = t;
		
		inv.setItem(18, InvUtil.createItem(Material.CHEST, prefix + "§fItem:", getItemStack("Item" +"."+t)));
		inv.setItem(20, InvUtil.createItem(Material.BOOK, prefix + "§fTalker:", getString("Talker" +"."+t)));
		inv.setItem(22, InvUtil.createItem(Material.PAINTING, prefix + "§fInterface:", getString("Interface" +"."+t)));
		inv.setItem(24, InvUtil.createItem(Material.NOTE_BLOCK, prefix + "§fSound:", getString("Sound" +"."+t)));
		inv.setItem(26, InvUtil.createItem(Material.COMMAND, prefix + "§fCommand:", getString("Command" +"."+t)));
		
		IntStream.range(0,8).forEach((int q) -> {
			inv.setItem(45 + q, InvUtil.createItem(Material.STAINED_GLASS_PANE, "§f", null));
		});
		inv.setItem(53, InvUtil.createItem(Material.STONE_BUTTON, prefix + "Back", null));
		status = "sub";
	}
	
	@EventHandler
	public void InvClose(InventoryCloseEvent e) {
		if (e.getPlayer().equals(player) && e.getInventory().equals(inv)) {
			unregisterEvents();
		}
	}
	@EventHandler
	public void InvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().equals(player) && e.getInventory().equals(inv)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null) return;
			switch(status) {
			case "global":
				if (e.getSlot() < 45 && e.getCurrentItem().getType().equals(Material.PAPER)) {
					if (e.isLeftClick() && !e.isShiftClick()) ChatEdit("Talk" +"."+ Integer.toString(e.getSlot()),"listset");
					if (e.isRightClick() && !e.isShiftClick()) TalkGui(e.getSlot() + 1);
					if (e.isLeftClick() && e.isShiftClick()) TalkBetween(e.getSlot(),1);
					if (e.isRightClick() && e.isShiftClick()) TalkBetween(e.getSlot(),-1);
					return;
				} else if(e.getSlot() < 45 && e.getCurrentItem().getType().equals(Material.BOOK)) {
					ChatEdit("Talk","list");
					return;
				} else if (e.getSlot() >= 45 && e.getCurrentItem().getType().equals(Material.WOOL)) {
					if (e.isLeftClick()) {
						switch (e.getSlot()) {
						case 45: {ChatEdit("LinkedDialog","list"); break;}
						case 46: {ChatEdit("LinkedSubDialog","list"); break;}
						case 47: {ChatEdit("ConditionVars","list"); break;}
						case 48: {ChatEdit("SetVars","list"); break;}
						case 49: {ChatEdit("CheckQuest","list"); break;}
						case 50: {ChatEdit("SetQuest","list"); break;}
						case 51: {ChatEdit("LinkedQnA","list"); break;}
						case 52: {ChatEdit("LinkedPresent","list"); break;}
						case 32: {ChatEdit("LinkedCommand","list"); break;}
						}
					}
					if (e.isRightClick()) {
						switch (e.getSlot()) {
						case 45: {delete("LinkedDialog"); break;}
						case 46: {delete("LinkedSubDialog"); break;}
						case 47: {delete("ConditionVars"); break;}
						case 48: {delete("SetVars"); break;}
						case 49: {delete("CheckQuest"); break;}
						case 50: {delete("SetQuest"); break;}
						case 51: {delete("LinkedQnA"); break;}
						case 52: {delete("LinkedPresent"); break;}
						case 32: {delete("LinkedCommand"); break;}
						}
					}
					return;
				}
				break;
			case "sub":
				switch (e.getSlot()) {
				case 18: {itemEdit("Item"+"."+this.tasknum,false,6); break;}
				case 20: {ChatEdit("Talker"+"."+this.tasknum,"string"); break;}
				case 22: {ChatEdit("Interface"+"."+this.tasknum,"string"); break;}
				case 24: {ChatEdit("Sound"+"."+this.tasknum,"string"); break;}
				case 26: {ChatEdit("Command"+"."+this.tasknum,"string"); break;}
				case 53: {reload(0); break;}
				}
				break;
			}
		}
	}
	
	private void TalkBetween(int key, int ind) {
		
		TalkNumberSet(key,ind);
		configSave();
		reload(0);
	}
	private void TalkNumberSet(int key, int add) {
		List<String> get2 = config.getStringList(this.key + ".Talk");
		if (get2 == null) get2 = new ArrayList<>();
		if (get2.size() < Math.abs(add)) add = get2.size();
		if (get2.size() < key + 1||add == 0) return;
		
		List<String> bef = new ArrayList<>(get2.subList(0, key + 1));
		List<String> aft = new ArrayList<>(get2.subList(key + 1, get2.size()));
		
		if (get2.size() >= 1)
		
		if (add >= 1) {
			key += add;
			IntStream.range(0, add).forEach((int i) -> {
				bef.add("new talk");
			});
		} else {
			int size = bef.size();
			IntStream.range(0, -add).forEach((int i) -> {
				bef.remove(size - i - 1);
			});
		}
		List<String> ret = new ArrayList<>();
		ret.addAll(bef);
		ret.addAll(aft);
		config.set(this.key + ".Talk", ret);
		for (String vallist : new String[] {"Item", "Sound","Interface","Command", "TypingSound"}) {
			String configkey = this.key + "." + vallist;
			if (!config.isSet(configkey) || !config.isConfigurationSection(configkey)) continue;
 			
 			List<Integer> mapconvert = new ArrayList<>();
 			
 			for (String pre : config.getConfigurationSection(configkey).getKeys(false)) {
 				if (Integer.parseInt(pre) > key) mapconvert.add(Integer.parseInt(pre));
 			}
 				Map<Integer, Object> convert = new WeakHashMap<>();
 				for (int loop = 0; loop < mapconvert.size(); loop ++) {
 					convert.put((mapconvert.get(loop) + add > 1) ? mapconvert.get(loop) + add : 1, config.get(configkey + "." + mapconvert.get(loop)));
	 				config.set(configkey+ "." + mapconvert.get(loop), null);
 				}
 				for (Integer save : convert.keySet() ) {
	 				config.set(configkey+ "." + Integer.toString(save), convert.get(save));
 				}
		}
	}
	private void delete(String s) {
		LastDelete(s);
		configSave();
		reload(0);
	}
}
