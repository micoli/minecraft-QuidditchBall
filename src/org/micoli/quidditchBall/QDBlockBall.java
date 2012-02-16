package org.micoli.quidditchBall;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class QDBlockBall {
	Block block;
	static QuidditchBall plugin;
	boolean gravity = true;
	boolean debug = false;
	private boolean flyingBall = false;
	private int serverMaxHeight = 128;

	public boolean isFlyingBall() {
		return flyingBall;
	}

	public void setFlyingBall(boolean flyingBall) {
		this.flyingBall = flyingBall;
		gravity = !flyingBall;
	}

	public QDBlockBall(Block b,Player player) {
		block = b;
		plugin = QuidditchBall.getInstance();
		serverMaxHeight= player.getWorld().getMaxHeight()-3;
	}

	public void switchBlock(Block block){
		block.setType(this.block.getType());
		this.block.setTypeId(0);
		this.block = block;
		if(gravity) {
			Block under = block.getRelative(0, -1, 0);
			if (under.getType() == Material.AIR) {
				switchBlock(under);
			}
		}
	}

	public int xyzDistance(Location location){
		return (int)Math.max(Math.max( Math.abs(location.getX()-this.block.getX()) , Math.abs(location.getY()-this.block.getY()) ) ,Math.abs(location.getZ()-this.block.getZ()) ) ;
	}

	public boolean touchBall(Player player,int strenght,int distance) {
		Block playerBlock = player.getWorld().getBlockAt(player.getLocation());
		Location locPlayer = playerBlock.getLocation();
		if(isFlyingBall()){
			locPlayer.setY(locPlayer.getY()+1);
		}
		Location locBall = this.block.getLocation();

		double ballPlayerDistance = this.xyzDistance(locPlayer);

		if (ballPlayerDistance > distance+1){
			return false;
		}

		Vector vector = locPlayer.subtract(locBall).toVector().normalize();

		double x=vector.getX(),y=vector.getY(),z=vector.getZ();

		vector = new Vector(-1*(x==0?0:x/Math.abs(x)),-1*(y==0?0:y/Math.abs(y)),-1*(z==0?0:z/Math.abs(z)));

		if(debug){
			QuidditchBall.sendComments(player,"distance" + String.format("%04.2f",ballPlayerDistance)+"->"+vector.toString()+" "+Double.toString(strenght),false);
		}

		Block target = this.block.getRelative((int)vector.getX()*strenght, isFlyingBall()?(int)vector.getY()*strenght:0, (int)vector.getZ()*strenght);
		if ((isFlyingBall() || playerBlock.getY() == this.block.getY()) && (target.getType() == Material.AIR)) {
			if(isFlyingBall()){
				if (target.getY()>=serverMaxHeight){
					QuidditchBall.sendComments(player,"too high",false);
					return false;
				}
				if(target.getRelative(0, -2,0).getType()!=Material.AIR){
					QuidditchBall.sendComments(player,"too low ",false);
					return false;
				}
			}
			if(debug){
				QuidditchBall.sendComments(player,this.block.getLocation().toString()+" "+target.getLocation().toString(),false);
			}
			switchBlock(target);
			QuidditchBall.sendComments(player,(new StringBuilder()).append(ChatColor.AQUA).append(player.getName()).append(" touch the ball.").toString(),true);
			return true;
		}
		return false;
	}

}
