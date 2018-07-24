package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.AIFlashing;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static java.lang.Math.sqrt;

/**
 * Created by voidcl on 2017/3/10.
 */
@Deprecated
public class EntityAIFlashing extends EntityAIBaseX
{
    AIFlashing skill;
    EntityLivingBase target;

    public EntityAIFlashing(EntityLivingBase target,AIFlashing skill)
    {
        super();
        this.target=target;
        this.skill=skill;
    }

    private boolean hasPlace(World world , double  x, double y, double z)
    {
        int ix=(int)x,iy=(int)y,iz=(int)z;
        Block b1 = world.getBlock(ix, iy, iz);
        Block b2  = world.getBlock(ix, iy + 1, iz);
        return !b1.canCollideCheck(world.getBlockMetadata(ix, iy, iz), false) && !b2.canCollideCheck(world.getBlockMetadata(ix, iy + 1, iz), false);
    }

    @Override
    public boolean execute(EntityLivingBase owner)
    {
        SkillExtendedEntityProperties ieep=SkillExtendedEntityProperties.get(owner);
        if(skill.isSkillInCooldown())
        {
            ieep.setAI(new EntityAIWander());
            if(owner instanceof EntityLiving)
                ((EntityLiving)owner).getNavigator().clearPathEntity();
        }
        if(target!=null)
        {
            if(target.isDead)
            {
                ieep.setAI(new EntityAIWander());
            }
            if(!skill.isChanting())
            {
                throw new RuntimeException();
            }
            else
            {
                double dist=sqrt(owner.getDistanceSqToEntity(target));
                double distBtwEntities=dist;
                dist=skill.getMaxDistance();
                if(target!=null && skill.available()&&distBtwEntities>=3)
                {

                    double dx= (target.posX-owner.posX)/distBtwEntities,
                            dy=(target.posY-owner.posY)/distBtwEntities,
                            dz=(target.posZ-owner.posZ)/distBtwEntities;
                    World world=owner.worldObj;
                    double x = owner.posX + dx * dist;
                    double y = owner.posY + dy * dist;
                    double z = owner.posZ + dz * dist;

                    if(hasPlace(world,x+1+RandUtils.nextInt(1),y+1,z+1+RandUtils.nextInt(1)))
                    {
                        this.skill.spell(x,y,z);
                    }


                }
            }
        }
        else
        {
            ieep.setAI(new EntityAIWander());
            return false;
        }
        return true;
    }
}
