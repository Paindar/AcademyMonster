package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIBodyIntensify;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/2/10.
 */
public class EntityAIBodyIntensify extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIBodyIntensify skill;

    public EntityAIBodyIntensify(EntityLiving speller,AIBodyIntensify skill)
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
        return speller.getAttackTarget()!=null && !skill.isSkillInCooldown()||(target instanceof EntityPlayer && !((EntityPlayer)target).capabilities.isCreativeMode);
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
    public void updateTask()
    {
        if (target!=null && !skill.isSkillInCooldown())
        {
            skill.spell();

            if(speller instanceof EntityCreeper)
            {
                speller.getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
            }
        }
    }
}
