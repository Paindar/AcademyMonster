package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIGroundShock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;

/**
 * Created by voidcl on 2017/3/20.
 */
public class EntityAIGroundShock extends EntityAIBase{
    private EntityLiving speller;
    private AIGroundShock skill;
    private EntityLivingBase target;

    public EntityAIGroundShock(EntityLiving speller,AIGroundShock skill)
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
        if(skill.hasPlace((int)target.posX,(int)(target.posY-1),(int) target.posZ))
            return false;
        return !skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
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
            MovingObjectPosition trace = Raytrace.traceLiving(speller, skill.getMaxDistance(), EntitySelectors.living());
            if(!skill.isSkillInCooldown()&&(trace!=null))
                skill.spell();
        }
    }
}
