package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/5/14.
 */
public class EntityAIWander extends EntityAIBaseX
{
    public EntityAIWander()
    {
        super();
    }

    @Override
    public boolean execute(EntityLivingBase owner)
    {

        EntityLivingBase target=null;
        if(owner instanceof EntityLiving)
            target = ((EntityLiving)owner).getAttackTarget();
        if(target==null)
        {
            if (owner instanceof EntityCreature && ((EntityCreature) owner).getEntityToAttack() instanceof EntityLivingBase)
                target = (EntityLivingBase) ((EntityCreature) owner).getEntityToAttack();
            else if (owner instanceof EntitySlime)
            {
                target = owner.worldObj.getClosestVulnerablePlayerToEntity(owner, 16.0D);
            }
        }

        if(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode)
        {
            target=null;
        }
        if(target!=null)
        {
            SkillExtendedEntityProperties ieep=SkillExtendedEntityProperties.get(owner);
            if(owner.getDistanceSqToEntity(target)>225)
            {
                ieep.setAI(new EntityAIChasing(target,30));
                return false;
            }
            else
            {
                ieep.setAI(new EntityAIRange(target));
                return false;
            }
        }
        return true;
    }
}
