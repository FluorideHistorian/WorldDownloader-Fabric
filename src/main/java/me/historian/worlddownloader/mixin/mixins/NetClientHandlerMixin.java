package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.WorldClientMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.NetClientHandlerAccessor;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet103SetSlot;
import net.minecraft.src.Packet104WindowItems;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;

/**
 * @author ChadGamer82342
 * @since 1/9/2022
 */
@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
	
	@Unique
	private IInventory currentInventory;
	
	@Inject(method = "func_20003_a", at = @At("HEAD"))
	private void handleSetSlot(final Packet103SetSlot packet, final CallbackInfo callbackInfo) {
		if(WorldDownloader.isDownloadingWorld() && packet.windowId == WorldDownloader.mc.thePlayer.craftingInventory.windowId && currentInventory != null) {
			if(packet.itemSlot < currentInventory.getSizeInventory()) currentInventory.setInventorySlotContents(packet.itemSlot, packet.myItemStack);
		}
	}
	
	@Inject(method = "func_20001_a", at = @At("HEAD"))
	private void handleWindowItems(final Packet104WindowItems packet, final CallbackInfo callbackInfo) {
		if(WorldDownloader.isDownloadingWorld() && packet.windowId == WorldDownloader.mc.thePlayer.craftingInventory.windowId) {
			final TileEntity tileEntity = WorldDownloader.getCurrentTileEntity();
			final Packet15Place packet0 = WorldDownloader.getOpenContainerPacket();
			if(tileEntity instanceof IInventory) {
				IInventory inventory = ((IInventory)tileEntity);
				if(tileEntity instanceof TileEntityChest) inventory = new InventoryBasic("", packet.itemStack.length);
				for(int i = 0; i < inventory.getSizeInventory(); i++) inventory.setInventorySlotContents(i, packet.itemStack[i]);
				currentInventory = inventory;
				if(tileEntity instanceof TileEntityChest) {
					((WorldClientMixinAccessor)((NetClientHandlerAccessor)WorldDownloader.mc.method_2145()).getWorldClient()).setNewChestTileEntity(packet0.xPosition, packet0.yPosition, packet0.zPosition, inventory);
				} else {
					((WorldClientMixinAccessor)((NetClientHandlerAccessor)WorldDownloader.mc.method_2145()).getWorldClient()).setNewBlockTileEntity(packet0.xPosition, packet0.yPosition, packet0.zPosition, tileEntity);
				}
			}
		}
	}
	
}
