package cn.paindar.academyzombie.ability;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academyzombie.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/9.
 */
public class AIPenetrateTeleport extends BaseAbility
{
    private static final String MSG_SOUND="msg_sound";
    public AIPenetrateTeleport(float abilityExp) {
        super((int)lerpf(200, 100, abilityExp), abilityExp);
    }

    public float getMaxDistance(){return lerpf(3, 10, getSkillExp());}

    public void spell(EntityLivingBase speller,double x, double y, double z)
    {
        if(isSkillInCooldown())
            return;
        if(speller.isRiding())
            speller.mountEntity(null);
        speller.setPositionAndUpdate(x,y,z);
        speller.fallDistance = 0;
        if(!speller.worldObj.isRemote)
        {
            List<Entity> list=WorldUtils.getEntities(speller, 25, EntitySelectors.player());
            for(Entity e:list)
            {

                NetworkManager.sendTo( "tp.tp",(EntityPlayerMP)e);
            }
        }
        super.spell();
    }
}
