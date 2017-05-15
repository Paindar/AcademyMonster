package cn.paindar.academymonster.ability;

import cn.academy.vanilla.electromaster.skill.BodyIntensify;
import cn.lambdalib.util.generic.RandUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import scala.collection.immutable.Vector;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/10.
 */
public class AIBodyIntensify extends BaseSkill
{
    public AIBodyIntensify(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(300, 200,exp), exp,"electromaster.body_intensify");
    }


    private double getProbability()
    {
        return lerpf(1,2.5f,getSkillExp());
    }
    private int getBuffTime()
    {
        return (int)lerpf(30f,100f, getSkillExp());
    }
    private int getBuffLevel()
    {
        return getSkillExp()>0.5?2:1;
    }


    public void spell()
    {
        if(!canSpell())
            return;
        double p = getProbability();
        int time = getBuffTime();
        int level=getBuffLevel();
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


}
