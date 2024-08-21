package moddedmite.mcpatcher.mixin.mcpatcherforge.mob;

import net.minecraft.Render;
import net.minecraft.Entity;
import net.minecraft.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.mob.MobRandomizer;

@Mixin(Render.class)
public abstract class MixinRender {

    @Shadow
    protected abstract ResourceLocation getEntityTexture(Entity entity);

    @Redirect(
        method = "bindEntityTexture(Lnet/minecraft/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Render;getEntityTexture(Lnet/minecraft/Entity;)Lnet/minecraft/ResourceLocation;"))
    private ResourceLocation modifyBindEntityTexture(Render instance, Entity entity) {
        return MobRandomizer.randomTexture(entity, getEntityTexture(entity));
    }
}
