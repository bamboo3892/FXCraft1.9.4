package com.okina.fxcraft.utils;

import net.minecraft.util.math.BlockPos;

public class Position {

	public final int x;
	public final int y;
	public final int z;

	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position(BlockPos pos) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public Position sum(int x, int y, int z) {
		return new Position(this.x + x, this.y + y, this.z + z);
	}

	public Position sum(Position p) {
		this.sum(p.x, p.y, p.z);
		return this;
	}

	public Position turnY(int times) {
		times %= 4;
		if(times == 0){
			return this;
		}else if(times == 1){
			return new Position(-z, y, x);
		}else if(times == 2){
			return new Position(-x, y, -z);
		}else{
			return new Position(z, y, -x);
		}
	}

	public static Position sum(Position a, Position b) {
		return new Position(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Position){
			Position p = (Position) o;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}

}
