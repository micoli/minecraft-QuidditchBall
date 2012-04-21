package org.micoli.minecraft.quidditchBall;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.quidditchBall.entities.QDObjectBall;
import org.micoli.minecraft.quidditchBall.entities.QDObjectGoal;
import org.micoli.minecraft.quidditchBall.entities.QDObjectGoal.GoalOrientation;
import org.micoli.minecraft.quidditchBall.entities.QDObjectGoal.GoalType;
import org.micoli.minecraft.quidditchBall.listeners.QDBlockListener;
import org.micoli.minecraft.quidditchBall.listeners.QDPlayerListener;
import org.micoli.minecraft.quidditchBall.managers.QDCommandManager;
import org.micoli.minecraft.utils.ChatFormater;

// TODO: Auto-generated Javadoc
/**
 * The Class QuidditchBall.
 */
public class QuidditchBall extends QDBukkitPlugin implements ActionListener {
	
	/** The executor. */
	protected QDCommandManager executor;
	
	/** The instance. */
	protected static QuidditchBall instance;
	
	/** The a balls. */
	private static Map<String, QDObjectBall> aBalls;
	
	/** The a goals. */
	public static Map<String, QDObjectGoal> aGoals;
	
	/** The cool downs. */
	private static Map<String, Long> coolDowns;
	
	/** The strenght. */
	private static int strenght = 1;
	
	/** The touch ball cooldown time. */
	private static int touchBallCooldownTime = 3000;

	/**
	 * Gets the single instance of QuidditchBall.
	 *
	 * @return the instance
	 */
	public static QuidditchBall getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.micoli.minecraft.bukkit.QDBukkitPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		super.onEnable();
		
		commandString		= "quidditchball";
		aBalls				= new HashMap<String, QDObjectBall>();
		aGoals				= new HashMap<String, QDObjectGoal>();
		coolDowns			= new HashMap<String, Long>();
		instance			= this;
		executor			= new QDCommandManager(this);

		pm.registerEvents(new QDPlayerListener(this), this);
		pm.registerEvents(new QDBlockListener(this), this);
		getCommand(getCommandString()).setExecutor(executor);

