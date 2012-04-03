package org.micoli.minecraft.quidditchBall.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.micoli.minecraft.quidditchBall.QuidditchBall;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving QDPlayer events.
 * The class that is interested in processing a QDPlayer
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addQDPlayerListener<code> method. When
 * the QDPlayer event occurs, that object's appropriate
 * method is invoked.
 *
 * @see QDPlayerEvent
 */
public class QDPlayerListener implements Listener {
	
	/** The plugin. */
	QuidditchBall plugin;

	/**
	 * Instantiates a new qD player listener.
	 *
	 * @param plugin the plugin
	 */
	public QDPlayerListener(QuidditchBall plugin) {
		this.plugin = plugin;
	}

	/**
	 * On player interact.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			plugin.playerTouch(event.getPlayer(), block);
		}
	}

	/**
	 * On player move.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		plugin.playerMove(player);
	}
}
