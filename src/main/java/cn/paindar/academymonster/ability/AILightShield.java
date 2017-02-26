package cn.paindar.academymonster.ability;

import cn.academy.vanilla.meltdowner.skill.LightShield;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.EntityLightShield;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/26.
 */
public class AILightShield extends BaseSkill
{
    private float touchDamage;
    private float absorbDamage;
    private int maxTime;
    private int time;
    private EntityLightShield shield;

    public AILightShield(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(100,40,exp), exp, LightShield.getFullName());
        maxTime=(int)lerpf(100,300,exp);
        touchDamage=lerpf(2, 6, exp);
        absorbDamage=lerpf(150,300,exp);
    }

    private boolean isEntityReachable(Entity e)
    {
        double dx = e.posX - speller.posX;
    //dy = e.posY - player.posY,
        double dz = e.posZ - speller.posZ;
        double yaw = -MathUtils.toDegrees(Math.atan2(dx, dz));
        return Math.abs(yaw - speller.rotationYaw) % 360 < 60;
    }

    public void spell()
    {
        if(!available())
            return;
        isChanting=true;
        shield = new EntityLightShield(speller);
        speller.worldObj.spawnEntityInWorld(shield);
        time=0;
        AcademyMonster.log.info("spell");
    }

    protected void onTick()
    {
        if(!isChanting)
            return;
        if( shield==null||shield.isDead|| speller.isDead||maxTime<=time)
        {
            if(isChanting)
            {
                stop();
            }
            return ;
        }
        time++;
        List<Entity> candidates= WorldUtils.getEntities(speller, 3,
                EntitySelectors.exclude(speller).and((Entity t)-> isEntityReachable(t)).and(EntitySelectors.exclude(shield)));
        for(Entity e:candidates)
        {
            if(e instanceof EntityLivingBase)
            {
                if (e.hurtResistantTime <= 0 )
                {
                    attack((EntityLivingBase)e, touchDamage);
                }
            }
            else
            {
                e.setDead();
                AcademyMonster.log.info("set "+e+" death.");
            }
        }
    }

    @SubscribeEvent
    public void onSpawnerHurt(LivingHurtEvent event)
    {
        if(event.entity==speller && isChanting)
        {
           if(event.ammount>=absorbDamage)
           {
               event.ammount-=absorbDamage;
               absorbDamage=0;
               stop();
           }
           else
           {
               absorbDamage -= event.ammount;
               event.setCanceled(true);
           }
        }
    }

    public void stop()
    {
        isChanting=false;
        super.spell();
        AcademyMonster.log.info("stop");
        if(shield!=null)
            shield.setDead();
    }
}
