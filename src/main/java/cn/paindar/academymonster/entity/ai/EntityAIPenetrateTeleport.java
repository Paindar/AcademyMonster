package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAIPenetrateTeleport  extends EntityAIBase
{
    EntityLiving speller;
    AIPenetrateTeleport skill;
    EntityLivingBase target;

    public EntityAIPenetrateTeleport(EntityLiving zombie,AIPenetrateTeleport skill)
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
        return this.speller.getAttackTarget() != null && !skill.isSkillInCooldown();
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

    private boolean hasPlace(World world ,double  x,double y,double z)
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
        dist=dist>skill.getMaxDistance()?skill.getMaxDistance():dist;
        if(target!=null && !skill.isSkillInCooldown() && dist >=0.5)
        {

            double dx= (target.posX-speller.posX)/distBtwEntitys,
                    dy=(target.posY-speller.posY)/distBtwEntitys,
                    dz=(target.posZ-speller.posZ)/distBtwEntitys;
            World world=speller.worldObj;
            for(double d=dist;d>0;d-=1)
            {
                double x = speller.posX + dx * d;
                double y = speller.posY + dy * d;
                double z = speller.posZ + dz * d;
                if(hasPlace(world,x,y,z))
                {
                    this.skill.spell(x,y,z);
                    speller.getNavigator().clearPathEntity();
                    break;
                }
                else if(hasPlace(world,x,y+1,z))
                {
                    this.skill.spell(x,y+1,z);
                    break;
                }
            }
        }

    }
}
