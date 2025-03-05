package jss.notfine.config;

import net.minecraftforge.common.Configuration;
import net.xiaoyu233.fml.FishModLoader;

import java.io.File;
import java.util.logging.Level;

public class MCPatcherForgeConfig {
    private static MCPatcherForgeConfig config = null;

    // CUSTOM_COLORS
    public boolean customColorsEnabled;
    public String customColorsLoggingLevel;
    public int yVariance;
    public int blockBlendRadius;
    public int fogBlendRadius;
    public boolean swampColors;
    public boolean ccWater;
    public boolean ccTree;
    public boolean ccRedstone;
    public boolean ccStem;
    public boolean ccOtherBlocks;
    public boolean smoothBiomes;
    public boolean testColorSmoothing;
    public boolean ccPotion;
    public boolean ccParticle;
    public boolean ccFog;
    public boolean ccClouds;
    public boolean ccMap;
    public boolean ccDye;
    public boolean ccText;
    public boolean ccXPOrb;
    public boolean ccEgg;
    public boolean ccLightmaps;

    // CUSTOM_ITEM_TEXTURES
    public boolean customItemTexturesEnabled;
    public String customItemTexturesLoggingLevel;
    public boolean citItems;
    public boolean citEnchantments;
    public boolean citArmor;

    // CONNECTED_TEXTURES
    public boolean connectedTexturesEnabled;
    public String connectedTexturesLoggingLevel;
    public int maxRecursion;
    public boolean debugTextures;
    public boolean betterGrass;
    public boolean ctmStandard;
    public boolean ctmNonStandard;
    public boolean ctmGlassPane;

    // EXTENDED_HD
    public boolean extendedHDEnabled;
    public String extendedHDLoggingLevel;
    public int maxMipMapLevel;
    public int anisotropicFiltering;
    public int lodBias;
    public boolean animations;
    public boolean fancyCompass;
    public boolean fancyClock;
    public boolean useGL13;
    public boolean useScratchTexture;
    public boolean hdFont;
    public boolean nonHDFontWidth;
    public boolean mipmap;

    // RANDOM_MOBS
    public boolean randomMobsEnabled;
    public String randomMobsLoggingLevel;
    public boolean leashLine;

    // BETTER_SKIES
    public boolean betterSkiesEnabled;
    public String betterSkiesLoggingLevel;
    public int horizon;
    public boolean brightenFireworks;
    public boolean skybox;
    public boolean unloadTextures;

    public enum Category {

        CUSTOM_COLORS,
        CUSTOM_ITEM_TEXTURES,
        CONNECTED_TEXTURES,
        EXTENDED_HD,
        RANDOM_MOBS,
        BETTER_SKIES;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static MCPatcherForgeConfig instance() {
        if (MCPatcherForgeConfig.config == null) {
            MCPatcherForgeConfig.config = new MCPatcherForgeConfig(new File(FishModLoader.CONFIG_DIR, "mcpatcherforge.cfg"));
        }
        return config;
    }

