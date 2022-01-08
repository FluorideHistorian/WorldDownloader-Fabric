/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader.mixin.mixins;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.historian.worlddownloader.WorldDownloader;
import me.historian.worlddownloader.mixin.ChunkMixinAccessor;
import me.historian.worlddownloader.mixin.mixins.accessor.PlayerNBTManagerAccessor;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.RegionFileCache;
import net.minecraft.src.TileEntity;
import net.minecraft.src.WorldProviderHell;

/**
 * @author historian
 * @since 1/8/2022
 */
@Mixin(Chunk.class)
public class ChunkMixin implements ChunkMixinAccessor {
	@Unique
	private Map<ChunkPosition, TileEntity> newChunkTileEntityMap = new HashMap<>();
	@Unique
	private boolean isFilled;
	
	@Inject(method = "setChunkData", at = @At("TAIL"))
	private void setChunkData(final byte[] bs, final int i, final int j, final int k, final int i1, final int i2, final int i3, final int i4, final CallbackInfoReturnable<Integer> callbackInfoReturnable) {
		isFilled = true;
	}
	
	@Override
	public void importOldChunkTileEntities() throws IOException {
		File file = ((PlayerNBTManagerAccessor)WorldDownloader.getSaveHandler()).callGetWorldDir();
		if(((Chunk)((Object)this)).worldObj.worldProvider instanceof WorldProviderHell) {
			file = new File(file, "DIM-1");
			file.mkdirs();
		}
		final DataInputStream dataInputStream = RegionFileCache.getChunkInputStream(file, ((Chunk)((Object)this)).xPosition, ((Chunk)((Object)this)).zPosition);
		if(dataInputStream != null) {
			final NBTTagCompound nbtTagCompound = CompressedStreamTools.method_337(dataInputStream);
			if(nbtTagCompound.hasKey("Level")) {
				final NBTTagList nbtTagList = nbtTagCompound.method_1033("Level").method_1034("TileEntities");
				if(nbtTagList != null) {
					for(int i = nbtTagList.tagCount(); --i >= 0;) {
						final TileEntity tileEntity = TileEntity.method_1068((NBTTagCompound)nbtTagList.method_1396(i));
						if(tileEntity != null) newChunkTileEntityMap.put(new ChunkPosition(tileEntity.xCoord & 15, tileEntity.yCoord, tileEntity.zCoord & 15), tileEntity);
					}
				}
			}
		}
	}
	
	@Override
	public void setNewChunkBlockTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
		tileEntity.worldObj = ((Chunk)((Object)this)).worldObj;
		tileEntity.xCoord = ((Chunk)((Object)this)).xPosition * 16 + x;
		tileEntity.yCoord = y;
		tileEntity.zCoord = ((Chunk)((Object)this)).zPosition * 16 + z;
		newChunkTileEntityMap.put(new ChunkPosition(x, y, z), tileEntity);
	}
	
	@Override
	public boolean isFilled() {
		return isFilled;
	}
	
	@Override
	public Map<ChunkPosition, TileEntity> getNewChunkTileEntityMap() {
		return newChunkTileEntityMap;
	}
}
