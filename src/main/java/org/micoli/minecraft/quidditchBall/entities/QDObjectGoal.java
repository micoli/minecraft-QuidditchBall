package org.micoli.minecraft.quidditchBall.entities;

import org.bukkit.block.Block;
import org.micoli.minecraft.quidditchBall.QuidditchBall;
import org.micoli.minecraft.utils.ChatFormater;

// TODO: Auto-generated Javadoc
/**
 * The Class QDObjectGoal.
 */
public class QDObjectGoal {
	
	/** The center block. */
	Block centerBlock;
	
	/** The type. */
	GoalType type;
	
	/** The radius. */
	int radius;
	
	/** The height. */
	int height;
	
	/** The width. */
	int width;
	
	/** The orientation. */
	GoalOrientation orientation;
	
	/** The Z. */
	double X, Y, Z;

	/**
	 * The Enum GoalType.
	 */
	public enum GoalType {
		
		/** The CIRCLE. */
		CIRCLE, 
 /** The RECTANGLE. */
 RECTANGLE
	}

	/**
	 * The Enum GoalOrientation.
	 */
	public enum GoalOrientation {
		
		/** The NS. */
		NS, 
 /** The EW. */
 EW
	}

	/**
	 * Instantiates a new qD object goal.
	 *
	 * @param type the type
	 */
	public QDObjectGoal(GoalType type) {
		this.type = type;
	}

	/**
	 * Sets the circle.
	 *
	 * @param centerBlock the center block
	 * @param radius the radius
	 * @param orientation the orientation
	 */
	public void setCircle(Block centerBlock, int radius,GoalOrientation orientation) {
		this.centerBlock = centerBlock;
		this.radius = radius;
		this.orientation = orientation;
	}

	/**
	 * Sets the rectangle.
	 *
	 * @param centerBlock the center block
	 * @param width the width
	 * @param height the height
	 * @param orientation the orientation
	 */
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

	/**
	 * Gets the center block.
	 *
	 * @return the center block
	 */
	public Block getCenterBlock() {
		return centerBlock;
	}

	/**
	 * Sets the center block.
	 *
	 * @param centerBlock the new center block
	 */
	public void setCenterBlock(Block centerBlock) {
		this.centerBlock = centerBlock;
	}

	/**
	 * Checks if is ball inside.
	 *
	 * @param ball the ball
	 * @return true, if is ball inside
	 */
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
