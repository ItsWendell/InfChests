package net.wmisiedjan.bukkit.InfChests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class InfChests extends JavaPlugin {

	public StackableLogger log = null;
	public PluginDescriptionFile pdfFile = null;
	public Permission permission = null;
	public Map<Block, ItemStack[]> inventories = new HashMap<Block, ItemStack[]>();
	//Block - CanUpdate(fill)
	public List<Location> timers = new ArrayList<Location>();
	public Metrics stats;

	public PlayerListener playerListener = null;

	public void onEnable() {
		// Initiating PDF
		pdfFile = getDescription();

		// Initiating Logger
		log = new StackableLogger(pdfFile.getName());

		// Initiating Listeners
		playerListener = new PlayerListener(this);
		
		//Stats
		try {
		    stats = new Metrics(this);
		    stats.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}

		// Showing initial loading Message
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion()
				+ " is loading...");
		
		versionMessages();

		// Registering Listeners
		getServer().getPluginManager().registerEvents(playerListener, this);
	}

	public void versionMessages() {
		if (pdfFile.getVersion().toLowerCase().contains("a")) {
			log.info("You're using a ALPHA version of " + pdfFile.getName()
					+ pdfFile.getVersion());
			log.info("This version is provided \"as is\" and may or may not be suitable for production use.");
			log.info("Please report any bugs or suggestions on BukkitDev or the Bukkit.org forums.");
		} else if (pdfFile.getVersion().toLowerCase().contains("b")) {
			log.info("You're using a BETA version of " + pdfFile.getName()
					+ pdfFile.getVersion());
			log.info("This version is provided \"as is\" and may or may not be suitable for production use.");
			log.info("Please report any bugs or suggestions on BukkitDev or the Bukkit.org forums.");
		}

	}
}
