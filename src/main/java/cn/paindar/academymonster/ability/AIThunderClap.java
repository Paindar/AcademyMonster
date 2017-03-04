package cn.paindar.academymonster.ability;

import cn.academy.vanilla.electromaster.skill.ThunderClap;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/23.
 */
public class AIThunderClap extends BaseSkill
{
    private float damage;
    private float range;
    private float distance=15;
    public AIThunderClap(EntityLivingBase speller,  float exp)
    {
        super(speller,(int) lerpf(600,300,exp), exp,"thunder_clap");
        damage=lerpf(18,120,exp);
        range=lerpf(10,25,exp);
    }

    public void spell(double hitX,double hitY,double hitZ)
    {
        EntityLightningBolt lightning = new EntityLightningBolt(speller.worldObj, hitX, hitY, hitZ);
        speller.worldObj.addWeatherEffect(lightning);
        List<Entity> list=WorldUtils.getEntities(speller,range, EntitySelectors.exclude(speller).and(EntitySelectors.living()));
        for(Entity e:list)
        {
            attack((EntityLivingBase)e,damage);
        }
        super.spell();
    }

    public float getMaxDistance(){return distance;}
}
