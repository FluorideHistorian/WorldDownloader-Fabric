/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.ChunkMixinAccessor;
import me.historian.worlddownloader.mixin.WorldClientMixinAccessor;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.IInventory;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.TileEntityNote;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldProvider;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(WorldClient.class)
public class WorldClientMixin extends World implements WorldClientMixinAccessor {
	private WorldClientMixin(final ISaveHandler iSaveHandler, final String levelName, final WorldProvider worldProvider, final long randomSeed) {
		super(iSaveHandler, levelName, worldProvider, randomSeed);
	}
	
	@Inject(method = "setSpawnLocation", at = @At("HEAD"), cancellable = true)
	private void setSpawnLocation(final CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}
	
	@Override
	public void saveWorld(final boolean flag, final IProgressUpdate loadingScreen) {
		if(WorldDownloader.isDownloadingWorld()) {
			WorldDownloader.getSaveHandler().method_1733(worldInfo, playerEntities);
			chunkProvider.saveChunks(flag, loadingScreen);
		}
		super.saveWorld(flag, loadingScreen);
	}
	
	@Override
	public void playNoteAt(final int x, final int y, final int z, final int instrument, final int note) {
		super.playNoteAt(x, y, z, instrument, note);
		if(WorldDownloader.isDownloadingWorld() && getBlockId(x, y, z) == Block.musicBlock.blockID) {
			TileEntityNote tileEntityNote = (TileEntityNote)method_1777(x, y, z);
			if(tileEntityNote == null) setBlockTileEntity(x, y, z, tileEntityNote = new TileEntityNote());
			tileEntityNote.note = (byte)(note % 25);
			tileEntityNote.onInventoryChanged();
			setNewBlockTileEntity(x, y, z, tileEntityNote);
		}
	}
	
	@Override
	public void setNewBlockTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
		final Chunk chunk = method_214(x >> 4, z >> 4);
		if(chunk != null) ((ChunkMixinAccessor)chunk).setNewChunkBlockTileEntity(x & 15, y, z & 15, tileEntity);
	}
	
	@Override
	public void setNewChestTileEntitiy(final int x, final int y, final int z, final IInventory iInventory) {
		TileEntityChest tileEntityChest = new TileEntityChest();
		for(int i = 0; i < 27; i++) tileEntityChest.setInventorySlotContents(i, iInventory.method_954(i));
		if(iInventory.getSizeInventory() == 27) {
			setNewBlockTileEntity(x, y, z, tileEntityChest);
			return;
		}
		TileEntityChest tileEntityChest0 = new TileEntityChest();
		for(int i = 0; i < 27; i++) tileEntityChest0.setInventorySlotContents(i, iInventory.method_954(27 + i));
		if(getBlockId(x - 1, y, z) == Block.chest.blockID) {
			setNewBlockTileEntity(x - 1, y, z, tileEntityChest);
			setNewBlockTileEntity(x, y, z, tileEntityChest0);
		}
		if(getBlockId(x + 1, y, z) == Block.chest.blockID) {
			setNewBlockTileEntity(x, y, z, tileEntityChest);
			setNewBlockTileEntity(x + 1, y, z, tileEntityChest0);
		}
		if(getBlockId(x, y, z - 1) == Block.chest.blockID) {
			setNewBlockTileEntity(x, y, z - 1, tileEntityChest);
			setNewBlockTileEntity(x, y, z, tileEntityChest0);
		}
		if(getBlockId(x, y, z + 1) == Block.chest.blockID) {
			setNewBlockTileEntity(x, y, z, tileEntityChest);
			setNewBlockTileEntity(x, y, z + 1, tileEntityChest0);
		}
	}
}
