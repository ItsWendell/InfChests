package net.wmisiedjan.bukkit.InfChests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ChestManager {

	private Map<Block, ItemStack[]> inventories;
	private List<Location> timerchests = new ArrayList<Location>();
	

	public ChestManager() {
		inventories = new HashMap<Block, ItemStack[]>();
	}

	public boolean contains(Block chest) {
		return inventories.containsKey(chest);
	}
	
	public void addChest(Block block, ItemStack[] stack) {
		inventories.put(block, stack);
	}
	
	public ItemStack[] getChest(Block block) {
		return inventories.get(block);
	}
	
	public boolean containsTimer(Location loc) {
		return timerchests.contains(loc);
	}
	
	public void addTimer(Location loc) {
		timerchests.add(loc);
	}
	
	public void removeTimer(Location loc) {
		timerchests.remove(loc);
	}
	
	
	
	

}
