package cn.paindar.academymonster.ability;

import cn.academy.vanilla.meltdowner.skill.ElectronBomb;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.entity.EntityMdBallNative;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/10.
 */
public class AIElectronBomb extends BaseSkill
{
    private float maxDistance;
    private float damage;

    public AIElectronBomb(EntityLivingBase speller,float exp) {
        super(speller,(int)lerpf(40,10, exp), exp,ElectronBomb.getFullName());
        damage=lerpf(6, 12, exp);
        maxDistance=lerpf(7,15,exp);
    }
    public float getMaxDistance(){return maxDistance;}

    private Vec3 getDest(EntityLivingBase speller){return Raytrace.getLookingPos(speller, maxDistance).getLeft();}

    public void spell()
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
                    , EntitySelectors.exclude(speller).and(EntitySelectors.living()));
            if (trace != null && trace.entityHit != null)
            {
                attack((EntityLivingBase) trace.entityHit, damage);
                List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                for(Entity e:list)
                {
                    NetworkManager.sendMdRayEffectTo(str,end,(EntityPlayerMP)e);
                }
             }


        }
    }) ;
        super.spell();
        speller.worldObj.spawnEntityInWorld(ball);

    }

}
