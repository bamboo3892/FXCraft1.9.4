package com.okina.fxcraft.tileentity;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.IHUDBlock;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.InfinitEnergyStorage;
import com.okina.fxcraft.utils.InfinitInteger;
import com.okina.fxcraft.utils.RenderingHelper;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;

public class EternalStorageEnergyTileEntity extends TileEntity implements IEnergyProvider, IEnergyReceiver, IEnergyStorage, IHUDBlock {

	public InfinitEnergyStorage storage = new InfinitEnergyStorage();

	public EternalStorageEnergyTileEntity() {
		storage.energy = new InfinitInteger(Integer.MAX_VALUE);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing facing) {
		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing facing) {
		return getEnergyStored();
	}

	@Override
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing facing) {
		return getMaxEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(EnumFacing facing, int maxReceive, boolean simulate) {
		return receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		markDirty();
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing facing, int maxExtract, boolean simulate) {
		return extractEnergy(maxExtract, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		markDirty();
		return storage.extractEnergy(maxExtract, simulate);
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
		storage.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		storage.writeToNBT(tag);
		return tag;
	}

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, RayTraceResult mop) {
		RenderingHelper.renderHUDCenter(mc, Lists.newArrayList(new ColoredString("0x" + storage.energy.getHexString() + " RF", 0xffffff)));
	}

	@Override
	public boolean comparePastRenderObj(Object object, RayTraceResult past, RayTraceResult current) {
		return this == object;
	}

}
