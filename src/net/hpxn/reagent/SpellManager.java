package net.hpxn.reagent;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class SpellManager {
	private static SpellManager instance = null;
	public final static int MAX_HEALTH = 20;
	
	public static SpellManager getInstance() {
		if ( instance == null ) {
			instance = new SpellManager();
		}
		return instance;
	}

	public boolean recall(Player player, Cast cast) {
		// Hmm. Find a good way to get other casts in the player spell map.
		return true;
	}
	
	public boolean mark(Player player, Cast cast) {
		cast.getProperties().put("location", player.getLocation());
		player.sendMessage("Location marked!");
		return true;
	}
	
	/**
	 * Casts the scan spell. Scans the area for monsters and returns how many 
	 * their are to the player. Should improve this eventually.
	 * 
	 * @param player
	 * @param cast
	 * @return true if successful
	 */
	public boolean scan( Player player, Cast cast ) {
		final int SCAN_MAX_DISTANCE = 30;
		int wMonsterCount = 0;
		for ( Entity wMonster : player.getWorld().getEntities() ) {
			if ( wMonster instanceof Monster ) {
				double wDistance = Util.getDistance(
						player.getLocation(), wMonster.getLocation() );
				if ( wDistance < SCAN_MAX_DISTANCE ) {
					wMonsterCount++;
				}
			}
		}
		player.sendMessage( wMonsterCount + " monsters are nearby..." );
		return true;
	}
	
	/**
	 * Casts the megabolt spell. This spell will strike all monsters in the area
	 * with a bolt of lightning 1 second apart. (30x30)
	 * 
	 * @param player
	 * @param cast
	 * @return true if success
	 */
	public boolean megabolt( final Player player, Cast cast ) {
		final int MEGABOLT_MAX_DISTANCE = 30;
		new Thread( new Runnable() {
			public void run() {
				for ( Entity wMonster : player.getWorld().getEntities() ) {
					if ( wMonster instanceof Monster ) {
						double wDistance = Util.getDistance(
								player.getLocation(), wMonster.getLocation() );
						if ( wDistance < MEGABOLT_MAX_DISTANCE ) {
							player.getWorld().strikeLightning(
									wMonster.getLocation() );
							try {
								Thread.sleep( 1000 );
							} catch ( InterruptedException e ) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} ).start();
		return true;
	}
	
	/**
	 * Casts the house spell. This spell builds a small house for the player to 
	 * rest in.
	 * 
	 * @param player
	 * @param cast
	 * @return true if success
	 */
	public boolean house(Player player, Cast cast) {
		// Bleh! I want this spell but I am too lazy.
		return true;
	}

	/**
	 * Casts the timetravel spell. This moves time forward around 2 hours.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean timetravel(Player player, Cast cast) {
		player.getWorld().setTime(player.getWorld().getTime() + 5000 );
		cast.setSuccess(true);
		cast.setLastUsed(new Date());
		return true;
	}

	/**
	 * Casts the teleportspawn spell. This teleports the player to their spawn
	 * location.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean teleportspawn(Player player, Cast cast) {
		player.teleport(player.getCompassTarget());
		return true;
	}

	/**
	 * Casts the air spell. This replenishes your air supply while underwater.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean air(Player player, Cast cast) {
		player.setMaximumAir(20);
		player.setRemainingAir(20);
		return true;
	}

	/**
	 * Casts the heal spell. This heals your character completely.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean heal(Player player, Cast cast) {
		player.setHealth(MAX_HEALTH);
		return true;
	}

	/**
	 * Casts the arrowstorm spell. This shoots 5 arrows simultaneously. This
	 * spell might suck.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean arrowstorm(Player player, Cast cast) {
		for (int x = 0; x < 5; x++) {
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
	public boolean skeleton(Player player, Cast cast) {
		Block wTargetBlock = player.getTargetBlock(null, 20).getFace(
				BlockFace.UP);
		player.getWorld().spawnCreature(wTargetBlock.getLocation(),
				CreatureType.SKELETON);
		return true;
	}

	/**
	 * Casts the stonewall spell. Creates a stone wall infront of the player. I
	 * am not really happy with this implementation. I just wanted to see how it
	 * looked. I will try and make this better eventually.
	 * 
	 * @param player
	 * @return
	 */
	public boolean stonewall(Player player, Cast cast) {
		Block wTargetBlock = player.getTargetBlock(null, 25);
		Util.setRelativeBlocks(wTargetBlock, BlockFace.UP,
				Material.COBBLESTONE, 3);
		Block wOneUp = wTargetBlock.getRelative(BlockFace.UP);
		Block wTwoUp = wOneUp.getRelative(BlockFace.UP);
		Block wThreeUp = wTwoUp.getRelative(BlockFace.UP);
		Block wFourUp = wThreeUp.getRelative(BlockFace.UP);

		BlockFace wDirection = Util.getPlayerDirection(player);
		if (wDirection == BlockFace.NORTH || wDirection == BlockFace.SOUTH) {
			Util.setRelativeBlocks(wOneUp, BlockFace.EAST,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wOneUp, BlockFace.WEST,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wTwoUp, BlockFace.EAST,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wTwoUp, BlockFace.WEST,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wThreeUp, BlockFace.EAST,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wThreeUp, BlockFace.WEST,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wFourUp, BlockFace.EAST,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wFourUp, BlockFace.WEST,
					Material.COBBLESTONE, 3);
		} else if (wDirection == BlockFace.EAST || wDirection == BlockFace.WEST) {
			Util.setRelativeBlocks(wOneUp, BlockFace.NORTH,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wOneUp, BlockFace.SOUTH,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wTwoUp, BlockFace.NORTH,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wTwoUp, BlockFace.SOUTH,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wThreeUp, BlockFace.NORTH,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wThreeUp, BlockFace.SOUTH,
					Material.COBBLESTONE, 3);

			Util.setRelativeBlocks(wFourUp, BlockFace.NORTH,
					Material.COBBLESTONE, 3);
			Util.setRelativeBlocks(wFourUp, BlockFace.SOUTH,
					Material.COBBLESTONE, 3);
		}
		return true;
	}

	public boolean firewall(Player player, Cast cast) {
		Block wTargetBlock = player.getTargetBlock(null, 25);
		Util.setRelativeBlocks(wTargetBlock, BlockFace.UP, Material.FIRE, 3);
		Block wOneUp = wTargetBlock.getRelative(BlockFace.UP);
		Block wTwoUp = wOneUp.getRelative(BlockFace.UP);

		BlockFace wDirection = Util.getPlayerDirection(player);
		if (wDirection == BlockFace.NORTH || wDirection == BlockFace.SOUTH) {
			Util.setRelativeBlocks(wOneUp, BlockFace.EAST, Material.FIRE, 3);
			Util.setRelativeBlocks(wOneUp, BlockFace.WEST, Material.FIRE, 3);

			Util.setRelativeBlocks(wTwoUp, BlockFace.EAST, Material.FIRE, 3);
			Util.setRelativeBlocks(wTwoUp, BlockFace.WEST, Material.FIRE, 3);
		} else if (wDirection == BlockFace.EAST || wDirection == BlockFace.WEST) {
			Util.setRelativeBlocks(wOneUp, BlockFace.NORTH, Material.FIRE, 3);
			Util.setRelativeBlocks(wOneUp, BlockFace.SOUTH, Material.FIRE, 3);

			Util.setRelativeBlocks(wTwoUp, BlockFace.NORTH, Material.FIRE, 3);
			Util.setRelativeBlocks(wTwoUp, BlockFace.SOUTH, Material.FIRE, 3);
		}
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
	public boolean teleport(Player player, Cast cast) {
		Block wTargetBlock = player.getTargetBlock(null, Util.MAX_DISTANCE);
		Block wOneAbove = wTargetBlock.getRelative(BlockFace.UP);
		Block wTwoAbove = wOneAbove.getRelative(BlockFace.UP);
		if (wOneAbove.getType() == Material.AIR
				&& wTwoAbove.getType() == Material.AIR) {
			player.teleport(wTargetBlock.getFace(BlockFace.UP).getLocation());
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
	public boolean bolt(Player player, Cast cast) {
		Location wLocation = player.getTargetBlock(null, Util.MAX_DISTANCE)
				.getLocation();
		player.getWorld().strikeLightning(wLocation);
		return true;
	}

	/**
	 * Casts the fire spell. 3x3 grid of fire.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean fire(Player player, Cast cast) {
		Block wTarget = player.getTargetBlock(null, Util.MAX_DISTANCE);
		wTarget.getFace(BlockFace.UP).setType(Material.FIRE);
		wTarget.getRelative(BlockFace.EAST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.WEST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.NORTH).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.SOUTH).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.NORTH_EAST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.NORTH_WEST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.SOUTH_EAST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		wTarget.getRelative(BlockFace.SOUTH_WEST).getFace(BlockFace.UP)
				.setType(Material.FIRE);
		return true;
	}

	/**
	 * Casts the storm spell. This is called storm because the precipitation
	 * type depends on the biome the player is currently in when the spell is
	 * cast.
	 * 
	 * @param player
	 * @return true if success
	 */
	public boolean storm(Player player, Cast cast) {
		player.getWorld().setStorm(true);
		player.getWorld().setThundering(true);
		player.getWorld().setThunderDuration(100);
		player.getWorld().setWeatherDuration(500);
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
	public boolean timebomb(Player player, Cast cast) {
		Block wTargetBlock = player.getTargetBlock(null, Util.MAX_DISTANCE);
		wTargetBlock.setTypeId(46, false);
		wTargetBlock.getFace(BlockFace.UP).setType(Material.FIRE);
		return true;
	}
}
