package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIRailgun;
import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/12.
 */
public class EntityAIRailgun extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIRailgun skill;
    public EntityAIRailgun(EntityLiving speller,AIRailgun skill)
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
        if (target==null)
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return this.speller.getAttackTarget().isEntityAlive() && !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
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
