package cn.paindar.academymonster.ability;

import cn.academy.vanilla.electromaster.skill.BodyIntensify;
import cn.lambdalib.util.generic.RandUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import scala.collection.immutable.Vector;
import scala.util.Random;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/10.
 */
public class AIBodyIntensify extends BaseAbility
{
    public AIBodyIntensify(EntityLivingBase speller, float abilityExp)
    {
        super(speller, (int)lerpf(900, 600,abilityExp), abilityExp);
    }


    private double getProbability(int ct)
    {
        return (ct - 10.0) / 18.0;
    }
    private int getBuffTime(int ct)
    {
        return (int)(RandUtils.ranged(1, 2) * ct *
                lerpf(1.5f, 2.5f, getSkillExp()));
    }
    private int getBuffLevel(int ct)
    {
        return (int)Math.floor(getProbability(ct));
    }


    public void spell()
    {
        int tick=(int)lerpf(10,40,getSkillExp());
        double p = getProbability(tick);
        int time = getBuffTime(tick);
        int level=getBuffLevel(tick);
        Vector<PotionEffect> vector=BodyIntensify.effects();

        for(int i=0;i<vector.size();i++)
        {
            if(RandUtils.ranged(0, 1+p)<p)
            {
                speller.addPotionEffect(BodyIntensify.createEffect(vector.apply(i), level, time));
            }
        }
        super.spell();
    }

    @Override
    public String getSkillName()
    {
        return BodyIntensify.getDisplayName();
    }
}
