/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(World.class)
public interface WorldAccessor {
	@Accessor
	IChunkProvider getChunkProvider();
	
	@Accessor
	WorldInfo getWorldInfo();
}
