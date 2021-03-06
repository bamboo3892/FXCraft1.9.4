package com.okina.fxcraft.item;

import java.util.List;

import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.client.model.ModelJentleArmor;
import com.okina.fxcraft.main.ClientProxy;
import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJentlemensPanz extends ItemArmor implements IToolTipUser {

	public ItemJentlemensPanz(ArmorMaterial material, int renderId) {
		super(material, renderId, EntityEquipmentSlot.LEGS);
		setMaxStackSize(1);
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setUnlocalizedName("fxcraft_jentlemens_panz");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote){
			if(player.isPotionActive(Potion.getPotionById(PotionType.getID(PotionTypes.MUNDANE)))){
				player.removePotionEffect(Potion.getPotionById(PotionType.getID(PotionTypes.MUNDANE)));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		ModelJentleArmor armorModel = ClientProxy.modelJentlemensCap;
		if(armorModel != null){
			armorModel.bipedHead.showModel = false;
			armorModel.bipedHeadwear.showModel = false;
			armorModel.bipedBody.showModel = false;
			armorModel.bipedRightArm.showModel = false;
			armorModel.bipedLeftArm.showModel = false;
			armorModel.bipedRightLeg.showModel = true;
			armorModel.bipedLeftLeg.showModel = true;

			armorModel.isSneak = _default.isSneak;
			armorModel.isRiding = _default.isRiding;
			armorModel.isChild = _default.isChild;
			armorModel.leftArmPose = _default.leftArmPose;
			armorModel.rightArmPose = _default.rightArmPose;
			armorModel.swingProgress = _default.swingProgress;
		}
		return armorModel;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer) {
		return FXCraft.MODID + ":textures/models/armor/jentlemens_armor.png";
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("Cure Wither");
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