		logger.log(ChatFormater.format("%s version enabled", pdfFile.getName(), pdfFile.getVersion()));
	}

	/**
	 * Sets the strenght.
	 *
	 * @param player the player
	 * @param s the s
	 */
	public void setStrenght(Player player, int s) {
		strenght = s;
		player.sendMessage(ChatFormater.format("{ChatColor.GREEN} strenght positionned (%d)", s));
	}

	/**
	 * Block break.
	 *
	 * @param event the event
	 */
	public void blockBreak(BlockBreakEvent event) {
		Iterator<String> iterator = aBalls.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectBall ball = (QDObjectBall) aBalls.get(key);
			if (ball.getBlock().equals(event.getBlock())) {
				aBalls.remove(key);
				getServer().broadcastMessage(ChatFormater.format("{ChatColor.RED} The ball %s has been automaticaly removed", key));
			}
		}

		iterator = aGoals.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectGoal goal = (QDObjectGoal) aGoals.get(key);
			if (goal.getCenterBlock().equals(event.getBlock())) {
				aGoals.remove(key);
				getServer().broadcastMessage(ChatFormater.format("{ChatColor.RED} The goal %s has been automaticaly removed", key));
			}
		}
	}

	/**
	 * Adds the goal.
	 *
	 * @param player the player
	 * @param type the type
	 * @param radiusOrWidth the radius or width
	 * @param height the height
	 */
	public void addGoal(Player player, GoalType type, int radiusOrWidth, int height) {
		String name = String.format("goal__%08d", aGoals.size());
		int orientation = (int) (player.getLocation().getYaw() + 180) % 360;
		GoalOrientation go = GoalOrientation.NS;
		String dir = "N";
		if (orientation < 45 + 0 * 90) {
			dir = "N";
			go = GoalOrientation.EW;
		} else if (orientation < 45 + 1 * 90) {
			dir = "E";
			go = GoalOrientation.NS;
		} else if (orientation < 45 + 2 * 90) {
			dir = "S";
			go = GoalOrientation.EW;
		} else if (orientation < 45 + 3 * 90) {
			dir = "W";
			go = GoalOrientation.NS;
		}
		Block block;
		switch (type) {
			case CIRCLE:
				QDObjectGoal cGoal = new QDObjectGoal(GoalType.CIRCLE);
				block = player.getWorld().getBlockAt(player.getLocation().add(new Vector(0, 1, 0)));
				block.setType(Material.GLASS);
				cGoal.setCircle(block, radiusOrWidth,go);
				aGoals.put(name, cGoal);
			break;
			case RECTANGLE:
				QDObjectGoal rGoal = new QDObjectGoal(GoalType.RECTANGLE);
				block = player.getWorld().getBlockAt(player.getLocation());
				block.setType(Material.GLASS);
				rGoal.setRectangle(block, radiusOrWidth, height, go);
				aGoals.put(name, rGoal);
			break;
		}
	}

	/**
	 * Adds the ball.
	 *
	 * @param player the player
	 * @param name the name
	 */
	public void addBall(Player player, String name) {
		Block block = player.getTargetBlock(null, 50);
		QDObjectBall ball = new QDObjectBall(block, player);
		if (aBalls.containsKey(name)) {
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} already exists.", name));
		} else {
			aBalls.put(name, ball);
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} added.", name));
		}
	}

	/**
	 * Creates the ball.
	 *
	 * @param player the player
	 * @param flyingMode the flying mode
	 */
	public void createBall(Player player, boolean flyingMode) {
		String name = String.format("ball__%08d", aBalls.size());
		List<Block> lBlock = player.getLineOfSight(null, 2);
		Block block;
		if (flyingMode) {
			block = lBlock.get(0);
		} else {
			block = lBlock.get(0).getRelative(0, -1, 0);
		}
		if (block.getType() == Material.AIR) {
			block.setType(Material.PUMPKIN);
			QDObjectBall ball = new QDObjectBall(block, player);
			ball.setFlyingBall(flyingMode);
			aBalls.put(name, ball);
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} added.", name));
		} else {
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} can not be added, not in front of AIR.", name));
		}
	}

	/**
	 * Removes the ball.
	 *
	 * @param player the player
	 * @param name the name
	 */
	public void removeBall(Player player, String name) {
		if (aBalls.containsKey(name)) {
			aBalls.remove(name);
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} removed.", name));
		} else {
			player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Ball {ChatColor.GOLD}%s{ChatColor.GREEN} does not exists.", name));
		}
	}

	/**
	 * Player touch.
	 *
	 * @param player the player
	 * @param block the block
	 */
	public void playerTouch(Player player, Block block) {
		long currentMillis = System.currentTimeMillis();
		String playerName = player.getName();
		if (coolDowns.containsKey(playerName)) {
			if (coolDowns.get(playerName) + touchBallCooldownTime > currentMillis) {
				player.sendMessage(ChatFormater.format("{ChatColor.GREEN}Player {ChatColor.GOLD}%s{ChatColor.GREEN} in cooldown", playerName));
				return;
			}
			coolDowns.remove(playerName);
		}

		Iterator<String> iterator = aBalls.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectBall ball = (QDObjectBall) aBalls.get(key);
			if (ball.getBlock().equals(block)) {
				if (ball.touchBall(player, strenght + 2, 2)) {
					player.sendMessage(ChatFormater.format("{ChatColor.GOLD}%s{ChatColor.GREEN} touch ball very fast", playerName));
					coolDowns.put(player.getName(), currentMillis);
				}
				break;
			}
		}
	}

	/**
	 * Player move.
	 *
	 * @param player the player
	 */
	public void playerMove(Player player) {
		Iterator<String> iterator = aBalls.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectBall ball = (QDObjectBall) aBalls.get(key);
			ball.touchBall(player, strenght, 1);
		}
	}
}