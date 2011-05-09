package net.hpxn.reagent.permissions;

import java.util.HashSet;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpPermissions implements PermissionProvider {
	public HashSet<String> opCommands = new HashSet<String>();

	public OpPermissions( String[] opCommands ) {
		for ( String opCommand : opCommands ) {
			this.opCommands.add( opCommand );
		}
	}

	public boolean has( CommandSender sender, String permission ) {
		if ( sender instanceof Player ) {
			return ((Player)sender).isOp();
		}
		return false;
	}
}
