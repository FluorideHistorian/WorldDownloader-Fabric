/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader;

import me.historian.worlddownloader.mixin.WorldClientMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.NetClientHandlerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.FabricLoaderImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.src.IChunkLoader;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.Packet15Place;

/**
 * @author historian
 * @since 1/8/2022
 */
public class WorldDL implements ClientModInitializer {
	public static final Minecraft mc = (Minecraft)FabricLoaderImpl.getInstance().getGameInstance();
	private static boolean downloadingWorld, allowMerging, saveContainers = true;
	private static Packet15Place openContainerPacket;
	private static IChunkLoader chunkLoader;
	private static ISaveHandler saveHandler;
	
	@Override
	public void onInitializeClient() {
		System.out.println("Starting");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Stopping")));
	}
	
	public static boolean isDownloadingWorld() {
		return downloadingWorld;
	}
	
	public static void setDownloadingWorld(final boolean downloadingWorld) {
		WorldDL.downloadingWorld = downloadingWorld;
	}
	
	public static IChunkLoader getChunkLoader() {
		return chunkLoader;
	}
	
	public static void setChunkLoader(final IChunkLoader chunkLoader) {
		WorldDL.chunkLoader = chunkLoader;
	}
	
	public static ISaveHandler getSaveHandler() {
		return saveHandler;
	}
	
	public static void setSaveHandler(final ISaveHandler saveHandler) {
		WorldDL.saveHandler = saveHandler;
	}
	
	public static Packet15Place getOpenContainerPacket() {
		return openContainerPacket;
	}
	
	public static void setOpenContainerPacket(final Packet15Place placePacket) {
		WorldDL.openContainerPacket = placePacket;
	}
	
	public static WorldClientMixinAccessor getWorldClientMixinAccessor() {
		return (WorldClientMixinAccessor)((NetClientHandlerAccessor)mc.method_2145()).getWorldClient();
	}
	
	public static boolean getAllowMerging() {
		return allowMerging;
	}
	
	public static void setAllowMerging(final boolean allowMerging) {
		WorldDL.allowMerging = allowMerging;
	}
	
	public static boolean getSaveContainers() {
		return saveContainers;
	}
	
	public static void setSaveContainers(final boolean saveContainers) {
		WorldDL.saveContainers = saveContainers;
	}
}
