package dev.aether.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import dev.aether.module.impl.ClickGuiModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Animated winter scene: sky, stars, aurora, snowfall, hills (+ moon, mountains and pines when scenic). */
public final class WinterBackground {
    private static final class Flake { float x, y, vy, drift, size, phase; int layer; }
    private static final List<Flake> FLAKES = new ArrayList<>();
    private static final Random RNG = new Random();
    private static long lastNs = System.nanoTime();
    private static int lastW = -1, lastH = -1;

    private static Flake newFlake(int w, int h, boolean anywhere) {
        Flake f = new Flake();
        f.layer = RNG.nextInt(3);
        f.x = RNG.nextFloat() * w;
        f.y = anywhere ? RNG.nextFloat() * h : -6 - RNG.nextFloat() * 20;
        f.vy = 14f + f.layer * 12f + RNG.nextFloat() * 8f;
        f.drift = 6f + RNG.nextFloat() * 10f;
        f.size = 1 + f.layer;
        f.phase = RNG.nextFloat() * 6.28f;
        return f;
    }

    private static double hill(int x, double a) { return 14 + 9 * Math.sin(x * 0.011 + a) + 6 * Math.sin(x * 0.027) + 4 * Math.sin(x * 0.06 + 2); }

    public static void draw(DrawContext g, int w, int h, boolean scenic) {
        long nowNs = System.nanoTime();
        float dt = Math.min(0.1f, (nowNs - lastNs) / 1e9f);
        lastNs = nowNs;
        double t = Util.getMeasuringTimeMs() / 1000.0;
        if (w != lastW || h != lastH) { FLAKES.clear(); lastW = w; lastH = h; }

        int q = ClickGuiModule.quality();
        // sky
        if (scenic) g.fillGradient(0, 0, w, h, 0xFF07122A, 0xFF21496F);
        else g.fillGradient(0, 0, w, h, 0x6A0B1F3A, 0x8A1C3F63);

        // stars
        for (int i = 0; i < (q == 0 ? (scenic ? 90 : 40) : q == 1 ? (scenic ? 45 : 20) : 15); i++) {
            int sx = (int) ((i * 97L * 31 % 1000) / 1000.0 * w);
            int sy = (int) ((i * 53L * 17 % 1000) / 1000.0 * h * 0.55);
            float tw = (float) (0.5 + 0.5 * Math.sin(t * 1.5 + i));
            g.fill(sx, sy, sx + 1, sy + 1, ((int) (60 + 150 * tw) << 24) | 0xDDEEFF);
        }

        // moon
        if (scenic) {
            int mx = (int) (w * 0.80), my = (int) (h * 0.20), r = Math.max(9, h / 22);
            for (int k = 5; k >= 1; k--) {
                int rr = r + k * 7;
                int col = (7 << 24) | 0xBFD9FF;
                for (int dy = -rr; dy <= rr; dy += 1) { int dx = (int) Math.sqrt(rr * rr - dy * dy); g.fill(mx - dx, my + dy, mx + dx + 1, my + dy + 1, col); }
            }
            for (int dy = -r; dy <= r; dy++) { int dx = (int) Math.sqrt(r * r - dy * dy); g.fill(mx - dx, my + dy, mx + dx + 1, my + dy + 1, 0xFFF4F8FF); }
            g.fill(mx - r / 3 - 2, my - r / 3, mx - r / 3 + 2, my - r / 3 + 3, 0xFFD6E2F2);
            g.fill(mx + r / 4, my + r / 4, mx + r / 4 + 3, my + r / 4 + 3, 0xFFD6E2F2);
        }

        // aurora
        if (ClickGuiModule.aurora() && q < 2) {
            int step = q == 0 ? 4 : 10, mid = (int) (h * 0.28), bot = (int) (h * 0.62);
            for (int x = 0; x < w; x += step) {
                double w1 = Math.sin(x * 0.012 + t * 0.55), w2 = Math.sin(x * 0.027 - t * 0.8), w3 = Math.sin(x * 0.005 + t * 0.2);
                double v = 0.5 + 0.5 * (0.55 * w1 + 0.3 * w2 + 0.15 * w3);
                int a = (int) ((scenic ? 90 : 70) * v * v);
                if (a < 4) continue;
                float hue = ClickGuiModule.baseHue() - 0.04f + 0.16f * (float) (0.5 + 0.5 * Math.sin(x * 0.006 + t * 0.3));
                int rgb = MathHelper.hsvToRgb(hue, 0.65f, 1f) & 0xFFFFFF;
                int y0 = (int) (mid - 40 + 25 * w1), y1 = (int) (bot - 40 + 30 * w2);
                g.fillGradient(x, y0 / 2, x + step, y0 + (y1 - y0) / 3, rgb, (a << 24) | rgb);
                g.fillGradient(x, y0 + (y1 - y0) / 3, x + step, y1, (a << 24) | rgb, rgb);
            }
        }

        // mountains
        if (scenic) {
            int mstep = q == 0 ? 3 : q == 1 ? 6 : 10;
            for (int layer = 0; layer < 2; layer++) {
                int top0 = layer == 0 ? 0xFF2C5680 : 0xFF183654;
                for (int x = 0; x < w; x += mstep) {
                    double m = h * (layer == 0 ? 0.34 : 0.24) + 26 * Math.sin(x * 0.006 + 0.5 + layer * 2) + 15 * Math.sin(x * 0.017 + 2 + layer) + 7 * Math.sin(x * 0.04);
                    int top = h - (int) m;
                    g.fillGradient(x, top, x + mstep, top + 10, 0xFFE4EFFB, top0);
                    g.fillGradient(x, top + 10, x + mstep, h, top0, layer == 0 ? 0xFF1B3B5C : 0xFF0F2540);
                }
            }
        }

        // hills
        int hstep = q == 0 ? 2 : 4;
        for (int x = 0; x < w; x += hstep) {
            int top = h - (int) hill(x, 1.3);
            if (q == 0) g.fillGradient(x, top, x + hstep, h, 0xE0EAF4FF, 0xF0BFD8EE); else g.fill(x, top, x + hstep, h, 0xF0D4E6F8);
        }

        // pines
        if (scenic && q < 2) {
            for (int i = 0; i * 26 < w + 26; i += (q == 0 ? 1 : 2)) {
                int x = i * 26 + (i * 37 % 13);
                int hgt = 20 + (i * 53 % 4) * 7;
                int base = h - (int) hill(x, 1.3) + 5;
                int dark = (i % 2 == 0) ? 0xFF0F2B26 : 0xFF133530;
                for (int r = 0; r < hgt; r++) {
                    int half = r / 3 + 1, y = base - hgt + r;
                    g.fill(x - half, y, x + half + 1, y + 1, dark);
                    if (r % 6 < 2) g.fill(x - half, y, x + half + 1, y + 1, 0xFFB8D0E6 & 0xCCFFFFFF);
                }
                g.fill(x - 1, base, x + 1, base + 3, 0xFF2A1F1A);
            }
        }
        for (int x = 0; x < w; x += hstep) {
            double hh = 6 + 5 * Math.sin(x * 0.02 + 4) + 3 * Math.sin(x * 0.05);
            int top = h - (int) hh;
            g.fill(x, top, x + hstep, h, 0xF2EEF6FF);
        }

        // snow
        int target = ClickGuiModule.snowAmount() * (q == 0 ? 10 : q == 1 ? 6 : 3) / 10;
        while (FLAKES.size() < target) FLAKES.add(newFlake(w, h, true));
        while (FLAKES.size() > target) FLAKES.remove(FLAKES.size() - 1);
        float speed = ClickGuiModule.snowSpeed();
        for (int i = 0; i < FLAKES.size(); i++) {
            Flake f = FLAKES.get(i);
            f.y += f.vy * speed * dt;
            f.x += (float) Math.sin(t * 0.9 + f.phase) * f.drift * dt + 3f * dt;
            if (f.y > h + 4 || f.x > w + 8) { FLAKES.set(i, newFlake(w, h, false)); continue; }
            if (f.x < -8) f.x = w + 4;
            int c = ((90 + f.layer * 55) << 24) | 0xFFFFFF;
            int x = (int) f.x, y = (int) f.y, sz = (int) f.size;
            g.fill(x, y, x + sz, y + sz, c);
            if (f.layer == 2) {
                int soft = (60 << 24) | 0xFFFFFF;
                g.fill(x - 1, y + 1, x, y + 2, soft); g.fill(x + sz, y + 1, x + sz + 1, y + 2, soft);
                g.fill(x + 1, y - 1, x + 2, y, soft); g.fill(x + 1, y + sz, x + 2, y + sz + 1, soft);
            }
        }

        // frost vignette
        int fw = q == 2 ? 0 : 26;
        for (int i = 0; i < fw; i += 4) {
            int a = (int) (70 * (1 - i / (float) fw) * (1 - i / (float) fw));
            int col = (a << 24) | 0xCFE8FF;
            g.fill(i, 0, i + 2, h, col);
            g.fill(w - i - 2, 0, w - i, h, col);
        }
    }
}
