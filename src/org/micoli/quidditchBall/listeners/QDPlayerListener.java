package org.micoli.quidditchBall.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.micoli.quidditchBall.QuidditchBall;

public class QDPlayerListener implements Listener {
	QuidditchBall plugin;

	public QDPlayerListener(QuidditchBall plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			plugin.playerTouch(event.getPlayer(), block);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		plugin.playerMove(player);
	}
}
