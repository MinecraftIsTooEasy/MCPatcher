package mitemod.mcpatcher.api;

public interface IPotion {
    int liquidColor = 0;
    int origColor = 0;
    default int getLiquidColor() {
        return 0;
    }
}
