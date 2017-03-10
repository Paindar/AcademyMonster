package cn.paindar.academymonster.ability;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by voidcl on 2017/3/10.
 */
public class AIFlashing extends BaseSkill{
    private float maxDistance;
    private int time;
    private int maxtime;
    public AIFlashing(EntityLivingBase speller, float exp) {
        super(speller,(int)lerpf(200, 100, exp), exp,"teleporter.flashing");
        isChanting=true;
        time=0;
        maxtime=20;
        maxDistance=lerpf(3,10, getSkillExp());
    }

    public float getMaxDistance(){return maxDistance;}

    public void spell(double x, double y, double z)
    {
        if(isSkillInCooldown())
            return;
        if(speller.isRiding())
            speller.mountEntity(null);
        speller.setPositionAndUpdate(x,y,z);
        isChanting=true;
        speller.fallDistance = 0;
        if(!speller.worldObj.isRemote)
        {
            List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
            for(Entity e:list)
            {

                NetworkManager.sendSoundTo("tp.tp",speller,.5f,(EntityPlayerMP)e);
            }
        }
        time=0;
    }
    @Override
    protected void onTick()
    {
        if(!isChanting)
        {
            return;
        }
        if(time>=maxtime)
        {
            isChanting=false;
            return;
        }
        time ++;
    }
}
