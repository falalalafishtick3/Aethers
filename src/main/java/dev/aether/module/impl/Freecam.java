package dev.aether.module.impl;

import dev.aether.module.*;
import dev.aether.setting.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Freecam extends ClientModule {
    public static boolean active;
    public static Vec3d pos = Vec3d.ZERO, prevPos = Vec3d.ZERO;

    private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1));
    private final BoolSetting sprintBoost = add(new BoolSetting("Sprint Boost", true));

    private ClientPlayerEntity bound;
    private Input savedInput;
    private float savedYaw, savedPitch;

    public Freecam() { super("Freecam", "Detached camera; your body stays put", Category.MOVEMENT); }

    private void start(MinecraftClient mc) {
        bound = mc.player;
        savedInput = bound.input;
        savedYaw = bound.getYaw(); savedPitch = bound.getPitch();
        bound.input = new Input();            // blocks WASD/jump/sneak from moving the real player
        pos = prevPos = bound.getEyePos();
        active = true;
    }

    private void stop(MinecraftClient mc) {
        active = false;
        if (bound != null && savedInput != null && bound == mc.player) {
            bound.input = savedInput;
            bound.setYaw(savedYaw); bound.setPitch(savedPitch);
        }
        bound = null; savedInput = null;
    }

    @Override public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) start(mc);
    }
    @Override public void onDisable() { stop(MinecraftClient.getInstance()); }

    @Override public void onTick(MinecraftClient mc) {
        if (!active || bound != mc.player) {
            if (bound != null && bound != mc.player) { active = false; bound = null; savedInput = null; }
            start(mc);
        }
        ClientPlayerEntity p = mc.player;
        prevPos = pos;
        if (mc.currentScreen != null) return;

        double s = speed.get() * 0.5 * (sprintBoost.get() && mc.options.sprintKey.isPressed() ? 2.0 : 1.0);
        float yaw = p.getYaw() * (float) (Math.PI / 180.0);
        Vec3d look = Vec3d.fromPolar(p.getPitch(), p.getYaw());
        Vec3d right = new Vec3d(-Math.cos(yaw), 0, -Math.sin(yaw));

        Vec3d v = Vec3d.ZERO;
        if (mc.options.forwardKey.isPressed()) v = v.add(look);
        if (mc.options.backKey.isPressed())    v = v.subtract(look);
        if (mc.options.rightKey.isPressed())   v = v.add(right);
        if (mc.options.leftKey.isPressed())    v = v.subtract(right);
        if (mc.options.jumpKey.isPressed())    v = v.add(0, 1, 0);
        if (mc.options.sneakKey.isPressed())   v = v.add(0, -1, 0);
        if (v.lengthSquared() > 1e-6) pos = pos.add(v.normalize().multiply(s));
    }
}
