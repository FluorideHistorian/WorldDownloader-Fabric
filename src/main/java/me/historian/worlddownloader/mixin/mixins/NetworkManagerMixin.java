/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import me.historian.worlddownloader.WorldDownloader;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(NetworkManager.class)
public class NetworkManagerMixin {
	@Inject(method = "addToSendQueue", at = @At("HEAD"))
	private void addToSendQueue(final Packet packet, final CallbackInfo callbackInfo) {
		if(WorldDownloader.isDownloadingWorld()) {
			if(packet instanceof Packet15Place) {
				final Packet15Place packet0 = (Packet15Place)packet;
				if(packet0.direction != 255) {
					final Block block = Block.blocksList[WorldDownloader.mc.theWorld.getBlockId(packet0.xPosition, packet0.yPosition, packet0.zPosition)];
					if(block instanceof BlockContainer) WorldDownloader.setOpenContainerPacket(packet0);
				}
			}
		}
	}
}
