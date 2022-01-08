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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.src.BlockChest;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(BlockChest.class)
public abstract class BlockChestMixin extends BlockContainer {
	private BlockChestMixin(final int id, final Material material) {
		super(id, material);
	}
	
	@Inject(method = "onBlockRemoval", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;method_1777(III)Lnet/minecraft/src/TileEntity;", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onBlockRemoval(final World world, final int x, final int y, final int z, CallbackInfo callbackInfo, final TileEntityChest tileEntityChest) {
		if(tileEntityChest == null) {
			callbackInfo.cancel();
			super.onBlockRemoval(world, x, y, z);
		}
	}
}
