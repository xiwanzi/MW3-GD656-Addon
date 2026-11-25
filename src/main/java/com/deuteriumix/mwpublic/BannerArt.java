package com.deuteriumix.mwpublic;

import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class BannerArt {
    private static ResourceLocation art(String name){return Feedback.image("banner/"+name);}
    private static final ResourceLocation HOLD=art("hold"), ENTRY=art("entry"), WIDE=art("wide"),
            WHITE=art("solid"),
            RETICLE=Feedback.image("reticle"),R104=art("reticle104"),R208=art("reticle208");
    private static final ResourceLocation[] TYPE={art("glyphs1"),art("glyphs2"),art("glyphs4")};
    private record Tile(float u,float v,float right,float bottom,float width,float height){}
    private static final Map<String,Tile> glyphs=new HashMap<>();
    static {
        try(var in=new InputStreamReader(Objects.requireNonNull(BannerArt.class.getResourceAsStream("/assets/mwpublic/banner-glyphs.json")),StandardCharsets.UTF_8)){
            var def=JsonParser.parseReader(in).getAsJsonObject();
            int width=def.get("width").getAsInt(),height=def.get("height").getAsInt();
            def.getAsJsonObject("labels").entrySet().forEach(e->{
                var v=e.getValue().getAsJsonArray();int x=v.get(0).getAsInt(),y=v.get(1).getAsInt(),w=v.get(2).getAsInt(),h=v.get(3).getAsInt();
                glyphs.put(e.getKey(),new Tile(x/(float)width,y/(float)height,(x+w)/(float)width,(y+h)/(float)height,w/4f,h/4f));
            });
        }catch(Exception e){throw new ExceptionInInitializerError(e);}
    }

    record Title(Component component,Tile tile) {
        static Title of(Component c){return new Title(c,glyphs.get(c.getString()));}
        float width(float em){return tile==null?Minecraft.getInstance().font.width(component)*em/9f:(tile.width-4)*em/28f;}
        void draw(GuiGraphics g,float left,float centerY,float em,int rgb,float alpha){
            float k=em/28f;
            if(tile==null){
                Feedback.label(g,component,left,centerY-em/2,em/9f,rgb,alpha);return;
            }
            float physical=k*Minecraft.getInstance().getWindow().getHeight()/1080f;
            ResourceLocation texture=TYPE[physical<=1.1f?0:physical<=2.15f?1:2];
            quad(g,texture,left-2*k,centerY-17*k,tile.width*k,tile.height*k,tile.u,tile.v,tile.right,tile.bottom,rgb,alpha);
        }
    }

    private static float unit(float v){return Math.max(0,Math.min(1,v));}
    private static float out(float t){float v=1-unit(t);return 1-v*v;}
    private static void quad(GuiGraphics g,ResourceLocation tex,float x,float y,float w,float h,
                             float u0,float v0,float u1,float v1,int rgb,float opacity){
        if(opacity<=0 || w<=0 || h<=0)return;
        var mat=g.pose().last().pose();var q=g.bufferSource().getBuffer(RenderType.text(tex));
        int color=Math.round(255*unit(opacity))<<24|rgb;
        q.vertex(mat,x,y+h,0).color(color).uv(u0,v1).uv2(LightTexture.FULL_BRIGHT).endVertex();
        q.vertex(mat,x+w,y+h,0).color(color).uv(u1,v1).uv2(LightTexture.FULL_BRIGHT).endVertex();
        q.vertex(mat,x+w,y,0).color(color).uv(u1,v0).uv2(LightTexture.FULL_BRIGHT).endVertex();
        q.vertex(mat,x,y,0).color(color).uv(u0,v0).uv2(LightTexture.FULL_BRIGHT).endVertex();
    }
    private static void img(GuiGraphics g,ResourceLocation tex,float x,float y,float w,float h,int rgb,float a){
        quad(g,tex,x,y,w,h,0,0,1,1,rgb,a);
    }
    private static void threeSlice(GuiGraphics g,ResourceLocation id,float x,float w,int tint,float alpha) {
        float cap=Math.min(24.5f,w/2);
        quad(g,id,x,-22,cap,44,0,0,.145f,1,tint,alpha);
        quad(g,id,x+cap,-22,w-2*cap,44,.145f,0,.855f,1,tint,alpha);
        quad(g,id,x+w-cap,-22,cap,44,.855f,0,1,1,tint,alpha);
    }

    // Coordinates in the native 1080-high LUI canvas, centered on the reticle.
    static void skill(GuiGraphics g,Title title,long age) {
        float intro=unit(age/70f), exit=unit((age-1650)/145f), fade=intro*(1-exit);
        float kick=unit((age-1630)/35f)*(1-exit);
        float left=13-140*kick, width=97+title.width(28)+140*kick;
        float reveal=unit((age-94)/130f),size=128*(1+.15f*(1-reveal));
        float phys=size*Minecraft.getInstance().getWindow().getHeight()/1080f;
        img(g,phys<105?R104:phys<210?R208:RETICLE,-size/2,-size/2,size,size,
                age<220?mix(0xdd3948,0xf5f5f5,unit((age-130)/90f)):0xf5f5f5,reveal*(1-exit));
        threeSlice(g,HOLD,left,width,0xffffff,fade);
        float heat=1-unit(age/260f);
        if(heat>0)threeSlice(g,ENTRY,left,width,0xffffff,heat*fade);
        // WideLines crosses the backer; the narrower gradient grid remains after it leaves.
        float travel=out((age-70)/340f);
        float flash=unit(age/80f)*(1-unit((age-80)/180f));
        img(g,WIDE,left-185+285*travel,-22,392-198*travel,44,0xff5a6c,.92f*flash);
        int rail=age<130?mix(0xd51935,0xff6e79,age/130f):mix(0xff6e79,0xe12843,unit((age-260)/130f));
        img(g,WHITE,left,-22,4,44,rail,fade);
        if(age>=85 && age<1650){
            float textIn=out((age-85)/180f);
            float flicker=age>=228 && age<283 && (age/12)%2==0?.4f:1;
            title.draw(g,left+18*textIn,-1,28,mix(0xff9da2,0xfffbfa,textIn),fade*flicker);
        }
        g.flush();
    }

    private static int mix(int a,int b,float t){
        int r=Math.round((a>>16&255)*(1-t)+(b>>16&255)*t);
        int g=Math.round((a>>8&255)*(1-t)+(b>>8&255)*t);
        return r<<16|g<<8|Math.round((a&255)*(1-t)+(b&255)*t);
    }
}
