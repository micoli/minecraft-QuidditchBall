package org.micoli.quidditchBall;

import org.bukkit.block.Block;

public class QDObjectGoal {
	Block centerBlock;
	GoalType type;
	int radius;
	int height;
	int width;
	GoalOrientation orientation;

	public enum GoalType {
		CIRCLE,RECTANGLE
	}

	public enum GoalOrientation {
		NS,EW
	}

	public QDObjectGoal(GoalType type){
		this.type = type;
	}

	public void setCircle(Block centerBlock,int radius){
		this.centerBlock = centerBlock;
		this.radius = radius;
	}

	public void setRectangle(Block centerBlock,int width,int height, GoalOrientation orientation){
		this.centerBlock = centerBlock;
		this.width = width;
		this.height = height;
		this.orientation = orientation;
		QuidditchBall.log("facing "+orientation.toString());
	}
}
