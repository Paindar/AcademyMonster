package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AILightShield;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by Paindar on 2017/2/26.
 */
public class EntityAILightShield extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AILightShield skill;

    public EntityAILightShield(EntityLiving speller,AILightShield skill)
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
        return target!=null&&skill.available();
    }

    @Override
    public void startExecuting()
    {
        this.target =this.speller.getAttackTarget();
    }

    @Override
    public void resetTask()
    {
        if(this.target.isDead)
        {
            this.target = null;
            skill.stop();
        }
    }

    public void updateTask(){
        if (target!=null )
        {
            double dstSq=speller.getDistanceSqToEntity(target);
            if(dstSq<=25 && skill.available())
            {
                skill.spell();
            }
        }
    }
}
