package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAIPenetrateTeleport extends EntityAIBaseX
{
    AIPenetrateTeleport skill;
    EntityLivingBase target;

    public EntityAIPenetrateTeleport(EntityLivingBase tgt, AIPenetrateTeleport skill)
    {
        super();
        target=tgt;
        this.skill=skill;
    }

    private boolean hasPlace(World world ,double  x,double y,double z)
    {
        int ix=(int)x,iy=(int)y,iz=(int)z;
        Block b1 = world.getBlock(ix, iy, iz);
        Block b2  = world.getBlock(ix, iy + 1, iz);
        return !b1.canCollideCheck(world.getBlockMetadata(ix, iy, iz), false) && !b2.canCollideCheck(world.getBlockMetadata(ix, iy + 1, iz), false);
    }

    public boolean execute(EntityLivingBase owner)
    {
        double dist=Math.sqrt(owner.getDistanceSqToEntity(target));
        double distBtwEntitiess=dist;
        dist=dist>skill.getMaxDistance()?skill.getMaxDistance():dist;
        if(target!=null && !skill.isSkillInCooldown() && dist >=0.5)
        {

            double dx= (target.posX-owner.posX)/distBtwEntitiess,
                    dy=(target.posY-owner.posY)/distBtwEntitiess,
                    dz=(target.posZ-owner.posZ)/distBtwEntitiess;
            World world=owner.worldObj;
            for(double d=dist;d>0;d-=1)
            {
                double x = owner.posX + dx * d;
                double y = owner.posY + dy * d;
                double z = owner.posZ + dz * d;
                if(hasPlace(world,x,y,z))
                {
                    this.skill.spell(x,y,z);
                    if(owner instanceof EntityLiving)
                        ((EntityLiving)owner).getNavigator().clearPathEntity();
                    break;
                }
                else if(hasPlace(world,x,y+1,z))
                {
                    this.skill.spell(x,y+1,z);
                    break;
                }
            }
        }
        SkillExtendedEntityProperties.get(owner).setAI(new EntityAIChasing(target,40));
        return true;
    }
}
