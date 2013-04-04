package com.github.SpectraFusion.InventoryAutomation;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdExe implements CommandExecutor{
	public InventoryAutomation plugin;
	private SortInventory sortInventory;
	public static boolean autoReplenishMode = false;
	
	// constructor
	public CmdExe(InventoryAutomation plugin){
		this.plugin = plugin;
		this.sortInventory = new SortInventory(this);
	}
	
	// clone the item
	public ItemStack cloneItem(ItemStack item){
		ItemStack clonedItem = new ItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
		clonedItem.getData().setData(item.getData().getData());
        for (Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
            clonedItem.addEnchantment(enchantment.getKey(), enchantment.getValue());
        }
        ItemMeta itemMeta = clonedItem.getItemMeta();
        itemMeta.setDisplayName(item.getItemMeta().getDisplayName());
        itemMeta.setLore(item.getItemMeta().getLore());
        return clonedItem;
	}
	
	// check to see if one item has the same enchantments as another item
	public boolean checkEnchantments(ItemStack item, ItemStack sortedItem){
		boolean sameEnchantments = true;
		Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
		Map<Enchantment, Integer> sortedItemEnchantments = sortedItem.getEnchantments();
		if (itemEnchantments.size() == sortedItemEnchantments.size()){
			Iterator<Entry<Enchantment, Integer>> sIEit = sortedItemEnchantments.entrySet().iterator();
			while (sIEit.hasNext()){
				Map.Entry<Enchantment, Integer> enchantmentPair = sIEit.next();
				Enchantment enchantment = enchantmentPair.getKey();
				int enchantmentLvl = enchantmentPair.getValue();
				if (itemEnchantments.containsKey(enchantment)){
					if (itemEnchantments.get(enchantment) != enchantmentLvl){
						sameEnchantments = false;
						break;
					}
				}
				else{
					sameEnchantments = false;
					break;
				}
			}
		}
		else{
			sameEnchantments = false;
		}
		return sameEnchantments;
	}

	// find the first same ItemStack in the inventory that is not the itemInHand ItemStack; return null if there is no match
	public ItemStack getFirstSimilarItem(ItemStack itemInHand, ItemStack[] inventory){
		ItemStack sameItem = null;
		for (int x = 0; x < 36; x++){
			ItemStack item = inventory[x];
			if (item != null){
				if (item != itemInHand && item.isSimilar(itemInHand)){
					sameItem = item;
					break;
				}
			}
		}
		return sameItem;
	}
	
	// toggle auto-replenishing of the item in the player's hand
	public void toggleAutoReplenish(Player player, CommandSender sender, String[] args){
		
		// if no second argument, output if auto-replenish is enabled or disabled
		if (args.length == 1){
			sender.sendMessage("Auto-replenish is currently " + ((autoReplenishMode) ? "enabled." : "disabled."));
		}
		
		// if second argument is 'e', enable auto-replenishing
		else if (args[1].equalsIgnoreCase("e")){
			autoReplenishMode = true;
			sender.sendMessage("Auto-replenish has been enabled.");
		}
		
		// if second argument is 'd', disable auto-replenishing
		else if (args[1].equalsIgnoreCase("d")){
			autoReplenishMode = false;
			sender.sendMessage("Auto-replenish has been disabled.");
		}
	}
	
	// command handler
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		// make sure it is a player before continuing
		if (sender instanceof Player){
			Player player = (Player) sender;
			
			// make sure the received command is 'auto'
			if (cmd.getName().equalsIgnoreCase("auto")){
				
				// if it is just the command
				// TODO change to more efficient method when there are more toggles
				if (args.length == 0){
					if (autoReplenishMode){
						sender.sendMessage("Auto-replenish: " + ((autoReplenishMode) ? "Enabled" : "Disabled"));
					}
				}
				else{
					
					// if first argument is 'sort', sort the player's inventory
					if (args[0].equalsIgnoreCase("sort")){
						sortInventory.sortInventory(player, sender);
						return true;
					}
					
					// else if first argument is 'rep', toggle auto-replenishing depending on the second argument
					else if (args[0].equalsIgnoreCase("rep")){
						toggleAutoReplenish(player, sender, args);
						return true;
					}
				}
			}
		}
		else{
			sender.sendMessage("You must be a player!");
			return false;
		}
		return false;
	}
}