package cn.paindar.academymonster.ability;

import cn.academy.ability.api.event.CalcEvent;
import cn.paindar.academymonster.ability.api.SkillDamageSourceNative;
import cn.paindar.academymonster.ability.api.event.CalcEventNative;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;

/**
 * Created by Paindar on 2017/2/9.
 */

public abstract class BaseSkill
{
    private int maxCooldown;
    private int remainCooldown=0;
    private float skillExp;
    protected EntityLivingBase speller;
    protected static String skillName;
    protected boolean isChanting=false;
    public BaseSkill(EntityLivingBase speller,int maxCooldown,float abilityExp)
    {
        this.maxCooldown=maxCooldown;
        this.skillExp=abilityExp;
        this.speller=speller;
        FMLCommonHandler.instance().bus().register(this);
    }

    public float getSkillExp(){return skillExp;}

    protected int getMaxCooldown(){return maxCooldown;}

    public boolean isSkillInCooldown(){return remainCooldown!=0;}

    public boolean isChanting(){return isChanting;}
    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        onTick();
        if(remainCooldown>0 && !isChanting)
            remainCooldown--;
    }

    protected void onTick()
    {

    }

    public void spell()
    {
        remainCooldown=maxCooldown;
    }
    private float getFinalDamage(float damage) {
        return damage;
    }

    public boolean attack(EntityLivingBase target,float damage)
    {
        damage = CalcEvent.calc(new CalcEventNative.SkillAttack(speller, this, target, damage));

        if (damage > 0)
        {
            target.attackEntityFrom(new SkillDamageSourceNative(speller, this), getFinalDamage(damage));
        }
        return true;
    }

    public boolean attackIgnoreArmor(EntityLivingBase target,float damage)
    {
        damage = CalcEvent.calc(new CalcEventNative.SkillAttack(speller, this, target, damage));

        if (damage > 0)
        {
            target.attackEntityFrom(new SkillDamageSourceNative(speller, this).setDamageBypassesArmor(), getFinalDamage(damage));
        }
        return true;
    }
    public static String getUnlocalizedSkillName(){return "ac.ability." + skillName + ".name";}
    public static String getSkillName(){return StatCollector.translateToLocal(skillName);}


}
