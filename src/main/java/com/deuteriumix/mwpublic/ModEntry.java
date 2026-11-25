package com.deuteriumix.mwpublic;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
@Mod("mwpublic")
public final class ModEntry {
    public ModEntry() {
        if (!ModList.get().isLoaded("mwfeedback")) OneShotKill.register();
    }
}
