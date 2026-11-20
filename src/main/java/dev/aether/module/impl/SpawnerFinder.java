package dev.aether.module.impl;

import dev.aether.setting.ColorSetting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;

public class SpawnerFinder extends BlockEspModule {
    private final ColorSetting color = add(new ColorSetting("Color", 0.95f, 0.85f, 1f));

    public SpawnerFinder() {
        super("SpawnerFinder", "Highlights mob spawners");
        addCommonSettings();
    }
    @Override protected Integer classify(BlockEntity be) {
        return be instanceof MobSpawnerBlockEntity ? color.rgb() : null;
    }
}
