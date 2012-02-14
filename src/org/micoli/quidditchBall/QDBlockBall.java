package org.micoli.quidditchBall;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class QDBlockBall {
	Block block;
	String lastPlayerName;
	QuidditchBall plugin;

	public QDBlockBall(Block b) {
		block = b;
		lastPlayerName = null;
		plugin = QuidditchBall.getInstance();
	}

	public void switchBlock(Block block){
		block.setType(this.block.getType());
		this.block.setTypeId(0);
		this.block = block;
	}

	public boolean touchBall(Player player,int strenght,int distance) {
		Block playerBlock = player.getWorld().getBlockAt(player.getLocation());
		double ballPlayerDistance = playerBlock.getLocation().distance(this.block.getLocation());

		if (ballPlayerDistance>(distance+2)){
			return false;
		}

		int[][] relatives = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 } };
		for (int[] rel : relatives) {
			if (playerBlock.getRelative(rel[0]*distance, 0, rel[1]*distance).equals(this.block)) {
				Block target = playerBlock.getRelative(rel[0] * strenght, 0, rel[1] * strenght);
				if (target.getType() == Material.AIR) {
					switchBlock(target);
					if (QuidditchBall.getComments()){
						if(player.getName()!=lastPlayerName){
							lastPlayerName = player.getName();
							plugin.getServer().broadcastMessage((new StringBuilder()).append(ChatColor.AQUA).append(player.getName()).append(" touch the ball.").toString());
						}
					}
					return true;
				} else {
					target = target.getRelative(0, -1, 0);
					if (target.getType() == Material.AIR) {
						switchBlock(target);
						break;
					}
				}

			}
		}
		return false;
	}

}
