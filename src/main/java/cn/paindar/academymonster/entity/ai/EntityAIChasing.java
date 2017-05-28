package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIBodyIntensify;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

import java.lang.annotation.Target;

/**
 * Created by Paindar on 2017/5/14.
 */
public class EntityAIChasing extends EntityAIBaseX
{
    EntityLivingBase target;
    float dist;
    EntityAIChasing(EntityLiving owner,EntityLivingBase target,float dst)
    {
        super(owner);
        this.target=target;
        this.dist=dst;
    }

    @Override
    public boolean execute()
    {
        double imaDist=owner.getDistanceSqToEntity(target);
        if(target==null || target.isDead ||dist*dist<imaDist)
        {
            ieep.setAI(new EntityAIWander(owner));
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
                ieep.setAI(new EntityAIPenetrateTeleport(owner,target, (AIPenetrateTeleport) skill));
                break;
            }
        }
        if(imaDist<=9)
            ieep.setAI(new EntityAIMelee(owner,target));
        else if(imaDist<=400)
            ieep.setAI(new EntityAIRange(owner,target));
        else
            return true;
        return false;
    }
}
