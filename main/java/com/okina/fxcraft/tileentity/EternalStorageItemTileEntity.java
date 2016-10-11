package com.okina.fxcraft.tileentity;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.IHUDBlock;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.InfinitInteger;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class EternalStorageItemTileEntity extends TileEntity implements IInventory, ITickable, IHUDBlock {

	public long lastClickedTime = 0;

	public ItemStack item = null;//ignoring stack count
	public InfinitInteger itemCount = InfinitInteger.ZERO;

	private ItemStack[] pastInv = new ItemStack[64];
	private ItemStack[] inventory = new ItemStack[64];

	public EternalStorageItemTileEntity() {}

	@Override
	public void update() {
		updateInventory();
		if(!worldObj.isRemote){
			markDirty();
		}
	}

	public void updateInventory() {
		if(item != null){
			int plus = 0;
			for (int i = 0; i < inventory.length; i++){
				plus += (inventory[i] == null ? 0 : inventory[i].stackSize) - (pastInv[i] == null ? 0 : pastInv[i].stackSize);
				inventory[i] = null;
			}
			itemCount = itemCount.plus(plus);

			int i1 = (int) itemCount.getLongValue();
			if(i1 < 0){
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			}else if(i1 == 0){
				item = null;
			}else{
				for (int i = 0; i < 32; i++){
					int count = Math.min(i1, item.getMaxStackSize());
					inventory[i] = item.copy();
					inventory[i].stackSize = count;
					i1 -= count;
					if(i1 <= 0){
						break;
					}
				}
			}

			for (int i = 0; i < inventory.length; i++){
				pastInv[i] = inventory[i] == null ? null : inventory[i].copy();
			}
		}
	}

	@Override
	public String getName() {
		return "eternalStorageItemTileEntity";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Eternal Item Storage");
	}

	@Override
	public int getSizeInventory() {
		return 64;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(inventory[index] != null){
			if(inventory[index].stackSize <= count){
				ItemStack itemstack1 = inventory[index];
				inventory[index] = null;
				markDirty();
				return itemstack1;
			}else{
				ItemStack itemstack = inventory[index].splitStack(count);
				if(inventory[index].stackSize == 0){
					inventory[index] = null;
				}
				markDirty();
				return itemstack;
			}
		}else{
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(inventory[index] != null){
			ItemStack itemstack = inventory[index];
			inventory[index] = null;
			return itemstack;
		}else{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(item == null){
			inventory[index] = stack;
			if(stack != null && stack.stackSize > getInventoryStackLimit()){
				stack.stackSize = getInventoryStackLimit();
			}
			item = stack;
		}else{
			if(item.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(item, stack)){
				inventory[index] = stack;
				if(stack != null && stack.stackSize > getInventoryStackLimit()){
					stack.stackSize = getInventoryStackLimit();
				}
			}
		}
		markDirty();
	}

	@Override
	public void clear() {
		for (int i = 0; i < inventory.length; ++i){
			inventory[i] = null;
		}
		item = null;
		itemCount = InfinitInteger.ZERO;
		markDirty();
	}

	//	@Override
	//	public ItemStack getStackInSlot(int index) {
	//		if(item != null){
	//			if(index >= 0 && index < 4){
	//				int count = (int) (itemCount.getLongValue() - index * item.getMaxStackSize());
	//				if(count <= 0){
	//					return null;
	//				}
	//				if(count > item.getMaxStackSize()){
	//					count = item.getMaxStackSize();
	//				}
	//				ItemStack rtn = item.copy();
	//				rtn.stackSize = count;
	//				return rtn;
	//			}
	//		}
	//		return null;
	//	}
	//
	//	@Override
	//	public ItemStack decrStackSize(int index, int count) {
	//		ItemStack itemStack = getStackInSlot(index);
	//		if(itemStack != null){
	//			int c = Math.min(itemStack.stackSize, Math.min(count, item.getMaxStackSize()));
	//			itemCount = itemCount.add(-c);
	//			ItemStack stack = itemStack.copy();
	//			stack.stackSize = c;
	//			if(itemCount.getLongValue() == 0){
	//				item = null;
	//			}
	//			return stack;
	//		}
	//		return null;
	//	}
	//
	//	@Override
	//	public ItemStack removeStackFromSlot(int index) {
	//		ItemStack itemStack = getStackInSlot(index);
	//		if(itemStack != null){
	//			itemCount = itemCount.add(-itemStack.stackSize);
	//			if(itemCount.getLongValue() == 0){
	//				item = null;
	//			}
	//			return itemStack;
	//		}
	//		return null;
	//	}
	//
	//	@Override
	//	public void setInventorySlotContents(int index, ItemStack stack) {
	//		if(index >= 0 && index < 8){
	//			if(item != null && itemCount.getLongValue() != 0){
	//				ItemStack itemStack = getStackInSlot(index);
	//				if(stack != null){
	//					if(item.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(item, stack)){
	//						int count = stack.stackSize - (itemStack != null ? itemStack.stackSize : 0);
	//						itemCount = itemCount.add(count);
	//					}
	//				}else{
	//					if(itemStack != null){
	//						int count = itemStack.stackSize;
	//						itemCount = itemCount.add(-count);
	//					}
	//				}
	//			}else{
	//				item = stack.copy();
	//				item.stackSize = 1;
	//				itemCount = new InfinitInteger(stack.stackSize);
	//			}
	//			if(itemCount.getLongValue() == 0){
	//				item = null;
	//			}
	//		}
	//	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(item != null){
			if(stack != null && index >= 0 && index < inventory.length){
				return item.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(item, stack);
			}
			return false;
		}else{
			return true;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
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
		item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
		String value = tag.getString("value");
		boolean negative = tag.getBoolean("negative");
		itemCount = new InfinitInteger(value, negative);
		updateInventory();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		updateInventory();
		if(item != null && itemCount.getLongValue() != 0){
			NBTTagCompound itemTag = new NBTTagCompound();
			item.writeToNBT(itemTag);
			tag.setTag("item", itemTag);
			tag.setString("value", itemCount.value);
			tag.setBoolean("negative", itemCount.negative);
		}
		return tag;
	}

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, RayTraceResult mop) {
		if(item != null && itemCount.getLongValue() != 0){
			RenderingHelper.renderHUDCenter(mc, Lists.newArrayList(new ColoredString(item.getDisplayName(), 0xffffff), new ColoredString("0x" + itemCount.getHexString(), 0xffffff)));
		}else{
			RenderingHelper.renderHUDCenter(mc, Lists.newArrayList(new ColoredString("No Item Stored", 0xffffff)));
		}
	}

	@Override
	public boolean comparePastRenderObj(Object object, RayTraceResult past, RayTraceResult current) {
		return this == object;
	}

}
