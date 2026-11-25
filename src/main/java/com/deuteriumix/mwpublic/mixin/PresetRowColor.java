package com.deuteriumix.mwpublic.mixin;

import com.deuteriumix.mwpublic.Feedback;
import org.mods.gd656killicon.client.gui.GuiConstants;
import org.mods.gd656killicon.client.gui.tabs.PresetConfigTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = PresetConfigTab.class, remap = false)
abstract class PresetRowColor {
    @ModifyArgs(method = "addPresetRow", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lorg/mods/gd656killicon/client/gui/elements/GDRowRenderer;addColumn(Ljava/lang/String;IIZZ)V"))
    private void colorName(Args column, String selectedId, String rowId, int row, int indent) {
        if (Feedback.ours(rowId)) column.set(2, GuiConstants.COLOR_GOLD);
    }
}
