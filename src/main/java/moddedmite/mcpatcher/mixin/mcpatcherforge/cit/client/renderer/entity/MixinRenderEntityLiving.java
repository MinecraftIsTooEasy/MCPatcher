package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer.entity;

import net.minecraft.*;

import net.xiaoyu233.fml.FishModLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRenderEntityLiving extends Render {

    @Final
    @Shadow
    private static ResourceLocation RES_ITEM_GLINT;
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected ModelBase renderPassModel;

    @Shadow
    protected abstract float interpolateRotation(float angle1, float angle2, float p_77034_3_);

    @Shadow
    protected abstract void renderModel(EntityLivingBase entityLivingBase, float p_77036_2_, float p_77036_3_,
        float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_);

    @Shadow
    protected abstract void renderLivingAt(EntityLivingBase entityLivingBase, double p_77039_2_, double p_77039_4_,
        double p_77039_6_);

    @Shadow
    protected abstract void rotateCorpse(EntityLivingBase entityLivingBase, float p_77043_2_, float p_77043_3_,
        float p_77043_4_);

    @Shadow
    protected abstract float renderSwingProgress(EntityLivingBase entityLivingBase, float p_77040_2_);

    @Shadow
    protected abstract float handleRotationFloat(EntityLivingBase entityLivingBase, float p_77044_2_);

    @Shadow
    protected abstract void renderEquippedItems(EntityLivingBase entityLivingBase, float p_77029_2_);

    @Shadow
    protected abstract int inheritRenderPass(EntityLivingBase entityLivingBase, int p_77035_2_, float p_77035_3_);

    @Shadow
    protected abstract int shouldRenderPass(EntityLivingBase entityLivingBase, int p_77032_2_, float p_77032_3_);

    @Shadow
    protected abstract void func_82408_c(EntityLivingBase entityLivingBase, int p_82408_2_, float p_82408_3_);

    @Shadow
    protected abstract int getColorMultiplier(EntityLivingBase entityLivingBase, float p_77030_2_, float p_77030_3_);

    @Shadow
    protected abstract void preRenderCallback(EntityLivingBase entityLivingBase, float p_77041_2_);

    @Shadow
    protected abstract void passSpecialRender(EntityLivingBase entityLivingBase, double p_77033_2_, double p_77033_4_,
        double p_77033_6_);

    /**
     * @author Mist475 (adapted from Paul Rupe)
     * @reason if statement modified into else-if
     */
    @SuppressWarnings("DuplicatedCode")
    @Overwrite
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.mainModel.onGround = this.renderSwingProgress((EntityLivingBase) par1Entity, par9);

        if (this.renderPassModel != null) {
            this.renderPassModel.onGround = this.mainModel.onGround;
        }

        this.mainModel.isRiding = par1Entity.isRiding();

        if (this.renderPassModel != null) {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }

        this.mainModel.isChild = ((EntityLivingBase) par1Entity).isChild();

        if (this.renderPassModel != null) {
            this.renderPassModel.isChild = this.mainModel.isChild;
        }

        try {
            float f2 = this.interpolateRotation(((EntityLivingBase) par1Entity).prevRenderYawOffset, ((EntityLivingBase) par1Entity).renderYawOffset, par9);
            float f3 = this.interpolateRotation(((EntityLivingBase) par1Entity).prevRotationYawHead, ((EntityLivingBase) par1Entity).rotationYawHead, par9);
            float f4;

            if (par1Entity.isRiding() && par1Entity.ridingEntity instanceof EntityLivingBase entitylivingbase1) {
                f2 = this.interpolateRotation(
                    entitylivingbase1.prevRenderYawOffset,
                    entitylivingbase1.renderYawOffset,
                    par9);
                f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

                if (f4 < -85.0F) {
                    f4 = -85.0F;
                }

                if (f4 >= 85.0F) {
                    f4 = 85.0F;
                }

                f2 = f3 - f4;

                if (f4 * f4 > 2500.0F) {
                    f2 += f4 * 0.2F;
                }
            }

            float f13 = par1Entity.prevRotationPitch + (par1Entity.rotationPitch - par1Entity.prevRotationPitch) * par9;
            this.renderLivingAt((EntityLivingBase) par1Entity, par2, par4, par6);
            f4 = this.handleRotationFloat((EntityLivingBase) par1Entity, par9);
            this.rotateCorpse((EntityLivingBase) par1Entity, f4, f2, par9);
            float f5 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback((EntityLivingBase) par1Entity, par9);
            GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);
            float f6 = ((EntityLivingBase) par1Entity).prevLimbSwingAmount + (((EntityLivingBase) par1Entity).limbSwingAmount - ((EntityLivingBase) par1Entity).prevLimbSwingAmount) * par9;
            float f7 = ((EntityLivingBase) par1Entity).limbSwing - ((EntityLivingBase) par1Entity).limbSwingAmount * (1.0F - par9);

            if (((EntityLivingBase) par1Entity).isChild()) {
                f7 *= 3.0F;
            }

            if (f6 > 1.0F) {
                f6 = 1.0F;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.mainModel.setLivingAnimations((EntityLivingBase) par1Entity, f7, f6, par9);
            this.renderModel((EntityLivingBase) par1Entity, f7, f6, f4, f3 - f2, f13, f5);
            int j;
            float f8;
            float f9;
            float f10;

            for (int i = 0; i < 4; ++i) {
                j = this.shouldRenderPass((EntityLivingBase) par1Entity, i, par9);

                if (j > 0) {
                    this.renderPassModel.setLivingAnimations((EntityLivingBase) par1Entity, f7, f6, par9);
                    this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);

                    if ((j & 240) == 16) {
                        this.func_82408_c((EntityLivingBase) par1Entity, i, par9);
                        this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);
                    }
                    // patch start
                    if (CITUtils.setupArmorEnchantments((EntityLivingBase) par1Entity, i)) {
                        while (CITUtils.preRenderArmorEnchantment()) {
                            this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);
                            CITUtils.postRenderArmorEnchantment();
                        }
                    } else if ((j & 15) == 15) {
                        // if -> else if
                        // patch end
                        f8 = (float) par1Entity.ticksExisted + par9;
                        this.bindTexture(RES_ITEM_GLINT);
                        GL11.glEnable(GL11.GL_BLEND);
                        f9 = 0.5F;
                        GL11.glColor4f(f9, f9, f9, 1.0F);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int k = 0; k < 2; ++k) {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            f10 = 0.76F;
                            GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1.0F);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            float f11 = f8 * (0.001F + (float) k * 0.003F) * 20.0F;
                            float f12 = 0.33333334F;
                            GL11.glScalef(f12, f12, f12);
                            GL11.glRotatef(30.0F - (float) k * 60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.0F, f11, 0.0F);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            GL11.glDepthMask(true);
            this.renderEquippedItems((EntityLivingBase) par1Entity, par9);
            float f14 = par1Entity.getBrightness(par9);
            j = this.getColorMultiplier((EntityLivingBase) par1Entity, f14, par9);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if ((j >> 24 & 255) > 0 || ((EntityLivingBase) par1Entity).hurtTime > 0 || ((EntityLivingBase) par1Entity).deathTime > 0) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);

                if (((EntityLivingBase) par1Entity).hurtTime > 0 || ((EntityLivingBase) par1Entity).deathTime > 0) {
                    GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                    this.mainModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);

                    for (int l = 0; l < 4; ++l) {
                        if (this.inheritRenderPass((EntityLivingBase) par1Entity, l, par9) >= 0) {
                            GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                            this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                if ((j >> 24 & 255) > 0) {
                    f8 = (float) (j >> 16 & 255) / 255.0F;
                    f9 = (float) (j >> 8 & 255) / 255.0F;
                    float f15 = (float) (j & 255) / 255.0F;
                    f10 = (float) (j >> 24 & 255) / 255.0F;
                    GL11.glColor4f(f8, f9, f15, f10);
                    this.mainModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);

                    for (int i1 = 0; i1 < 4; ++i1) {
                        if (this.inheritRenderPass((EntityLivingBase) par1Entity, i1, par9) >= 0) {
                            GL11.glColor4f(f8, f9, f15, f10);
                            this.renderPassModel.render(par1Entity, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        } catch (Exception exception) {
            FishModLoader.LOGGER.error("Couldn't render par1Entity", exception);
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        this.passSpecialRender((EntityLivingBase) par1Entity, par2, par4, par6);
    }
}
