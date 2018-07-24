package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIBodyIntensify;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/5/14.
 */
public class EntityAIChasing extends EntityAIBaseX
{
    EntityLivingBase target;
    float dist;
    EntityAIChasing(EntityLivingBase target,float dst)
    {
        super();
        this.target=target;
        this.dist=dst;
    }

    @Override
    public boolean execute(EntityLivingBase owner)
    {
        double imaDist=owner.getDistanceSqToEntity(target);
        SkillExtendedEntityProperties ieep = SkillExtendedEntityProperties.get(owner);
        if(target==null || target.isDead ||dist*dist<imaDist||(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode))
        {
            ieep.setAI(new EntityAIWander());
            return false;
        }
        //may it can add Vector Accelerate or other
        for(BaseSkill skill:ieep.list)
        {
            if(skill instanceof AIBodyIntensify && skill.canSpell())
            {
                ((AIBodyIntensify)skill).spell();
                break;
            }
            else if(skill instanceof AIPenetrateTeleport && skill.canSpell())
            {
                ieep.setAI(new EntityAIPenetrateTeleport(target, (AIPenetrateTeleport) skill));
                break;
            }
        }
        if(imaDist<=9)
            ieep.setAI(new EntityAIMelee(target));
        else if(imaDist<=400)
            ieep.setAI(new EntityAIRange(target));
        else
            return true;
        return false;
    }
}
