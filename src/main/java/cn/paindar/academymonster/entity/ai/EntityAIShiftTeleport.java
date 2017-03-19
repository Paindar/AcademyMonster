package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIShiftTeleport;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;


/**
 * Created by voidcl on 2017/3/19.
 */
public class EntityAIShiftTeleport extends EntityAIBase {
    private EntityLiving speller;
    private AIShiftTeleport skill;
    private EntityLivingBase target;

    public EntityAIShiftTeleport(EntityLiving speller,AIShiftTeleport skill)
    {
        this.speller=speller;
        this.skill=skill;
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null||skill.isSkillInCooldown())
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxdistance() * skill.getMaxdistance();
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



    public void updateTask()
    {
        if (target!=null )
        {
            MovingObjectPosition trace = Raytrace.traceLiving(speller, skill.getMaxdistance(), EntitySelectors.living());
            if(!skill.isSkillInCooldown()&&(trace!=null))
                skill.spell();
        }
    }
}
