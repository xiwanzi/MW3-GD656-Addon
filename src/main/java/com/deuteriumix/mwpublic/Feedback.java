package com.deuteriumix.mwpublic;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.client.event.*;
import org.mods.gd656killicon.client.config.*;
import org.mods.gd656killicon.client.sounds.ExternalSoundManager;
import java.nio.file.*;

import java.util.Objects;

@EventBusSubscriber(modid="mwpublic",value=Dist.CLIENT)
public final class Feedback {
    static ResourceLocation image(String path){return new ResourceLocation("mwpublic","textures/"+path+".png");}
    static void label(GuiGraphics g,Component c,float x,float y,float scale,int color,float alpha){
        int a=Math.round(alpha*255);if(a<4)return;
        g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);
        g.drawString(Minecraft.getInstance().font,c,0,0,a<<24|color,false);g.pose().popPose();
    }
    private static final String[] TYPES={"击杀","爆头","爆炸击杀","命中要害","助攻","载具被摧毁","夺取","载具摧毁助攻","复苏","追踪助攻","营救"};
    private static final String[] SOUNDS={"scrolling_default","scrolling_headshot","scrolling_explosion","scrolling_crit","scrolling_assist","scrolling_vehicle","scrolling_assist","scrolling_assist","scrolling_assist","scrolling_assist","scrolling_assist"};
    private static final String[] MULTI={"双杀","三杀","怒杀","狂杀","超杀","大杀特杀","终极杀戮","连环杀戮"};
    private record Notice(BannerArt.Title title,String slot){}
    private static final BannerQueue queue=new BannerQueue();
    private static Notice current;
    private static long started;
    private static boolean checked;
    private static boolean ours(String id){
        var cfg=ElementConfigManager.getElementConfig(id,"kill_icon/scrolling");
        return cfg!=null && cfg.has("mwiii_public_queue") && cfg.get("mwiii_public_queue").getAsBoolean();
    }
    public static boolean owns(String category,String name){
        return "kill_icon".equals(category) && ("scrolling".equals(name)||"combo".equals(name)) && ours(ConfigManager.getCurrentPresetId());
    }
    @SubscribeEvent public static void install(net.minecraftforge.event.TickEvent.ClientTickEvent e){
        if(e.phase != net.minecraftforge.event.TickEvent.Phase.END || checked)return;
        var mc=Minecraft.getInstance();
        if(!(mc.screen instanceof TitleScreen) && mc.level==null)return;
        checked=true;
        var marker=FMLPaths.CONFIGDIR.get().resolve("mwpublic-installed.txt");
        if(Files.isRegularFile(marker))return;
        try{
            String id="62024";
            if(!ours(id)){
                if(ConfigManager.getPresetIds().contains(id))id=PresetPackManager.generateRandomId();
                Path file=Files.createTempFile("mwpublic-", ".gdpack");
                try(var in=Objects.requireNonNull(Feedback.class.getResourceAsStream("/assets/mwpublic/MWIII-Native.gdpack"))){
                    Files.copy(in,file,StandardCopyOption.REPLACE_EXISTING);
                    if(!PresetPackManager.importPreset(file.toFile(),id))throw new java.io.IOException("Preset import failed");
                }finally{Files.deleteIfExists(file);}
            }
            ConfigManager.setCurrentPresetId(id);
            Files.writeString(marker,id);
        }catch(Exception ex){com.mojang.logging.LogUtils.getLogger().error("Could not install the MWIII public preset",ex);}
    }
    public static void enqueue(String renderer,int type,int combo){
        String title,slot;
        if("combo".equals(renderer)){
            if(combo<2)return;
            title=MULTI[Math.min(combo,9)-2];slot="combo_"+Math.min(combo,6);
        }else{
            if(type<0||type>=TYPES.length || (type<=3 && combo>=2))return;
            title=TYPES[type];slot=SOUNDS[type];
        }
        queue.offer(new BannerQueue.Entry(title,slot,"combo".equals(renderer)?Math.min(combo,9):0,0));
    }
    public static void special(String title){
        if(title==null || !ours(ConfigManager.getCurrentPresetId()))return;
        int rank=switch(title){
            case "嗜血杀手" -> 2; case "冷酷无情" -> 3; case "残暴" -> 4;
            case "无情" -> 5; case "野蛮" -> 6; case "刀枪不入" -> 7;
            default -> 1;
        };
        queue.offer(new BannerQueue.Entry(title,"combo_2",0,rank));
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut e){current=null;queue.clear();}
    @SubscribeEvent public static void render(RenderGuiEvent.Post e){
        if(current==null && queue.empty())return;
        var mc=Minecraft.getInstance();
        if(mc.level==null || !ours(ConfigManager.getCurrentPresetId())){queue.clear();current=null;return;}
        if(mc.screen!=null || mc.options.hideGui)return;
        long now=Util.getMillis();
        if(current==null || now-started>=1800){
            var entry=queue.next();
            if(entry==null){current=null;return;}
            current=new Notice(BannerArt.Title.of(Component.literal(entry.label())),entry.slot());
            started=now;ExternalSoundManager.playConfiguredSound(ConfigManager.getCurrentPresetId(),current.slot);
        }
        var g=e.getGuiGraphics();float scale=g.guiHeight()/1080f;
        g.pose().pushPose();
        try{g.pose().translate(g.guiWidth()*.5f-180*scale,254.25f*scale,0);g.pose().scale(scale,scale,1);BannerArt.skill(g,current.title,now-started);}
        finally{g.pose().popPose();}
    }
}