    public MCPatcherForgeConfig(File file) {
        Configuration config = new Configuration(file);

//         spotless:off

        customColorsEnabled = config.get(Category.CUSTOM_COLORS.toString(),"enabled",true,"Enable the custom colors module").getBoolean(true);
        customColorsLoggingLevel = config.get(Category.CUSTOM_COLORS.toString(),"logging", Level.INFO.getName(),"logging level").getString();
        yVariance = config.get(Category.CUSTOM_COLORS.toString(),"yVariance",0).getInt();
        blockBlendRadius = config.get(Category.CUSTOM_COLORS.toString(),"blockBlendRadius",4).getInt();
        fogBlendRadius = config.get(Category.CUSTOM_COLORS.toString(),"fogBlendRadius",7).getInt();
        swampColors = config.get(Category.CUSTOM_COLORS.toString(),"swampColors",true).getBoolean(true);
        ccWater = config.get(Category.CUSTOM_COLORS.toString(),"water",true).getBoolean(true);
        ccTree = config.get(Category.CUSTOM_COLORS.toString(),"tree",true).getBoolean(true);
        ccRedstone = config.get(Category.CUSTOM_COLORS.toString(),"redstone",true).getBoolean(true);
        ccStem = config.get(Category.CUSTOM_COLORS.toString(),"stem",true).getBoolean(true);
        ccOtherBlocks = config.get(Category.CUSTOM_COLORS.toString(),"otherBlocks",true).getBoolean(true);
        smoothBiomes = config.get(Category.CUSTOM_COLORS.toString(),"smoothBiomes",true).getBoolean(true);
        testColorSmoothing = config.get(Category.CUSTOM_COLORS.toString(),"testColorSmoothing",false).getBoolean(false);
        ccPotion = config.get(Category.CUSTOM_COLORS.toString(),"potion",true).getBoolean(true);
        ccParticle = config.get(Category.CUSTOM_COLORS.toString(),"particle",true).getBoolean(true);
        ccFog = config.get(Category.CUSTOM_COLORS.toString(),"fog",true).getBoolean(true);
        ccClouds = config.get(Category.CUSTOM_COLORS.toString(),"clouds",true).getBoolean(true);
        ccMap = config.get(Category.CUSTOM_COLORS.toString(),"map",true).getBoolean(true);
        ccDye = config.get(Category.CUSTOM_COLORS.toString(),"dye",true).getBoolean(true);
        ccText = config.get(Category.CUSTOM_COLORS.toString(),"text",true).getBoolean(true);
        ccXPOrb = config.get(Category.CUSTOM_COLORS.toString(),"xporb",true).getBoolean(true);
        ccEgg = config.get(Category.CUSTOM_COLORS.toString(),"egg",true).getBoolean(true);
        ccLightmaps = config.get(Category.CUSTOM_COLORS.toString(),"lightmaps",true).getBoolean(true);

        customItemTexturesEnabled = config.get(Category.CUSTOM_ITEM_TEXTURES.toString(),"enabled",true,"Enable the custom item textures module").getBoolean(true);
        customItemTexturesLoggingLevel = config.get(Category.CUSTOM_ITEM_TEXTURES.toString(),"logging",Level.INFO.getName(),"logging level").getString();
        citItems = config.get(Category.CUSTOM_ITEM_TEXTURES.toString(),"items",true).getBoolean(true);
        citEnchantments = config.get(Category.CUSTOM_ITEM_TEXTURES.toString(),"enchantments",true).getBoolean(true);
        citArmor = config.get(Category.CUSTOM_ITEM_TEXTURES.toString(),"armor",true).getBoolean(true);

        connectedTexturesEnabled = config.get(Category.CONNECTED_TEXTURES.toString(),"enabled",true,"Enable the connected textures module").getBoolean(true);
        connectedTexturesLoggingLevel = config.get(Category.CONNECTED_TEXTURES.toString(),"logging",Level.INFO.getName(),"logging level").getString();
        maxRecursion = config.get(Category.CONNECTED_TEXTURES.toString(),"maxRecursion",4).getInt();
        debugTextures = config.get(Category.CONNECTED_TEXTURES.toString(),"debugTextures",false).getBoolean(false);
        betterGrass = config.get(Category.CONNECTED_TEXTURES.toString(),"betterGrass",false).getBoolean(false);
        ctmStandard = config.get(Category.CONNECTED_TEXTURES.toString(),"standard",true).getBoolean(true);
        ctmNonStandard = config.get(Category.CONNECTED_TEXTURES.toString(),"nonStandard",true).getBoolean(true);
        ctmGlassPane = config.get(Category.CONNECTED_TEXTURES.toString(),"glassPane",false).getBoolean(false);

        extendedHDEnabled = config.get(Category.EXTENDED_HD.toString(),"enabled",true,"Enable the extended hd module").getBoolean(true);
        extendedHDLoggingLevel = config.get(Category.EXTENDED_HD.toString(),"logging",Level.INFO.getName(),"logging level").getString();
        maxMipMapLevel = config.get(Category.EXTENDED_HD.toString(),"maxMipMapLevel",3).getInt();
        anisotropicFiltering = config.get(Category.EXTENDED_HD.toString(),"anisotropicFiltering",1).getInt();
        lodBias = config.get(Category.EXTENDED_HD.toString(),"lod bias",0).getInt();

        animations = config.get(Category.EXTENDED_HD.toString(),"animations",true).getBoolean(true);
        fancyCompass = config.get(Category.EXTENDED_HD.toString(),"fancyCompass",true).getBoolean(true);
        fancyClock = config.get(Category.EXTENDED_HD.toString(),"fancyClock",true).getBoolean(true);
        useGL13 = config.get(Category.EXTENDED_HD.toString(),"useGL13",true).getBoolean(true);
        useScratchTexture = config.get(Category.EXTENDED_HD.toString(),"useScratchTexture",true).getBoolean(true);
        hdFont = config.get(Category.EXTENDED_HD.toString(),"HDFont",true).getBoolean(true);
        nonHDFontWidth = config.get(Category.EXTENDED_HD.toString(),"nonHDFontWidth",false).getBoolean(false);
        mipmap = config.get(Category.EXTENDED_HD.toString(),"mipmap",false).getBoolean(false);

        randomMobsEnabled = config.get(Category.RANDOM_MOBS.toString(),"enabled",true,"Enable the random mobs module").getBoolean(true);
        randomMobsLoggingLevel = config.get(Category.RANDOM_MOBS.toString(),"logging",Level.INFO.getName(),"logging level").getString();
        leashLine = config.get(Category.RANDOM_MOBS.toString(),"leashLine",true).getBoolean(true);

        betterSkiesEnabled = config.get(Category.BETTER_SKIES.toString(),"enabled",true,"Enable the better skies module").getBoolean(true);
        betterSkiesLoggingLevel = config.get(Category.BETTER_SKIES.toString(),"logging",Level.INFO.getName(),"logging level").getString();
        horizon = config.get(Category.BETTER_SKIES.toString(),"horizon",16).getInt();
        brightenFireworks = config.get(Category.BETTER_SKIES.toString(),"brightenFireworks",true).getBoolean(true);
        skybox = config.get(Category.BETTER_SKIES.toString(),"skybox",true).getBoolean(true);
        unloadTextures = config.get(Category.BETTER_SKIES.toString(),"unloadTextures",true).getBoolean(true);

        // spotless:on
        if (config.hasChanged()) config.save();
        }
}
