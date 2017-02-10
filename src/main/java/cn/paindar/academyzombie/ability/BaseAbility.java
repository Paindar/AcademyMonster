package cn.paindar.academyzombie.ability;

import cn.academy.ability.SkillDamageSource;
import cn.academy.ability.api.event.CalcEvent;
import cn.paindar.academyzombie.ability.api.SkillDamageSourceNative;
import cn.paindar.academyzombie.ability.api.event.CalcEventNative;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/9.
 */

public abstract class BaseAbility
{
    private int maxCooldown;
    protected int remainCooldown=0;
    private float skillExp;
    protected EntityLivingBase speller;
    protected boolean isChanting=false;
    protected Vec3 targetPos;
    public BaseAbility(EntityLivingBase speller,int maxCooldown,float abilityExp)
    {
        this.maxCooldown=maxCooldown;
        this.skillExp=abilityExp;
        this.speller=speller;
        FMLCommonHandler.instance().bus().register(this);
    }

    public float getSkillExp(){return skillExp;}

    protected int getMaxCooldown(){return maxCooldown;}

    public boolean isSkillInCooldown(){return remainCooldown!=0;}

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        if(remainCooldown>0)
            remainCooldown--;
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
    public abstract String getSkillName();


}
