package com.prupe.mcpatcher.mal.resource;

import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.MCPatcherUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Minecraft;
import net.minecraft.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class TexturePackAPI {
    private static final MCLogger logger = MCLogger.getLogger(MCLogger.Category.TEXTURE_PACK);

    public static final String DEFAULT_NAMESPACE = "minecraft";

    private static final TexturePackAPI instance = new TexturePackAPI();

    public static final String MCPATCHER_SUBDIR = "";
    public static final ResourceLocation ITEMS_PNG = new ResourceLocation("textures/atlas/items.png");
    public static final ResourceLocation BLOCKS_PNG = new ResourceLocation("textures/atlas/blocks.png");

    private final List<Field> textureMapFields = new ArrayList<Field>();
    
    public static boolean isInitialized() {
        return instance != null && Minecraft.getMinecraft().getResourcePackRepository() != null;
    }

    public static void scheduleTexturePackRefresh() {
        Minecraft.getMinecraft().getResourcePackRepository().updateRepositoryEntriesAll();
    }

    public static List<ResourcePack> getResourcePacks(String namespace) {
        List<ResourcePack> resourcePacks = new ArrayList<ResourcePack>();

        ResourcePack resourcePack = instance.getTexturePack();
        if (resourcePack != null) {
            resourcePacks.add(resourcePack);
        }

        return resourcePacks;
    }

    public static Set<String> getNamespaces() {
        Set<String> set = new HashSet<String>();
        set.add(DEFAULT_NAMESPACE);
        return set;
    }

    public static boolean isDefaultTexturePack() {
        ResourcePack texturePack = instance.getTexturePack();
        return texturePack == null || texturePack instanceof TexturePackDefault;
    }

//    public static InputStream getInputStream(ResourceLocation resource) {
//        try {
//            if (resource instanceof ResourceLocationWithSource resourceLocationWithSource) {
//                try {
//                    return resourceLocationWithSource.getSource()
//                            .getInputStream(resource);
//                } catch (Exception ignore) {}
//            }
//            return resource == null ? null
//                    : Minecraft.getMinecraft()
//                    .getResourceManager()
//                    .getResource(resource)
//                    .getInputStream();
//        } catch (Exception ignore) {
//            return null;
//        }
//    }

    public static InputStream getInputStream(ResourceLocation resource) {
        if (resource == null) {
            return null;
        }

        ResourcePack resourcePack;

        if (resource instanceof ResourceLocationWithSource) {
            resourcePack = ((ResourceLocationWithSource) resource).getSource();
        } else {
            resourcePack = instance.getTexturePack();
        }

//        return null;
        return resourcePack == null ? null : resourcePack.getInputStream(resource);
    }

    public static boolean hasResource(ResourceLocation resource) {
        if (resource == null) {
            return false;
        } else if (resource.getResourcePath().startsWith("font/")) {
            return false;
        } else if (resource.getResourcePath().startsWith("misc/")) {
            return false;
        } else if (resource.getResourcePath().endsWith(".png")) {
            return getImage(resource) != null;
        } else if (resource.getResourcePath().endsWith(".properties")) {
            return getProperties(resource) != null;
        } else {
            InputStream is = getInputStream(resource);
            MCPatcherUtils.close(is);
            return is != null;
        }
    }

    public static boolean hasCustomResource(ResourceLocation resource) {
        InputStream jar = null;
        InputStream pack = null;
        try {
            String path = resource.getResourcePath();
            pack = getInputStream(resource);
            if (pack == null) {
                return false;
            }
            jar = Minecraft.class.getResourceAsStream(path);
            if (jar == null) {
                return true;
            }
            byte[] buffer1 = new byte[4096];
            byte[] buffer2 = new byte[4096];
            int read1;
            int read2;
            while ((read1 = pack.read(buffer1)) > 0) {
                read2 = jar.read(buffer2);
                if (read1 != read2) {
                    return true;
                }
                for (int i = 0; i < read1; i++) {
                    if (buffer1[i] != buffer2[i]) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MCPatcherUtils.close(jar);
            MCPatcherUtils.close(pack);
        }
        return false;
    }

    public static BufferedImage getImage(ResourceLocation resource) {
        if (resource == null) {
            return null;
        }
        InputStream input = getInputStream(resource);
        BufferedImage image = null;
        if (input != null) {
            try {
                image = ImageIO.read(input);
            } catch (IOException e) {
                logger.error("could not read %s", resource);
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(input);
            }
        }
        return image;
    }

    public static Properties getProperties(ResourceLocation resource) {
        Properties properties = new Properties();
        if (getProperties(resource, properties)) {
            return properties;
        } else {
            return null;
        }
    }

    public static boolean getProperties(ResourceLocation resource, Properties properties) {
        if (properties != null) {
            InputStream input = getInputStream(resource);
            try {
                if (input != null) {
                    properties.load(input);
                    return true;
                }
            } catch (IOException e) {
                logger.error("could not read %s", resource);
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(input);
            }
        }
        return false;
    }

    public static ResourceLocation transformResourceLocation(ResourceLocation resource, String oldExt, String newExt) {
        return new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath().replaceFirst(Pattern.quote(oldExt) + "$", newExt));
    }

    public static ResourceLocation parsePath(String path) {
        return MCPatcherUtils.isNullOrEmpty(path) ? null : new ResourceLocation(path.replace(File.separatorChar, '/'));
    }

    public static ResourceLocation parseResourceLocation(String path) {
        return parseResourceLocation(new ResourceLocation(DEFAULT_NAMESPACE, "a"), path);
    }

    public static ResourceLocation parseResourceLocation(ResourceLocation baseResource, String path) {
    	if (MCPatcherUtils.isNullOrEmpty(path)) {
    		return null;
    	}
        
        if (path.startsWith("~/")) {
            // Relative to namespace mcpatcher dir:
            // ~/path -> /path
            path = path.substring(1);
        }
        if (path.startsWith("./")) {
            // Relative to properties file:
            // ./path -> (dir of base file)/path
            return new ResourceLocation(baseResource.getResourceDomain(), baseResource.getResourcePath().replaceFirst("[^/]+$", "") + path.substring(2));
        } else if (path.startsWith("/")) {
            return new ResourceLocation(path);
        } else {
            return new ResourceLocation(baseResource.getResourceDomain(), baseResource.getResourcePath().replaceFirst("[^/]+$", "") + path);
        }
    }

    public static ResourceLocation newMCPatcherResourceLocation(String path) {
    	return new ResourceLocation(MCPATCHER_SUBDIR + path.replaceFirst("^/+", ""));
    }

    public static TextureObject getTextureIfLoaded(ResourceLocation resource) {
        if (resource == null) {
        	return null;
        }
        
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        String path = resource.getResourcePath();
        if (path.equals("/terrain.png") || path.equals("/gui/items.png")) {
            return renderEngine.getTexture(resource);
        }
        for (Field field : instance.textureMapFields) {
            try {
                HashMap map = (HashMap) field.get(renderEngine);
                if (map != null) {
                    Object value = map.get(resource.toString());
                    if (value instanceof TextureObject) {
                        return (TextureObject) value;
                    }
                }
            } catch (IllegalAccessException e) {
            }
        }
        return null;
    }

    public static boolean isTextureLoaded(ResourceLocation resource) {
        return getTextureIfLoaded(resource) != null;
    }

    public static void bindTexture(ResourceLocation resource) {
        if (resource != null) {
        	Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        }
    }

    public static void unloadTexture(ResourceLocation resource) {
        if (resource != null) {
            TextureManager textureManager = Minecraft.getMinecraft()
                    .getTextureManager();
            TextureObject texture = textureManager.getTexture(resource);
            if (texture != null && !(texture instanceof TextureMap) && !(texture instanceof DynamicTexture)) {
                if (texture instanceof AbstractTexture) {
                    ((jss.notfine.util.AbstractTextureExpansion) texture).unloadGLTexture();
                }
                logger.finer("unloading texture %s", resource);
                textureManager.mapTextureObjects.remove(resource);
            }
        }
    }

    public static void flushUnusedTextures() {
    	// switching packs is so hopelessly broken in 1.5 that there's no point
    }
    
    private TexturePackAPI() {
        try {
            for (Field field : TextureManager.class.getDeclaredFields()) {
                if (HashMap.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    textureMapFields.add(field);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private ResourcePack getTexturePack() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return null;
        }
        ResourcePackRepository texturePackList = minecraft.getResourcePackRepository();
        if (texturePackList == null) {
            return null;
        }
        ResourcePackRepositoryEntry currentResourcePack = getCurrentResourcePack(texturePackList);
        return currentResourcePack == null ? texturePackList.rprDefaultResourcePack : currentResourcePack.getResourcePack();
    }

    private ResourcePackRepositoryEntry getCurrentResourcePack(ResourcePackRepository texturePackList)
    {
        return texturePackList.getRepositoryEntries().isEmpty() ?
                null : ((ResourcePackRepositoryEntry)texturePackList.getRepositoryEntries().get(0));
    }

    private static class TexturePackDefault {
    }
}
// ---END EDIT---
