/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.Packet15PlaceMixinAccessor;
import me.historian.worlddownloader.mixin.WorldClientMixinAccessor;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
	@Shadow
	private WorldClient worldClient;
	
	@Inject(method = "func_20004_a", at = @At("TAIL"))
	private void func_20004_a(final Packet100OpenWindow packet, final CallbackInfo callbackInfo) {
		if(WorldDownloader.isDownloadingWorld() && WorldDownloader.getOpenContainerPacket() != null) {
			((WorldClientMixinAccessor)worldClient).setNewBlockTileEntity(WorldDownloader.getOpenContainerPacket().xPosition, WorldDownloader.getOpenContainerPacket().yPosition,
			WorldDownloader.getOpenContainerPacket().zPosition, ((Packet15PlaceMixinAccessor)WorldDownloader.getOpenContainerPacket()).getContainer());
			WorldDownloader.setOpenContainerPacket(null);
		}
	}
}
