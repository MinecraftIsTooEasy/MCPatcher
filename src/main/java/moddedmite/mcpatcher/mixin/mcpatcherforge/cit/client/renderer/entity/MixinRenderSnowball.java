package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer.entity;

import net.minecraft.RenderSnowball;
import net.minecraft.Entity;
import net.minecraft.Item;
import net.minecraft.Icon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(RenderSnowball.class)
public abstract class MixinRenderSnowball {

    @Redirect(
        method = "doRender(Lnet/minecraft/Entity;DDDFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/Item;getIconFromSubtype(I)Lnet/minecraft/Icon;"))
    private Icon modifyDoRender(Item item, int damage, Entity entity) {
        return CITUtils.getEntityIcon(item.getIconFromSubtype(damage), entity);
    }
}
