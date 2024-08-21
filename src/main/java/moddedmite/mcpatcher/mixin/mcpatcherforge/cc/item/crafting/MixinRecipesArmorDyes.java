package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.item.crafting;

import net.minecraft.*;

import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.prupe.mcpatcher.cc.ColorizeEntity;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipesArmorDyes.class)
public abstract class MixinRecipesArmorDyes {
    @Shadow
    private float difficulty = 100.0f;
    @Shadow
    private int[] skillsets;

    /**
     * @author Mist475 (adapted from Paul Rupe)
     * @reason Fleece color redirection is beyond my skill atm
     */
    @SuppressWarnings("DuplicatedCode")
    @Overwrite
    public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
        ItemStack itemstack = null;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;
        EnumQuality quality_override = null;
        int k;
        int l;
        float f;
        float f1;
        int l1;

        for (k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k) {
            ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(k);

            if (itemstack1 != null) {
                if (itemstack1.getItem() instanceof ItemArmor) {
                    itemarmor = (ItemArmor) itemstack1.getItem();

                    if (itemarmor.getArmorMaterial() != Material.leather || itemstack != null) {
                        return null;
                    }

                    quality_override = itemstack1.getQuality();
                    itemstack = itemstack1.copy();
                    itemstack.stackSize = 1;

                    if (itemarmor.hasColor(itemstack1)) {
                        l = itemarmor.getColor(itemstack);
                        f = (float) (l >> 16 & 255) / 255.0F;
                        f1 = (float) (l >> 8 & 255) / 255.0F;
                        float f2 = (float) (l & 255) / 255.0F;
                        i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int) ((float) aint[0] + f * 255.0F);
                        aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
                        aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (itemstack1.getItem() != Item.dyePowder) {
                        return null;
                    }
                    // patch
                    float[] afloat = ColorizeEntity.getArmorDyeColor(
                        EntitySheep.fleeceColorTable[BlockColored.getBlockFromDye(itemstack1.getItemDamage())],
                        BlockColored.getBlockFromDye(itemstack1.getItemDamage()));
                    int j1 = (int) (afloat[0] * 255.0F);
                    int k1 = (int) (afloat[1] * 255.0F);
                    l1 = (int) (afloat[2] * 255.0F);
                    i += Math.max(j1, Math.max(k1, l1));
                    aint[0] += j1;
                    aint[1] += k1;
                    aint[2] += l1;
                    ++j;
                }
            }
        }

        if (itemarmor == null) {
            return null;
        } else {
            k = aint[0] / j;
            int i1 = aint[1] / j;
            l = aint[2] / j;
            f = (float) i / (float) j;
            f1 = (float) Math.max(k, Math.max(i1, l));
            k = (int) ((float) k * f / f1);
            i1 = (int) ((float) i1 * f / f1);
            l = (int) ((float) l * f / f1);
            l1 = (k << 8) + i1;
            l1 = (l1 << 8) + l;
            itemarmor.func_82813_b(itemstack, l1);
            return new CraftingResult(itemstack, this.difficulty, this.skillsets, ReflectHelper.dyCast(this)).setExperienceCostExempt().setQualityOverride(quality_override);
        }
    }
}
