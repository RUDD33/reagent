package net.hpxn.reagent;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.hpxn.reagent.permissions.NijikokunPermissions;
import net.hpxn.reagent.permissions.OpPermissions;
import net.hpxn.reagent.permissions.PermissionProvider;

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
	private final ReagentPlayerListener pLst = new ReagentPlayerListener( this );
	public ConcurrentHashMap<Player, String> playerSpellMap;
	public PermissionProvider permissions;

	public void onDisable() {
		log.info( pdf.getName() + " v" + pdf.getVersion() + " - Disabled." );
	}

	public void onEnable() {
		config = getConfiguration();
		pdf = getDescription();

		permissions = NijikokunPermissions.create( getServer(), "reagent" );
		if ( permissions == null )
			permissions = new OpPermissions( new String[] { "reagent" } );

		pLst.setConfig( config );
		playerSpellMap = new ConcurrentHashMap<Player, String>();

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
		if ( args.length > 0 ) {
			String spell = args[ 0 ];

			if ( !permissions.has( sender, spell.toLowerCase() ) ) {
				sender.sendMessage( "You don't have permission to use this spell!" );
				return true;
			}

			if ( !isSpellAvailable( spell ) ) {
				player.sendMessage( "Unknown spell..." );
				return true;
			}

			if ( hasMaterials( player, spell, true ) ) {
				playerSpellMap.put( player, spell );
			}
			return true;
		}
		return false;
	}

	/**
	 * Check if a spell has been configured in the config.yml. If not then the
	 * spell is disabled.
	 * 
	 * @param spell
	 * @return true if spell is in config.yml. false otherwise.
	 */
	private boolean isSpellAvailable( String spell ) {
		if ( config.getProperty( "spells." + spell ) == null ) {
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
		for ( Entry<?, ?> wMlsAmt : ((Map<?, ?>) config
				.getProperty( "spells." + spell )).entrySet() ) {
			Material wMaterial = Material.valueOf( ((String) wMlsAmt
					.getKey()).toUpperCase() );
			Integer wCost = (Integer) wMlsAmt.getValue();
			if ( !player.getInventory().contains( wMaterial, wCost ) ) {
				player.sendMessage( "Not enough materials to cast " + spell
						+ "." );
				if ( config.getBoolean( "hint", false ) ) {
					player.sendMessage( "Missing "
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
				+ spell )).entrySet() ) {
			Material wMtl = Material.valueOf( ((String) wMtlsAmt.getKey())
					.toUpperCase() );
			Integer wAmt = (Integer) wMtlsAmt.getValue();
			removeItems( player, wMtl, wAmt );
			wSpellcost += wAmt + " " + wMtl.name().toLowerCase() + " ";
		}
		wSpellcost = wSpellcost.replace( '_', ' ' );
		player.sendMessage( spell + " spell ready! " + wSpellcost + "consumed." );
	}

	/**
	 * Removes item(s) from a players inventory.
	 * 
	 * @param player
	 * @param material
	 * @param amount
	 */
	private void removeItems( Player player, Material material, int amount ) {
		for ( ItemStack wItemStack : player.getInventory().getContents() ) {
			if ( wItemStack != null && wItemStack.getType() == material ) {
				if ( wItemStack.getAmount() == amount ) {
					player.getInventory().remove( wItemStack );
				} else if ( wItemStack.getAmount() > amount ) {
					wItemStack.setAmount( wItemStack.getAmount() - amount );
				}
			}
		}
	}
}
