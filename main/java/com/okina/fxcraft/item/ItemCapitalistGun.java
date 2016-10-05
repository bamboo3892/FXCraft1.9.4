package com.okina.fxcraft.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.IAccountInfoContainer;
import com.okina.fxcraft.client.IHUDItem;
import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.network.CommandPacket;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.RenderingHelper;
import com.okina.fxcraft.utils.UtilMethods;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCapitalistGun extends Item implements IToolTipUser, IHUDItem {

	public static final int LotToShot = 10;

	public ItemCapitalistGun() {
		setUnlocalizedName("fxcraft_capitalist_gun");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean equipped) {
		if(equipped && !world.isRemote && entity instanceof EntityPlayerMP){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				AccountInfo account = AccountHandler.instance.getAccountInfo(tag.getString("account"));
				if(account != null){
					tag.setDouble("balance", account.balance);
				}else{
					tag.setDouble("balance", 0);
				}
			}
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IAccountInfoContainer){
			AccountInfo account = ((IAccountInfoContainer) tile).getAccountInfo();
			if(account != null){
				NBTTagCompound tag = stack.getTagCompound();
				if(tag == null) tag = new NBTTagCompound();
				tag.setString("account", account.name);
				stack.setTagCompound(tag);
				if(world.isRemote) player.addChatComponentMessage(new TextComponentString("Account Registered!"));
			}
			return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		NBTTagCompound tag = itemStackIn.getTagCompound();
		if(tag == null || !tag.hasKey("account")){
			if(worldIn.isRemote) playerIn.addChatComponentMessage(new TextComponentString("Not Account Registered"));
		}else{
			String name = tag.getString("account");
			if(!worldIn.isRemote && playerIn instanceof EntityPlayerMP){
				AccountInfo account = AccountHandler.instance.getAccountInfo(name);
				if(account != null){
					if(AccountHandler.instance.decBalance(name, LotToShot)){
						worldIn.playSoundAtEntity(playerIn, FXCraft.MODID + ":gun", 1f, 1f);
						Entity entity = UtilMethods.getCollidedEntityFromEntity(worldIn, playerIn, 20);
						if(entity instanceof EntityLivingBase){
							EntityLivingBase living = (EntityLivingBase) entity;
							living.attackEntityFrom(DamageSource.outOfWorld, 0.5f);
							FXCraft.proxy.sendCommandPacket(new CommandPacket("gun", living.posX + "," + (living.posY + living.height) + "," + living.posZ), (EntityPlayerMP) playerIn);
						}
					}else{
						playerIn.addChatComponentMessage(new TextComponentString("Not Enough Balance"));
					}
				}else{
					playerIn.addChatComponentMessage(new TextComponentString("Invalid Account"));
				}
			}else if(worldIn.isRemote){
				if(name != null && !"".equals(name)){
					double balance = tag.getDouble("balance");
					if(balance >= LotToShot){
						float f = (float) (Math.random() * 3);
						playerIn.cameraPitch -= f;
						playerIn.rotationPitch -= f * 0.5f;
						Vec3d vec = playerIn.getLookVec();
						Vec3d v = vec.crossProduct(new Vec3d(0, 1, 0));
						double x = playerIn.posX + v.xCoord * 0.1;
						double y = playerIn.posY + playerIn.getEyeHeight() - 0.05;
						double z = playerIn.posZ + v.zCoord * 0.1;
						FXCraft.proxy.spawnParticle(worldIn, FXCraft.PARTICLE_GUN, x, y, z, vec.xCoord, vec.yCoord, vec.zCoord);
					}
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("1 Shot = " + LotToShot + " Lot");
		toolTip.add("Account: " + (itemStack.hasTagCompound() ? itemStack.getTagCompound().getString("account") : ""));
	}

	@Override
	public int getNeutralLines() {
		return 0;
	}

	@Override
	public int getShiftLines() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack) {
		EntityPlayer player = mc.thePlayer;
		if(player != null){
			if(itemStack != null && itemStack.getItem() == FXCraft.capitalist_gun){
				NBTTagCompound tag = itemStack.getTagCompound();
				if(tag != null && tag.hasKey("account")){
					String account = tag.getString("account");
					double balance = tag.getDouble("balance");
					RenderingHelper.renderHUDRight(mc, Lists.<ColoredString> newArrayList(new ColoredString("Account: " + account, 0xFFFFFF), new ColoredString(String.format("%.1f", balance), 0x7fff00)));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean comparePastRenderObj(ItemStack object) {
		return this == object.getItem();
	}

}
