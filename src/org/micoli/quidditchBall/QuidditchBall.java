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
	private QDCommandManager myExecutor;
	private static QuidditchBall instance;
	private static Map<String,QDBlockBall> aBalls;
	private static Map<String,Long> coolDowns;
	private static String commandString = "quidditchball";
	private static int strenght = 1;
	private static int touchBallCooldownTime = 3000;
	private static boolean comments = true;
	private static String lastMsg = "";

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

	public static void sendComments(Player player,String text,boolean global){
		if (getComments()){
			if(!QuidditchBall.lastMsg.equalsIgnoreCase(text)){
				QuidditchBall.lastMsg = text+"";
				if(global){
					getInstance().getServer().broadcastMessage(text);
				}else{
					player.sendMessage(text);
				}
			}
		}
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		log((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" Version ").append(pdfFile.getVersion()).append(" has been Disabled.").toString());
	}

	@Override
	public void onEnable() {
		aBalls					= new HashMap<String,QDBlockBall>();
		coolDowns				= new HashMap<String,Long>();
		instance				= this;
		myExecutor				= new QDCommandManager(this);
		PluginManager			pm = getServer().getPluginManager();
		PluginDescriptionFile	pdfFile = getDescription();

		pm.registerEvents(new QDPlayerListener(this), this);
		pm.registerEvents(new QDBlockListener(this), this);
		getCommand("quidditchball").setExecutor(myExecutor);

		log((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" Version. ").append(pdfFile.getVersion()).append(" is Enabled.").toString());
	}

	public void actionPerformed(ActionEvent event) {
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
		QDBlockBall ball = new QDBlockBall(block,player);
		if (aBalls.containsKey(name)){
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" already exists.").toString());
		}else{
			aBalls.put(name,ball);
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" added.").toString());
		}
	}

	public void createBall(Player player,boolean flyingMode) {
		String name=String.format("ball__%08d",aBalls.size());
		List<Block> lBlock = player.getLineOfSight(null, 2);
		Block block = lBlock.get(0);
		if (block.getType() == Material.AIR){
			block.setType(Material.PUMPKIN);
			QDBlockBall ball = new QDBlockBall(block,player);
			ball.setFlyingBall(flyingMode);
			aBalls.put(name,ball);
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GOLD).append(name).append(ChatColor.GREEN).append(" added.").toString());
		}else{
			player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Ball ").append(ChatColor.GREEN).append(" can not be added, not in front of AIR.").toString());
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