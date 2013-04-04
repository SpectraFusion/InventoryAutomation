package com.github.SpectraFusion.InventoryAutomation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SortInventory{
	private CmdExe cmdExe;
	
	// constructor
	public SortInventory(CmdExe cmdExe){
		this.cmdExe = cmdExe;
	}
	
	// sort inventory by material
	// TODO sort against enchantments as well
	public void sortInventory(Player player, CommandSender sender){
		Inventory inventory = player.getInventory();
		ItemStack[] allItems = inventory.getContents();
		
		// save the player's current shortcut slots in an array; they will be excluded from sorting
		List<ItemStack> activeItems = new ArrayList<ItemStack>(9);
		for (int x = 0; x < 9; x++){
			activeItems.add(allItems[x]);
		}
		
		// obtain the rest of the items in the inventory and place them in another array
		ItemStack[] items = new ItemStack[27];
		for (int x = 0; x < 27; x++){
			items[x] = allItems[x + 9];
		}
		
		List<ItemStack> sortedItems = new ArrayList<ItemStack>(27);
		for (ItemStack x:items){
			if (x != null){
				ItemStack item = cmdExe.cloneItem(x);

				// if it is the first item, just place it into the list
				if (sortedItems.isEmpty()){
					sortedItems.add(item);
				}
				
				// if not, combine and sort the item
				else{
					boolean sorted = false;
					boolean moreEnchantments = false;
					int itemId = item.getData().getItemTypeId();
					int maxStacks = item.getMaxStackSize();
					int currDur = item.getDurability();
					int firstMatch = -1;
					int firstIndex;
					int lastIndex = -1;
					
					for (firstIndex = 0; firstIndex <= sortedItems.size() - 1; firstIndex++){
						ItemStack sortedItem = sortedItems.get(firstIndex);
						int sortedItemId = sortedItem.getData().getItemTypeId();
						
						if (sortedItemId == itemId){
							/*
							if (firstMatch == -1){
								firstMatch = firstIndex;
							}
							*/
							int initIndex = firstIndex;
							//lastIndex = initIndex;
							//Iterator<ItemStack> sIit = sortedItems.listIterator(initIndex);
							Map<ItemStack, Integer> similarItems = new HashMap<ItemStack, Integer>();
							
							for (int index = firstIndex; index < sortedItems.size(); index++){
								ItemStack similarItem = sortedItems.get(index);
								if (similarItem != null){
									int similarItemId = similarItem.getData().getItemTypeId();
									if (similarItemId == itemId){
										similarItems.put(similarItem, index);
										if (moreEnchantments == false){
											moreEnchantments = item.getEnchantments().size() >= similarItem.getEnchantments().size();
											if (moreEnchantments && firstMatch == -1){
												firstMatch = index;
											}
										}
									}
								}
							}
							
							sender.sendMessage("" + similarItems);
							sender.sendMessage(" ");
							
							Map<ItemStack, Integer> sameItems = new HashMap<ItemStack, Integer>();
							Iterator<ItemStack> siIit = similarItems.keySet().iterator();
							
							while (siIit.hasNext()){
								ItemStack sameItem = siIit.next();
								if (cmdExe.checkEnchantments(item, sameItem)){
									sameItems.put(sameItem, similarItems.get(sameItem));
								}
							}
							
							
							/*
							// get a list of all similar items and their indexes
							while (sIit.hasNext()){
								ItemStack sameItem = sIit.next();
								int sameItemId = sameItem.getData().getItemTypeId();
								if (sameItemId == itemId){
									sameItems.put(sameItem, )
								}
								else{
									break;
								}
							}
							*/
							
							
							
							Iterator<ItemStack> sIit = sameItems.keySet().iterator();
							
							//for (int z = initIndex; z <= lastIndex; z++){
							while (sIit.hasNext()){
								int currStacks = item.getAmount();
								ItemStack sameItem = sIit.next();
								int sortedStacks = sameItem.getAmount();
								int sortedDur = sameItem.getDurability();
								int index = sameItems.get(sameItem);
								
								// if it is a tool(e.g. a sword)
								if (!item.getType().isBlock() && item.getType().getMaxDurability() != 0){
									
									// if the item durability is the same or greater than of sameItem, add it to the list at the first matching index
									if (currDur <= sortedDur){
										sender.sendMessage("1");
										sortedItems.add(index, item);
										sorted = true;
										break;
									}
									
									// if the item durability is less than the durability of sameItem, add the item to the next index of sameItem
									else if (currDur > sortedDur && index < (sameItems.size() - 1) && (currDur <= sortedItems.get(index + 1).getDurability())){
										sender.sendMessage("2");
										sortedItems.add(index + 1, item);
										sorted = true;
										break;
									}
								}
								
								else{
									// if item is at the max number of stacks, just add to the list at the first matching index
									if (currStacks == maxStacks){
										sender.sendMessage("3");
										sortedItems.add(initIndex, item);
										sorted = true;
										break;
									}
									
									// if item is not at the max number of stacks
									else{
										
										// add currStacks and sortedStacks together to determine if they make a full set or not
										int totalStacks = currStacks + sortedStacks;
										
										// if they make a full stack with a remainder, set the sortedItem stack size to the maxStack and the item to the remaining stacks
										if (totalStacks > maxStacks){
											sender.sendMessage("5");
											sameItem.setAmount(maxStacks);
											item.setAmount(totalStacks - maxStacks);
										}
										
										// if the total stacks is less or equal to maxStacks, just set the sortedItem stack size to the totalStacks
										else if (totalStacks <= maxStacks){
											sender.sendMessage("6");
											sameItem.setAmount(totalStacks);
											sorted = true;
											break;
										}
									}
								}
							}
							
							// break the loop since the first match was only needed
							break;
						}
					}
					
					// if the item was not sorted and there are no other matching items in the inventory, add the item to the next empty slot in the inventory
					if (sorted == false && lastIndex == -1){
						sender.sendMessage("7");
						if (moreEnchantments && firstMatch != -1){
							sortedItems.add(firstMatch, item);
						}
						else{
							sortedItems.add(firstIndex, item);
						}
					}
					
					// if the item was not sorted but there were matching items, add it the the end of the matching items
					else if (sorted == false && lastIndex != -1){
						sender.sendMessage("8");
						sortedItems.add(lastIndex + 1, item);
					}
				}
			}
		}
		inventory.clear();
		Iterator<ItemStack> aIit = activeItems.iterator();
		int index = 0;
		
		// add back the shortcut slot items
		while (aIit.hasNext()){
			ItemStack item = aIit.next();
			if (item != null){
				inventory.setItem(index, item);
			}
			index++;
		}
		
		Iterator<ItemStack> sIit = sortedItems.iterator();
		
		// add the sortedItem array back into the inventory in order
		while (sIit.hasNext()){
			ItemStack item = sIit.next();
			if (item != null){
				inventory.setItem(index, item);
			}
			index++;
		}
		
		player.getInventory().setContents(inventory.getContents());
		sender.sendMessage("Inventory has been sorted by material.");
	}
}