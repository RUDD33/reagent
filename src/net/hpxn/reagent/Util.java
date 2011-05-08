package net.hpxn.reagent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Util {
	// Maximum block target distance.
	public final static int MAX_DISTANCE = 120;
	/**
	 * Returns the distance between 2 points in 3d space... I think.
	 * 
	 * @param loc1
	 * @param loc2
	 * @return double - the distance
	 */
	public static double getDistance(Location loc1, Location loc2) {
		return Math.sqrt( Math.pow(
				(loc2.getX() - loc1.getX()), 2 )
				+ Math.pow( (loc2.getZ() - loc1.getZ()), 2 ) +
				Math.pow( (loc2.getY() - loc1.getY()), 2 ) );
	}
	
	/**
	 * Sets relative blocks to a type based on side. Should fix this up...
	 * 
	 * @param targetBlock
	 * @param blockFace
	 * @param material
	 * @param num
	 */
	public static void setRelativeBlocks(Block targetBlock, BlockFace blockFace,
			Material material, int num) {
		targetBlock = targetBlock.getRelative(blockFace);
		targetBlock.setType(material);

		if (num > 0) {
			num--;
			setRelativeBlocks(targetBlock, blockFace, material, num);
		}
	}

	/**
	 * Returns what direction the player is facing as a block face. If anyone
	 * knows a better way to determine this let me know.
	 * 
	 * @param player
	 * @return BlockFace
	 */
	public static BlockFace getPlayerDirection(Player player) {
		Block wTargetBlock = player.getTargetBlock(
			null, MAX_DISTANCE);
		double wTargetX = wTargetBlock.getX();
		double wTargetZ = wTargetBlock.getZ();

		double wCenterX = player.getLocation().getX();
		double wCenterZ = player.getLocation().getZ();

		double wAngle = Math
				.atan((wTargetX - wCenterX) / (wCenterZ - wTargetZ))
				* (180 / Math.PI);

		if (wTargetX > wCenterX && wTargetZ > wCenterZ) {
			wAngle = (90 + wAngle) + 90;
		} else if (wTargetX < wCenterX && wTargetZ > wCenterZ) {
			wAngle = wAngle + 180;
		} else if (wTargetX < wCenterX && wTargetZ < wCenterZ) {
			wAngle = (90 + wAngle) + 270;
		}

		BlockFace wDirection = null;
		if (wAngle < 45) {
			// player facing east.
			wDirection = BlockFace.EAST;
		} else if (wAngle < 135) {
			// player facing south.
			wDirection = BlockFace.SOUTH;
		} else if (wAngle < 225) {
			// player facing west.
			wDirection = BlockFace.WEST;
		} else if (wAngle < 315) {
			// player facing north.
			wDirection = BlockFace.NORTH;
		} else if (wAngle < 360) {
			// player facing east.
			wDirection = BlockFace.EAST;
		}

		return wDirection;
	}
}
