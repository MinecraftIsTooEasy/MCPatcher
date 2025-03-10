package com.prupe.mcpatcher.hd;

import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.MCPatcherUtils;
import com.prupe.mcpatcher.mal.resource.GLAPI;
import com.prupe.mcpatcher.mal.resource.PropertiesFile;
import com.prupe.mcpatcher.mal.resource.TexturePackAPI;

import jss.notfine.config.MCPatcherForgeConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MipmapHelper {
    private static final MCLogger logger = MCLogger.getLogger(MCLogger.Category.EXTENDED_HD);

    private static final ResourceLocation MIPMAP_PROPERTIES = TexturePackAPI.newMCPatcherResourceLocation("mipmap.properties");

    static final int TEX_FORMAT = GL12.GL_BGRA;
    static final int TEX_DATA_TYPE = GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

    private static final int MIN_ALPHA = 0x1a;
    private static final int MAX_ALPHA = 0xe5;

    private static final boolean mipmapSupported;
    static final boolean mipmapEnabled = MCPatcherForgeConfig.instance().mipmap;
    static final int maxMipmapLevel = MCPatcherForgeConfig.instance().maxMipMapLevel;
    private static final boolean useMipmap;
//    private static final int mipmapAlignment = (1 << Config.getInt(MCPatcherUtils.EXTENDED_HD, "mipmapAlignment", 3)) - 1;

    private static final boolean anisoSupported;
    static final int anisoLevel;
    private static final int anisoMax;

    private static final boolean lodSupported;
    private static final int lodBias;

    private static final Map<String, Reference<BufferedImage>> imagePool = new HashMap<String, Reference<BufferedImage>>();
    private static final Map<Integer, Reference<ByteBuffer>> bufferPool = new HashMap<Integer, Reference<ByteBuffer>>();

    private static final Map<String, Boolean> mipmapType = new HashMap<String, Boolean>();

    static {
        mipmapSupported = GLContext.getCapabilities().OpenGL12;
        useMipmap = mipmapSupported && mipmapEnabled && maxMipmapLevel > 0;

        anisoSupported = GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;
        if (anisoSupported) {
            anisoMax = (int) GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
            checkGLError("glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT)");
            anisoLevel = Math.max(Math.min(MCPatcherForgeConfig.instance().anisotropicFiltering, anisoMax), 1);
        } else {
            anisoMax = anisoLevel = 1;
        }

        lodSupported = GLContext.getCapabilities().GL_EXT_texture_lod_bias;
        if (lodSupported) {
            lodBias = MCPatcherForgeConfig.instance().lodBias;
        } else {
            lodBias = 0;
        }

        logger.config("mipmap: supported=%s, enabled=%s, level=%d", mipmapSupported, mipmapEnabled, maxMipmapLevel);
        logger.config("anisotropic: supported=%s, level=%d, max=%d", anisoSupported, anisoLevel, anisoMax);
        logger.config("lod bias: supported=%s, bias=%d", lodSupported, lodBias);
    }

    static void setupTexture(int width, int height, boolean blur, boolean clamp, String textureName) {
        int mipmaps = useMipmapsForTexture(textureName) ? getMipmapLevels(width, height, 1) : 0;
        logger.finer("setupTexture(%s) %dx%d %d mipmaps", textureName, width, height, mipmaps);
        int magFilter = blur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
        int minFilter = mipmaps > 0 ? GL11.GL_NEAREST_MIPMAP_LINEAR : magFilter;
        int wrap = clamp ? GL11.GL_CLAMP : GL11.GL_REPEAT;
        if (mipmaps > 0) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);
            checkGLError("%s: set GL_TEXTURE_MAX_LEVEL = %d", textureName, mipmaps);
            if (anisoSupported && anisoLevel > 1) {
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisoLevel);
                checkGLError("%s: set GL_TEXTURE_MAX_ANISOTROPY_EXT = %f", textureName, anisoLevel);
            }
            if (lodSupported) {
                GL11.glTexEnvi(EXTTextureLODBias.GL_TEXTURE_FILTER_CONTROL_EXT, EXTTextureLODBias.GL_TEXTURE_LOD_BIAS_EXT, lodBias);
                checkGLError("%s: set GL_TEXTURE_LOD_BIAS_EXT = %d", textureName, lodBias);
            }
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
        for (int level = 0; level <= mipmaps; level++) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, width, height, 0, TEX_FORMAT, TEX_DATA_TYPE, (IntBuffer) null);
            checkGLError("%s: glTexImage2D %dx%d level %d", textureName, width, height, level);
            width >>= 1;
            height >>= 1;
        }
    }

    public static void setupTexture(int[] rgb, int width, int height, int x, int y, boolean blur, boolean clamp, String textureName) {
        setupTexture(width, height, blur, clamp, textureName);
        copySubTexture(rgb, width, height, x, y, textureName);
    }

    public static int setupTexture(int glTexture, BufferedImage image, boolean blur, boolean clamp, ResourceLocation textureName) {
        int width = image.getWidth();
        int height = image.getHeight();
        GLAPI.glBindTexture(glTexture);
        logger.finer("setupTexture(%s, %d, %dx%d, %s, %s)", textureName, glTexture, width, height, blur, clamp);
        int[] rgb = new int[width * height];
        image.getRGB(0, 0, width, height, rgb, 0, width);
        setupTexture(rgb, width, height, 0, 0, blur, clamp, textureName.getResourcePath());
        return glTexture;
    }

    public static void setupTexture(ByteBuffer rgb, int width, int height) {
        IntBuffer buffer = rgb.asIntBuffer();
        int mipmaps = getMipmapLevelsForCurrentTexture();
        for (int level = 0; ; level++) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, width, height, 0, TEX_FORMAT, TEX_DATA_TYPE, buffer);
            if (level >= mipmaps) {
                break;
            }
            IntBuffer newBuffer = getPooledBuffer(width * height).asIntBuffer();
            scaleHalf(buffer, width, height, newBuffer, 0);
            buffer = newBuffer;
            width >>= 1;
            height >>= 1;
        }
    }

    public static void setupTexture(int glTexture, int width, int height, String textureName) {
        GLAPI.glBindTexture(glTexture);
        logger.finer("setupTexture(tilesheet %s, %d, %dx%d)", textureName, glTexture, width, height);
        setupTexture(width, height, false, false, textureName);
    }

    public static void copySubTexture(int[] rgb, int width, int height, int x, int y, String textureName) {
        if (rgb == null) {
            logger.error("copySubTexture %s %d,%d %dx%d: rgb data is null", textureName, x, y, width, height);
            return;
        }
        IntBuffer buffer = getPooledBuffer(width * height * 4).asIntBuffer();
        buffer.put(rgb).position(0);
        int mipmaps = getMipmapLevelsForCurrentTexture();
        IntBuffer newBuffer;
        logger.finest("copySubTexture %s %d,%d %dx%d %d mipmaps", textureName, x, y, width, height, mipmaps);
        for (int level = 0; ; ) {
            if (width <= 0 || height <= 0) {
                break;
            }
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, x, y, width, height, TEX_FORMAT, TEX_DATA_TYPE, buffer);
            checkGLError("%s: glTexSubImage2D(%d, %d, %d, %d, %d)", textureName, level, x, y, width, height);
            if (level >= mipmaps) {
                break;
            }
            newBuffer = getPooledBuffer(width * height).asIntBuffer();
            scaleHalf(buffer, width, height, newBuffer, 0);
            buffer = newBuffer;
            level++;
            x >>= 1;
            y >>= 1;
            width >>= 1;
            height >>= 1;
        }
    }

    private static IntBuffer[] generateMipmaps(int[] rgb, int width, int height, int mipmaps) {
        if (rgb == null) {
            return null;
        }
        ArrayList<IntBuffer> mipmapData = new ArrayList<IntBuffer>();
        IntBuffer buffer = newIntBuffer(width * height * 4);
        buffer.put(rgb).position(0);
        IntBuffer newBuffer;
        for (int level = 0; ; ) {
            mipmapData.add(buffer);
            if (width <= 0 || height <= 0 || level >= mipmaps) {
                break;
            }
            newBuffer = newIntBuffer(width * height);
            scaleHalf(buffer, width, height, newBuffer, 0);
            buffer = newBuffer;
            level++;
            width >>= 1;
            height >>= 1;
        }
        return mipmapData.toArray(new IntBuffer[0]);
    }

    static BufferedImage fixTransparency(ResourceLocation name, BufferedImage image) {
        if (image == null) {
            return image;
        }
        long s1 = System.currentTimeMillis();
        image = convertToARGB(image);
        int width = image.getWidth();
        int height = image.getHeight();
        IntBuffer buffer = getImageAsARGBIntBuffer(image);
        IntBuffer scaledBuffer = buffer;
        outer:
        while (width % 2 == 0 && height % 2 == 0) {
            for (int i = 0; i < scaledBuffer.limit(); i++) {
                if (scaledBuffer.get(i) >>> 24 == 0) {
                    IntBuffer newBuffer = getPooledBuffer(width * height).asIntBuffer();
                    scaleHalf(scaledBuffer, width, height, newBuffer, 8);
                    scaledBuffer = newBuffer;
                    width >>= 1;
                    height >>= 1;
                    continue outer;
                }
            }
            break;
        }
        long s2 = System.currentTimeMillis();
        if (scaledBuffer != buffer) {
            setBackgroundColor(buffer, image.getWidth(), image.getHeight(), scaledBuffer, image.getWidth() / width);
        }
        long s3 = System.currentTimeMillis();
        logger.finest("bg fix (tile %s): scaling %dms, setbg %dms", name, s2 - s1, s3 - s2);
        return image;
    }

    static void reset() {
        mipmapType.clear();
        mipmapType.put("terrain", true);
        mipmapType.put("items", false);
        PropertiesFile properties = PropertiesFile.get(logger, MIPMAP_PROPERTIES);
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey().trim();
                boolean value = Boolean.parseBoolean(entry.getValue().trim().toLowerCase());
                if (key.endsWith(".png")) {
                    mipmapType.put(key, value);
                }
            }
        }
    }

    static boolean useMipmapsForTexture(String texture) {
        if (!useMipmap || texture == null) {
            return false;
        } else if (mipmapType.containsKey(texture)) {
            return mipmapType.get(texture);
        } else if (texture.contains("item") ||
            texture.startsWith("textures/colormap/") ||
            texture.startsWith("textures/environment/") ||
            texture.startsWith("textures/font/") ||
            texture.startsWith("textures/gui/") ||
            texture.startsWith("textures/map/") ||
            texture.startsWith("textures/misc/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "colormap/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "cit/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "dial/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "font/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "lightmap/") ||
            texture.startsWith(TexturePackAPI.MCPATCHER_SUBDIR + "sky/") ||
            // 1.5 stuff
            texture.startsWith("%") ||
            texture.startsWith("##") ||
            texture.startsWith("/achievement/") ||
            texture.startsWith("/environment/") ||
            texture.startsWith("/font/") ||
            texture.startsWith("/gui/") ||
            texture.startsWith("/misc/") ||
            texture.startsWith("/terrain/") ||
            texture.startsWith("/title/")) {
            return false;
        } else {
            return true;
        }
    }

    static int getMipmapLevelsForCurrentTexture() {
        int filter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        if (filter != GL11.GL_NEAREST_MIPMAP_LINEAR && filter != GL11.GL_NEAREST_MIPMAP_NEAREST) {
            return 0;
        }
        return Math.min(maxMipmapLevel, GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL));
    }

    private static int gcd(int a, int b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    private static int getMipmapLevels(int width, int height, int minSize) {
        int size = gcd(width, height);
        int mipmap;
        for (mipmap = 0; size >= minSize && ((size & 1) == 0) && mipmap < maxMipmapLevel; size >>= 1, mipmap++) {
        }
        return mipmap;
    }

    private static BufferedImage getPooledImage(int width, int height, int index) {
        String key = String.format("%dx%d#%d", width, height, index);
        Reference<BufferedImage> ref = imagePool.get(key);
        BufferedImage image = (ref == null ? null : ref.get());
        if (image == null) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            imagePool.put(key, new SoftReference<BufferedImage>(image));
        }
        return image;
    }

    static IntBuffer newIntBuffer(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.asIntBuffer();
    }

    private static ByteBuffer getPooledBuffer(int size) {
        Reference<ByteBuffer> ref = bufferPool.get(size);
        ByteBuffer buffer = (ref == null ? null : ref.get());
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(size);
            bufferPool.put(size, new SoftReference<ByteBuffer>(buffer));
        }
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        return buffer;
    }

    private static BufferedImage convertToARGB(BufferedImage image) {
        if (image == null) {
            return null;
        } else if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
            return image;
        } else {
            int width = image.getWidth();
            int height = image.getHeight();
            logger.finest("converting %dx%d image to ARGB", width, height);
            BufferedImage newImage = getPooledImage(width, height, 0);
            Graphics2D graphics = newImage.createGraphics();
            Arrays.fill(getImageAsARGBIntBuffer(newImage).array(), 0);
            graphics.drawImage(image, 0, 0, null);
            return newImage;
        }
    }

    private static IntBuffer getImageAsARGBIntBuffer(BufferedImage image) {
        DataBuffer buffer = image.getRaster().getDataBuffer();
        if (buffer instanceof DataBufferInt) {
            return IntBuffer.wrap(((DataBufferInt) buffer).getData());
        } else if (buffer instanceof DataBufferByte) {
            return ByteBuffer.wrap(((DataBufferByte) buffer).getData()).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        } else {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            return IntBuffer.wrap(pixels);
        }
    }

    private static void setBackgroundColor(IntBuffer buffer, int width, int height, IntBuffer scaledBuffer, int scale) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int k = width * j + i;
                int pixel = buffer.get(k);
                if ((pixel & 0xff000000) == 0) {
                    pixel = scaledBuffer.get((j / scale) * (width / scale) + i / scale);
                    buffer.put(k, pixel & 0x00ffffff);
                }
            }
        }
    }

    static void scaleHalf(IntBuffer in, int w, int h, IntBuffer out, int rotate) {
        for (int i = 0; i < w / 2; i++) {
            for (int j = 0; j < h / 2; j++) {
                int k = w * 2 * j + 2 * i;
                int pixel00 = in.get(k);
                int pixel01 = in.get(k + 1);
                int pixel10 = in.get(k + w);
                int pixel11 = in.get(k + w + 1);
                if (rotate != 0) {
                    pixel00 = Integer.rotateLeft(pixel00, rotate);
                    pixel01 = Integer.rotateLeft(pixel01, rotate);
                    pixel10 = Integer.rotateLeft(pixel10, rotate);
                    pixel11 = Integer.rotateLeft(pixel11, rotate);
                }
                int pixel = average4RGBA(pixel00, pixel01, pixel10, pixel11);
                if (rotate != 0) {
                    pixel = Integer.rotateRight(pixel, rotate);
                }
                out.put(w / 2 * j + i, pixel);
            }
        }
    }

    private static int average4RGBA(int pixel00, int pixel01, int pixel10, int pixel11) {
        int a00 = pixel00 & 0xff;
        int a01 = pixel01 & 0xff;
        int a10 = pixel10 & 0xff;
        int a11 = pixel11 & 0xff;
        switch ((a00 << 24) | (a01 << 16) | (a10 << 8) | a11) {
            case 0xff000000:
                return pixel00;

            case 0x00ff0000:
                return pixel01;

            case 0x0000ff00:
                return pixel10;

            case 0x000000ff:
                return pixel11;

            case 0xffff0000:
                return average2RGBA(pixel00, pixel01);

            case 0xff00ff00:
                return average2RGBA(pixel00, pixel10);

            case 0xff0000ff:
                return average2RGBA(pixel00, pixel11);

            case 0x00ffff00:
                return average2RGBA(pixel01, pixel10);

            case 0x00ff00ff:
                return average2RGBA(pixel01, pixel11);

            case 0x0000ffff:
                return average2RGBA(pixel10, pixel11);

            case 0x00000000:
            case 0xffffffff:
                return average2RGBA(average2RGBA(pixel00, pixel11), average2RGBA(pixel01, pixel10));

            default:
                int a = a00 + a01 + a10 + a11;
                int pixel = a >> 2;
                for (int i = 8; i < 32; i += 8) {
                    int average = (a00 * ((pixel00 >> i) & 0xff) + a01 * ((pixel01 >> i) & 0xff) +
                        a10 * ((pixel10 >> i) & 0xff) + a11 * ((pixel11 >> i) & 0xff)) / a;
                    pixel |= (average << i);
                }
                return pixel;
        }
    }

    private static int average2RGBA(int a, int b) {
        return (((a & 0xfefefefe) >>> 1) + ((b & 0xfefefefe) >>> 1)) | (a & b & 0x01010101);
    }

    private static void checkGLError(String format, Object... params) {
        int error = GL11.glGetError();
        if (error != 0) {
            String message = GLU.gluErrorString(error) + ": " + String.format(format, params);
            new RuntimeException(message).printStackTrace();
        }
    }
}
// ---END EDIT---
