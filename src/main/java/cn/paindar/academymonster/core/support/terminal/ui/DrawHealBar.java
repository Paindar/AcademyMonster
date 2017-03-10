package cn.paindar.academymonster.core.support.terminal.ui;

import cn.lambdalib.cgui.gui.Widget;
import cn.lambdalib.cgui.gui.component.Component;
import cn.lambdalib.cgui.gui.event.FrameEvent;
import cn.lambdalib.util.client.HudUtils;
import cn.lambdalib.util.client.RenderUtils;
import cn.lambdalib.util.helper.Color;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**
 * Created by Paindar on 2017/3/10.
 */
public class DrawHealBar  extends Component
{

    public static final ResourceLocation MISSING = new ResourceLocation("lambdalib:textures/cgui/missing.png");
    public ResourceLocation texture;
    public Color color;
    public double zLevel;
    public boolean writeDepth;
    private float drawLen=1;
    private int shaderId;

    public DrawHealBar() {
        this(MISSING);
    }

    public DrawHealBar(ResourceLocation texture) {
        this(texture, Color.white());
    }

    public DrawHealBar(String name, ResourceLocation _texture, Color _color) {
        super(name);
        this.zLevel = 0.0D;
        this.writeDepth = true;
        this.shaderId = 0;
        this.texture = _texture;
        this.color = _color;
        this.listen(FrameEvent.class, (w, e) -> {
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3008);
            GL11.glDepthMask(this.writeDepth);
            GL20.glUseProgram(this.shaderId);
            this.color.bind();
            double preLevel = HudUtils.zLevel;
            HudUtils.zLevel = this.zLevel;
            if(this.texture != null && !this.texture.getResourcePath().equals("<null>")) {
                RenderUtils.loadTexture(this.texture);
                HudUtils.rawRect(0, 0,0,0, w.transform.width*drawLen, w.transform.height,drawLen,1);

            } else {
                HudUtils.colorRect(0.0D, 0.0D, w.transform.width, w.transform.height);
            }

            HudUtils.zLevel = preLevel;
            GL20.glUseProgram(0);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        });
    }


    public DrawHealBar(ResourceLocation _texture, Color _color) {
        this("DrawHealBar", _texture, _color);
    }

    public void setShaderId(int id) {
        this.shaderId = id;
    }

    public void setDrawLen(float len){drawLen=len;}

    public DrawHealBar setTex(ResourceLocation t) {
        this.texture = t;
        return this;
    }

    public DrawHealBar setColor(Color c) {
        this.color.from(c);
        return this;
    }

    public DrawHealBar setColor4i(int r, int g, int b, int a) {
        this.color.setColor4d((double)r / 255.0D, (double)g / 255.0D, (double)b / 255.0D, (double)a / 255.0D);
        return this;
    }

    public DrawHealBar setColor4d(double _r, double _g, double _b, double _a) {
        this.color.setColor4d(_r, _g, _b, _a);
        return this;
    }

    public static DrawHealBar get(Widget w) {
        return (DrawHealBar)w.getComponent("DrawHealBar");
    }
}

