package cn.paindar.academymonster.entity.ai;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.AILocationTeleport;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.block.Block;
import net.minecraft.world.World;


/**
 * Created by voidcl on 2017/3/20.
 */
public class EntityAILocationTeleport extends EntityAIBase {
    EntityLiving speller;
    AILocationTeleport skill;
    EntityLivingBase target;

    public EntityAILocationTeleport(EntityLiving speller,AILocationTeleport skill)
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
        if(target==null)
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        World world=speller.worldObj;
        Block b=world.getBlock((int)target.posX,(int)target.posY,(int) target.posZ);
        if(b.canCollideCheck(world.getBlockMetadata((int)target.posX,(int)target.posY,(int) target.posZ),false))
        {
            return false;
        }
        return !skill.isSkillInCooldown()&&dist<=3;

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
        if(target!=null)
        {
            MovingObjectPosition trace= Raytrace.traceLiving(speller,2.5f);
            if(!skill.isSkillInCooldown()&&(trace!=null))
                skill.spell(target);
        }
    }

}
