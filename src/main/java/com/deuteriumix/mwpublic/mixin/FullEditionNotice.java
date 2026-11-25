package com.deuteriumix.mwpublic.mixin;
import com.deuteriumix.mwpublic.Feedback;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets="com.deuteriumix.mwfeedback.Feedback",remap=false)
abstract class FullEditionNotice {
    @Inject(method="notice(Ljava/lang/String;Z)V",at=@At("HEAD"))
    private static void received(String key,boolean banner,CallbackInfo ci){
        if(banner && "one_shot_kill".equals(key))Feedback.special("一枪一个");
    }
}
