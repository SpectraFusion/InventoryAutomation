package com.github.SpectraFusion.InventoryAutomation;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplenishItem extends BukkitRunnable {
	private CmdExe cmdExe;
	private PlayerInteractEvent event;
	private Player player;
	
	// constructor
	public ReplenishItem(PlayerInteractEvent event, CmdExe cmdExe){
		this.event = event;
		this.cmdExe = cmdExe;
		this.player = event.getPlayer();
	}
	
	public void run(){
		ItemStack itemInHand = event.getItem();
		
		// if they have an item in their hand
		if (itemInHand != null){
			int currStacks = itemInHand.getAmount();
			
			// if the stack size has reached 0(stack has been used up)
			if (currStacks == 0){
				Inventory inventory = player.getInventory();
				ItemStack[] items = inventory.getContents();
				ItemStack sameItem = cmdExe.getFirstSimilarItem(itemInHand, items);
				
				// if they have matching stacks in their inventory
				if (sameItem != null){
					ItemStack itemMove = cmdExe.cloneItem(sameItem);
					inventory.removeItem(sameItem);
					
					// set the item in the player's hand to that of the cloned stack and remove the item from their backpack
					player.setItemInHand(itemMove);
				}
			}
		}
	}
}
