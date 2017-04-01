package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AIGroundShock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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

        if (target==null||skill.isSkillInCooldown()||(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode))
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return speller.onGround&&!skill.isSkillInCooldown() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
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


    private double vecMuiltply(Vec3 a,Vec3 b)
    {
        return a.xCoord*b.xCoord+a.yCoord*b.yCoord+a.zCoord*b.zCoord;
    }
    public void updateTask()
    {
        if (target!=null )
        {
            MovingObjectPosition trace = Raytrace.traceLiving(speller, skill.getMaxDistance(), EntitySelectors.living());
            Vec3 lookPos=speller.getLookVec().normalize(),
                    locPos=Vec3.createVectorHelper(target.posX-speller.posX,target.posY-speller.posY,target.posZ-speller.posZ).normalize();
            if(!skill.isSkillInCooldown()&&(trace!=null)&& vecMuiltply(lookPos,locPos)>=0.866)
            {

                skill.spell();
            }
        }
    }
}
