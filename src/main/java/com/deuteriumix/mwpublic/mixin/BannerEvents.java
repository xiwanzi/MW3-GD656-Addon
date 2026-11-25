package com.deuteriumix.mwpublic.mixin;
import com.deuteriumix.mwpublic.Feedback;
import org.mods.gd656killicon.client.render.HudElementManager;
import org.mods.gd656killicon.client.render.IHudRenderer.TriggerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.mods.gd656killicon.common.BonusType;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=HudElementManager.class,remap=false)
abstract class BannerEvents {
 @Inject(method="trigger",at=@At("HEAD"),cancellable=true)
 private static void banner(String category,String name,TriggerContext c,CallbackInfo ci){
   if("subtitle".equals(category) && "bonus_list".equals(name)){
     Feedback.special(switch(c.type()){
       case BonusType.AVENGE -> "复仇";
       case BonusType.INTERRUPTED_STREAK -> "大失所望";
       case BonusType.BLOODTHIRSTY -> "嗜血杀手";
       case BonusType.MERCILESS -> "冷酷无情";
       case BonusType.VALIANT -> "残暴";
       case BonusType.FIERCE -> "无情";
       case BonusType.SAVAGE -> "野蛮";
       case BonusType.PURGE -> "刀枪不入";
       case BonusType.KILL_LONG_DISTANCE -> "远射";
       case BonusType.SAVIOR -> "救星";
       case BonusType.SLAY_THE_LEADER -> "弑君者";
       case BonusType.ONE_BULLET_MULTI_KILL -> "一箭双雕";
       default -> null;
     });
   }
   if(Feedback.owns(category,name)){Feedback.enqueue(name,c.type(),c.comboCount());ci.cancel();}
 }
}
