package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIScatterBomb;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/3/12.
 */
public class EntityAIScatterBomb extends EntityAIBaseX
{
    private EntityLivingBase target;
    private AIScatterBomb skill;

    EntityAIScatterBomb(EntityLivingBase target,AIScatterBomb skill)
    {
        super();
        this.target=target;
        this.skill=skill;
    }

    @Override
    public boolean execute(EntityLivingBase owner)
    {
        SkillExtendedEntityProperties ieep= SkillExtendedEntityProperties.get(owner);
        if(skill.isSkillInCooldown())
        {
            ieep.setAI(new EntityAIRange(target));
        }
        if(target!=null)
        {
            if(target.isDead||(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode))
            {
                if(skill.isChanting())
                {
                    skill.stop();
                }
                ieep.setAI(new EntityAIWander());
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
                        ieep.setAI(new EntityAIRange(target));
                    }
                }
            }
        }
        else
        {
            ieep.setAI(new EntityAIWander());
            return false;
        }
        return true;
    }
}
