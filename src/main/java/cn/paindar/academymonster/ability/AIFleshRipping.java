package cn.paindar.academymonster.ability;

import cn.academy.vanilla.teleporter.skill.FleshRipping;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/11.
 */
public class AIFleshRipping extends BaseAbility
{
    private float maxDistance;
    private float damage;
    public AIFleshRipping(EntityLivingBase speller, float abilityExp)
    {
        super(speller, (int)lerpf(90, 40, abilityExp), abilityExp);
        maxDistance=lerpf(6, 14, abilityExp);
        damage=lerpf(5, 12, abilityExp);
    }

    public float getMaxDistance(){return maxDistance;}

    private EntityLivingBase getAttackTarget()
    {
        MovingObjectPosition trace = Raytrace.traceLiving(speller, maxDistance, EntitySelectors.living());
        EntityLivingBase target = null;
        if (trace != null) {
            target = (EntityLivingBase)trace.entityHit;
        }
        return target;
    }
    public void spell()
    {
        EntityLivingBase target=getAttackTarget();
        if(target==null || isSkillInCooldown())
            return;
        attackIgnoreArmor(target,damage);
        List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
        for(Entity e:list)
        {
            NetworkManager.sendFleshRippingEffectTo(target,(EntityPlayerMP)e);
        }
        super.spell();
    }

    @Override
    public String getSkillName()
    {
        return FleshRipping.getDisplayName();
    }
}
