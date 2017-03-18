package cn.paindar.academymonster.core;

import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.core.support.terminal.ui.BossHealthBar;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

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
    public void onEntityConstruct(EntityEvent.EntityConstructing event)
    {
        SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(event.entity);
    }

    @SubscribeEvent
    public void onEntityJoinedWorld(EntityJoinWorldEvent  event)
    {
        if(!event.world.isRemote && event.entity instanceof EntityLiving && AcademyMonster.isClassAllowed((EntityLiving)event.entity))
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(event.entity);
            String savedSkills=data.getSkillData();
            if(!(event.entity instanceof IBossDisplayData))
            {
                if(savedSkills.equals(""))
                {
                    AcademyMonster.instance.addSkill((EntityLiving)event.entity);
                }
                else
                {
                    //AcademyMonster.log.info("entity " + event.entity + " have skills:" + savedSkills);
                    AcademyMonster.instance.refreshSkills((EntityLiving)event.entity,savedSkills);
                }
            }
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
