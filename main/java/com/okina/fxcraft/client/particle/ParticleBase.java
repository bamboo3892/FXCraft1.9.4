package com.okina.fxcraft.client.particle;

import java.awt.Color;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleBase extends Particle {

	protected int textureSizeX = 1;
	protected int textureSizeY = 1;
	protected Color color;

	protected ParticleBase(World world, double x, double y, double z) {
		this(world, x, y, z, 0xFFFFFF);
	}

	protected ParticleBase(World world, double x, double y, double z, int color) {
		super(world, x, y, z);
		this.color = new Color(color);
		particleRed = this.color.getRed() / 256f;
		particleGreen = this.color.getGreen() / 256f;
		particleBlue = this.color.getBlue() / 256f;
		particleMaxAge = 20;
		//		noClip = true;
	}

	//	@Override
	//	public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	//		float ageScaled = (particleAge + partialTicks) / particleMaxAge;
	//
	//		updateColor(ageScaled);
	//		updateTexture(ageScaled);
	//		updateScale(ageScaled);
	//
	//		float f6 = particleTextureIndexX / 16.0F;
	//		float f7 = f6 + textureSizeX / 16F;
	//		float f8 = particleTextureIndexY / 16.0F;
	//		float f9 = f8 + textureSizeY / 16F;
	//		float f10 = 0.1F * particleScale;
	//
	//		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
	//		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
	//		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
	//		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
	//		worldRendererIn.pos((double) (f5 - p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4), (double) (f7 - p_180434_6_ * f4 - p_180434_8_ * f4)).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	//		worldRendererIn.pos((double) (f5 - p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4), (double) (f7 - p_180434_6_ * f4 + p_180434_8_ * f4)).tex((double) f1, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	//		worldRendererIn.pos((double) (f5 + p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4), (double) (f7 + p_180434_6_ * f4 + p_180434_8_ * f4)).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	//		worldRendererIn.pos((double) (f5 + p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4), (double) (f7 + p_180434_6_ * f4 - p_180434_8_ * f4)).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	//	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
		float ageScaled = (particleAge + partialTicks) / particleMaxAge;
		updateColor(ageScaled);
		updateTexture(ageScaled);
		updateScale(ageScaled);

		float f = particleTextureIndexX / 16.0F;
		float f1 = f + 0.0624375F;
		float f2 = particleTextureIndexY / 16.0F;
		float f3 = f2 + 0.0624375F;
		float f4 = 0.1F * particleScale;

		if(particleTexture != null){
			f = particleTexture.getMinU();
			f1 = particleTexture.getMaxU();
			f2 = particleTexture.getMinV();
			f3 = particleTexture.getMaxV();
		}

		float f5 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f6 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f7 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
		int i = getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		worldRendererIn.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(f1, f3).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(f1, f2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(f, f2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(f, f3).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if(particleAge++ >= particleMaxAge){
			setExpired();
		}
		updatePosition(((float) particleAge) / (float) particleMaxAge);
	}

	/**called on every partial tick*/
	protected void updateColor(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateTexture(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateScale(float ageScaled) {

	}

	/**called on every tick*/
	protected void updatePosition(float ageScaled) {

	}

	/**return color multiply?*/
	@Override
	public int getBrightnessForRender(float partialTick) {
		int i = super.getBrightnessForRender(partialTick);
		float ageScaled = (float) particleAge / (float) particleMaxAge;
		ageScaled *= ageScaled;
		ageScaled *= ageScaled;
		int j = i & 255;
		int alpha = i >> 16 & 255;
		alpha += (int) (ageScaled * 15.0F * 16.0F);

		if(alpha > 240){
			alpha = 240;
		}

		return j | alpha << 16;
	}

	//	/**
	//	 * Gets how bright this entity is.
	//	 */
	//	@Override
	//	public float getBrightness(float partialTicks) {
	//		float brightness = super.getBrightness(partialTicks);
	//		float ageScaled = (float) particleAge / (float) particleMaxAge;
	//		ageScaled = ageScaled * ageScaled * ageScaled * ageScaled;
	//		return brightness * (1.0F - ageScaled) + ageScaled;
	//	}

}
