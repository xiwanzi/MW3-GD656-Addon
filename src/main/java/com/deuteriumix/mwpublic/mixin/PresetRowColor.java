package com.deuteriumix.mwpublic.mixin;

import com.deuteriumix.mwpublic.Feedback;
import org.mods.gd656killicon.client.config.ElementConfigManager;
import org.mods.gd656killicon.client.gui.tabs.PresetConfigTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PresetConfigTab.class, remap = false)
abstract class PresetRowColor {
    // Only the first check selects the label color; later checks govern editing.
    @Redirect(method = "addPresetRow", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lorg/mods/gd656killicon/client/config/ElementConfigManager;isOfficialPreset(Ljava/lang/String;)Z"))
    private boolean useBuiltInLabelColor(String id) {
        return ElementConfigManager.isOfficialPreset(id) || Feedback.ours(id);
    }
}
