package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.item;

import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock extends Item {

    @Shadow
    public Block getBlock() {
        return null;
    }

    @Override
    public int getColorFromItemStack(final ItemStack itemStack, final int meta) {
        final Block block = this.getBlock();
        if (block != null) {
            return block.getRenderColor(meta);
        }
        return super.getColorFromItemStack(itemStack, meta);
    }
}
