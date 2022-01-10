/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.historian.worlddownloader.WorldDL;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet15Place;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(NetworkManager.class)
public class NetworkManagerMixin {
	@Inject(method = "addToSendQueue", at = @At("HEAD"))
	private void addToSendQueue(final Packet packet, final CallbackInfo callbackInfo) {
		if(WorldDL.isDownloadingWorld() && WorldDL.getSaveContainers() && packet instanceof Packet15Place) {
			final Packet15Place packet0 = (Packet15Place)packet;
			if(packet0.direction != 255 && (Block.blocksList[WorldDL.mc.theWorld.getBlockId(packet0.xPosition, packet0.yPosition, packet0.zPosition)] instanceof BlockContainer)) WorldDL.setOpenContainerPacket(packet0);
		}
	}
}
