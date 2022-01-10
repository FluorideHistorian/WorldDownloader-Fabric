/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import me.historian.worlddownloader.WorldDL;
import net.minecraft.src.IInventory;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.TileEntityDispenser;
import net.minecraft.src.TileEntityFurnace;

/**
 * @author ChadGamer82342
 * @since 1/9/2022
 * 
 * TODO this isn't the greatest solution for mod compatibility
 */
@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIChest(Lnet/minecraft/src/IInventory;)V"))
	private IInventory modifyDisplayGUIChestInventory(final IInventory oldInventory) {
		final Packet15Place packet;
		if(WorldDL.isDownloadingWorld() && (packet = WorldDL.getOpenContainerPacket()) != null) {
			WorldDL.setOpenContainerPacket(null);
			return WorldDL.getWorldClientMixinAccessor().createNewChestInventory(packet.xPosition, packet.yPosition, packet.zPosition, oldInventory.getSizeInventory());
		}
		return oldInventory;
	}
	
	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIFurnace(Lnet/minecraft/src/TileEntityFurnace;)V"))
	private TileEntityFurnace modifyDisplayGUIFurnaceInventory(final TileEntityFurnace oldTileEntityFurnace) {
		final Packet15Place packet;
		if(WorldDL.isDownloadingWorld() && (packet = WorldDL.getOpenContainerPacket()) != null) {
			final TileEntityFurnace tileEntityFurnace = new TileEntityFurnace();
			WorldDL.getWorldClientMixinAccessor().setNewBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition, tileEntityFurnace);
			WorldDL.setOpenContainerPacket(null);
			return tileEntityFurnace;
		}
		return oldTileEntityFurnace;
	}
	
	@ModifyArg(method = "func_20004_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;displayGUIDispenser(Lnet/minecraft/src/TileEntityDispenser;)V"))
	private TileEntityDispenser modifyDisplayGUIDispenserInventory(final TileEntityDispenser oldTileEntityDispenser) {
		final Packet15Place packet;
		if(WorldDL.isDownloadingWorld() && (packet = WorldDL.getOpenContainerPacket()) != null) {
			final TileEntityDispenser tileEntityDispenser = new TileEntityDispenser();
			WorldDL.getWorldClientMixinAccessor().setNewBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition, tileEntityDispenser);
			WorldDL.setOpenContainerPacket(null);
			return tileEntityDispenser;
		}
		return oldTileEntityDispenser;
	}
	
}
