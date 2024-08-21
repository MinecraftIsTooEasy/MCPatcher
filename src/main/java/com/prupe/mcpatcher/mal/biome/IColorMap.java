package com.prupe.mcpatcher.mal.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.IBlockAccess;

import java.util.Collection;

import com.prupe.mcpatcher.mal.resource.FakeResourceLocation;
import net.minecraft.ResourceLocation;

@Environment(EnvType.CLIENT)
public interface IColorMap {
    boolean isHeightDependent();

    int getColorMultiplier();

    int getColorMultiplier(IBlockAccess blockAccess, int i, int j, int k);

    float[] getColorMultiplierF(IBlockAccess blockAccess, int i, int j, int k);

    void claimResources(Collection<ResourceLocation> resources);

    IColorMap copy();
}
