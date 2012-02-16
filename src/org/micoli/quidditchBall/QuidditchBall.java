package org.micoli.quidditchBall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.micoli.minecraft.utils.ChatFormatter;
import org.micoli.quidditchBall.listeners.QDBlockListener;
import org.micoli.quidditchBall.listeners.QDPlayerListener;
import org.micoli.quidditchBall.managers.QDCommandManager;

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
		player.sendMessage(ChatFormatter.format("{ChatColor.RED} %s",(active?"comments activated":"comments desactived")));
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
		log(ChatFormatter.format("%s version disabled",pdfFile.getName(),pdfFile.getVersion()));
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
		getCommand(getCommandString()).setExecutor(myExecutor);

		log(ChatFormatter.format("%s version enabled",pdfFile.getName(),pdfFile.getVersion()));
	}

	public void actionPerformed(ActionEvent event) {
	}

	public void setStrenght(Player player, int s) {
		strenght = s;
		player.sendMessage(ChatFormatter.format("{ChatColor.GREEN} strenght positionned (%d)",s));
	}

	public void blockBreak(BlockBreakEvent event) {
		Iterator<String> iterator = aBalls.keySet().iterator();
		while(iterator.hasNext()){
			String key =iterator.next();
			QDBlockBall ball = (QDBlockBall) aBalls.get(key);
			if (ball.block.equals(event.getBlock())) {
				aBalls.remove(key);
				getServer().broadcastMessage(ChatFormatter.format("{ChatColor.RED} The ball %s has been automaticaly removed",key));
			}
		}
	}

	public void addBall(Player player, String name) {
		Block block = player.getTargetBlock(null, 50);
		QDBlockBall ball = new QDBlockBall(block,player);
		if (aBalls.containsKey(name)){
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} already exists.",name));
		}else{
			aBalls.put(name,ball);
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} added.",name));
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
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} added.",name));
		}else{
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} can not be added, not in front of AIR.",name));
		}
	}

	public void removeBall(Player player, String name) {
		if (aBalls.containsKey(name)){
			aBalls.remove(name);
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} removed.",name));
		}else{
			player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} does not exists.",name));
		}
	}

	public void playerTouch(Player player,Block block) {
		long currentMillis = System.currentTimeMillis();
		String playerName = player.getName();
		if (coolDowns.containsKey(playerName)){
			if (coolDowns.get(playerName)+touchBallCooldownTime>currentMillis){
				player.sendMessage(ChatFormatter.format("{ChatColor.GREEN}Player {ChatColor.GOLD}%s{ChatColor.GREEN} in cooldown",playerName));
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
					player.sendMessage(ChatFormatter.format("{ChatColor.GOLD}%s{ChatColor.GREEN} touch ball very fast",playerName));
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