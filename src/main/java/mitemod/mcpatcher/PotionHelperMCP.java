package mitemod.mcpatcher;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.PotionHelper;

import java.util.Map;

public class PotionHelperMCP {

    @Environment(EnvType.CLIENT)
    public static Map getPotionColorCache() {
        return PotionHelper.field_77925_n;
    }
}
