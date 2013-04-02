package com.github.SpectraFusion.InventoryAutomation;

import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryAutomation extends JavaPlugin{
	
	// when plugin is enabled, set the command executor and register events
	@Override
	public void onEnable(){
		getCommand("auto").setExecutor(new CmdExe(this));
		this.getServer().getPluginManager().registerEvents(new AutoReplenishListener(this, new CmdExe(this)), this);
		getLogger().info("InventoryAutomation has been enabled!");
	}
	
	// when plugin is disabled, do nothing
	@Override
	public void onDisable(){
		
	}
}