package net.hpxn.reagent;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Util {
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
			null, ReagentPlayerListener.MAX_DISTANCE);
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
