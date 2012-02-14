package org.micoli.quidditchBall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.micoli.quidditchBall.listeners.*;
import org.micoli.quidditchBall.managers.*;

import java.util.*;

public class QuidditchBall extends JavaPlugin implements ActionListener {
	private static Logger logger = Logger.getLogger("Minecraft");
	private static QDCommandManager commandManager;
	private static QuidditchBall instance;
	private static Map<String,QDBlockBall> aBalls;
	private static Map<String,Long> coolDowns;
	private static String commandString = "activeball";
	private static int strenght = 2;
	private static int touchBallCooldownTime = 4000;
	private static boolean comments = true;

	/**
	 * @return the instance
	 */
	public static QuidditchBall getInstance() {
		return instance;
	}

	public static String getCommandString(){
		return commandString;
	}

	public static void setComments(Player player, boolean active){
		comments = active;
		player.sendMessage((new StringBuilder()).append(ChatColor.RED).append(active?"comments activated":"comments desactived").toString());
	}

	public static boolean getComments(){
		return comments;
	}

	public static void log(String str) {
		logger.info(str);
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		log((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" Version ").append(pdfFile.getVersion()).append(" has been Disabled.").toString());
	}

	public void onEnable() {
		aBalls					= new HashMap<String,QDBlockBall>();
		coolDowns				= new HashMap<String,Long>();
		instance				= this;
		commandManager			= new QDCommandManager();
		PluginManager			pm = getServer().getPluginManager();
		PluginDescriptionFile	pdfFile = getDescription();

		pm.registerEvents(new QDPlayerListener(this), this);
		pm.registerEvents(new QDBlockListener(this), this);
		getCommand(commandString).setExecutor(commandManager);

		log((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" Version ccc ").append(pdfFile.getVersion()).append(" is Enabled.").toString());
	}

	public void actionPerformed(ActionEvent event) {
		int mface[][] = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		log("ici ");
		Iterator<String> ballIterator = aBalls.keySet().iterator();
		while(ballIterator.hasNext()){
			String ballKey =ballIterator.next();
			QDBlockBall ball = (QDBlockBall) aBalls.get(ballKey);
			Block block = ball.block;
			if (block.getRelative(0, -1, 0).getType() == Material.AIR) {
				Block b = block.getRelative(0, -1, 0);
				ball.switchBlock(b);
			} else {
				for(Player player : getServer().getOnlinePlayers()) {
					Block pBlock = player.getWorld().getBlockAt(player.getLocation());
					for (int[] face : mface) {
						if ((block.getRelative(face[0], -1, face[1]).getType() == Material.AIR) & (!block.getRelative(face[0], -1, face[2]).equals(pBlock))) {
							Block b = block.getRelative(face[0], -1, face[2]);
							ball.switchBlock(b);
							break;
						}
					}
				}
			}
		}
	}

	public void setStrenght(Player player, int s) {
		strenght = s;
		player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("strenght positionned (").append(Integer.toString(s)).append(")").toString());
	}

	public void blockBreak(BlockBreakEvent event) {
		Iterator<String> iterator = aBalls.keySet().iterator();
		while(iterator.hasNext()){
			String key =iterator.next();
			QDBlockBall ball = (QDBlockBall) aBalls.get(key);
			if (ball.block.equals(event.getBlock())) {
				aBalls.remove(key);
				getServer().broadcastMessage((new StringBuilder()).append(ChatColor.RED).append("The ball ").append(key).append(" has been automaticaly removed").toString());
			}
		}
	}

	public void addBall(Player player, String name) {
		Block block = player.getTargetBlock(null, 50);
		QDBlockBall ball = new QDBlockBall(block);
		if (aBalls.containsKey(name)){
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" already exists.").toString());
		}else{
			aBalls.put(name,ball);
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" added.").toString());
		}
	}

	public void removeBall(Player player, String name) {
		if (aBalls.containsKey(name)){
			aBalls.remove(name);
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" removed.").toString());
		}else{
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" does not exists.").toString());
		}
	}

	public void playerTouch(Player player,Block block) {
		long currentMillis = System.currentTimeMillis();
		String playerName = player.getName();
		if (coolDowns.containsKey(playerName)){
			if (coolDowns.get(playerName)+touchBallCooldownTime>currentMillis){
				player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Player ").append(ChatColor.GOLD).append(playerName).append(ChatColor.RED).append(" in cooldown.").toString());
				return;
			}
			coolDowns.remove(playerName);
		}

		Iterator<String> iterator = aBalls.keySet().iterator();
		while(iterator.hasNext()){
			String key =iterator.next();
			QDBlockBall ball = (QDBlockBall) aBalls.get(key);
			if (ball.block.equals(block)){
				if (ball.touchBall(player,strenght+2,2)){
					player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Player ").append(ChatColor.GOLD).append(playerName).append(ChatColor.GREEN).append(" touch ball very fast.").toString());
					coolDowns.put(player.getName(),currentMillis);
				}
				break;
			}
		}
	}

	public void playerMove(Player player) {
		Iterator<String> iterator = aBalls.keySet().iterator();
		while(iterator.hasNext()){
			String key =iterator.next();
			QDBlockBall ball = (QDBlockBall) aBalls.get(key);
			ball.touchBall(player,strenght,1);
		}

	}
}