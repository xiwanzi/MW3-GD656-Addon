package com.deuteriumix.mwpublic;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
public record OneShotKill() {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("mwpublic", "combat"), () -> "1",
        NetworkRegistry.acceptMissingOr("1"), NetworkRegistry.acceptMissingOr("1"));
    static void register() {
        CHANNEL.registerMessage(0, OneShotKill.class, (msg, buf) -> {}, buf -> new OneShotKill(),
            (msg, supplier) -> {
                var ctx = supplier.get();
                ctx.enqueueWork(() -> Feedback.special("一枪一个"));
                ctx.setPacketHandled(true);
            }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
