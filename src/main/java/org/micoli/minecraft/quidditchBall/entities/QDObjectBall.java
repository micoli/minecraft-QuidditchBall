package org.micoli.minecraft.quidditchBall.entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.micoli.minecraft.quidditchBall.QuidditchBall;
import org.micoli.minecraft.utils.ChatFormater;

// TODO: Auto-generated Javadoc
/**
 * The Class QDObjectBall.
 */
public class QDObjectBall {
	
	/** The block. */
	Block block;
	
	/**
	 * Gets the block.
	 *
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Sets the block.
	 *
	 * @param block the new block
	 */
	public void setBlock(Block block) {
		this.block = block;
	}

	/** The plugin. */
	static QuidditchBall plugin;
	
	/** The gravity. */
	boolean gravity = true;
	
	/** The debug. */
	boolean debug = false;
	
	/** The flying ball. */
	private boolean flyingBall = false;
	
	/** The server max height. */
	private int serverMaxHeight = 128;
	
	/** The hour fmt. */
	private SimpleDateFormat hourFmt = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Instantiates a new qD object ball.
	 *
	 * @param b the b
	 * @param player the player
	 */
	public QDObjectBall(Block b, Player player) {
		block = b;
		plugin = QuidditchBall.getInstance();
		serverMaxHeight = player.getWorld().getMaxHeight() - 3;
	}

	/**
	 * Checks if is flying ball.
	 *
	 * @return true, if is flying ball
	 */
	public boolean isFlyingBall() {
		return flyingBall;
	}

	/**
	 * Sets the flying ball.
	 *
	 * @param flyingBall the new flying ball
	 */
	public void setFlyingBall(boolean flyingBall) {
		this.flyingBall = flyingBall;
		gravity = !flyingBall;
	}

	/**
	 * switch the current ball block and the block argument.
	 *
	 * @param block the block
	 */
	public void switchBlock(Block block) {
		block.setType(this.block.getType());
		this.block.setTypeId(0);
		this.block = block;
		if (gravity) {
			Block under = block.getRelative(0, -1, 0);
			if (under.getType() == Material.AIR) {
				switchBlock(under);
			}
		}
	}

	/**
	 * Checks if is in goal.
	 *
	 * @return true, if is in goal
	 */
	boolean isInGoal() {
		Iterator<String> iterator = QuidditchBall.aGoals.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectGoal goal = (QDObjectGoal) QuidditchBall.aGoals.get(key);
			return goal.isBallInside(this);
		}
		return false;
	}

	/**
	 * calculate a raw distance between the ball and the Location.
	 *
	 * @param location the location
	 * @return the int
	 */
	public int xyzDistance(Location location) {
		return (int) Math.max(Math.max(Math.abs(location.getX() - this.block.getX()), Math.abs(location.getY() - this.block.getY())), Math.abs(location.getZ() - this.block.getZ()));
	}

	/**
	 * Touch ball.
	 *
	 * @param player the player
	 * @param strenght the strenght
	 * @param distance the distance
	 * @return true, if successful
	 */
	public boolean touchBall(Player player, int strenght, int distance) {
		Block playerBlock = player.getWorld().getBlockAt(player.getLocation());
		Location locPlayer = playerBlock.getLocation();

		if (isFlyingBall()) {
			locPlayer.setY(locPlayer.getY() + 1);
		}

		Location locBall = this.block.getLocation();

		double ballPlayerDistance = this.xyzDistance(locPlayer);

		if (ballPlayerDistance > distance + 1) {
			return false;
		}

		Vector vector = locPlayer.subtract(locBall).toVector().normalize();

		double x = vector.getX(), y = vector.getY(), z = vector.getZ();

		vector = new Vector(-1 * (x == 0 ? 0 : x / Math.abs(x)), -1 * (y == 0 ? 0 : y / Math.abs(y)), -1 * (z == 0 ? 0 : z / Math.abs(z)));

		if (debug) {
			plugin.sendComments(player, "distance" + String.format("%04.2f", ballPlayerDistance) + "->" + vector.toString() + " " + Double.toString(strenght), false);
		}

		Block target = this.block.getRelative((int) vector.getX() * strenght, isFlyingBall() ? (int) vector.getY() * strenght : 0, (int) vector.getZ() * strenght);
		if ((isFlyingBall() || playerBlock.getY() == this.block.getY()) && (target.getType() == Material.AIR)) {
			if (isFlyingBall()) {
				if (target.getY() >= serverMaxHeight) {
					plugin.sendComments(player, "too high", false);
					return false;
				}
				if (target.getRelative(0, -2, 0).getType() != Material.AIR) {
					plugin.sendComments(player, "too low ", false);
					return false;
				}
			}
			if (debug) {
				plugin.sendComments(player, this.block.getLocation().toString() + " " + target.getLocation().toString(), false);
			}
			switchBlock(target);
			if (isInGoal()) {
				Date now = new Date();
				plugin.sendComments(player, ChatFormater.format("{ChatColor.AQUA}And it's a goaaaalllll from {ChatColor.GOLD}%s{ChatColor.AQUA} at %s", player.getName(), hourFmt.format(now)), true);
			}
			plugin.sendComments(player, ChatFormater.format("{ChatColor.GOLD}%s{ChatColor.AQUA} touch the ball.", player.getName()), true);
			return true;
		}
		return false;
	}
}
