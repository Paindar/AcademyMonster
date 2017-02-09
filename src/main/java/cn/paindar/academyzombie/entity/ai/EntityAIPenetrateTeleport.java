package cn.paindar.academyzombie.entity.ai;

import cn.paindar.academyzombie.ability.AIPenetrateTeleport;
import cn.paindar.academyzombie.ability.BaseAbility;
import cn.paindar.academyzombie.entity.EntityAcademyZombie;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAIPenetrateTeleport  extends EntityAIBase
{
    EntityAcademyZombie speller;
    AIPenetrateTeleport skill;
    EntityLivingBase target;

    public EntityAIPenetrateTeleport(EntityAcademyZombie zombie)
    {
        speller=zombie;
        for(BaseAbility ability:speller.abilityList)
        {
            if(ability instanceof AIPenetrateTeleport)
            {
                skill = (AIPenetrateTeleport) ability;
                break;
            }
        }
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {

        if(this.speller.getAttackTarget()!=null && skill.isSkillInCooldown() && this.speller.getDistanceSqToEntity(this.speller.getAttackTarget())<=skill.getMaxDistance())
            return true;
        return false;
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
     * Updates the task
     */
    private float getDepth(double x,double y,double z)
    {
        for(int i=0;i<4;i++)
        {
            Block block=speller.worldObj.getBlock((int)x,(int)y-i,(int)z);
            if(y-i<=0)
                break;
            if(!block.isAir(speller.worldObj,(int)x,(int)y-i,(int)z))
                return i;
        }
        return 25565;
    }

    public void updateTask()
    {
        double dist=this.speller.getDistanceSqToEntity(target);
        if(target!=null && skill.isSkillInCooldown() && dist<=skill.getMaxDistance() && dist >=0.5)
        {

            double dx= (target.posX-speller.posX)/dist,
                    dy=(target.posY-speller.posY)/dist,
                    dz=(target.posZ-speller.posZ)/dist;
            World world=speller.worldObj;
            for(double d=1;d<dist;d+=1)
            {
                double x=target.posX-dx*d,
                        y=target.posY-dy*d,
                        z=target.posZ-dz*d;
                if(world.getBlock((int)x,(int)y,(int)z).getMaterial()== Material.air
                        && world.getBlock((int)x,(int)y+1,(int)z).getMaterial()== Material.air
                        && getDepth(x,y,z)<3)
                {
                    this.skill.spell(speller,x,y,z);
                    break;
                }
            }

        }

    }
}
