/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import me.historian.worlddownloader.GuiWorldDL;
import me.historian.worlddownloader.WorldDL;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngameMenu;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(GuiIngameMenu.class)
public class GuiIngameMenuMixin extends GuiScreen {
	@Unique
	private int stopDownloadIn = -69;
	@Unique
	private GuiButton button;
	
	@SuppressWarnings("unchecked")
	@Inject(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void initGui(final CallbackInfo callbackInfo) {
		((GuiButton)controlList.get(0)).yPosition = height / 4 + 144 - 16;
		controlList.add(button = new GuiButton(69, width / 2 - 100, height / 4 + 120 - 16, 180, 20, WorldDL.isDownloadingWorld() ? "Stop downloading this world" : "Download this world"));
		controlList.add(new GuiButton(70, width / 2 + 80, height / 4 + 120 - 16, 20, 20, "..."));
	}
	
	@Inject(method = "drawScreen", at = @At("TAIL"))
	private void drawScreen(final int mouseX, final int mouseY, final float renderPartialTicks, final CallbackInfo callbackInfo) {
		if(stopDownloadIn-- == 0) {
			stopDownload();
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
			stopDownloadIn = -69;
		}
	}
	
	@Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;sendQuittingDisconnectingPacket()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void actionPerformed(final GuiButton button, final CallbackInfo callbackInfo) {
		if(WorldDL.isDownloadingWorld()) stopDownload();
	}
	
	@Inject(method = "actionPerformed", at = @At("TAIL"))
	private void actionPerformed0(final GuiButton button, final CallbackInfo callbackInfo) {
		if(button.id == 69 && stopDownloadIn <= 0) {
			if(WorldDL.isDownloadingWorld()) {
				this.button.displayString = "Saving a shitload of data...";
				stopDownloadIn = 2;
			} else {
				startDownload();
				mc.displayGuiScreen(null);
			}
		} else if(button.id == 70) {
			mc.displayGuiScreen(new GuiWorldDL());
		}
	}

	private void startDownload() {
		WorldDL.startWorldDownload();
		mc.ingameGUI.addChatMessage("\247c[WorldDL] \2476Download started.");
	}

	private void stopDownload() {
		WorldDL.stopWorldDownload();
		mc.ingameGUI.addChatMessage("\247c[WorldDL] \2476Download stopped.");
	}
}
