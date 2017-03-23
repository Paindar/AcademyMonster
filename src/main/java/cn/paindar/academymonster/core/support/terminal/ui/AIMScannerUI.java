package cn.paindar.academymonster.core.support.terminal.ui;

import cn.academy.ability.api.Skill;
import cn.academy.ability.api.data.AbilityData;
import cn.academy.core.ModuleCoreClient;
import cn.academy.terminal.TerminalData;
import cn.lambdalib.cgui.gui.CGui;
import cn.lambdalib.cgui.gui.Widget;
import cn.lambdalib.cgui.gui.WidgetContainer;
import cn.lambdalib.cgui.gui.component.Component;
import cn.lambdalib.cgui.gui.component.DrawTexture;
import cn.lambdalib.cgui.gui.component.TextBox;
import cn.lambdalib.cgui.xml.CGUIDocument;
import cn.lambdalib.util.client.auxgui.AuxGui;
import cn.lambdalib.util.client.auxgui.AuxGuiHandler;
import cn.lambdalib.util.client.font.IFont;
import cn.lambdalib.util.helper.Color;
import cn.lambdalib.util.helper.GameTimer;
import cn.lambdalib.util.key.KeyHandler;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.core.support.terminal.AppAIMScanner;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/2/13.
 */

@SideOnly(Side.CLIENT)
public class AIMScannerUI extends AuxGui
{
    private static final String OVERRIDE_GROUP = "AM_AIMScanner";
    private static AuxGui current = null;
    private static WidgetContainer loaded ;
    private Entity lastFocus=null;
    private Entity focus=null;
    private List<Widget> existedList=new ArrayList<>();
    private static Widget skillItem;

    public static KeyHandler keyHandler = new KeyHandler()
    {
        @Override
        public void onKeyUp()
        {
            EntityPlayer player = getPlayer();
            TerminalData tData = TerminalData.get(player);
            if(tData.isTerminalInstalled())
            {
                if(tData.getInstalledApps().indexOf(AppAIMScanner.instance)!=-1)
                {
                    if (current == null || current.isDisposed())
                    {
                        current = new AIMScannerUI();
                        AuxGuiHandler.register(current);
                    } else if (current instanceof AIMScannerUI)
                    {
                        current.dispose();
                        current = null;
                    }
                }
                else
                    player.addChatComponentMessage(new ChatComponentTranslation("am.aim_scanner_skill_list.notinstalled"));
            }

        }

    };

