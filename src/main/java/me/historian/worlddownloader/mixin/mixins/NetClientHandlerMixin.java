package me.historian.worlddownloader.mixin.mixins;

import me.historian.worlddownloader.WorldDownloader;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author ChadGamer82342
 * @since 1/9/2022
 */
@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {

	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIChest(Lnet/minecraft/src/IInventory;)V"))
	private IInventory modifyDisplayGUIChestInventory(IInventory oldInventory) {
		final Packet15Place packet;
		if(WorldDownloader.isDownloadingWorld() && (packet = WorldDownloader.getOpenContainerPacket()) != null) {
			return WorldDownloader.getWorldClientMixinAccessor().createNewChestInventory(packet.xPosition, packet.yPosition, packet.zPosition, oldInventory.getSizeInventory());
		}
		return oldInventory;
	}

	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIFurnace(Lnet/minecraft/src/TileEntityFurnace;)V"))
	private TileEntityFurnace modifyDisplayGUIFurnaceInventory(TileEntityFurnace oldTileEntityFurnace) {
		final Packet15Place packet;
		if(WorldDownloader.isDownloadingWorld() && (packet = WorldDownloader.getOpenContainerPacket()) != null) {
			TileEntityFurnace tileEntityFurnace = new TileEntityFurnace();
			WorldDownloader.getWorldClientMixinAccessor().setNewBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition, tileEntityFurnace);
			return tileEntityFurnace;
		}
		return oldTileEntityFurnace;
	}

	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIDispenser(Lnet/minecraft/src/TileEntityDispenser;)V"))
	private TileEntityDispenser modifyDisplayGUIDispenserInventory(TileEntityDispenser oldTileEntityDispenser) {
		final Packet15Place packet;
		if(WorldDownloader.isDownloadingWorld() && (packet = WorldDownloader.getOpenContainerPacket()) != null) {
			TileEntityDispenser tileEntityDispenser = new TileEntityDispenser();
			WorldDownloader.getWorldClientMixinAccessor().setNewBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition, tileEntityDispenser);
			return tileEntityDispenser;
		}
		return oldTileEntityDispenser;
	}

	
}
