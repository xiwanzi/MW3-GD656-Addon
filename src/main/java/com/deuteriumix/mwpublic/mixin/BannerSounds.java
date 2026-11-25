package com.deuteriumix.mwpublic.mixin;
import com.deuteriumix.mwpublic.Feedback;
import org.mods.gd656killicon.client.sounds.SoundTriggerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=SoundTriggerManager.class,remap=false)
abstract class BannerSounds {
 @Inject(method="tryPlaySound",at=@At("HEAD"),cancellable=true)
 private static void delay(String category,String name,int type,int combo,boolean helmet,boolean player,CallbackInfo ci){
   if(Feedback.owns(category,name))ci.cancel();
 }
}
