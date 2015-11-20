package com.unascribed.lanthanoid.effect;

import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityGlyphFX extends EntityEnchantmentTableParticleFX {

	public EntityGlyphFX(World p_i1204_1_, double p_i1204_2_, double p_i1204_4_, double p_i1204_6_, double p_i1204_8_, double p_i1204_10_, double p_i1204_12_) {
		super(p_i1204_1_, p_i1204_2_, p_i1204_4_, p_i1204_6_, p_i1204_8_, p_i1204_10_, p_i1204_12_);
	}

	@Override
	public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		if (particleAge < 2) {
			return;
		}
		super.renderParticle(p_70539_1_, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
	}
	
}
