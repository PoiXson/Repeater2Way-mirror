package com.poixson.repeater2way;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Repeater;


public class RepeaterDAO {

	public final Block blockA, blockB;
	public final BlockFace facingA, facingB;



	public RepeaterDAO(final Block block) {
		this.blockA    = block;
		final Repeater repeaterA = (Repeater) block.getBlockData();
		this.facingA   = repeaterA.getFacing();
		this.blockB    = block.getRelative(this.facingA.getOppositeFace());
		final Repeater repeaterB = (Repeater) this.blockB.getBlockData();
		this.facingB   = repeaterB.getFacing();
	}



	public boolean activate() {
		if (ValidateRepeater(this.blockB, this.facingB)) {
			final Repeater repeaterA = (Repeater) this.blockA.getBlockData();
			if (repeaterA.isPowered()) {
				final Repeater repeaterB = (Repeater) this.blockB.getBlockData();
				repeaterB.setFacing(this.facingA);
				repeaterB.setPowered(true);
				this.blockB.setBlockData(repeaterB);
				return true;
			}
		}
		return false;
	}

	public boolean restore() {
		if (ValidateRepeater(this.blockB, this.facingA)) {
			final Repeater repeaterB = (Repeater) this.blockB.getBlockData();
			repeaterB.setPowered(false);
			repeaterB.setFacing(this.facingB);
			this.blockB.setBlockData(repeaterB);
			return true;
		}
		return false;
	}



	public static boolean ValidateRepeater(final Block block, final BlockFace facing) {
		if (block == null) return false;
		if (Material.REPEATER.equals(block.getType())) {
			final Repeater repeater = (Repeater) block.getBlockData();
			if (repeater == null) return false;
			if (facing != null) {
				if (!repeater.getFacing().equals(facing))
					return false;
			}
			if (repeater.getDelay() == 1
			&& !repeater.isLocked() ) {
				return true;
			}
		}
		return false;
	}



}
