package cn.paindar.academymonster.entity.ai;

import cn.paindar.academymonster.ability.AIElectronMissile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Paindar on 2017/3/12.
 */
public class EntityAIElectronMissile extends EntityAIBase
{
    private final EntityLiving speller;
    private EntityLivingBase target;
    private AIElectronMissile skill;

    public EntityAIElectronMissile(EntityLiving speller,AIElectronMissile skill)
    {
        this.speller=speller;
        this.skill=skill;
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase target=speller.getAttackTarget();
        if (target==null|| !skill.available()||(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode))
            return false;
        double dist=speller.getDistanceSqToEntity(target);
        return this.speller.getAttackTarget().isEntityAlive() && skill.available() && dist >= 2.25 && dist <= skill.getMaxDistance() * skill.getMaxDistance();
    }

    public void startExecuting()
    {
        skill.spell();
    }

}
