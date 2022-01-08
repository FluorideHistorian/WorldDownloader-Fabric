/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.Packet15PlaceMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.BlockContainerAccessor;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.TileEntity;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(Packet15Place.class)
public class Packet15PlaceMixin implements Packet15PlaceMixinAccessor {
	@Unique
	private TileEntity container;
	
	@Inject(method = "<init>(IIIILnet/minecraft/src/ItemStack;)V", at = @At("TAIL"))
	private void Packet15Place(final int xPosition, final int yPosition, final int zPosition, final int direction, final ItemStack itemStack, final CallbackInfo callbackInfo) {
		if(direction != 255 && WorldDownloader.isDownloadingWorld()) {
			final Block block = Block.blocksList[WorldDownloader.mc.theWorld.getBlockId(((Packet15Place)((Object)this)).xPosition, ((Packet15Place)((Object)this)).yPosition, ((Packet15Place)((Object)this)).zPosition)];
			if(block instanceof BlockContainer) {
				final TileEntity tileEntity = ((BlockContainerAccessor)block).callMethod_1251();
				if(tileEntity instanceof IInventory) {
					container = tileEntity;
					WorldDownloader.setOpenContainerPacket(((Packet15Place)((Object)this)));
					return;
				}
			}
		}
		WorldDownloader.setOpenContainerPacket(null);
	}
	
	@Override
	public TileEntity getContainer() {
		return container;
	}
}
