/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin;

import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;

/**
 * @author historian
 * @since 1/8/2022
 */
public interface WorldClientMixinAccessor {
	void setNewBlockTileEntity(final int x, final int y, final int z, final TileEntity tileEntity);
	TileEntity getBlockTileEntity(final int x, final int y, final int z);
	IInventory createNewChestInventory(final int x, final int y, final int z, final int size);
}
