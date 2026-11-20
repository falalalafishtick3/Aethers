package dev.aether.hud;

public final class HudUtil {
    public static final String[] CORNERS = {"Top Left", "Top Right", "Bottom Left", "Bottom Right"};

    /** Top-left pixel of a w*h box anchored to a corner (or "Crosshair") with offsets. */
    public static int[] pos(String corner, int ox, int oy, int w, int h, int sw, int sh) {
        if (corner.equals("Crosshair")) return new int[]{sw / 2 + ox, sh / 2 + oy};
        int x = corner.endsWith("Right") ? sw - w - ox : ox;
        int y = corner.startsWith("Bottom") ? sh - h - oy : oy;
        return new int[]{x, y};
    }

    /** Multiply the alpha channel of an ARGB color. */
    public static int a(int argb, float alpha) {
        int na = Math.max(0, Math.min(255, Math.round(((argb >>> 24) & 255) * alpha)));
        return (na << 24) | (argb & 0xFFFFFF);
    }
}
