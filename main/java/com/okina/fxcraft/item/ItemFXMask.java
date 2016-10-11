package com.okina.fxcraft.item;

import java.util.List;

import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.client.model.ModelFXMask;
import com.okina.fxcraft.main.ClientProxy;
import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFXMask extends ItemArmor implements IToolTipUser {

	public ItemFXMask(ArmorMaterial material, int renderId) {
		super(material, renderId, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setUnlocalizedName("fxcraft_fx_mask");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote){

		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		ModelFXMask armorModel = ClientProxy.modelFXMask;

		armorModel.isSneak = _default.isSneak;
		armorModel.isRiding = _default.isRiding;
		armorModel.isChild = _default.isChild;
		armorModel.leftArmPose = _default.leftArmPose;
		armorModel.rightArmPose = _default.rightArmPose;
		armorModel.swingProgress = _default.swingProgress;

		return armorModel;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer) {
		return FXCraft.MODID + ":textures/models/armor/fx_mask.png";
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("You Have Nothing To Lost!");
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