    public static void __init()
    {

//        loaded = CGUIDocument.panicRead(new ResourceLocation("academymonster:gui/aim_scanner_ui.xml"));
        loaded=new WidgetContainer();
        Widget board=new Widget().size(640,340);
        loaded.addWidget("backBroad",board,true);
        //.addComponent(new Transform().setSize(640,340).setPos(0,0).setAlign(Transform.WidthAlign.LEFT, Transform.HeightAlign.TOP))
        board.addComponent(new DrawTexture()
                .setTex(new ResourceLocation("academymonster:textures/gui/aim_scanner_back.png"))
                .setColor(new Color(1,1,1,0.5)));
        board.addWidget("monsterName",new Widget(30,30,640,100)
                .addComponent(new TextBox(new IFont.FontOption(45, IFont.FontAlign.CENTER,new Color(1,1,1,0.6))).setContent(""))
                );
        board.addWidget("Skill Label",new Widget(30,60,640,100)
                .addComponent(new TextBox(
                new IFont.FontOption(
                        35, IFont.FontAlign.LEFT,new Color(1,1,1,0.6)))
                .setContent("Skill Name")));
        Widget skillList=new Widget(30,100,640,340);
        skillList.addWidget("skillItem",new Widget(30,30,640,50) .addComponent(
                new TextBox(
                        new IFont.FontOption(40, IFont.FontAlign.LEFT,new Color(1,1,1,0.6))).setContent("A Test Skill")));

        board.addWidget("skill",skillList);
        try
        {
            CGUIDocument.write(loaded,new File("ui.xml"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        skillItem=skillList.getWidget("skillItem");
        ModuleCoreClient.keyManager.addKeyHandler("open_aim_scanner",Keyboard.KEY_H,keyHandler);
    }

    CGui gui;
    Widget root;


    long createTime;
    long lastFrameTime;


    public AIMScannerUI() {
        gui = new CGui();
        gui.addWidget(root = loaded.getWidget("backBroad").copy());
        requireTicking=true;

        initGui();
    }

    private String wrapTime(int val) {
        assert val >= 0 && val < 100;
        return val < 10 ? ("0" + val) : (String.valueOf(val));
    }


    private void initGui()
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        float aspect = (float)Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().displayHeight;
        createTime = GameTimer.getTime();
        {
            TextBox textBox = root.getWidget("Skill Label").getComponent(TextBox.class);
            textBox.setContent(StatCollector.translateToLocal("am.app.aim_scanner_skill_list.name"));
            Widget skillPart=root.getWidget("skill");
            skillPart.removeWidget("skillItem");
        }


//        TextBox.get(root.getWidget("text_username")).content = player.getCommandSenderName();

        // Obsolete stuff

//        updateAppList(data);

        createTime = GameTimer.getTime();
    }

    @Override
    public void tick()
    {
        EntityClientPlayerMP player=Minecraft.getMinecraft().thePlayer;
        if(Minecraft.getMinecraft().theWorld==null)
        {
            current.dispose();
            current=null;
            return ;
        }
        if(player == null)
            return ;
        MovingObjectPosition trace=Raytrace.traceLiving(player,20, EntitySelectors.living().and(EntitySelectors.exclude(player)));
        if(trace!=null)
        {
            focus=trace.entityHit;
        }
        else
        {
            focus=null;
        }
        DrawTexture bg=root.getComponent(DrawTexture.class);
        if(lastFocus!=focus)
        {
            while (!existedList.isEmpty())
            {
                existedList.get(0).dispose();
                existedList.remove(0);
            }
            if (focus == null)
            {
                root.getWidget("monsterName").getComponent(TextBox.class).setContent(StatCollector.translateToLocalFormatted("am.app.aim_scanner_no_focus.name"));
                bg.setColor4d(1,1,1,0.1);
            }
            else
            {
                bg.setColor4d(1,1,1,0.5);
                if( focus instanceof EntityPlayer)
                {
                    EntityPlayer tPlayer=(EntityPlayer) focus;
                    AbilityData data = AbilityData.get(tPlayer);
                    if(!data.hasCategory())
                    {
                        root.getWidget("monsterName").getComponent(TextBox.class).setContent(tPlayer.getDisplayName()+ " Level 0" );
                    }
                    else
                    {
                        root.getWidget("monsterName").getComponent(TextBox.class).setContent(tPlayer.getDisplayName()+" "+data.getCategory().getDisplayName() +" Level "+data.getLevel());
                        Widget list = root.getWidget("skill");
                        int num = 1;
                        for(Skill skill:data.getLearnedSkillList())
                        {

                            Widget widget = skillItem.copy().pos(30, 30 * num);
                            num++;
                            widget.getComponent(TextBox.class).setContent(StatCollector.translateToLocal(skill.getDisplayName()));
                            existedList.add(widget);
                            list.addWidget(widget);
                        }
                    }
                }
                else if(focus instanceof EntityLiving)
                {
                    root.getWidget("monsterName").getComponent(TextBox.class).setContent(focus.getCommandSenderName());
                    String skill = SkillExtendedEntityProperties.get(focus).getSkillData();
                    if (skill != null)
                    {
                        String[] skillName = skill.split("-");
                        Widget list = root.getWidget("skill");
                        int num = 1;
                        for (String item : skillName)
                        {
                            String[] data = item.split("~");

                            Widget widget = skillItem.copy().pos(30, 30 * num);
                            num++;
                            if (data.length == 2)
                            {
                                widget.getComponent(TextBox.class).setContent(StatCollector.translateToLocal(data[0]) );//+ String.format("(%.4f)", Float.parseFloat(data[1]))
                            } else if (data.length == 1)
                                widget.getComponent(TextBox.class).setContent(StatCollector.translateToLocal(data[0]));// + ("(0.00)")
                            else
                                continue;
                            existedList.add(widget);
                            list.addWidget(widget);
                        }
                    }
                }
            }
        }

    }

    /**
     * Judge if this GUI is a foreground GUI and interrupts key listening.
     */
    @Override
    public boolean isForeground()
    {
        return false;
    }

    @Override
    public void draw(ScaledResolution sr)
    {
        Minecraft mc = Minecraft.getMinecraft();
        long time = GameTimer.getTime();
        if(lastFrameTime == 0) lastFrameTime = time;
        long dt = time - lastFrameTime;
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
        {
            lastFocus=focus;
        }

        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glCullFace(GL11.GL_BACK);
    }
}
