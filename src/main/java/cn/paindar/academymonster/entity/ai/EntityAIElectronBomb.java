package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIElectronBomb;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/10.
 */
public class EntityAIElectronBomb extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIElectronBomb skill;

    public EntityAIElectronBomb(EntityLiving speller,AIElectronBomb skill)
    {
        this.speller=speller;
        this.skill=skill;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null)
            return false;
        double dist=speller.getDistanceSqToEntity(target);
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

    /**
     * Update the task.
     */
    public void updateTask(){
        if (target!=null ) {
            MovingObjectPosition trace = Raytrace.rayTraceBlocks(speller.worldObj,
                    Vec3.createVectorHelper(speller.posX, speller.posY + speller.getEyeHeight(), speller.posZ),
                    Vec3.createVectorHelper(target.posX,target.posY+target.getEyeHeight(),target.posZ), BlockSelectors.filNothing
            );
            if(trace==null || trace.typeOfHit!= MovingObjectPosition.MovingObjectType.BLOCK)
                skill.spell();
        }
    }
}
