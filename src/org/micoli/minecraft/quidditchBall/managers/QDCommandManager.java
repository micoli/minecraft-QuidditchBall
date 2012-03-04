package org.micoli.minecraft.quidditchBall.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.quidditchBall.QuidditchBall;
import org.micoli.minecraft.quidditchBall.entities.QDObjectGoal.GoalType;
import org.micoli.minecraft.utils.ChatFormater;

public final class QDCommandManager implements CommandExecutor {
	private QuidditchBall plugin;

	public QDCommandManager(QuidditchBall plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (command.getName().equalsIgnoreCase(QuidditchBall.getCommandString()))
					if (args.length > 0) {
						QuidditchBall.log("[QuidditchBall] Command " + args[0]);
						if (args[0].equalsIgnoreCase("commentsOn")) {
							QuidditchBall.setComments(player, true);
						} else if (args[0].equalsIgnoreCase("commentsOff")) {
							QuidditchBall.setComments(player, false);
						} else if (args[0].equalsIgnoreCase("create")) {
							plugin.createBall(player, false);
						} else if (args[0].equalsIgnoreCase("create3D")) {
							plugin.createBall(player, true);
						} else if (args[0].equalsIgnoreCase("remove")) {
							plugin.removeBall(player, args[1]);
						} else if (args[0].equalsIgnoreCase("convert")) {
							plugin.addBall(player, args[1]);
						} else if (args[0].equalsIgnoreCase("strenght")) {
							plugin.setStrenght(player, Integer.parseInt(args[1]));
						} else if (args[0].equalsIgnoreCase("createCircleGoal")) {
							plugin.addGoal(player, GoalType.CIRCLE, Integer.parseInt(args[1]), 0);
						} else if (args[0].equalsIgnoreCase("createRectangleGoal")) {
							plugin.addGoal(player, GoalType.RECTANGLE, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
						} else {
							player.sendMessage(ChatFormater.format("{ChatColor.RED} command unknown"));
						}
					} else {
						player.sendMessage(ChatFormater.format("{ChatColor.RED} Need more arguments"));
					}
			} else {
				QuidditchBall.log(ChatFormater.format("[QuidditchBall] Pushball requires you to be a Player"));
			}
			return false;
		} catch (Exception ex) {
			QuidditchBall.log(ChatFormater.format("[QuidditchBall] Command failure: %s", ex.getMessage()));
		}

		return false;
	}
}