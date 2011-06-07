package net.hpxn.reagent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.hpxn.reagent.permissions.NijikokunPermissions;
import net.hpxn.reagent.permissions.OpPermissions;
import net.hpxn.reagent.permissions.PermissionProvider;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ReagentPlugin extends JavaPlugin {

	protected static final Logger log = Logger.getLogger( "Minecraft" );
	private Configuration config;
	private PluginDescriptionFile pdf;
	private ReagentPlayerListener pLst;
	public ConcurrentHashMap<Player, HashMap<String, Cast>> playerSpellMap;
	public PermissionProvider permissions;

	public void onDisable() {
		playerSpellMap = null;
		log.info( pdf.getName() + " v" + pdf.getVersion() + " - Disabled." );
	}

	public void onEnable() {
		config = getConfiguration();
		pdf = getDescription();

		permissions = NijikokunPermissions.create( getServer(), "reagent" );
		if ( permissions == null )
			permissions = new OpPermissions( new String[] { "reagent" } );

		pLst = new ReagentPlayerListener( this, config );

		playerSpellMap = new ConcurrentHashMap<Player, HashMap<String, Cast>>();

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent( Event.Type.PLAYER_INTERACT, pLst, Priority.Normal,
				this );

		log.info( pdf.getName() + " v" + pdf.getVersion() + " - Enabled." );
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label,
			String[] args ) {
		if ( !cmd.getName().equalsIgnoreCase( "reagent" ) ) {
			return false;
		}
		Player player = null;
		if ( sender instanceof Player ) {
			player = (Player) sender;
		}
		if ( args.length > 0 && config.getBoolean( "command", true ) ) {
			String spell = args[ 0 ];

			if ( !permissions.has( sender, "spells." + spell.toLowerCase() ) ) {
				sender.sendMessage( ChatColor.DARK_RED
						+ "You don't have permission to use this spell!" );
				return true;
			}

			if ( !isSpellAvailable( spell ) ) {
				player.sendMessage( ChatColor.YELLOW + "Unknown spell..." );
				return true;
			}

			initializeSpell( player, spell );
			return true;
		}
		return false;
	}

	public boolean isInitialized( Player player ) {
		HashMap<String, Cast> wCastMap = playerSpellMap.get( player );
		if ( wCastMap != null ) {
			for ( Entry<String, Cast> wCast : wCastMap.entrySet() ) {
				if ( wCast.getValue().isInitialized() ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean canCastSpell( Player player, String spell ) {
		return permissions.has( player, "spells." + spell.toLowerCase() );
	}

	public boolean initializeSpell( Player player, String spell ) {
		boolean wRemoveMaterials = true;
		if ( permissions.has( player, "free" )
				&& !(permissions instanceof OpPermissions) ) {
			player.sendMessage( ChatColor.AQUA + spell + " initialized. Free!" );
			wRemoveMaterials = false;
		}
		HashMap<String, Cast> wCastMap = playerSpellMap.get( player );
		if ( wCastMap != null ) {
			for ( Entry<String, Cast> wCast : wCastMap.entrySet() ) {
				if ( wCast.getValue().isInitialized() ) {
					player.sendMessage( ChatColor.DARK_RED + "The "
							+ wCast.getKey()
							+ " spell is already initialized. Cast it first." );
					return false;
				}
			}
			Cast wCast = wCastMap.get( spell );
			if ( wCast != null ) {
				int wCoolDown = getSpellCoolDown( spell );
				Calendar wNow = Calendar.getInstance();
				Calendar wLastUsed = Calendar.getInstance();
				if ( wCast.getLastUsed() != null ) {
					wLastUsed.setTime( wCast.getLastUsed() );
					wLastUsed.add( Calendar.SECOND, wCoolDown );
					if ( wNow.after( wLastUsed ) ) {
						if ( hasMaterials( player, spell, wRemoveMaterials ) ) {
							wCastMap.put( spell, new Cast() );
							playerSpellMap.put( player, wCastMap );
						}
					} else {
						wLastUsed.setTime( wCast.getLastUsed() );
						long wMilliseconds = wCoolDown
								* 1000
								- (wNow.getTimeInMillis() - wLastUsed
										.getTimeInMillis());
						player.sendMessage( ChatColor.DARK_RED + spell
								+ " must cooldown. " + (wMilliseconds / 1000)
								+ " seconds remaining." );
						return false;
					}
				}
			} else {
				if ( hasMaterials( player, spell, wRemoveMaterials ) ) {
					wCastMap.put( spell, new Cast() );
				}
			}
		} else {
			if ( hasMaterials( player, spell, wRemoveMaterials ) ) {
				wCastMap = new HashMap<String, Cast>();
				wCastMap.put( spell, new Cast() );
				playerSpellMap.put( player, wCastMap );
			}
		}
		return true;
	}

	/**
	 * Returns the cooldown for the specified spell.
	 * 
	 * @param spell
	 * @return int - cooldown in seconds
	 */
	private int getSpellCoolDown( String spell ) {
		return config.getInt( "spells." + spell + ".cooldown", 0 );
	}

	/**
	 * Check if a spell has been configured in the config.yml. If not then the
	 * spell is disabled.
	 * 
	 * @param spell
	 * @return true if spell is in config.yml. false otherwise.
	 */
	private boolean isSpellAvailable( String spell ) {
		if ( config.getProperty( "spells." + spell + ".materials" ) == null ) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if a player has the required spell materials in their inventory.
	 * If the remove boolean is true then the items will also be removed.
	 * 
	 * @param player
	 * @param spell
	 * @param remove
	 * @return true if player has all required materials. false otherwise.
	 */
	private boolean hasMaterials( Player player, String spell, boolean remove ) {
		for ( Entry<?, ?> wMlsAmt : ((Map<?, ?>) config.getProperty( "spells."
				+ spell + ".materials" )).entrySet() ) {
			Material wMaterial = Material.valueOf( ((String) wMlsAmt.getKey())
					.toUpperCase() );
			Integer wCost = (Integer) wMlsAmt.getValue();
			if ( !player.getInventory().contains( wMaterial, wCost ) ) {
				player.sendMessage( ChatColor.RED
						+ "Not enough materials to cast " + spell + "." );
				if ( config.getBoolean( "hint", false ) ) {
					player.sendMessage( ChatColor.RED + "Missing "
							+ wMaterial.name().toLowerCase() + "." );
				}
				return false;
			}
		}
		if ( remove ) {
			removeMaterials( player, spell );
		}
		return true;
	}

	/**
	 * Removes items from a players inventory based on spell type.
	 * 
	 * @param player
	 * @param spell
	 */
	private void removeMaterials( Player player, String spell ) {
		String wSpellcost = "";
		for ( Entry<?, ?> wMtlsAmt : ((Map<?, ?>) config.getProperty( "spells."
				+ spell + ".materials" )).entrySet() ) {
			Material wMtl = Material.valueOf( ((String) wMtlsAmt.getKey())
					.toUpperCase() );
			Integer wAmt = (Integer) wMtlsAmt.getValue();
			player.getInventory().removeItem( new ItemStack( wMtl, wAmt ) );
			wSpellcost += wAmt + " " + wMtl.name().toLowerCase() + " ";
		}
		wSpellcost = wSpellcost.replace( '_', ' ' );
		player.sendMessage( ChatColor.AQUA + spell + " spell ready! "
				+ wSpellcost + "consumed." );
	}
}
