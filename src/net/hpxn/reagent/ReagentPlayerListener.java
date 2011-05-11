package net.hpxn.reagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

public class ReagentPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static ReagentPlugin rp;
	private Configuration config;

	public ReagentPlayerListener(ReagentPlugin plugin, Configuration config) {
		rp = plugin;
		this.config = config;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
				|| (event.getAction() == Action.RIGHT_CLICK_AIR)) {
			Player player = event.getPlayer();
			castSpell(player);
		}
	}
	
	private void castSpell(Player player) {
		try {
			HashMap<String, Cast> wCastMap = rp.playerSpellMap.get(player);
			if (wCastMap != null) {
				for (Entry<String, Cast> wCast : wCastMap.entrySet()) {
					if (wCast.getValue().isInitialized()) {
						SpellManager wSpellManager = SpellManager.getInstance();
						Method wMethod = wSpellManager.getClass().getMethod(
								wCast.getKey(), Player.class, Cast.class);
						boolean isSuccess = (Boolean) wMethod.invoke(
								wSpellManager, player, wCast.getValue());

						if (isSuccess) {
							wCast.getValue().setInitialized(false);
							wCast.getValue().setLastUsed(new Date());
							if (config.getBoolean("broadcast", true)) {
								rp.getServer().broadcastMessage(
										ChatColor.AQUA + player.getName()
												+ " cast " + wCast.getKey()
												+ "!");
							} else {
								player.sendMessage(ChatColor.AQUA + "You cast "
										+ wCast.getKey() + "!");
							}
						} else {
							player.sendMessage(ChatColor.YELLOW + "fizzle...");
						}
					}
				}
			}
		} catch (SecurityException e) {
			log.severe("Reagent: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			log.severe("Reagent: " + e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			player.sendMessage(ChatColor.YELLOW + "Unknown spell....");
		} catch (IllegalAccessException e) {
			log.severe("Reagent: " + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.severe("Reagent: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
