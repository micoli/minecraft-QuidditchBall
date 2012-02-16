package org.micoli.quidditchBall.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.utils.ChatFormatter;
import org.micoli.quidditchBall.QuidditchBall;

public final class QDCommandManager implements CommandExecutor {
	private QuidditchBall plugin;

	public QDCommandManager(QuidditchBall plugin){
		this.plugin = plugin;
	}

	/**
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 * @return
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try{
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (command.getName().equalsIgnoreCase(QuidditchBall.getCommandString()))
					if (args.length > 0) {
						QuidditchBall.log("[QuidditchBall] Command " + args[0]);
						if (args.length == 1){
							if (args[0].equalsIgnoreCase("commentsOn")){
								QuidditchBall.setComments(player, true);
							}else if (args[0].equalsIgnoreCase("commentsOff")){
								QuidditchBall.setComments(player, false);
							}else if (args[0].equalsIgnoreCase("create")){
								plugin.createBall(player,false);
							}else if (args[0].equalsIgnoreCase("create3D")){
								plugin.createBall(player,true);
							}
						}
						if (args.length > 1){
							if (args[0].equalsIgnoreCase("remove")){
								plugin.removeBall(player, args[1]);
							}else if (args[0].equalsIgnoreCase("convert")){
								plugin.addBall(player,args[1]);
							}else if (args[0].equalsIgnoreCase("strenght")){
								plugin.setStrenght(player, Integer.parseInt(args[1]));
							}
						}
					} else {
						player.sendMessage(ChatFormatter.format("{ChatColor.RED} Need more arguments"));
					}
			} else {
				QuidditchBall.log(ChatFormatter.format("{ChatColor.RED}Pushball requires you to be a Player"));
			}
			return false;
		} catch (Exception ex) {
			QuidditchBall.log(ChatFormatter.format("{ChatColor.RED}Command failure: %s",ex.getMessage()));
		}

		return false;
	}
}
