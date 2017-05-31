package cn.paindar.academymonster.ability;

import cn.academy.ability.api.event.CalcEvent;
import cn.paindar.academymonster.ability.api.SkillDamageSourceNative;
import cn.paindar.academymonster.ability.api.event.CalcEventNative;
import cn.paindar.academymonster.core.support.tile.AbilityInterfManager;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * Created by Paindar on 2017/2/9.
 */

public abstract class BaseSkill
{
    private int maxCooldown;
    private int remainCooldown=0;
    private float skillExp;
    protected EntityLivingBase speller;
    private String skillName;
    boolean isChanting=false;
    public BaseSkill(EntityLivingBase speller,int maxCooldown,float abilityExp,String name)
    {
        this.maxCooldown=maxCooldown;
        this.skillExp=abilityExp;
        this.speller=speller;
        this.skillName=name;
        FMLCommonHandler.instance().bus().register(this);
    }

    public float getSkillExp(){return skillExp;}

    public boolean isInterf(){return AbilityInterfManager.instance.find(speller);}
    public boolean isSkillInCooldown(){return remainCooldown!=0;}

    /**
     * checked if skill can spell.
     * Type:
     * Cooldown Chant   Interfer    Result
     * F        F       F           Valid(True)
     * F        F       T           Interfered(False)
     * F        T       F           Chanting(False)
     * F        T       T           Interrupted(False, impossible)
     * T        F       F           Cooldown(False)
     * T        F       T           Interfered(False)
     * T        T       F           Impossible(Error)
     * T        T       T           Impossible(Error)
     * @return nothing
     */
    public boolean canSpell()
    {
        return !isSkillInCooldown()&&!isChanting&&!isInterf();
    }

    public boolean available()
    {
        return !isSkillInCooldown() &&isChanting&& !isInterf();
    }
    public boolean isChanting(){return isChanting;}
    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        onTick();
        if(isInterf()&&isChanting)
        {
            isChanting=false;
        }
        if(remainCooldown>0 && !isChanting)
            remainCooldown--;
    }

    protected void onTick()
    {

    }

    void spell()
    {
        remainCooldown=maxCooldown;
    }
    private float getFinalDamage(float damage) {
        return damage;
    }

    boolean attack(EntityLivingBase target,float damage)
    {
        damage = CalcEvent.calc(new CalcEventNative.SkillAttack(speller, this, target, damage));

        if (damage > 0)
        {
            target.attackEntityFrom(new SkillDamageSourceNative(speller, this), getFinalDamage(damage));
        }
        return true;
    }

    boolean attackIgnoreArmor(EntityLivingBase target,float damage)
    {
        damage = CalcEvent.calc(new CalcEventNative.SkillAttack(speller, this, target, damage));

        if (damage > 0)
        {
            target.attackEntityFrom(new SkillDamageSourceNative(speller, this).setDamageBypassesArmor(), getFinalDamage(damage));
        }
        return true;
    }

    public String getUnlocalizedSkillName(){return "ac.ability." + skillName + ".name";}
    public String getSkillName(){return StatCollector.translateToLocal(skillName);}

    @SubscribeEvent
    public void onSpellerDeath(LivingDeathEvent event)
    {

        FMLCommonHandler.instance().bus().unregister(this);
    }
}
