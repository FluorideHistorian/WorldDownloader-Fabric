/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import me.historian.worlddownloader.WorldDL;
import net.minecraft.client.Minecraft;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author ChadGamer82342
 * @since 1/9/2022
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public World theWorld;

    @Inject(method = "startGame", at = @At("HEAD"))
    public void onStartGame(CallbackInfo ci) {
        WorldDL.mc = (Minecraft) (Object) this; // Changed to this for support of different fabric-loader implementations
    }
    @Inject(method = "changeWorld1", at = @At("TAIL"))
    public void onChangeWorld(World newWorld, CallbackInfo ci) {
        if(this.theWorld != null && newWorld != null && WorldDL.isDownloadingWorld()) {
            WorldDL.postChangeWorlds();
        } else if(this.theWorld != null && newWorld == null && WorldDL.isDownloadingWorld()) {
            // Setting world to null probably getting disconnected from the server.
            WorldDL.stopWorldDownload();
        }
    }

}
