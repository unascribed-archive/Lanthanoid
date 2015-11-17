package com.unascribed.lanthanoid.client;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.tile.TileEntityEldritch;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SoundEldritch extends MovingSound {
	
	private TileEntityEldritch te;
	public SoundEldritch(ResourceLocation loc, TileEntityEldritch te) {
		super(loc);
		this.te = te;
		this.xPosF = te.xCoord+0.5f;
		this.yPosF = te.yCoord+0.5f;
		this.zPosF = te.zCoord+0.5f;
		this.field_147663_c = (te.playerAnim/40f)+0.5f;
		this.volume = te.playerAnim/10f;
		this.repeat = true;
	}

	@Override
	public void update() {
		if (te == null || !te.hasWorldObj() || te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord) != LBlocks.machine) {
			stop();
			return;
		}
		this.xPosF = te.xCoord+0.5f;
		this.yPosF = te.yCoord+0.5f;
		this.zPosF = te.zCoord+0.5f;
		this.field_147663_c = (te.playerAnim/40f)+0.5f;
		this.volume = te.playerAnim/10f;
	}
	
	public void stop() {
		this.donePlaying = true;
	}

}
