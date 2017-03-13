package cn.paindar.academymonster.ability;

import cn.academy.core.client.sound.ACSounds;
import cn.academy.vanilla.electromaster.client.effect.ArcPatterns;
import cn.academy.vanilla.electromaster.entity.EntityArc;
import cn.lambdalib.util.entityx.handlers.Life;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.entity.EntityArcNative;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/3/7.
 */
public class AIThunderBolt extends BaseSkill
{
    private float aoeDamage;
    private float damage;
    private static float range=20f;
    private static float aoeRange=7f;

    public AIThunderBolt(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(200,100,exp), exp, "electromaster.thunder_bolt");
        aoeDamage = lerpf(9.6f, 17.4f, exp);
        damage = lerpf(16f, 29f, exp);
    }

    public void spell()
    {
        MovingObjectPosition result = Raytrace.traceLiving(speller, range);
        Vec3 end;
        if(result == null) {
            end = new Motion3D(speller).move(range).getPosVec();
        } else {
            end = result.hitVec;
            if(result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                end.yCoord += result.entityHit.getEyeHeight();
            }
        }

        boolean hitEntity = !(result == null || result.entityHit == null);
        Predicate<Entity> exclusion= (hitEntity)? EntitySelectors.exclude(speller) : EntitySelectors.exclude(speller, result.entityHit);
        EntityLivingBase target = (hitEntity)? (EntityLivingBase)result.entityHit : null;
        List<Entity> aoes = WorldUtils.getEntities(
            speller.worldObj, end.xCoord, end.yCoord, end.zCoord,
            aoeRange, EntitySelectors.living().and(exclusion));


        if(target != null)
        {
            attack(target, damage);
            if(getSkillExp() > 0.2 && RandUtils.ranged(0, 1) < 0.8 ) {
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 3));
            }
        }

        for(Entity e:aoes)
        {
            attack((EntityLivingBase) e, aoeDamage);

            if (getSkillExp() > 0.2 && RandUtils.ranged(0, 1) < 0.8)
            {
                ((EntityLivingBase)e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 3));
            }
        }
        List<Entity> list= WorldUtils.getEntities(speller, 40, EntitySelectors.player());
        for(Entity e:list)
        {
            NetworkManager.sendThunderBoltTo(speller,target,aoes,(EntityPlayerMP)e);
        }

        super.spell();
    }

    public float getMaxDistance(){return range;}

    @SideOnly(Side.CLIENT)
    public static void spawnEffect(EntityLivingBase ori,EntityLivingBase target,List<Entity> aoes)
    {
        for(int i= 0 ;i<2;i++)
        {
            EntityArcNative mainArc = new EntityArcNative(ori, ArcPatterns.strongArc);
            mainArc.length = range;
            ori.worldObj.spawnEntityInWorld(mainArc);
            mainArc.addMotionHandler(new Life(20));
        }

        for(Entity e:aoes)
        {
            EntityArcNative aoeArc = new EntityArcNative(ori, ArcPatterns.aoeArc);
            aoeArc.lengthFixed = false;
            aoeArc.setFromTo(target.posX, target.posY,target.posZ,
                    e.posX, e.posY + e.getEyeHeight(), e.posZ);
            aoeArc.addMotionHandler(new Life(RandUtils.rangei(15, 25)));
            ori.worldObj.spawnEntityInWorld(aoeArc);
        }

        ACSounds.playClient(ori, "em.arc_strong", 0.6f);
    }
}
