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
		lastPlayerName = "";
		plugin = QuidditchBall.getInstance();
	}

	public void switchBlock(Block block){
		block.setType(this.block.getType());
		this.block.setTypeId(0);
		this.block = block;
	}

	public boolean touchBall(Player player,int strenght,int distance) {
		Block block = player.getWorld().getBlockAt(player.getLocation());
		int[][] relatives = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 } };

		for (int[] rel : relatives) {
			if (block.getRelative(rel[0]*distance, 0, rel[1]*distance).equals(this.block)) {
				Block b = block.getRelative(rel[0] * strenght, 0, rel[1] * strenght);
				if (b.getType() == Material.AIR) {
					switchBlock(b);
					if (QuidditchBall.getComments()){
						if(player.getName()!=lastPlayerName){
							lastPlayerName = player.getName();
							plugin.getServer().broadcastMessage((new StringBuilder()).append(ChatColor.AQUA).append(player.getName()).append(" touch the ball.").toString());
						}
					}
					return true;
				} else {
					b = b.getRelative(0, -1, 0);
					if (b.getType() == Material.AIR) {
						switchBlock(b);
						break;
					}
				}

			}
		}
		return false;
	}

}
