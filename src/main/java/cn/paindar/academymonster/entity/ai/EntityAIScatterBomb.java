package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIScatterBomb;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by Paindar on 2017/3/12.
 */
public class EntityAIScatterBomb extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIScatterBomb skill;

    public EntityAIScatterBomb(EntityLiving speller,AIScatterBomb skill)
    {
        this.speller=speller;
        this.skill=skill;
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null|| skill.isSkillInCooldown())
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return this.speller.getAttackTarget().isEntityAlive() && !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
    }

    public void startExecuting()
    {
        this.target =this.speller.getAttackTarget();
    }

    @Override
    public void updateTask()
    {
        if(target!=null)
        {
            if(!skill.isChanting())
            {
                skill.spell();
            }
            else
            {
                double range = speller.getDistanceSqToEntity(target);
                if (range <= skill.getMaxDistance() * skill.getMaxDistance())
                {
                    if(skill.getBallSize()>=7)
                    {
                        skill.spell();
                    }
                }
            }
        }
    }
}
