package dev.aether.mixin;

import dev.aether.module.impl.Velocity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityVelocityUpdate(Lnet/minecraft/network/packet/s2c/play/EntityVelocityUpdateS2CPacket;)V",
            at = @At("HEAD"), cancellable = true)
    private void aether$velocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!mc.isOnThread()) return; // let vanilla re-queue onto the main thread first
        Velocity v = Velocity.INSTANCE;
        if (v == null || !v.enabled || mc.player == null || packet.getEntityId() != mc.player.getId()) return;
        ci.cancel();
        Vec3d vel = packet.getVelocity();
        mc.player.setVelocity(vel.x * v.h(), vel.y * v.v(), vel.z * v.h());
    }
}
