/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins.accessor;

import java.io.File;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.src.PlayerNBTManager;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(PlayerNBTManager.class)
public interface PlayerNBTManagerAccessor {
	@Invoker("getWorldDir")
	File callGetWorldDir();
}
