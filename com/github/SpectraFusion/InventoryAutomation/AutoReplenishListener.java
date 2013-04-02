package com.github.SpectraFusion.InventoryAutomation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

public class AutoReplenishListener implements Listener{
	private InventoryAutomation plugin;
	private CmdExe cmdExe;
	
	// constructor
	public AutoReplenishListener(InventoryAutomation plugin, CmdExe cmdExe){
		this.plugin = plugin;
		this.cmdExe = cmdExe;
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onPlayerRightClick(PlayerInteractEvent event){
		
		// check is auto-replenish is enabled
		if (CmdExe.autoReplenishMode){
			
			// if the player right clicks
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
				BukkitTask replenishTask = new ReplenishItem(event, cmdExe).runTaskLater(plugin, 0);
			}
		}
	}
}