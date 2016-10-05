package com.okina.fxcraft.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.IAccountInfoContainer;
import com.okina.fxcraft.client.IHUDArmor;
import com.okina.fxcraft.client.IHUDItem;
import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCapitalistGuard extends ItemArmor implements IHUDItem, IHUDArmor, IToolTipUser {

	public static final int HEAL_COST = 1000;

	public ItemCapitalistGuard(ArmorMaterial material, int renderId) {
		super(material, renderId, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setUnlocalizedName("fxcraft_capitalist_guard");
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean equipped) {
		if(equipped && !world.isRemote){
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
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote){
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
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return FXCraft.MODID + ":textures/models/armor/captalist_guard.png";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack) {
		EntityPlayer player = mc.thePlayer;
		if(player != null){
			if(itemStack != null && itemStack.getItem() == FXCraft.capitalist_guard){
				NBTTagCompound tag = itemStack.getTagCompound();
				if(tag != null && tag.hasKey("account")){
					String account = tag.getString("account");
					double balance = tag.getDouble("balance");
					ColoredString s = new ColoredString(" ", 0);
					RenderingHelper.renderHUDRight(mc, Lists.<ColoredString> newArrayList(s, s, s, s, new ColoredString("Account: " + account, 0xFFFFFF), new ColoredString(String.format("%.1f", balance), 0x7fff00)));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean comparePastRenderObj(ItemStack object) {
		return this == object.getItem();
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("1 Heart = " + (HEAL_COST * 2) + " Lot");
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

}
