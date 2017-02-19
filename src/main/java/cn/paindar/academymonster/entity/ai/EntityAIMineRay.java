package cn.paindar.academymonster.entity.ai;

import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIMineRay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Paindar on 2017/2/19.
 */
public class EntityAIMineRay extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIMineRay skill;

    public EntityAIMineRay(EntityLiving speller,AIMineRay skill)
    {
        this.speller=speller;
        this.skill=skill;
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase target=speller.getAttackTarget();
        return target!=null&&!skill.isSkillInCooldown()&&speller.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.target =this.speller.getAttackTarget();
    }

    public void resetTask()
    {
        if(skill.isChanting())
            skill.stop();
        this.target = null;
    }

    public void updateTask(){
        if (target!=null )
        {
            MovingObjectPosition trace = Raytrace.traceLiving(speller,skill.getMaxDistance(), EntitySelectors.nothing());
            if(trace==null)
                return ;
            Block block=speller.worldObj.getBlock(trace.blockX,trace.blockY,trace.blockZ);
            if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(speller.worldObj, trace.blockX,trace.blockY,trace.blockZ))&&!skill.isChanting())
            {
                skill.spell();
            }
            else
            if(skill.isChanting())skill.stop();
        }
    }
}
