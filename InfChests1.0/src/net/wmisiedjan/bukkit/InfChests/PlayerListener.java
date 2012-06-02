package net.wmisiedjan.bukkit.InfChests;

import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	private InfChests plugin;

	public PlayerListener(InfChests plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		//SIGN CHECK
		if (!event.getLine(0).toLowerCase().contains("[infchest]")) {
			return;
		}

		//PERMISSIONS CHECK "infchests.create"
		try {
			if (!plugin.permission.has(event.getPlayer().getWorld(), event.getPlayer()
					.getName(), "infchests.create")) {
				event.getPlayer()
						.sendMessage(
								"[InfChests] You're not allowed to create Infinite Chests");
				event.getBlock().breakNaturally();
				return;
			}
		} catch (Exception ex) {
			if (!event.getPlayer().isOp()) {
				event.getPlayer()
						.sendMessage(
								"[InfChests] You're not allowed to create Infinite Chests");
				event.getBlock().breakNaturally();
				return;
			}
		}
		
		//SAVED CONTENT CHEST - SIGN CHECK
		if (event.getLine(1).isEmpty()
				|| event.getLine(1).toLowerCase().contains("contents")) {
			Chest chest = getNearestChest(event.getBlock());

			if (chest == null) {
				event.getPlayer().sendMessage(
						"[InfChests] There is no chest to save contents from!");
				event.getBlock().breakNaturally();
			}

			plugin.inventories.put(chest.getBlock(), chest
					.getInventory().getContents());
			event.getPlayer().sendMessage(
					"[InfChests] Saved chest contents to be infinite.");

			return;
		}

		//ITEM CHEST - SIGN CHECK
		if (Items.itemByName(event.getLine(1)) == null) {
			event.getPlayer().sendMessage(
					"[InfChests] The item you entered doesn't exists.");
			event.getBlock().breakNaturally();
			return;
		}
		
		//STACKSIZE CHECK - SIGN CHECK
		if (!event.getLine(2).isEmpty()) {
			try {
				int sizetest = Integer.parseInt(event.getLine(2));
				if (sizetest > 64 && sizetest < 1) {
					event.getPlayer()
							.sendMessage(
									"[InfChests] The Stacksize isn't valid. Its to big or to small!");
					event.getBlock().breakNaturally();
					return;
				}
			} catch (Exception ex) {
				event.getPlayer()
						.sendMessage(
								"[InfChests] The Stacksize isn't valid. Use only numbers on line 3.");
				event.getBlock().breakNaturally();
				return;
			}
		}
		
		//STACKSIZE CHECK - SUCCESS!
		event.getPlayer().sendMessage(
				"[InfChests] Sucessfully created the information sign!");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		//RIGHT CLICKED?
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign;
			
			//CHECK IF BLOCK IS CHEST
			if (event.getClickedBlock().getType() != Material.CHEST) {
				return;
			}
			
			//FINDING CLOSEST SIGN
			sign = getNearestSign(event.getClickedBlock());
			
			//CHECK IF SIGN IS INFCHEST
			if (sign == null
					|| !sign.getLine(0).toLowerCase().contains("[infchest]")) {
				return;
			}
			
			//CHECK IF CAN OPEN. TEMP DISABLED. ACTIVATED ON REQUEST.
			//PERMISSIONS CHECK "!infchests.open"
			/*
			try {
				if (!plugin.permission.has(event.getPlayer().getWorld(), event.getPlayer()
						.getName(), "!infchests.open")) {
					event.getPlayer()
							.sendMessage(
									"[InfChests] You're not allowed to open Infinite Chests");
					
					event.setCancelled(true);
					return;
				}
			} catch (Exception ex) {
				if (!event.getPlayer().isOp()) {
					event.getPlayer()
							.sendMessage(
									"[InfChests] You're not allowed to open Infinite Chests");

					event.setCancelled(true);
					return;
				}
			}
			*/
			
			//SAVED CONTENTS CHEST.
			if (sign.getLine(1).isEmpty()
					|| sign.getLine(1).toLowerCase().contains("contents")) {
				if (plugin.inventories.containsKey(event
						.getClickedBlock())) {
					Chest chest = (Chest) event.getClickedBlock().getState();
					chest.getInventory().setContents(
							plugin.inventories.get(event
									.getClickedBlock()));
				} else {
					Chest chest = (Chest) event.getClickedBlock().getState();
					plugin.inventories.put(event.getClickedBlock(),
							chest.getInventory().getContents());
				}

				return;
			}
			
			//ITEM CHEST
			ItemInfo iinfo = Items.itemByName(sign.getLine(1));

			// StackSize option line
			int stacksize = 64;
			if (!sign.getLine(2).isEmpty()) {
				stacksize = Integer.parseInt(sign.getLine(2));
			}

			// Generating item with data.
			ItemStack item = new ItemStack(iinfo.toStack());
			item.setAmount(stacksize);

			// Filling in chests
			Chest chest = (Chest) event.getClickedBlock().getState();
			for (int i = 0; i < chest.getInventory().getSize(); i++) {
				chest.getInventory().setItem(i, item);
			}
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getType() != Material.DISPENSER) {
			return;
		}

		if (event.getBlock().isBlockPowered()
				|| event.getBlock().isBlockIndirectlyPowered()) {

			Sign sign;

			sign = getNearestSign(event.getBlock());

			if (sign == null
					|| !sign.getLine(0).toLowerCase().contains("[infchest]")) {
				return;
			}

			if (sign.getLine(1).isEmpty()
					|| sign.getLine(1).toLowerCase().contains("contents")) {
				if (plugin.inventories.containsKey(event.getBlock())) {
					Dispenser chest = (Dispenser) event.getBlock().getState();
					chest.getInventory().setContents(
							plugin.inventories.get(event.getBlock()));
				} else {
					Dispenser chest = (Dispenser) event.getBlock().getState();
					plugin.inventories.put(event.getBlock(), chest
							.getInventory().getContents());
				}

				return;
			}

			ItemInfo iteminfo = Items.itemByName(sign.getLine(1));

			// StackSize option line
			int stacksize = 64;
			if (!sign.getLine(2).isEmpty()) {
				stacksize = Integer.parseInt(sign.getLine(2));
			}

			//APPLYING STACK AMOUT
			ItemStack item = new ItemStack(iteminfo.toStack());
			item.setAmount(stacksize);

			// Filling in dispenser
			Dispenser chest = (Dispenser) event.getBlock().getState();
			for (int i = 0; i < chest.getInventory().getSize(); i++) {
				chest.getInventory().setItem(i, item);
			}
		}
	}

	public Sign getNearestSign(Block b) {
		int maxradius = 1;
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST,
				BlockFace.SOUTH, BlockFace.WEST };
		BlockFace[][] orth = { { BlockFace.UP, BlockFace.EAST },
				{ BlockFace.NORTH, BlockFace.EAST },
				{ BlockFace.NORTH, BlockFace.UP },
				{ BlockFace.EAST, BlockFace.EAST },
				{ BlockFace.SOUTH, BlockFace.EAST } };
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 10; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if (s >= 5)
					f = f.getOppositeFace();
				Block c = b.getRelative(f, r);
				for (int x = -r; x <= r; x++) {
					for (int y = -r; y <= r; y++) {
						Block a = c.getRelative(o[0], x).getRelative(o[1], y);
						if (a.getType() == Material.SIGN
								|| a.getType() == Material.WALL_SIGN
								|| a.getType() == Material.SIGN_POST)
							// && a.getRelative(BlockFace.UP).getTypeId() == 0)
							return (Sign) a.getState();
					}
				}
			}
		}
		return null;// no empty space within a cube of (2*(maxradius+1))^3
	}

	public Chest getNearestChest(Block b) {
		int maxradius = 1;
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST,
				BlockFace.SOUTH, BlockFace.WEST };
		BlockFace[][] orth = { { BlockFace.UP, BlockFace.EAST },
				{ BlockFace.NORTH, BlockFace.EAST },
				{ BlockFace.NORTH, BlockFace.UP },
				{ BlockFace.EAST, BlockFace.EAST },
				{ BlockFace.SOUTH, BlockFace.EAST } };
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 10; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if (s >= 5)
					f = f.getOppositeFace();
				Block c = b.getRelative(f, r);
				for (int x = -r; x <= r; x++) {
					for (int y = -r; y <= r; y++) {
						Block a = c.getRelative(o[0], x).getRelative(o[1], y);
						if (a.getType() == Material.CHEST)
							// && a.getRelative(BlockFace.UP).getTypeId() == 0)
							return (Chest) a.getState();
					}
				}
			}
		}
		return null;// no empty space within a cube of (2*(maxradius+1))^3
	}
}
