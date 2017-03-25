package cn.paindar.academymonster.core.support.terminal.ui;

import cn.lambdalib.cgui.gui.CGui;
import cn.lambdalib.cgui.gui.Widget;
import cn.lambdalib.cgui.gui.WidgetContainer;
import cn.lambdalib.cgui.gui.component.DrawTexture;
import cn.lambdalib.cgui.gui.component.TextBox;
import cn.lambdalib.util.client.auxgui.AuxGui;
import cn.lambdalib.util.client.auxgui.AuxGuiHandler;
import cn.lambdalib.util.client.font.IFont;
import cn.lambdalib.util.helper.Color;
import cn.lambdalib.util.helper.GameTimer;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Paindar on 2017/3/8.
 */
@SideOnly(Side.CLIENT)
public class BossHealthBar extends AuxGui
{
    private static int ticks=0;
    private static float flushRange=20f;
    private static Minecraft mc;
    private static WidgetContainer loaded ;
    private static List<Widget> existedList=new ArrayList<>();
    private static AuxGui current = null;
    private static List<IBossDisplayData> list=new ArrayList<>();
    private static Widget healWidget;

    public static void preInit()
    {
        mc = FMLClientHandler.instance().getClient();
        loaded=new WidgetContainer();
        Widget board=new Widget().size(1200,400);
        loaded.addWidget("backBroad",board,true);
        Widget skillList=new Widget(350,-480,1000,125);
        healWidget=new Widget(900,133) .addComponent(new TextBox(
                new IFont.FontOption(50, IFont.FontAlign.CENTER,new Color(1,0,0,0.8))).setContent("A Test Skill"));

        Widget healBar= new Widget(900,100).pos(30,60).addComponent(new TextBox(new IFont.FontOption(40, IFont.FontAlign.CENTER,new Color(1,1,1,0.6))).setContent("A Test Health"));
        Widget barTextures=new Widget(900,53).pos(30,22).addComponent(new DrawHealBar(new ResourceLocation("academymonster:textures/gui/hp_bar.png")).setColor(new Color(1,1,1,0.6)));
        healBar.addWidget("textures",barTextures);
        healWidget.addWidget("heal_bar",healBar);
        skillList.addWidget("skillItem",healWidget);
        board.addWidget("skill",skillList);
    }

    CGui gui;
    Widget root;

    BossHealthBar()
    {
        gui = new CGui();
        gui.addWidget(root = loaded.getWidget("backBroad").copy());
        requireTicking=true;

        initGui();
    }

    private void initGui()
    {
        Widget skillPart=root.getWidget("skill");
        skillPart.removeWidget("skillItem");
    }

    @Override
    public void tick()
    {
        EntityClientPlayerMP player=mc.thePlayer;
        if(player == null || list == null)
            return ;
        for(Widget widget:existedList)
        {
            widget.dispose();
        }

        existedList.clear();
        Widget widget = root.getWidget("skill");
        int i=0;
        for(IBossDisplayData entity:list)
        {
            Widget w=healWidget.copy().pos(30,30+130*i);
            w.getComponent(TextBox.class).setContent(((Entity)entity).getCommandSenderName());
            float per=entity.getHealth()/entity.getMaxHealth();
            Widget healBar=w.getWidget("heal_bar");
            healBar.getComponent(TextBox.class).setContent(String.format("%.2f/%.2f",entity.getHealth(),entity.getMaxHealth()));
            healBar.getWidget("textures").getComponent(DrawHealBar.class).setDrawLen(per);
            i++;
            existedList.add(w);
            widget.addWidget(w);
        }

    }

    private static void createNewHealthBar(List<Entity> list)
    {
        for(Widget widget:existedList)
            widget.dispose();
    }

    public static void flushHealthBar(RenderGameOverlayEvent.Pre event)
    {
        List<Entity> tmpList=WorldUtils.getEntities(mc.thePlayer, flushRange, (Entity entity) -> (!entity.isDead && entity instanceof IBossDisplayData));
        list.clear();
        for(Entity entity:tmpList)
        {
            if(entity instanceof IBossDisplayData)
            {
                list.add((IBossDisplayData)entity);
            }
        }
        if (current == null && list.size() != 0)
        {
            current = new BossHealthBar();
            AuxGuiHandler.register(current);
        }
        else if (current != null && list.size() == 0)
        {
            current.dispose();
            current=null;
        }

    }

    @Override
    public boolean isForeground()
    {
        return false;
    }

    @Override
    public void draw(ScaledResolution scaledResolution)
    {
        float aspect = (float)mc.displayWidth / mc.displayHeight;

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GLU.gluPerspective(50,
                aspect,
                1f, 100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4d(1, 1, 1, 1);

        double scale = 1.0 / 310;

        GL11.glTranslated(-3, .5, -4);

        //GL11.glTranslated(1, -1.8, 0);

        GL11.glScaled(scale, -scale, scale);
        gui.draw();

        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glCullFace(GL11.GL_BACK);
    }
}
