package com.okina.fxcraft.tileentity;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.AccountUpdateHandler;
import com.okina.fxcraft.account.FXDeal;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.account.GetPositionOrder;
import com.okina.fxcraft.account.IFXDealer;
import com.okina.fxcraft.account.SettlePositionOrder;
import com.okina.fxcraft.client.gui.DummyContainer;
import com.okina.fxcraft.client.gui.fxdealer.FXDealerGui;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.main.GuiHandler.IGuiTile;
import com.okina.fxcraft.network.ISimpleTilePacketUser;
import com.okina.fxcraft.network.PacketType;
import com.okina.fxcraft.network.SimpleTilePacket;
import com.okina.fxcraft.rate.NoValidRateException;
import com.okina.fxcraft.rate.RateData;
import com.okina.fxcraft.utils.Position;
import com.okina.fxcraft.utils.UtilMethods;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class FXDealerTileEntity extends TileEntity implements IGuiTile, ISimpleTilePacketUser, IFXDealer, IPeripheral {

	private long lastAccountUpdate = 100;
	private AccountInfo loginAccount;

	/**Server only*/
	private List<IComputerAccess> mountComputerList = Lists.newArrayList();

	//client only
	public int lastOpenedTab = 0;

	public FXDealerTileEntity() {}

	public void tryLogIn(String name, String password) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setString("password", password);
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
	}

	@Override
	public boolean hasAccountUpdate(long lastAccountUpdate) {
		return this.lastAccountUpdate > lastAccountUpdate;
	}

	@Override
	public AccountInfo getAccountInfo() {
		return loginAccount;
	}

	public void logOut() {
		loginAccount = null;
		lastAccountUpdate = System.currentTimeMillis();
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
	}

	public void tryGetPosition(String pair, int dealLot, int deposit, boolean askOrBid) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("pair", pair);
			tag.setInteger("deal", dealLot);
			tag.setInteger("deposit", deposit);
			tag.setBoolean("askOrBid", askOrBid);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_GET_POSITION, tag));
			//			FXCraft.proxy.appendPopUp("Send " + (askOrBid ? "ask" : "bid") + " packet");
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryGetPositionOrder(String pair, int dealLot, int deposit, boolean askOrBid, double limits) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("pair", pair);
			tag.setInteger("deal", dealLot);
			tag.setInteger("deposit", deposit);
			tag.setBoolean("askOrBid", askOrBid);
			tag.setDouble("limits", limits);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_ORDER_GET_POSITION, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void trySettlePosition(FXPosition position, int dealLot) {
		if(position != FXPosition.NO_INFO){
			if(loginAccount != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("account", loginAccount.name);
				tag.setString("id", position.positionID);
				tag.setInteger("deal", dealLot);
				FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_SETTLE_POSITION, tag));
			}else{
				FXCraft.proxy.appendPopUp("Please Login");
			}
		}
	}

	public void trySettlePositionOrder(FXPosition position, int dealLot, double limits) {
		if(position != FXPosition.NO_INFO){
			if(loginAccount != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("account", loginAccount.name);
				tag.setString("id", position.positionID);
				tag.setInteger("deal", dealLot);
				tag.setDouble("limits", limits);
				FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_ORDER_SETTLE_POSITION, tag));
			}else{
				FXCraft.proxy.appendPopUp("Please Login");
			}
		}
	}

	public void tryDeleteGetOrder(GetPositionOrder order) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("id", order.orderID);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_DELETE_GET_ORDER, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryDeleteSettleOrder(SettlePositionOrder order) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("id", order.position.positionID);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_DELETE_SETTLE_ORDER, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void validate() {
		super.validate();
		AccountUpdateHandler.instance.registerUpdateObject(this);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new DummyContainer() : new FXDealerGui(player, this);
	}

	/**Server only*/
	@Override
	public void updateAccountInfo(AccountInfo account) {
		if(account.equals(loginAccount)){
			loginAccount = account;
			lastAccountUpdate = System.currentTimeMillis();
			NBTTagCompound tag = new NBTTagCompound();
			account.writeToNBT(tag);
			FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_UPDATE, tag));
		}
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public void receiveResult(FXDeal deal, boolean success, String message, Object... obj) {
		FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, deal + ": " + message));
		List list = Lists.newArrayList();
		list.add(success);
		list.add(message);
		if(obj != null){
			for (Object o : obj){
				list.add(o);
			}
		}
		Object[] ooo = list.toArray();
		for (IComputerAccess computer : mountComputerList){
			computer.queueEvent(deal.toString(), ooo);
		}
	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		return null;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){//server
			if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				String password = tag.getString("password");
				if(AccountHandler.instance.canLogIn(name, password)){
					AccountInfo info = AccountHandler.instance.getAccountInfo(name);
					NBTTagCompound infoTag = new NBTTagCompound();
					info.writeToNBT(infoTag);
					tag.setTag("info", infoTag);
					tag.setBoolean("result", true);
					tag.removeTag("password");
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}else{
					tag.setBoolean("result", false);
					tag.removeTag("password");
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
				loginAccount = null;
				lastAccountUpdate = System.currentTimeMillis();
			}else if(type == PacketType.FX_GET_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String pair = tag.getString("pair");
					int dealLot = tag.getInteger("deal");
					int deposit = tag.getInteger("deposit");
					boolean askOrBid = tag.getBoolean("askOrBid");
					AccountHandler.instance.tryGetPosition(this, accountName, pair, dealLot, deposit, askOrBid);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_ORDER_GET_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String pair = tag.getString("pair");
					int dealLot = tag.getInteger("deal");
					int deposit = tag.getInteger("deposit");
					boolean askOrBid = tag.getBoolean("askOrBid");
					double limits = tag.getDouble("limits");
					AccountHandler.instance.tryGetPositionOrder(this, accountName, pair, dealLot, deposit, askOrBid, limits);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_SETTLE_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String id = tag.getString("id");
					int dealLot = tag.getInteger("deal");
					AccountHandler.instance.trySettlePosition(this, accountName, id, dealLot);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_ORDER_SETTLE_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String id = tag.getString("id");
					int dealLot = tag.getInteger("deal");
					double limits = tag.getDouble("limits");
					AccountHandler.instance.trySettlePositionOrder(this, accountName, id, dealLot, limits);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_DELETE_GET_ORDER && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String orderID = tag.getString("id");
					AccountHandler.instance.tryDeleteOrder(this, accountName, orderID, true);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_DELETE_SETTLE_ORDER && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String orderID = tag.getString("id");
					AccountHandler.instance.tryDeleteOrder(this, accountName, orderID, false);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}
		}else{//client
			if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				Boolean result = tag.getBoolean("result");
				if(result){
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag.getCompoundTag("info"));
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.appendPopUp("LogIn: " + name);
				}else{
					FXCraft.proxy.appendPopUp("LogIn failed : " + name);
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				if(loginAccount != null){
					FXCraft.proxy.appendPopUp("LogOut : " + loginAccount.name);
					loginAccount = null;
					lastAccountUpdate = System.currentTimeMillis();
				}else{
					FXCraft.proxy.appendPopUp("LogOut");
				}
			}else if(type == PacketType.ACCOUNT_UPDATE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				if(tag.hasKey("name")){
					String name = tag.getString("name");
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag);
					loginAccount = info;
				}else{
					loginAccount = null;
				}
				lastAccountUpdate = System.currentTimeMillis();
			}else if(type == PacketType.MESSAGE && value instanceof String){
				FXCraft.proxy.appendPopUp((String) value);
			}
		}
	}

	@Override
	public Position getPosition() {
		return new Position(pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		return tag;
	}

	//computer craft///////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final String[] METHODS = { "getPosition", "getGetOrder", "getSettleOrder", "getPositions", "getGetOrders", "getSettleOrders", "getCurrencyPairs", "getRate", "getRates", "ask", "bid", "settle", "askOrder", "bidOrder", "settleOrder", "deleteGetOrder", "deleteSettleOrder", "getAccount" };

	@Override
	public String getType() {
		return "fx_dealer";
	}

	@Override
	public String[] getMethodNames() {
		return METHODS;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method) {
		case 0://getPosition
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					FXPosition position = loginAccount.getPosition((String) arguments[0]);
					if(position == null){
						return null;
					}else{
						return new Object[] { UtilMethods.getTableFromPosition(position) };
					}
				}else{
					throw new LuaException("USAGE: getPosition(positionID)");
				}
			}
		case 1://getGetOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					GetPositionOrder order = loginAccount.getGetOrder((String) arguments[0]);
					if(order == null){
						return null;
					}else{
						return new Object[] { UtilMethods.getTableFromGetOrder(order) };
					}
				}else{
					throw new LuaException("USAGE: getGetOrder(orderID)");
				}
			}
		case 2://getSettleOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					SettlePositionOrder order = loginAccount.getSettleOrder((String) arguments[0]);
					if(order == null){
						return null;
					}else{
						return new Object[] { UtilMethods.getTableFromSettleOrder(order) };
					}
				}else{
					throw new LuaException("USAGE: getSettleOrder(orderID)");
				}
			}
		case 3://getPositions
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments == null || arguments.length == 0){
					List list = Lists.newArrayList();
					for (FXPosition position : loginAccount.positionList){
						list.add(UtilMethods.getTableFromPosition(position));
					}
					return list.toArray();
				}else{
					throw new LuaException("USAGE: getPositions()");
				}
			}
		case 4://getGetOrders
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments == null || arguments.length == 0){
					List list = Lists.newArrayList();
					for (GetPositionOrder order : loginAccount.getPositionOrder){
						list.add(UtilMethods.getTableFromGetOrder(order));
					}
					return list.toArray();
				}else{
					throw new LuaException("USAGE: getGetOrders()");
				}
			}
		case 5://getSettleOrders
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments == null || arguments.length == 0){
					List list = Lists.newArrayList();
					for (SettlePositionOrder order : loginAccount.settlePositionOrder){
						list.add(UtilMethods.getTableFromSettleOrder(order));
					}
					return list.toArray();
				}else{
					throw new LuaException("USAGE: getSettleOrders()");
				}
			}
		case 6://getCurrencyPairs
			return new Object[] { "USDJPY", "EURJPY", "EURUSD" };
		case 7://getRate
			if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
				String pair = (String) arguments[0];
				try{
					RateData rate = FXCraft.rateGetter.getEarliestRate(pair);
					if(rate != null){
						return new Object[] { UtilMethods.getTableFromRate(rate) };
					}else{
						return new Object[] { Maps.newHashMap() };
					}
				}catch (NoValidRateException e){
					return new Object[] { Maps.newHashMap() };
				}catch (IllegalArgumentException e){
					throw new LuaException("Available Rate : USDJPY, EURJPY, EURUSD");
				}
			}else{
				throw new LuaException("USAGE: getRate(pairName)");
			}
		case 8://getRates
			try{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					String pair = (String) arguments[0];
					List<RateData> dataList = FXCraft.rateGetter.getAllRates(pair);
					List<HashMap> list = Lists.newArrayList();
					for (RateData data : dataList){
						list.add(UtilMethods.getTableFromRate(data));
					}
					return list.toArray();
				}else{
					throw new LuaException("USAGE: getRates(pairName)");
				}
			}catch (IllegalArgumentException e){
				throw new LuaException("Available Rate : USDJPY, EURJPY, EURUSD");
			}
		case 9://ask
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 3 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double){
					AccountHandler.instance.tryGetPosition(this, loginAccount.name, (String) arguments[0], (Double) arguments[1], (Double) arguments[2], true);
					return getAFE(context.pullEvent(FXDeal.GET_POSITION.toString()));
				}else{
					throw new LuaException("USAGE: ask(pairName, dealLot, deposit)");
				}
			}
		case 10://bid
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 3 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double){
					AccountHandler.instance.tryGetPosition(this, loginAccount.name, (String) arguments[0], (Double) arguments[1], (Double) arguments[2], false);
					return getAFE(context.pullEvent(FXDeal.GET_POSITION.toString()));
				}else{
					throw new LuaException("USAGE: bid(pairName, dealLot, deposit)");
				}
			}
		case 11://settle
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
					AccountHandler.instance.trySettlePosition(this, loginAccount.name, (String) arguments[0], (Double) arguments[1]);
					return getAFE(context.pullEvent(FXDeal.SETTLE_POSITION.toString()));
				}else{
					throw new LuaException("USAGE: settle(positionID, dealLot)");
				}
			}
		case 12://askOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 4 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double){
					AccountHandler.instance.tryGetPositionOrder(this, loginAccount.name, (String) arguments[0], (Double) arguments[1], (Double) arguments[2], true, (Double) arguments[3]);
					return getAFE(context.pullEvent(FXDeal.GET_POSITION_ORDER.toString()));
				}else{
					throw new LuaException("USAGE: ask(pairName, dealLot, deposit, limits)");
				}
			}
		case 13://bidOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 4 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double){
					AccountHandler.instance.tryGetPositionOrder(this, loginAccount.name, (String) arguments[0], (Double) arguments[1], (Double) arguments[2], true, (Double) arguments[3]);
					return getAFE(context.pullEvent(FXDeal.GET_POSITION_ORDER.toString()));
				}else{
					throw new LuaException("USAGE: ask(pairName, dealLot, deposit, limits)");
				}
			}
		case 14://settleOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 3 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double){
					AccountHandler.instance.trySettlePositionOrder(this, loginAccount.name, (String) arguments[0], (Double) arguments[1], (Double) arguments[1]);
					return getAFE(context.pullEvent(FXDeal.SETTLE_POSITION_ORDER.toString()));
				}else{
					throw new LuaException("USAGE: settle(positionID, dealLot, limits)");
				}
			}
		case 15://deleteGetOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					AccountHandler.instance.tryDeleteOrder(this, loginAccount.name, (String) arguments[0], true);
					return getAFE(context.pullEvent(FXDeal.DELETE_GET_ORDER.toString()));
				}else{
					throw new LuaException("USAGE: deleteGetOrder(orderID)");
				}
			}
		case 16://deleteSettleOrder
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments != null && arguments.length == 1 && arguments[0] instanceof String){
					AccountHandler.instance.tryDeleteOrder(this, loginAccount.name, (String) arguments[0], false);
					return getAFE(context.pullEvent(FXDeal.DELETE_SETTLE_ORDER.toString()));
				}else{
					throw new LuaException("USAGE: deleteSettleOrder(orderID)");
				}
			}
		case 17://getAccount
			if(loginAccount == null){
				throw new LuaException("Please Login");
			}else{
				if(arguments == null || arguments.length == 0){
					return new Object[] { UtilMethods.getTableFromAccount(loginAccount) };
				}else{
					throw new LuaException("USAGE: getAccount()");
				}
			}
		}
		throw new LuaException("Not Supported Method");
	}

	private Object[] getAFE(Object[] objs) {
		if(objs == null || objs.length <= 1){
			return null;
		}else{
			Object[] rtn = new Object[objs.length - 1];
			for (int i = 1; i < objs.length; i++){
				rtn[i - 1] = objs[i];
			}
			return rtn;
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
		mountComputerList.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		mountComputerList.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}

}
