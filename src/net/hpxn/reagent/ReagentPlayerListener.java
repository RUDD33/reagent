package net.hpxn.reagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

public class ReagentPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger( "Minecraft" );
	private final ReagentPlugin rp;
	private Configuration config;
	private final int MAX_HEALTH = 20;
	private final int MAX_DISTANCE = 120;

	public ReagentPlayerListener( ReagentPlugin rp ) {
		this.rp = rp;
	}

	public void onPlayerInteract( PlayerInteractEvent event ) {
		if ( (event.getAction() == Action.RIGHT_CLICK_BLOCK)
						|| (event.getAction() == Action.RIGHT_CLICK_AIR) ) {
			Player player = event.getPlayer();
			try {
				String wSpell = rp.playerSpellMap.get( player );
				if ( wSpell != null ) {
					Method wMethod =
						this.getClass().getMethod( wSpell, Player.class );
					boolean isSuccess = (Boolean) wMethod.invoke( this, player );

					if ( isSuccess ) {
						if ( config.getBoolean( "broadcast", true ) ) {
							rp.getServer().broadcastMessage(
								player.getName() + " cast " + wSpell + "!" );
						} else {
							player.sendMessage( "You cast " + wSpell + "!" );
						}
						rp.playerSpellMap.remove( player );
					} else {
						player.sendMessage( "fizzle..." );
					}
				}
			} catch ( SecurityException e ) {
				log.severe( "Reagent: " + e.getMessage() );
			} catch ( IllegalArgumentException e ) {
				log.severe( "Reagent: " + e.getMessage() );
			} catch ( NoSuchMethodException e ) {
				player.sendMessage( "Unknown spell...." );
			} catch ( IllegalAccessException e ) {
				log.severe( "Reagent: " + e.getMessage() );
			} catch ( InvocationTargetException e ) {
				log.severe( "Reagent: " + e.getMessage() );
			}
		}
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig( Configuration config ) {
		this.config = config;
	}

	/**
	 * Casts the timetravel spell. This moves time forward 1 hour.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean timetravel( Player player ) {
		// TODO timetravel.
		return true;
	}

	/**
	 * Casts the teleportspawn spell. This teleports the player to their spawn
	 * location.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean teleportspawn( Player player ) {
		player.teleport( player.getCompassTarget() );
		return true;
	}

	/**
	 * Casts the air spell. This replenishes your air supply while underwater.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean air( Player player ) {
		player.setMaximumAir( 20 );
		player.setRemainingAir( 20 );
		return true;
	}

	/**
	 * Casts the heal spell. This heals your character completely.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean heal( Player player ) {
		player.setHealth( MAX_HEALTH );
		return true;
	}

	/**
	 * Casts the arrowstorm spell. This shoots 5 arrows simultaneously. This
	 * spell might suck.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean arrowstorm( Player player ) {
		for ( int x = 0; x < 5; x++ ) {
			player.shootArrow();
		}
		return true;
	}

	/**
	 * Casts the skeleton spell. This spell spawns a skeleton at a maximum of 20
	 * blocks from the player location. Not much use since it will probably
	 * attack you.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean skeleton( Player player ) {
		Block wTargetBlock =
			player.getTargetBlock( null, 20 ).getFace( BlockFace.UP );
		player.getWorld().spawnCreature( wTargetBlock.getLocation(),
			CreatureType.SKELETON );
		return true;
	}

	public boolean stonewall( Player player ) {
		Block wTargetBlock = player.getTargetBlock( null, 20 );
		double wTargetX = wTargetBlock.getX();
		double wTargetZ = wTargetBlock.getZ();

		double wCenterX = player.getLocation().getX();
		double wCenterZ = player.getLocation().getZ();

		double wAngle = Math.atan( (wTargetX - wCenterX) / (wCenterZ - wTargetZ) ) * (180 / Math.PI);
		
		if (wTargetX > wCenterX && wTargetZ > wCenterZ) {
			wAngle = (90 + wAngle) + 90;
		} else if (wTargetX < wCenterX && wTargetZ > wCenterZ ) {
			wAngle = wAngle + 180;
		} else if ( wTargetX < wCenterX && wTargetZ < wCenterZ ) {
			wAngle = (90 + wAngle) + 270;
		}
		
		player.sendMessage( "Degrees: " + wAngle );

		return true;
	}

	public boolean firewall( Player player ) {
		// TODO firewall.
		return true;
	}

	/**
	 * Casts the teleport spell. Player will be teleported to the selected cube.
	 * Maximum teleport distance of MAX_DISTANCE squares. The target block must
	 * have at least 2 air blocks above it or the spell will fizzle.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean teleport( Player player ) {
		Block wTargetBlock = player.getTargetBlock( null, MAX_DISTANCE );
		Block wOneAbove = wTargetBlock.getRelative( BlockFace.UP );
		Block wTwoAbove = wOneAbove.getRelative( BlockFace.UP );
		if ( wOneAbove.getType() == Material.AIR
						&& wTwoAbove.getType() == Material.AIR ) {
			player.teleport( wTargetBlock.getFace( BlockFace.UP ).getLocation() );
			return true;
		}
		return false;
	}

	/**
	 * Casts the bolt spell. 1 square of lightning will hit the ground.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean bolt( Player player ) {
		Location wLocation =
			player.getTargetBlock( null, MAX_DISTANCE ).getLocation();
		player.getWorld().strikeLightning( wLocation );
		return true;
	}

	/**
	 * Casts the fire spell. 3x3 grid of fire.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean fire( Player player ) {
		Block wTarget = player.getTargetBlock( null, MAX_DISTANCE );
		wTarget.getFace( BlockFace.UP ).setType( Material.FIRE );
		wTarget.getRelative( BlockFace.EAST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.WEST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.NORTH )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.SOUTH )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.NORTH_EAST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.NORTH_WEST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.SOUTH_EAST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		wTarget.getRelative( BlockFace.SOUTH_WEST )
						.getFace( BlockFace.UP )
						.setType( Material.FIRE );
		return true;
	}

	/**
	 * Casts the precip spell. This is called precip because the precipitation
	 * type depends on the biome the player is currently in when the spell is
	 * cast.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean precip( Player player ) {
		player.getWorld().setStorm( true );
		player.getWorld().setThundering( true );
		player.getWorld().setThunderDuration( 100 );
		player.getWorld().setWeatherDuration( 500 );
		return true;
	}

	/**
	 * Casts the timebomb spell. This kind of a hack because there does not seem
	 * to be a way to actually make a block explode. This will change the
	 * targeted block into TNT then light the TNT on fire.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean timebomb( Player player ) {
		Block wTargetBlock = player.getTargetBlock( null, MAX_DISTANCE );
		wTargetBlock.setTypeId( 46, false );
		wTargetBlock.getFace( BlockFace.UP ).setType( Material.FIRE );
		return true;
	}
}
