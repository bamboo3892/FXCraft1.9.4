package com.okina.fxcraft.main;

import java.io.File;

import com.okina.fxcraft.integrate.PeripheralProvider;
import com.okina.fxcraft.rate.FXRateUpdateThread;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = FXCraft.MODID, name = FXCraft.NAME, version = FXCraft.VERSION)
public class FXCraft {

	public static final String MODID = "FXCraft";
	public static final String NAME = "FXCraft";
	public static final String VERSION = "1.0";

	@Mod.Instance(MODID)
	public static FXCraft instance;
	@SidedProxy(clientSide = "com.okina.fxcraft.main.ClientProxy", serverSide = "com.okina.fxcraft.main.CommonProxy")
	public static CommonProxy proxy;

	//FX rate update thread
	public static FXRateUpdateThread rateGetter = new FXRateUpdateThread();

	//configuration
	public static File ConfigFile;

	//block instance
	public static Block accountManager;
	public static Block fxDealer;
	public static Block eternalStorage;
	//	public static Block test;

	//item instance
	public static Item iPhone;
	public static Item[] limit_dealLot = new Item[5];
	public static Item[] limit_leverage = new Item[5];
	public static Item[] limit_position = new Item[5];
	public static Item limit_limits_trade;
	public static Item jentlemens_cap;
	public static Item jentlemens_panz;
	public static Item capitalist_gun;
	public static Item capitalist_guard;
	public static Item fx_mask;

	//creative tab
	public static final CreativeTabs FXCraftCreativeTab = new CreativeTabs("fxcraftCreativeTab") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(fxDealer);
		}
	};

	//GUI ID
	public static final int ITEM_GUI_ID = 0;
	public static final int BLOCK_GUI_ID_0 = 1;
	public static final int BLOCK_GUI_ID_1 = 2;
	public static final int BLOCK_GUI_ID_2 = 3;
	public static final int BLOCK_GUI_ID_3 = 4;
	public static final int BLOCK_GUI_ID_4 = 5;
	public static final int BLOCK_GUI_ID_5 = 6;

	//Particle ID
	public static final int PARTICLE_GUN = 0;

	//Sound Event
	public static SoundEvent SOUND_GUN;

	//packet
	public static SimpleNetworkWrapper packetDispatcher;
	public static final int SIMPLETILE_PACKET_ID = 0;
	public static final int SIMPLETILE_REPLY_PACKET_ID = 1;
	public static final int COMMAND_PACKET_ID = 2;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigFile = event.getModConfigurationDirectory();
		proxy.loadConfiguration(event.getSuggestedConfigurationFile());
		proxy.registerBlock();
		proxy.registerItem();
		proxy.initFXThread();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntity();
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
		proxy.registerRecipe();
		proxy.registerRenderer();
		packetDispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerPacket();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerRecipe();
	}

}
