package cn.paindar.academymonster.ability.event;

import cn.paindar.academymonster.core.AcademyMonster;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

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
    public void onEntityJoinedWorld(EntityJoinWorldEvent event)
    {
        if(event.entity instanceof EntityLiving)
        {
            String savedSkills=event.entity.getEntityData().getString(AcademyMonster.MODID);
            if(savedSkills.equals(""))
            {
                AcademyMonster.instance.addSkill((EntityLiving)event.entity);
            }


        }


    }
}
