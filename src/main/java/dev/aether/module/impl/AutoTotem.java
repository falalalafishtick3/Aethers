package dev.aether.module.impl;

import dev.aether.module.*;
import dev.aether.setting.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends ClientModule {
    private final NumberSetting delay = add(new NumberSetting("Delay", 2, 0, 10, 1));
    private int timer;

    public AutoTotem() { super("AutoTotem", "Keeps a totem in your offhand", Category.COMBAT); }

    @Override public void onTick(MinecraftClient mc) {
        if (timer > 0) { timer--; return; }
        var p = mc.player;
        if (p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        if (mc.currentScreen != null) return;
        for (int i = 0; i < 36; i++) {
            if (p.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                int slot = i < 9 ? i + 36 : i;
                mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, slot, 40, SlotActionType.SWAP, p);
                timer = delay.i();
                return;
            }
        }
    }
}
