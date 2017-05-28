package cn.paindar.academymonster.ability;

import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
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

import java.util.ArrayList;
import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/3/12.
 */
public class AIScatterBomb extends BaseSkill
{
    private List<EntityMdBallNative> ballList=new ArrayList<>();
    private float damage;
    private float range;
    private float time;
    private final float maxTime=240;
    public AIScatterBomb(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(160,100,exp), exp, "meltdowner.scatter_bomb");
        damage=lerpf(3,9,exp);
        range=lerpf(5,11,exp);
    }

    @Override
    public void spell()
    {
        if(!canSpell())
            return;
        if (!isChanting)
        {
            isChanting=true;
            time=0;

        }
    }

    @Override
    public void onTick()
    {
        if(!isChanting)
            return;

        time++;
        if(speller.isDead||isInterf())
        {
            isChanting=false;
            for(EntityMdBallNative ball:ballList)
            {
                ball.setDead();
                super.spell();
            }
        }
        if(time>20&&time%10==0 && time<=100)
        {
            EntityMdBallNative ball=new EntityMdBallNative(speller,2333333);
            ballList.add(ball);
            speller.worldObj.spawnEntityInWorld(ball);
        }
        else if(time>=maxTime)
        {
            spell();
        }
    }

    public void stop()
    {
        List<Entity> trgList= WorldUtils.getEntities(speller,range, EntitySelectors.living().and(EntitySelectors.exclude(speller)));
        Vec3 dst=new Motion3D(speller, 5, true).move(range).getPosVec();
        for(EntityMdBallNative ball:ballList)
        {
            Vec3 str= VecUtils.vec(ball.posX, ball.posY, ball.posZ);
            if(!trgList.isEmpty())
            {
                dst=new Motion3D(trgList.get(RandUtils.nextInt(trgList.size())),5,true).getPosVec();
            }
            MovingObjectPosition trace = Raytrace.perform(speller.worldObj,str,dst
                    , EntitySelectors.exclude(speller).and(EntitySelectors.living()));
            if (trace != null && trace.entityHit != null)
            {
                attack((EntityLivingBase) trace.entityHit, damage);
                List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                for(Entity e:list)
                {
                    NetworkManager.sendMdRayEffectTo(str,dst,(EntityPlayerMP)e);
                }
            }
            ball.setDead();
        }
        ballList.clear();
        isChanting=false;
        super.spell();
    }
    public int getBallSize(){return ballList.size();}

    public float getMaxDistance(){return range;}
}
