package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/5/13.
 */
public abstract class EntityAIBaseX
{
    EntityAIBaseX()
    {
    }

    public abstract boolean execute(EntityLivingBase owner);

    boolean isTargetInHorizon(EntityLivingBase owner, EntityLivingBase target)
    {
        Vec3 lookingPos=owner.getLookVec(),direct=Vec3.createVectorHelper(target.posX-owner.posX,target.posY-owner.posY,target.posZ-owner.posZ).normalize();

        lookingPos=lookingPos.normalize();
        MovingObjectPosition trace = Raytrace.rayTraceBlocks(owner.worldObj,
                Vec3.createVectorHelper(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ),
                Vec3.createVectorHelper(target.posX,target.posY+target.getEyeHeight(),target.posZ), BlockSelectors.filNothing
        );
        return (lookingPos.xCoord*direct.xCoord+lookingPos.zCoord*direct.zCoord>=0.5)&&(trace==null || trace.typeOfHit!= MovingObjectPosition.MovingObjectType.BLOCK);
    }

    boolean isTargetInHorizonIgnoreBlock(EntityLivingBase owner, EntityLivingBase target)
    {
        Vec3 lookingPos=owner.getLookVec().normalize(),direct=Vec3.createVectorHelper(target.posX-owner.posX,target.posY-owner.posY,target.posZ-owner.posZ).normalize();
        return (lookingPos.xCoord*direct.xCoord+lookingPos.zCoord*direct.zCoord>=0.5);
    }
}
