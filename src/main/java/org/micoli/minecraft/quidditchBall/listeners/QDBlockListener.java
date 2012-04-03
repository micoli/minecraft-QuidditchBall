package org.micoli.minecraft.quidditchBall.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.micoli.minecraft.quidditchBall.QuidditchBall;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving QDBlock events.
 * The class that is interested in processing a QDBlock
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addQDBlockListener<code> method. When
 * the QDBlock event occurs, that object's appropriate
 * method is invoked.
 *
 * @see QDBlockEvent
 */
public class QDBlockListener implements Listener {
	
	/** The active ball. */
	QuidditchBall activeBall;

	/**
	 * Instantiates a new qD block listener.
	 *
	 * @param activeBall the active ball
	 */
	public QDBlockListener(QuidditchBall activeBall) {
		this.activeBall = activeBall;
	}

	/**
	 * On block break.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		activeBall.blockBreak(event);
	}
}
