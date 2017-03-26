package cn.paindar.academymonster.core;

import cn.paindar.academymonster.core.support.terminal.ui.BossHealthBar;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.EntitySkillAICommon;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.lang.reflect.Method;

/**
 * Created by Paindar on 2017/2/12.
 * A test class used to spawn AcademyEntity.
 */
public class GlobalEventHandle
{
    public GlobalEventHandle()
    {
    }


    @SubscribeEvent
    public void onEntityJoinedWorld(EntityJoinWorldEvent  event)
    {
        if(!event.world.isRemote && event.entity instanceof EntityLiving && AcademyMonster.isClassAllowed((EntityLiving)event.entity))
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(event.entity);
            String savedSkills=data.getSkillData();
            if(savedSkills.isEmpty())
            {
                SkillManager.instance.addSkill((EntityLiving)event.entity);
            }
            data.init();
            boolean isAIEnabled;
            try
            {
                Method method=event.entity.getClass().getMethod("isAIEnabled");
                method.setAccessible(true);
                isAIEnabled=(Boolean) method.invoke(event.entity, (Object[]) null);
            } catch (Exception e)
            {
                isAIEnabled=false;
            }
            if(!isAIEnabled)
                new EntitySkillAICommon((EntityLiving)event.entity);

            EntityTracker tracker = ((WorldServer)event.world).getEntityTracker();
            for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(event.entity)) {
                NetworkManager.sendEntitySkillInfoTo((EntityLiving)event.entity, (EntityPlayerMP)entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void playerStartedTracking(PlayerEvent.StartTracking e) {
        if(e.target instanceof EntityLiving)
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(e.target);
            if (data != null)
            {
                NetworkManager.sendEntitySkillInfoTo((EntityLiving) e.target,(EntityPlayerMP) e.entityPlayer);
            }

        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event)
    {
        BossHealthBar.flushHealthBar(event);
    }

}
