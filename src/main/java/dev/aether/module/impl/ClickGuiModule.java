package dev.aether.module.impl;

import dev.aether.module.*;
import dev.aether.setting.*;

public class ClickGuiModule extends ClientModule {
    public static ClickGuiModule INSTANCE;
    private final ColorSetting accent = add(new ColorSetting("Accent", 0.46f, 0.9f, 0.75f));
    private final NumberSetting dim = add(new NumberSetting("Background Dim", 35, 0, 80, 1));

    private final ModeSetting menuBg = add(new ModeSetting("Menu Background", "Winter: All Menus", "Winter: Title Only", "Vanilla"));
    private final BoolSetting winter = add(new BoolSetting("Winter In GUI", true));
    private final NumberSetting snowAmount = add(new NumberSetting("Snow Amount", 140, 0, 500, 5));
    private final NumberSetting snowSpeed = add(new NumberSetting("Snow Speed", 1.0, 0.2, 3.0, 0.1));
    private final BoolSetting aurora = add(new BoolSetting("Aurora", true));
    private final BoolSetting startupSound = add(new BoolSetting("Startup Sound", true));
    private final NumberSetting startupVol = add(new NumberSetting("Startup Volume", 80, 0, 100, 1));
    private final NumberSetting quality = add(new NumberSetting("Winter Quality", 1, 0, 2, 1));
    private final BoolSetting perServer = add(new BoolSetting("Separate Server Profiles", false));
    private final NumberSetting scanBudgetMs = add(new NumberSetting("Block Scan Budget (ms)", 2, 1, 10, 1));

    public ClickGuiModule() {
        super("ClickGUI", "GUI appearance", Category.CLIENT);
        INSTANCE = this;
        snowAmount.visibleWhen(winter::get);
        snowSpeed.visibleWhen(winter::get);
        aurora.visibleWhen(winter::get);
    }
    public static int accent() { return 0xFF000000 | INSTANCE.accent.rgb(); }
    public static String menuMode() { return INSTANCE == null ? "Winter: All Menus" : INSTANCE.menuBg.get(); }
    public static boolean winter() { return INSTANCE == null || INSTANCE.winter.get(); }
    public static int snowAmount() { return INSTANCE == null ? 140 : INSTANCE.snowAmount.i(); }
    public static float snowSpeed() { return INSTANCE == null ? 1f : INSTANCE.snowSpeed.f(); }
    public static boolean aurora() { return INSTANCE == null || INSTANCE.aurora.get(); }
    public static boolean startupSoundEnabled() { return INSTANCE == null || INSTANCE.startupSound.get(); }
    public static float startupVolume() { return INSTANCE == null ? 0.8f : INSTANCE.startupVol.f() / 100f; }
    public static int dimARGB() { return INSTANCE == null ? 0 : ((int) (INSTANCE.dim.get() / 100.0 * 255)) << 24; }

    // Shared appearance/performance settings used by the GUI and background renderers.
    public static float baseHue() { return INSTANCE == null ? 0.46f : INSTANCE.accent.h; }
    public static float sat() { return INSTANCE == null ? 0.9f : INSTANCE.accent.s; }
    public static int quality() { return INSTANCE == null ? 1 : INSTANCE.quality.i(); }
    public static boolean perServer() { return INSTANCE != null && INSTANCE.perServer.get(); }
    public static long scanBudgetNs() {
        return (long) (INSTANCE == null ? 2_000_000L : INSTANCE.scanBudgetMs.i() * 1_000_000L);
    }
}
