package com.okina.fxcraft.tileentity;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.IHUDBlock;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.InfinitFluidTank;
import com.okina.fxcraft.utils.InfinitInteger;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class EternalStorageFluidTileEntity extends TileEntity implements IFluidHandler, IHUDBlock {

	private InfinitFluidTank tank = new InfinitFluidTank();

	public EternalStorageFluidTileEntity() {
		tank.fluid = new FluidStack(FluidRegistry.WATER, 1000);
		tank.amount = new InfinitInteger(1123115);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return tank.fluid == null ? true : tank.fluid.getFluid() == fluid;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return tank.fluid == null ? false : tank.fluid.getFluid() == fluid;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(resource != null && canDrain(from, resource.getFluid())){
			return tank.drain(resource.amount, doDrain);
		}else{
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public void markDirty() {
		super.markDirty();
		List<EntityPlayer> list = getWorld().playerEntities;
		for (EntityPlayer player : list){
			if(player instanceof EntityPlayerMP){
				((EntityPlayerMP) player).connection.sendPacket(getUpdatePacket());
			}
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return new SPacketUpdateTileEntity(pos, 1, nbtTagCompound);
	}

	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbtTagCompound = pkt.getNbtCompound();
		readFromNBT(nbtTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		return tag;
	}

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, RayTraceResult mop) {
		if(tank.getFluidAmount() != 0){
			RenderingHelper.renderHUDCenter(mc, Lists.newArrayList(new ColoredString(tank.fluid.getLocalizedName(), 0xffffff), new ColoredString("0x" + tank.amount.getHexString() + " mb", 0xffffff)));
		}else{
			RenderingHelper.renderHUDCenter(mc, Lists.newArrayList(new ColoredString("No Fluid Stored", 0xffffff)));
		}
	}

	@Override
	public boolean comparePastRenderObj(Object object, RayTraceResult past, RayTraceResult current) {
		return this == object;
	}

}
