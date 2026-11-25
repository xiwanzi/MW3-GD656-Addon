package com.deuteriumix.mwpublic;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
@Mod("mwpublic")
public final class ModEntry {
    public ModEntry(IEventBus bus){bus.addListener(ModEntry::network);}
    private static void network(RegisterPayloadHandlersEvent event){
        if (net.neoforged.fml.ModList.get().isLoaded("mwfeedback")) return;
        event.registrar("1").optional().playToClient(OneShotKill.TYPE,OneShotKill.CODEC,
                (payload,ctx)->ctx.enqueueWork(()->Feedback.special("一枪一个")));
    }
}
