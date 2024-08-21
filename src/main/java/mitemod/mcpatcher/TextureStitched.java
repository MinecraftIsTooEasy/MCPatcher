//package mitemod.mcpatcher;
//
//import com.prupe.mcpatcher.hd.Wrapper15;
//import com.prupe.mcpatcher.hd.Wrapper15;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.*;
//
//import java.io.BufferedReader;
//import java.util.ArrayList;
//import java.util.List;
//
//@Deprecated
//public abstract class TextureStitched extends TextureAtlasSprite implements Icon {
//    private final String textureName;
//    protected Texture textureSheet;
//    protected List textureList;
//    private List listAnimationTuples;
//    protected boolean rotated;
//    protected int originX;
//    protected int originY;
//    private int width;
//    private int height;
//    private float minU;
//    private float maxU;
//    private float minV;
//    private float maxV;
//    private float widthNorm;
//    private float heightNorm;
//    protected int frameCounter = 0;
//    protected int tickCounter = 0;
//
//    public static TextureAtlasSprite makeTextureStitched(String par0Str) {
//        return (TextureAtlasSprite)("clock".equals(par0Str) ? new TextureClock("") : ("compass".equals(par0Str) ? new TextureCompass("") : Wrapper15.createSprite(par0Str)));
//    }
//
//    public TextureStitched(String par1) {
//        this.textureName = par1;
//    }
//
//    public void init(Texture par1Texture, List par2List, int par3, int par4, int par5, int par6, boolean par7) {
//        this.textureSheet = par1Texture;
//        this.textureList = par2List;
//        this.originX = par3;
//        this.originY = par4;
//        this.width = par5;
//        this.height = par6;
//        this.rotated = par7;
//        float var8 = 0.01F / (float)par1Texture.getWidth();
//        float var9 = 0.01F / (float)par1Texture.getHeight();
//        this.minU = (float)par3 / (float)par1Texture.getWidth() + var8;
//        this.maxU = (float)(par3 + par5) / (float)par1Texture.getWidth() - var8;
//        this.minV = (float)par4 / (float)par1Texture.getHeight() + var9;
//        this.maxV = (float)(par4 + par6) / (float)par1Texture.getHeight() - var9;
//        this.widthNorm = (float)par5 / 16.0F;
//        this.heightNorm = (float)par6 / 16.0F;
//    }
//
//    public void copyFrom(TextureStitched par1TextureStitched) {
//        this.init(par1TextureStitched.textureSheet, par1TextureStitched.textureList, par1TextureStitched.originX, par1TextureStitched.originY, par1TextureStitched.width, par1TextureStitched.height, par1TextureStitched.rotated);
//    }
//
//    public int getOriginX() {
//        return this.originX;
//    }
//
//    public int getOriginY() {
//        return this.originY;
//    }
//
//    public float getMinU() {
//        return this.minU;
//    }
//
//    public float getMaxU()
//    {
//        return this.maxU;
//    }
//
//    public float getInterpolatedU(double par1) {
//        float var3 = this.maxU - this.minU;
//        return this.minU + var3 * ((float)par1 / 16.0F);
//    }
//
//    public float getMinV() {
//        return this.minV;
//    }
//
//    public float getMaxV()
//    {
//        return this.maxV;
//    }
//
//    public float getInterpolatedV(double par1) {
//        float var3 = this.maxV - this.minV;
//        return this.minV + var3 * ((float)par1 / 16.0F);
//    }
//
//    public String getIconName() {
//        return this.textureName;
//    }
//
//    public int getSheetWidth() {
//        return this.textureSheet.getWidth();
//    }
//
//    /**
//     * Returns the height of the texture sheet this icon is on, in pixels.
//     */
//    public int getSheetHeight() {
//        return this.textureSheet.getHeight();
//    }
//
//    public void updateAnimation() {
//        if (this.listAnimationTuples != null) {
//            Tuple var1 = (Tuple)this.listAnimationTuples.get(this.frameCounter);
//            ++this.tickCounter;
//
//            if (this.tickCounter >= ((Integer)var1.getSecond()).intValue()) {
//                int var2 = ((Integer)var1.getFirst()).intValue();
//                this.frameCounter = (this.frameCounter + 1) % this.listAnimationTuples.size();
//                this.tickCounter = 0;
//                var1 = (Tuple)this.listAnimationTuples.get(this.frameCounter);
//                int var3 = ((Integer)var1.getFirst()).intValue();
//
//                if (var2 != var3 && var3 >= 0 && var3 < this.textureList.size()) {
//                    this.textureSheet.func_104062_b(this.originX, this.originY, (Texture)this.textureList.get(var3));
//                }
//            }
//        } else {
//            int var4 = this.frameCounter;
//            this.frameCounter = (this.frameCounter + 1) % this.textureList.size();
//
//            if (var4 != this.frameCounter) {
//                this.textureSheet.func_104062_b(this.originX, this.originY, (Texture) this.textureList.get(this.frameCounter));
//            }
//        }
//    }
//
//    public void readAnimationInfo(BufferedReader par1BufferedReader) {
//        ArrayList var2 = new ArrayList();
//        try {
//            for (String var3 = par1BufferedReader.readLine(); var3 != null; var3 = par1BufferedReader.readLine()) {
//                var3 = var3.trim();
//                if (var3.length() > 0) {
//                    String[] var4 = var3.split(",");
//                    String[] var5 = var4;
//                    int var6 = var4.length;
//                    for (int var7 = 0; var7 < var6; ++var7) {
//                        String var8 = var5[var7];
//                        int var9 = var8.indexOf(42);
//                        if (var9 > 0) {
//                            Integer var10 = Integer.valueOf(var8.substring(0, var9));
//                            Integer var11 = Integer.valueOf(var8.substring(var9 + 1));
//                            var2.add(new Tuple(var10, var11));
//                        } else {
//                            var2.add(new Tuple(Integer.valueOf(var8), Integer.valueOf(1)));
//                        }
//                    }
//                }
//            }
//        } catch (Exception var12) {
//            System.err.println("Failed to read animation info for " + this.textureName + ": " + var12.getMessage());
//        }
//
//        if (!var2.isEmpty() && var2.size() < 600) {
//            this.listAnimationTuples = var2;
//        }
//    }
//
//    public boolean isProcedurallyAnimated() {
//        return false;
//    }
//}
