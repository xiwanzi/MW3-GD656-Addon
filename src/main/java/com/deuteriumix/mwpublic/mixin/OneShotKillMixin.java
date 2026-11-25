package com.deuteriumix.mwpublic.mixin;

import com.deuteriumix.mwpublic.OneShotKill;
import com.deuteriumix.mwpublic.ShotRegion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.mods.gd656killicon.server.event.ServerCombatEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=ServerCombatEngine.class,remap=false)
abstract class OneShotKillMixin {
    @Inject(method="handlePlayerKill",at=@At(value="INVOKE",target="Lorg/mods/gd656killicon/server/network/ServerPacketDispatcher;sendKillDistance(Lnet/minecraft/server/level/ServerPlayer;D)V"))
    private static void confirmed(ServerPlayer killer, LivingEntity victim, DamageSource source, CallbackInfo ci) {
        if (net.neoforged.fml.ModList.get().isLoaded("mwfeedback")) return;
        if (source.getEntity()==killer && !source.is(DamageTypeTags.IS_EXPLOSION)
                && source.getDirectEntity() instanceof ShotRegion bullet
                && bullet.mwpublic$consumeFullHealthHit(victim.getId())
                && killer.connection.hasChannel(OneShotKill.TYPE)) {
            PacketDistributor.sendToPlayer(killer,OneShotKill.INSTANCE);
        }
    }
}
