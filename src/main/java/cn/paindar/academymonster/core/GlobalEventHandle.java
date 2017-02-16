package cn.paindar.academymonster.core;

import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
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
    public void onEntityJoinedWorld(EntityJoinWorldEvent  event)
    {
        if(!event.world.isRemote && event.entity instanceof EntityLiving)
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(event.entity);
            String savedSkills=data.getSkillData();
            if(savedSkills.equals(""))
            {
                AcademyMonster.instance.addSkill((EntityLiving)event.entity);
            }
            else
            {
                //AcademyMonster.log.info("entity " + event.entity + " have skills:" + savedSkills);
                AcademyMonster.instance.refreshSkills((EntityLiving)event.entity,savedSkills);
            }
            EntityTracker tracker = ((WorldServer)event.world).getEntityTracker();

            for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(event.entity)) {
                NetworkManager.sendEntitySkillInfoTo((EntityLiving)event.entity, (EntityPlayerMP)entityPlayer);
                AcademyMonster.log.info(" send "+event.entity+" to "+entityPlayer);
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

}
