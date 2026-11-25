package com.deuteriumix.mwpublic;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OneShotKill() implements CustomPacketPayload {
    public static final OneShotKill INSTANCE = new OneShotKill();
    public static final Type<OneShotKill> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mwfeedback","one_shot_kill"));
    public static final StreamCodec<RegistryFriendlyByteBuf,OneShotKill> CODEC = StreamCodec.unit(INSTANCE);
    @Override public Type<OneShotKill> type() { return TYPE; }
}
