package net.hpxn.reagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class ReagentPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static ReagentPlugin rp;
	private Configuration config;
	private Map<Player, Integer> selectedSpell;

	public ReagentPlayerListener(ReagentPlugin plugin, Configuration config) {
		rp = plugin;
		this.config = config;
		selectedSpell = new ConcurrentHashMap<Player, Integer>();
	}

	public void onPlayerInteract( PlayerInteractEvent event ) {
		Player player = event.getPlayer();
		String wCastItem = config.getString( "item", "false" );
		Material wCastMaterial = null;
		
		boolean wUseItem = !"false".equalsIgnoreCase( wCastItem );
		
		if ( wUseItem ) {
			wCastMaterial = Material.valueOf( wCastItem.toUpperCase() );
		}
		
		ItemStack wItemInHand = player.getItemInHand();
		
		if ( (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				|| (event.getAction() == Action.RIGHT_CLICK_AIR) ) {

			if ( castSpell( player ) ) {
				return;
			}

			if ( wItemInHand.getType() == wCastMaterial && wUseItem ) {
				player.sendMessage( "Selected spell: "
						+ getNextSpell( player ) );
			}

		} else if ( (event.getAction() == Action.LEFT_CLICK_BLOCK)
				|| (event.getAction() == Action.LEFT_CLICK_AIR) ) {
			if ( wItemInHand.getType() == wCastMaterial && wUseItem ) {
				rp.initializeSpell( player, getSelectedSpell( player ) );
			}
			
		}
	}
	
	private String getSelectedSpell( Player player ) {
		LinkedList<String> wSpellList = new LinkedList<String>();
		for ( Entry<?, ?> wSpell : ((Map<?, ?>) config.getProperty( "spells" ))
				.entrySet() ) {
			if ( rp.canCastSpell( player, (String) wSpell.getKey() ) ) {
				wSpellList.add( (String) wSpell.getKey() );
			}
		}
		return wSpellList.get( selectedSpell.get( player ) );
	}
	
	private String getNextSpell( Player player ) {
		int wSelectedSpell = 0;
		if ( selectedSpell.get( player ) != null ) {
			wSelectedSpell = selectedSpell.get( player );
		}
		LinkedList<String> wSpellList = new LinkedList<String>();
		for ( Entry<?, ?> wSpell : ((Map<?, ?>) config.getProperty( "spells" ))
				.entrySet() ) {
			if ( rp.canCastSpell( player, (String) wSpell.getKey() ) ) {
				wSpellList.add( (String) wSpell.getKey() );
			}
		}

		String wNextSpellName;
		int wNextSpellId = 0;
		try {
			wNextSpellId = wSelectedSpell + 1;
			wNextSpellName = wSpellList.get( wNextSpellId );
		} catch ( IndexOutOfBoundsException e ) {
			wNextSpellId = 0;
			wNextSpellName = wSpellList.get( 0 );
		}
		selectedSpell.put( player, wNextSpellId );
		return wNextSpellName;
	}
	
	private boolean castSpell(Player player) {
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
								return true;
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
		
		return false;
	}
}
