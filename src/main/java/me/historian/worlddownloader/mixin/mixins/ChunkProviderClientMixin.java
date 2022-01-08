/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import java.io.IOException;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.ChunkMixinAccessor;
import me.historian.worlddownloader.mixin.ChunkProviderClientMixinAccessor;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.ChunkProviderClient;
import net.minecraft.src.IInventory;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityNote;
import net.minecraft.src.World;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(ChunkProviderClient.class)
public class ChunkProviderClientMixin implements ChunkProviderClientMixinAccessor {
	@Shadow
	private Map<ChunkCoordIntPair, Chunk> chunkMapping;
	@Shadow
	private World worldObj;
	
	@Inject(method = "method_1954", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void method_1954(final int x, final int z, final CallbackInfo callbackInfo, final Chunk chunk) {
		if(WorldDownloader.isDownloadingWorld() && !chunk.neverSave && ((ChunkMixinAccessor)chunk).isFilled()) {
			try {
				saveChunk(chunk);
				WorldDownloader.getChunkLoader().method_814(worldObj, chunk);
			} catch(final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Inject(method = "method_1807", at = @At("TAIL"))
	private void method_1807(final int x, final int z, final CallbackInfoReturnable<Chunk> callbackInfoReturnable) {
		if(WorldDownloader.isDownloadingWorld()) {
			try {
				((ChunkMixinAccessor)callbackInfoReturnable.getReturnValue()).importOldChunkTileEntities();
			} catch(final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Inject(method = "saveChunks", at = @At("TAIL"))
	private void saveChunks(final boolean flag, final IProgressUpdate loadingScreen, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(WorldDownloader.isDownloadingWorld()) {
			try {
				for(final Chunk chunk : chunkMapping.values()) {
					if(chunk != null && !chunk.neverSave && ((ChunkMixinAccessor)chunk).isFilled()) {
						if(flag) WorldDownloader.getChunkLoader().method_814(worldObj, chunk);
						saveChunk(chunk);
					}
				}
				if(flag) WorldDownloader.getChunkLoader().saveExtraData();
			} catch(final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Unique
	private void saveChunk(final Chunk chunk) throws IOException {
		if(WorldDownloader.isDownloadingWorld()) {
			chunk.lastSaveTime = worldObj.getWorldTime();
			chunk.isTerrainPopulated = true;
			for(Map.Entry<ChunkPosition, TileEntity> entry : ((ChunkMixinAccessor)chunk).getNewChunkTileEntityMap().entrySet()) {
				final TileEntity tileEntity = entry.getValue();
				if(tileEntity instanceof IInventory || tileEntity instanceof TileEntityNote) chunk.chunkTileEntityMap.put(entry.getKey(), tileEntity);
			}
			WorldDownloader.getChunkLoader().method_812(worldObj, chunk);
		}
	}
	
	@Unique
	private Chunk loadChunk(final int x, final int z) throws IOException {
		return WorldDownloader.getChunkLoader().method_811(worldObj, x, z);
	}
	
	@Override
	public void importOldTileEntities() {
		for(final Chunk chunk : chunkMapping.values()) {
			if(chunk != null && ((ChunkMixinAccessor)chunk).isFilled()) {
				try {
					((ChunkMixinAccessor)chunk).importOldChunkTileEntities();
				} catch(final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
