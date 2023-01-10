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
import net.fabricmc.loader.FabricLoaderImpl;
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
	public static final Minecraft mc = (Minecraft)FabricLoaderImpl.getInstance().getGameInstance();
	private static boolean downloadingWorld, allowMerging, saveContainers = true;
	private static Packet15Place openContainerPacket;
	private static IChunkLoader chunkLoader;
	private static ISaveHandler saveHandler;
	private static WorldClient worldClient;
	private static String lastWorldName;
	
	@Override
	public void onInitializeClient() {
		System.out.println("Starting");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Stopping")));
	}

	public static void startWorldDownload() {
		worldClient = ((NetClientHandlerAccessor)mc.method_2145()).getWorldClient();

		// Setup the world name
		lastWorldName = mc.gameSettings.lastServer;
		if(lastWorldName.isEmpty()) lastWorldName = "Downloaded World";
		if(!WorldDL.getAllowMerging()) lastWorldName += " " + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now());
		((WorldAccessor)worldClient).getWorldInfo().setWorldName(lastWorldName);
		WorldDL.setSaveHandler(mc.method_2127().method_1009(lastWorldName, false));

		WorldDL.setChunkLoader(WorldDL.getSaveHandler().method_1734(worldClient.worldProvider));
		((WorldAccessor)worldClient).getWorldInfo().setSizeOnDisk(getFileSizeRecursive(((PlayerNBTManagerAccessor)WorldDL.getSaveHandler()).callGetWorldDir()));
		((ChunkProviderClientMixinAccessor)((WorldAccessor)worldClient).getChunkProvider()).importOldTileEntities();
		WorldDL.setDownloadingWorld(true);
	}

	public static void postChangeWorlds() {
		// When we change worlds it creates a new world client instance, so we have to set up a save handler for that
		worldClient = ((NetClientHandlerAccessor)mc.method_2145()).getWorldClient();

		((WorldAccessor)worldClient).getWorldInfo().setWorldName(lastWorldName);
		WorldDL.setSaveHandler(mc.method_2127().method_1009(lastWorldName, false));

		WorldDL.setChunkLoader(WorldDL.getSaveHandler().method_1734(worldClient.worldProvider));
		((WorldAccessor)worldClient).getWorldInfo().setSizeOnDisk(getFileSizeRecursive(((PlayerNBTManagerAccessor)WorldDL.getSaveHandler()).callGetWorldDir()));
		((ChunkProviderClientMixinAccessor)((WorldAccessor)worldClient).getChunkProvider()).importOldTileEntities();
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
		if(file == null) return 0L;
		long size = 0;
		File[] fileList = file.listFiles();
		if(fileList == null) return 0L;
		for(final File file0 : fileList) {
			if(file0.isDirectory()) size += getFileSizeRecursive(file0);
			else if(file0.isFile()) size += file0.length();
		}
		return size;
	}
}
