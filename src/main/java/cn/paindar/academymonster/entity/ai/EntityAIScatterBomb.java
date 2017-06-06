package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIScatterBomb;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/3/12.
 */
public class EntityAIScatterBomb extends EntityAIBaseX
{
    private EntityLivingBase target;
    private AIScatterBomb skill;

    EntityAIScatterBomb(EntityLiving owner,EntityLivingBase target,AIScatterBomb skill)
    {
        super(owner);
        this.target=target;
        this.skill=skill;
    }

    @Override
    public boolean execute()
    {
        if(skill.isSkillInCooldown())
        {
            ieep.setAI(new EntityAIRange(owner,target));
        }
        if(target!=null)
        {
            if(target.isDead)
            {
                if(skill.isChanting())
                {
                    skill.stop();
                }
                ieep.setAI(new EntityAIWander(owner));
            }
            if(!skill.isChanting())
            {
                skill.spell();
            }
            else
            {
                double range = owner.getDistanceSqToEntity(target);
                if (range <= skill.getMaxDistance() * skill.getMaxDistance())
                {
                    if(skill.getBallSize()>=7)
                    {
                        skill.stop();
                        ieep.setAI(new EntityAIRange(owner,target));
                    }
                }
            }
        }
        else
        {
            ieep.setAI(new EntityAIWander(owner));
            return false;
        }
        return true;
    }
}
