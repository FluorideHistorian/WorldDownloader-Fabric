/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import me.historian.worlddownloader.GuiWorldDL;
import me.historian.worlddownloader.WorldDL;
import me.historian.worlddownloader.mixin.ChunkProviderClientMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.NetClientHandlerAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.PlayerNBTManagerAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.WorldAccessor;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngameMenu;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.WorldClient;

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
		String worldName = mc.gameSettings.lastServer;
		if(worldName.isEmpty()) worldName = "Downloaded World";
		if(!WorldDL.getAllowMerging()) worldName += " " + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now());
		final WorldClient worldClient = ((NetClientHandlerAccessor)mc.method_2145()).getWorldClient();
		((WorldAccessor)worldClient).getWorldInfo().setWorldName(worldName);
		WorldDL.setSaveHandler(mc.method_2127().method_1009(worldName, false));
		WorldDL.setChunkLoader(WorldDL.getSaveHandler().method_1734(worldClient.worldProvider));
		((WorldAccessor)worldClient).getWorldInfo().setSizeOnDisk(getFileSizeRecursive(((PlayerNBTManagerAccessor)WorldDL.getSaveHandler()).callGetWorldDir()));
		((ChunkProviderClientMixinAccessor)((WorldAccessor)worldClient).getChunkProvider()).importOldTileEntities();
		WorldDL.setDownloadingWorld(true);
		mc.ingameGUI.addChatMessage("\247c[WorldDL] \2476Download started.");
	}
	
	private void stopDownload() {
		((NetClientHandlerAccessor)mc.method_2145()).getWorldClient().saveWorld(true, null);
		WorldDL.setDownloadingWorld(false);
		WorldDL.setChunkLoader(null);
		WorldDL.setSaveHandler(null);
		mc.ingameGUI.addChatMessage("\247c[WorldDL] \2476Download stopped.");
	}
	
	private static long getFileSizeRecursive(final File file) {
		long size = 0;
		for(final File file0 : file.listFiles()) {
			if(file0.isDirectory()) size += getFileSizeRecursive(file0);
			else if(file0.isFile()) size += file0.length();
		}
		return size;
	}
}
