/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader;

import me.historian.worlddownloader.mixin.ChunkProviderClientMixinAccessor;
import me.historian.worlddownloader.mixin.WorldClientMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.NetClientHandlerAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.PlayerNBTManagerAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.WorldAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.src.IChunkLoader;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.WorldClient;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author historian
 * @since 1/8/2022
 */
public class WorldDL implements ClientModInitializer {
	public static Minecraft mc;
	private static boolean downloadingWorld, allowMerging, saveContainers = true;
	private static Packet15Place openContainerPacket;
	private static IChunkLoader chunkLoader;
	private static ISaveHandler saveHandler;
	private static WorldClient worldClient;
	
	@Override
	public void onInitializeClient() {
		System.out.println("Starting");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Stopping")));
	}

	public static void startWorldDownload() {
		String worldName = mc.gameSettings.lastServer;
		if(worldName.isEmpty()) worldName = "Downloaded World";
		if(!WorldDL.getAllowMerging()) worldName += " " + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now());
		worldClient = ((NetClientHandlerAccessor)mc.method_2145()).getWorldClient();
		((WorldAccessor)worldClient).getWorldInfo().setWorldName(worldName);
		WorldDL.setSaveHandler(mc.method_2127().method_1009(worldName, false));
		WorldDL.setChunkLoader(WorldDL.getSaveHandler().method_1734(worldClient.worldProvider));
		((WorldAccessor)worldClient).getWorldInfo().setSizeOnDisk(getFileSizeRecursive(((PlayerNBTManagerAccessor)WorldDL.getSaveHandler()).callGetWorldDir()));
		((ChunkProviderClientMixinAccessor)((WorldAccessor)worldClient).getChunkProvider()).importOldTileEntities();
		WorldDL.setDownloadingWorld(true);
	}

	public static void changeWorlds() {

	}

	public static void stopWorldDownload() {
		if(worldClient == null) return;
		worldClient.saveWorld(true, null);
		worldClient = null;
		WorldDL.setDownloadingWorld(false);
		WorldDL.setChunkLoader(null);
		WorldDL.setSaveHandler(null);

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

	private static long getFileSizeRecursive(final File file) {
		long size = 0;
		for(final File file0 : file.listFiles()) {
			if(file0.isDirectory()) size += getFileSizeRecursive(file0);
			else if(file0.isFile()) size += file0.length();
		}
		return size;
	}
}
