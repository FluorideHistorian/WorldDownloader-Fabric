/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin;

import net.minecraft.src.TileEntity;

/**
 * @author historian
 * @since 1/8/2022
 */
public interface Packet15PlaceMixinAccessor {
	TileEntity getContainer();
}
