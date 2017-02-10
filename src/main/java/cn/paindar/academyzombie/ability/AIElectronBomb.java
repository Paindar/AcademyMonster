package cn.paindar.academyzombie.ability;

import cn.academy.vanilla.meltdowner.skill.MDDamageHelper;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academyzombie.entity.EntityMdBallNative;
import cn.paindar.academyzombie.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/10.
 */
public class AIElectronBomb extends BaseAbility
{
    private float maxDistance;
    private EntityLivingBase speller;
    private float damage;

    public AIElectronBomb(EntityLivingBase speller,float abilityExp) {
        super(speller,(int)lerpf(20, 10, abilityExp), abilityExp);
        damage=lerpf(6, 12, abilityExp);
        maxDistance=lerpf(7,15,abilityExp);
        this.speller=speller;
    }
    public float getMaxDistance(){return maxDistance;}

    private Vec3 getDest(EntityLivingBase speller){return Raytrace.getLookingPos(speller, maxDistance).getLeft();}

    public void spell(double x,double y,double z)
    {
        if(isSkillInCooldown())
            return;
        if(speller.worldObj.isRemote)
            return;
        EntityMdBallNative ball = new EntityMdBallNative(speller,(int)lerpf(20,5,getSkillExp()),new EntityCallback<EntityMdBallNative>() {
         public void execute(EntityMdBallNative ball) {
             Vec3 str= VecUtils.vec(ball.posX, ball.posY, ball.posZ),
             end=getDest(speller);
             MovingObjectPosition trace = Raytrace.perform(speller.worldObj,str,end
                    , EntitySelectors.exclude(speller).and(EntitySelectors.of(EntityMdBallNative.class).negate()));
            if (trace != null && trace.entityHit != null)
            {
                attack((EntityLivingBase) trace.entityHit, damage);
                List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                for(Entity e:list)
                {
                    NetworkManager.sendTo(str,end,(EntityPlayerMP)e);
                }
             }


        }
    }) ;
        super.spell();
        speller.worldObj.spawnEntityInWorld(ball);

    }

    @Override
    public String getSkillName() {
        return "ac.ability.meltdowner.electron_bomb.name";
    }
}
