package cn.paindar.academymonster.ability;

import cn.academy.ability.api.event.ReflectEvent;
import cn.academy.vanilla.vecmanip.skill.EntityAffection;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.RichEntity;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.playerskill.electromaster.events.RayShootingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/3/11.
 */
public class AIVecReflect extends BaseSkill
{
    private int time=0;
    private final int maxTime;
    private float reflectRate;
    private final float maxDamage;
    private float dmg;
    public AIVecReflect(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(400,300,exp), exp, "vecmanip.vec_reflection");
        maxTime=(int)lerpf(60,240,exp);
        maxDamage=lerpf(200,1200,exp);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @Override
    public void spell()
    {
        throw new RuntimeException();
    }

    @Override
    public void onTick()
    {
        if (time == 0||dmg<=1e-6)
        {
            if(isChanting)
            {
                isChanting = false;
                dmg=0;
                time=0;
                super.spell();
            }
            return;
        }
        time--;
        if(speller==null||speller.isDead)
            return;
        //
        List<Entity> entities = WorldUtils.getEntities(speller, 5, (Entity entity) -> (!EntityAffection.isMarked(entity)));
        for (Entity entity : entities)
        {

            if (entity instanceof EntityFireball)
            {
                createNewFireball((EntityFireball) entity);
            }
            else if(!(entity instanceof EntityLivingBase))
            {
                reflect(entity, speller);

                EntityAffection.mark(entity);
            }


        }
    }

    @SubscribeEvent
    public void onReflect(RayShootingEvent evt)
    {
        if (evt.target.equals(speller)&&isChanting) {
            dmg-=evt.damage*reflectRate;
            if(dmg>=0){
                evt.setCanceled(true);
            }
        }
    }

    /**
     * @param passby If passby=true, and this isn't a complete absorb, the action will not perform. Else it will.
     * @return (Whether action had been really performed, processed damage)
     */
    private float handleAttack(DamageSource dmgSource, float dmg,Boolean passby)
    {
        float refDmg=0;
        float returnRatio = reflectRate;
        if (!passby)
        { // Perform the action.
            Entity sourceEntity = dmgSource.getSourceOfDamage();

            if (sourceEntity != null && sourceEntity != speller)
            {
                if(sourceEntity instanceof EntityLivingBase)
                {
                    if(this.dmg>=returnRatio * dmg)
                    {
                        refDmg=returnRatio * dmg;
                        this.dmg-=refDmg;
                        attack((EntityLivingBase) sourceEntity, refDmg);
                    }
                    else
                    {
                        refDmg=this.dmg;
                        this.dmg=0;
                        attack((EntityLivingBase) sourceEntity, refDmg);
                    }

                }
                else
                {
                    reflect(sourceEntity, speller);
                    EntityAffection.mark(sourceEntity);
                }
            }
            return Math.max(0,dmg-refDmg);
        }
        else
        {
            if(this.dmg>=returnRatio * dmg)
            {
                refDmg=returnRatio * dmg;
            }
            else
            {
                refDmg=this.dmg;
            }
            this.dmg-=refDmg;
            return Math.max(0,dmg-refDmg);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent evt)
    {
        if(!evt.entity.equals(speller))
            return;
        if(canSpell())
        {
            isChanting = true;//make skill available
            reflectRate=lerpf(0.3f,2f,getSkillExp());
            time=maxTime;
            dmg=maxDamage;
        }
        if (evt.entityLiving.equals(speller)&&isChanting) {
            dmg-=evt.ammount*reflectRate;
            if ( handleAttack(evt.source, evt.ammount,  true)<=0) {
                evt.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt)
    {
        if(!evt.entity.equals(speller))
            return;
        if(canSpell())
        {
            isChanting = true;//make skill available
            reflectRate=lerpf(0.3f,2f,getSkillExp());
            time=maxTime;
            dmg=maxDamage;
        }
        evt.ammount = handleAttack(evt.source, evt.ammount, false);
}

    private static void reflect(Entity entity,EntityLivingBase player)
    {
//        Vec3 lookPos = Vec3.createVectorHelper(player.posX-entity.posX,player.posY-entity.posY,player.posZ-entity.posZ);
//        VecUtils.multiply(lookPos,20.0/lookPos.lengthVector());
        Vec3 lookPos = Raytrace.getLookingPos(player, 20).getLeft();
        double speed = VecUtils.vec(entity.motionX, entity.motionY, entity.motionZ).lengthVector();
        Vec3 vel = VecUtils.multiply(VecUtils.subtract(lookPos,(new RichEntity(entity)).headPosition()).normalize(),speed);
        new RichEntity(entity).setVel(vel);

    }

    private void createNewFireball(EntityFireball source)
    {
        source.setDead();

        EntityLivingBase shootingEntity = source.shootingEntity;
        EntityFireball fireball;
        if(source instanceof EntityLargeFireball)
        {
            fireball = new EntityLargeFireball(((EntityLargeFireball) source).worldObj, shootingEntity, shootingEntity.posX,
                    shootingEntity.posY, shootingEntity.posZ);
            ((EntityLargeFireball)fireball).field_92057_e = ((EntityLargeFireball)source).field_92057_e;
        }
        else
        {
            if(source.shootingEntity==null)
            {
                fireball = new EntitySmallFireball(source.worldObj, source.posX, source.posY, source.posZ,
                        source.posX, source.posY, source.posZ);

            }
            else
            {
                fireball = new EntitySmallFireball(source.worldObj, shootingEntity, shootingEntity.posX,
                        shootingEntity.posY, shootingEntity.posZ);
            }
        }
        fireball.setPosition(source.posX, source.posY, source.posZ);
        Vec3 lookPos = Raytrace.getLookingPos(speller, 20).getLeft();
        double speed = VecUtils.vec(source.motionX, source.motionY, source.motionZ).lengthVector();
        Vec3 vel = VecUtils.multiply(VecUtils.subtract(lookPos,(new RichEntity(source)).headPosition()).normalize(),speed);
        new RichEntity(fireball).setVel(vel);
        EntityAffection.mark(fireball);
        source.worldObj.spawnEntityInWorld(fireball);
    }

    @Override
    @SubscribeEvent
    public void onSpellerDeath(LivingDeathEvent event)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
        super.onSpellerDeath(event);
    }

}
