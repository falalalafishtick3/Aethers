package dev.aether.module.impl;

import dev.aether.hud.HudUtil;
import dev.aether.module.*;
import dev.aether.setting.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;

public class TargetHud extends ClientModule {
    private final ModeSetting corner = add(new ModeSetting("Position", "Crosshair", "Top Left", "Top Right", "Bottom Left", "Bottom Right"));
    private final NumberSetting offX = add(new NumberSetting("Offset X", 24, 0, 400, 1));
    private final NumberSetting offY = add(new NumberSetting("Offset Y", 24, 0, 400, 1));
    private final NumberSetting hold = add(new NumberSetting("Hold (s)", 2, 0, 10, 0.5));

    private LivingEntity target;
    private long seen;

    public TargetHud() { super("TargetHud", "Shows health of whoever you aim at", Category.HUD); }

    @Override public void onTick(MinecraftClient mc) {
        if (mc.crosshairTarget instanceof EntityHitResult r && r.getEntity() instanceof LivingEntity le && le.isAlive()) {
            target = le; seen = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - seen > hold.get() * 1000) target = null;
    }

    @Override public void onHud(DrawContext g, RenderTickCounter tc) {
        if (target == null || !target.isAlive()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        var tr = mc.textRenderer;
        String name = target.getName().getString();
        int w = Math.max(110, tr.getWidth(name) + 16), h = 34;
        int[] p = HudUtil.pos(corner.get(), offX.i(), offY.i(), w, h, g.getScaledWindowWidth(), g.getScaledWindowHeight());
        float hp = target.getHealth(), max = Math.max(1f, target.getMaxHealth());
        float f = MathHelper.clamp(hp / max, 0f, 1f);
        int bar = 0xFF000000 | MathHelper.hsvToRgb(0.33f * f, 0.75f, 1f);

        g.fill(p[0], p[1], p[0] + w, p[1] + h, 0xA0000000);
        g.fill(p[0], p[1], p[0] + 2, p[1] + h, ClickGuiModule.accent());
        g.drawText(tr, name, p[0] + 8, p[1] + 6, 0xFFFFFFFF, true);
        g.fill(p[0] + 8, p[1] + 19, p[0] + w - 8, p[1] + 25, 0x66FFFFFF);
        g.fill(p[0] + 8, p[1] + 19, p[0] + 8 + (int) ((w - 16) * f), p[1] + 25, bar);
        String hpText = String.format("%.1f / %.1f", hp, max);
        g.drawText(tr, hpText, p[0] + 8, p[1] + 26, 0xFFCCCCCC, false);
        String dist = String.format("%.1fm", mc.player.distanceTo(target));
        g.drawText(tr, dist, p[0] + w - 8 - tr.getWidth(dist), p[1] + 26, 0xFFCCCCCC, false);
    }
}
