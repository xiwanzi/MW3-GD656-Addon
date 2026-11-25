package com.deuteriumix.mwpublic.mixin;

import com.deuteriumix.mwpublic.ShotRegion;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

/** The resolved TaCZ Pre event precedes Minecraft damage and GD656's damage-sound packet. */
@Pseudo
@Mixin(targets="com.tacz.guns.entity.EntityKineticBullet",remap=false)
abstract class TaczShotMixin implements ShotRegion {
    @Unique private int mwpublic$victim=-1;
    @Unique private boolean mwpublic$head;
    @Unique private boolean mwpublic$fullHealth;
    @WrapOperation(method="onHitEntity",at=@At(value="INVOKE",target="Lcom/tacz/guns/api/event/common/EntityHurtByGunEvent$Pre;isHeadShot()Z"))
    private boolean record(EntityHurtByGunEvent.Pre event,Operation<Boolean> original) {
        mwpublic$head=original.call(event);
        mwpublic$victim=event.getHurtEntity().getId();
        mwpublic$fullHealth=event.getHurtEntity() instanceof LivingEntity target
                && target.getHealth() >= target.getMaxHealth();
        return mwpublic$head;
    }
    public boolean mwpublic$matches(int victim) {return victim==mwpublic$victim;}
    public boolean mwpublic$isHead() {return mwpublic$head;}
    public boolean mwpublic$consumeFullHealthHit(int victim) {
        if (victim != mwpublic$victim || !mwpublic$fullHealth) return false;
        mwpublic$fullHealth=false;
        return true;
    }
}
