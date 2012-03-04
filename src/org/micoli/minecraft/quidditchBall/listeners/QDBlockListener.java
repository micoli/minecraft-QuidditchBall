package org.micoli.minecraft.quidditchBall.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.micoli.minecraft.quidditchBall.QuidditchBall;

public class QDBlockListener implements Listener {
	QuidditchBall activeBall;

	public QDBlockListener(QuidditchBall activeBall) {
		this.activeBall = activeBall;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		activeBall.blockBreak(event);
	}
}
