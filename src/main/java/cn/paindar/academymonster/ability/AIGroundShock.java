package cn.paindar.academymonster.ability;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.api.event.AMSpecialExplosion;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;


import static cn.lambdalib.util.generic.MathUtils.lerpf;
/**
 * Created by voidcl on 2017/3/20.
 */
public class AIGroundShock extends BaseSkill{
    private final float maxDistance=5;
    private float damage;
    private final int flyHeight=5;
    private int headDamage=0;
    public AIGroundShock(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(60,20,exp),exp,"vecmanip.ground_shock");
        damage=lerpf(2,8,exp);
    }

    public void fly(EntityLivingBase target)
    {
        int x,y,z;
        headDamage=0;
        x=(int)target.posX;y=(int)target.posY;z=(int)target.posZ;
        int currFlyHeight=flyHeight;
        for(int curr=2;curr<flyHeight;curr++)
        {

            if(hasPlace(x,y+curr,z))
            {
                continue;
            }else
            {
                headDamage=flyHeight-curr;
                currFlyHeight=curr-1;
            }
        }
        target.setPositionAndUpdate(x,currFlyHeight+y,z);
        attack(target,headDamage);


    }

   // public float getHeadDamage(){return headDamage;}
    public float getMaxDistance()
    {
        return maxDistance;
    }

    public boolean hasPlace(int x,int y,int z)
    {
        World world=speller.worldObj;
        Block b1=world.getBlock(x,y,z);
        Block b2=world.getBlock(x,y+1,z);
        if(b2.isCollidable())
            headDamage++;
        return !b1.canCollideCheck(world.getBlockMetadata(x, y, z), false) && !b2.canCollideCheck(world.getBlockMetadata(x, y + 1, z), false);


    }

    public void destroyBlock(Entity target)
    {
        World world=speller.worldObj;
       // world.newExplosion(target,target.posX,target.posY-1,target.posZ,0.8f,false,true);
        AMSpecialExplosion explosion=new AMSpecialExplosion(world,target,target.posX,target.posY,target.posZ,0.8f);
        explosion.doExplosionA();
        explosion.SpecialDoExplosionB(true);
        //System.out.println("dd");
        //world.createExplosion(target,target.posX,target.posY-1,target.posZ,0.8f,true);

    }

    public void spell()
    {
        MovingObjectPosition trace= Raytrace.traceLiving(speller,maxDistance, EntitySelectors.living());
        EntityLivingBase target=null;
        if(trace!=null&&trace.typeOfHit==MovingObjectPosition.MovingObjectType.ENTITY)
        {
            target=(EntityLivingBase)trace.entityHit;
        }
        if(target!=null&&!isSkillInCooldown())
        {

            attack(target,damage);
            destroyBlock(target);
            fly(target);
            super.spell();
        }else
        {
            return ;
        }


    }
}
