package com.okina.fxcraft.item;

import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemIPhone extends Item {

	public ItemIPhone() {
		setUnlocalizedName("fxcraft_iphone");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof FXDealerTileEntity){
			if(!world.isRemote){
				NBTTagCompound tag = stack.getTagCompound();
				if(tag == null) tag = new NBTTagCompound();
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				stack.setTagCompound(tag);
				player.addChatComponentMessage(new TextComponentString("Connection Established!"));
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(!worldIn.isRemote){
			NBTTagCompound tag = itemStackIn.getTagCompound();
			if(tag == null || !tag.hasKey("x") || !tag.hasKey("y") || !tag.hasKey("z")){
				playerIn.addChatComponentMessage(new TextComponentString("No Connection"));
			}else{
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				TileEntity tile = worldIn.getTileEntity(new BlockPos(x, y, z));
				if(tile instanceof FXDealerTileEntity){
					playerIn.openGui(FXCraft.instance, FXCraft.BLOCK_GUI_ID_0, worldIn, x, y, z);
				}else{
					playerIn.addChatComponentMessage(new TextComponentString("Cannot Find FXDealer at Registered Position"));
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
