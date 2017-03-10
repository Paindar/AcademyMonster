package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.AIFlashing;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

/**
 * Created by voidcl on 2017/3/10.
 */
public class EntityAIFlashing extends EntityAIBase{

    EntityLiving speller;
    AIFlashing skill;
    EntityLivingBase target;

    public EntityAIFlashing(EntityLiving zombie,AIFlashing skill)
    {
        speller=zombie;
        this.skill=skill;
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {

        //&& this.speller.getDistanceSqToEntity(this.speller.getAttackTarget())<=skill.getMaxDistance()
        return this.speller.getAttackTarget() != null && !skill.isSkillInCooldown() && this.speller.getAttackTarget().isEntityAlive();
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

    private boolean hasPlace(World world , double  x, double y, double z)
    {
        int ix=(int)x,iy=(int)y,iz=(int)z;
        Block b1 = world.getBlock(ix, iy, iz);
        Block b2  = world.getBlock(ix, iy + 1, iz);
        return !b1.canCollideCheck(world.getBlockMetadata(ix, iy, iz), false) && !b2.canCollideCheck(world.getBlockMetadata(ix, iy + 1, iz), false);
    }

    public void updateTask()
    {
        double dist=Math.sqrt(this.speller.getDistanceSqToEntity(target));
        double distBtwEntitys=dist;
        dist=skill.getMaxDistance();
        if(target!=null && skill.available()&&distBtwEntitys>=3)
        {

            double dx= (target.posX-speller.posX)/distBtwEntitys,
                    dy=(target.posY-speller.posY)/distBtwEntitys,
                    dz=(target.posZ-speller.posZ)/distBtwEntitys;
            World world=speller.worldObj;
            double x = speller.posX + dx * dist;
            double y = speller.posY + dy * dist;
            double z = speller.posZ + dz * dist;

            if(hasPlace(world,x+1+RandUtils.nextInt(1),y+1,z+1+RandUtils.nextInt(1)))
            {
                this.skill.spell(x,y,z);
                speller.getNavigator().clearPathEntity();

            }


        }

    }
}
