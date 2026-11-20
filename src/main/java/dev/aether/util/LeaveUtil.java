package dev.aether.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class LeaveUtil {
    /** Disconnect from the current multiplayer server (no-op message in singleplayer). */
    public static void leave(MinecraftClient mc, String who, String why) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.getCurrentServerEntry() == null) {
            mc.player.sendMessage(Text.literal("[Aether] " + who + ": " + why + " (not disconnecting in singleplayer)"), false);
            return;
        }
        mc.getNetworkHandler().getConnection().disconnect(Text.literal("[Aether] " + who + ": " + why));
    }
}
