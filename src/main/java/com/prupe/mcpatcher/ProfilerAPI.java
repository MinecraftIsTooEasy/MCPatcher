package com.prupe.mcpatcher;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Minecraft;

@Environment(EnvType.CLIENT)
public class ProfilerAPI {
    private static final boolean enable = Config.getInstance().extraProfiling;

    public static void startSection(String name) {
        if (enable) {
            Minecraft.getMinecraft().mcProfiler.startSection(name);
        }
    }

    public static void endStartSection(String name) {
        if (enable) {
            Minecraft.getMinecraft().mcProfiler.endStartSection(name);
        }
    }

    public static void endSection() {
        if (enable) {
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }
}
