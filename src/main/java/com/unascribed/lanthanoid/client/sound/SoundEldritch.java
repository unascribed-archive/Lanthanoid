package com.unascribed.lanthanoid.client.sound;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.tile.TileEntityEldritch;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SoundEldritch extends MovingSound {
	
	public static int amountPlaying = 0;
	private TileEntityEldritch te;
	private boolean counted = true;
	
	public SoundEldritch(ResourceLocation loc, TileEntityEldritch te) {
		super(loc);
		this.te = te;
		this.xPosF = te.xCoord+0.5f;
		this.yPosF = te.yCoord+0.5f;
		this.zPosF = te.zCoord+0.5f;
		this.pitch = (te.playerAnim/40f)+0.5f;
		this.volume = te.playerAnim/20f;
		this.repeat = true;
		amountPlaying++;
	}

	@Override
	public void update() {
		if (donePlaying && counted) {
			counted = false;
			amountPlaying--;
		}
		if (te == null || !te.hasWorldObj() || te.getWorld().getBlock(te.xCoord, te.yCoord, te.zCoord) != LBlocks.machine) {
			stop();
			return;
		}
		this.xPosF = te.xCoord+0.5f;
		this.yPosF = te.yCoord+0.5f;
		this.zPosF = te.zCoord+0.5f;
		float player = te.playerAnim/40f;
		float pitch = 0.5f;
		pitch += player;
		float glyphs = (te.getMilliglyphs()/(float)te.getMaxMilliglyphs());
		if (glyphs >= 1) {
			pitch *= 1.75f;
		} else {
			pitch += glyphs*player;
		}
		this.pitch = pitch;
		this.volume = te.playerAnim/120f;
	}
	
	public void stop() {
		this.donePlaying = true;
		if (counted) {
			counted = false;
			amountPlaying--;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (counted) {
			counted = false;
			amountPlaying--;
		}
	}

}
