package org.micoli.quidditchBall;

import org.bukkit.block.Block;
import org.micoli.minecraft.utils.ChatFormater;

public class QDObjectGoal {
	Block centerBlock;
	GoalType type;
	int radius;
	int height;
	int width;
	GoalOrientation orientation;
	double X, Y, Z;

	public enum GoalType {
		CIRCLE, RECTANGLE
	}

	public enum GoalOrientation {
		NS, EW
	}

	public QDObjectGoal(GoalType type) {
		this.type = type;
	}

	public void setCircle(Block centerBlock, int radius,GoalOrientation orientation) {
		this.centerBlock = centerBlock;
		this.radius = radius;
		this.orientation = orientation;
	}

	public void setRectangle(Block centerBlock, int width, int height, GoalOrientation orientation) {
		this.centerBlock = centerBlock;
		this.width = width;
		this.height = height;
		this.orientation = orientation;
		this.X = centerBlock.getX();
		this.Y = centerBlock.getY();
		this.Z = centerBlock.getZ();

		QuidditchBall.log("facing " + orientation.toString());
	}

	public boolean isBallInside(QDObjectBall ball) {
		double X1 = ball.block.getX(), Y1 = ball.block.getY(), Z1 = ball.block.getZ();
		QuidditchBall.log(this.orientation.toString());
		switch (this.type) {
			case CIRCLE:
				if (ball.xyzDistance(this.centerBlock.getLocation()) <= this.radius ){//&& (this.orientation==GoalOrientation.NS?X == X1:Z == Z1)) {
					return true;
				}
			break;
			case RECTANGLE:
				if (ball.xyzDistance(this.centerBlock.getLocation()) > Math.min(width, height)) {
					return false;
				}
				try {
					QuidditchBall.log(ChatFormater.format("%03f,%03f,%03f  %03f,%03f,%03f %d,%d", X, Y, Z, X1, Y1, Z1, this.height, this.width));
					if (Y <= Y1 && Y1 <= Y + this.height) {
						switch (this.orientation) {
							case NS:
								if ((Z - this.width / 2) <= Z1 && Z1 <= (Z + this.width / 2) && X == X1) {
									return true;
								}
							break;
							case EW:
								if ((X - this.width / 2) <= X1 && X1 <= (X + this.width / 2) && Z == Z1) {
									return true;
								}
							break;
						}
					}
				} catch (Exception ex) {
					QuidditchBall.log(ChatFormater.format("[QuidditchBall] Command failure: %s", ex.getMessage()));
				}
			break;
		}
		return false;
	}

}
