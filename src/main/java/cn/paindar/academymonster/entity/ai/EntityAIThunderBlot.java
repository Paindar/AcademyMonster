package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIThunderBolt;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;

/**
 * Created by Paindar on 2017/3/7.
 */
public class EntityAIThunderBlot extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIThunderBolt skill;

    public EntityAIThunderBlot(EntityLiving speller,AIThunderBolt skill)
    {
        this.speller=speller;
        this.skill=skill;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null||skill.isSkillInCooldown())
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
    }


    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.target =this.speller.getAttackTarget();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.target = null;
    }

    /**
     * Update the task.
     */
    public void updateTask(){
        if (target!=null ) {
            MovingObjectPosition trace = Raytrace.traceLiving(speller,skill.getMaxDistance(), EntitySelectors.living(), BlockSelectors.filNothing);
            if (trace != null && trace.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
            {
                if(trace.entityHit==target)
                {
                    skill.spell();
                }
            }
        }
    }
}
