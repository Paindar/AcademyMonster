package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIMeltdowner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by Paindar on 2017/3/13.
 */
public class EntityAIMeltdowner extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIMeltdowner skill;

    public EntityAIMeltdowner(EntityLiving speller, AIMeltdowner skill)
    {
        this.speller = speller;
        this.skill = skill;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null)
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return this.speller.getAttackTarget().isEntityAlive() && !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance()/4;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.target = this.speller.getAttackTarget();
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
    public void updateTask()
    {
        if(!skill.isSkillInCooldown())
        {
            skill.spell();
        }
    }
}
