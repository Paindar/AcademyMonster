package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/5/14.
 */
public class EntityAIWander extends EntityAIBaseX
{
    public EntityAIWander(EntityLiving owner)
    {
        super(owner);
    }

    @Override
    public boolean execute()
    {
        EntityLivingBase target=(owner.getAttackTarget()==null)?null:owner.getAttackTarget();
        target=(owner.getAITarget()==null)?target:owner.getAITarget();
        if(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode)
        {
            target=null;
        }
        if(target!=null)
        {
            if(owner.getDistanceSqToEntity(target)>225)
            {
                ieep.setAI(new EntityAIChasing(owner,target,30));
                return false;
            }
            else
            {
                //if it have Mc AI, keep it empty here.
            }
        }
        return true;
    }
}
