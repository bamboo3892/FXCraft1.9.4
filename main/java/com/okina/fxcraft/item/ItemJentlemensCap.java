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
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJentlemensCap extends ItemArmor implements IToolTipUser {

	public ItemJentlemensCap(ArmorMaterial material, int renderId) {
		super(material, renderId, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setUnlocalizedName("fxcraft_jentlemens_cap");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote){
			if(player.isPotionActive(Potion.getPotionById(PotionType.getID(PotionTypes.POISON)))){
				player.removePotionEffect(Potion.getPotionById(PotionType.getID(PotionTypes.POISON)));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemstack, int armorSlot) {
		ModelJentleArmor armorModel = ClientProxy.modelJentlemensCap;
		if(armorModel != null){
			armorModel.bipedHead.showModel = true;
			armorModel.bipedHeadwear.showModel = false;
			armorModel.bipedBody.showModel = false;
			armorModel.bipedRightArm.showModel = false;
			armorModel.bipedLeftArm.showModel = false;
			armorModel.bipedRightLeg.showModel = false;
			armorModel.bipedLeftLeg.showModel = false;

			armorModel.isSneak = entityLiving.isSneaking();
			armorModel.isRiding = entityLiving.isRiding();
			armorModel.isChild = entityLiving.isChild();

			armorModel.heldItemRight = 0;
			armorModel.aimedBow = false;

			EntityPlayer player = (EntityPlayer) entityLiving;
			ItemStack held_item = player.getEquipmentInSlot(0);
			if(held_item != null){
				armorModel.heldItemRight = 1;
				if(player.getItemInUseCount() > 0){
					EnumAction enumaction = held_item.getItemUseAction();
					if(enumaction == EnumAction.BOW){
						armorModel.aimedBow = true;
					}else if(enumaction == EnumAction.BLOCK){
						armorModel.heldItemRight = 3;
					}
				}
			}
		}
		return armorModel;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer) {
		return FXCraft.MODID + ":textures/models/armor/jentlemens_armor.png";
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("Cure Poison");
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
