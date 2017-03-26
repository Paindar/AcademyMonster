package cn.paindar.academymonster.entity.ai;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.EntityLiving;

/**
 * Created by Paindar on 2017/3/26.
 * Used to control all the skills Mob has, and
 */
public class EntitySkillAICommon
{
    private EntityLiving owner;
    public EntitySkillAICommon(EntityLiving owner)
    {
        this.owner=owner;
        FMLCommonHandler.instance().bus().register(this);
    }

    protected void action()
    {
        if(owner.getAITarget()!=null && owner.getAttackTarget()==null)
            owner.setAttackTarget(owner.getAITarget());
        owner.tasks.onUpdateTasks();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if(owner==null ||owner.isDead)
            FMLCommonHandler.instance().bus().unregister(this);
        else
            action();
    }
}
