package cn.paindar.academymonster.ability;

import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.entity.EntityMdBallNative;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/3/12.
 */
public class AIElectronMissile extends BaseSkill
{
    private List<EntityMdBallNative> ballList=new ArrayList<>();
    private int freq;
    private int time;
    private int maxTick;
    private float range;
    private float damage;
    public AIElectronMissile(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(800,400,exp), exp, "meltdowner.electron_missile");
        freq=(int)lerpf(20,10,exp);
        maxTick=(int)lerpf(100,200,exp);
        range=(int)lerpf(8,12,exp);
        damage=lerpf(5,12,exp);
    }

    @Override
    public void spell()
    {
        if(!canSpell()||available())
            return;
        isChanting=true;
        time=0;
    }

    @Override
    public void onTick()
    {
        if(!isChanting)
            return;

        time++;
        if(speller.isDead || time>=maxTick||isInterf())
        {
            isChanting=false;
            super.spell();
            for(EntityMdBallNative ball:ballList)
            {
                ball.setDead();
            }
        }
        if(time%freq==0)
        {
            EntityMdBallNative ball=new EntityMdBallNative(speller,2333333);
            ballList.add(ball);
            speller.worldObj.spawnEntityInWorld(ball);
        }
        List<Entity> list=WorldUtils.getEntities(speller,range,EntitySelectors.exclude(speller).and(EntitySelectors.living()));
        if(!list.isEmpty() && !ballList.isEmpty())
        {
            Vec3 str= VecUtils.vec(ballList.get(0).posX, ballList.get(0).posY, ballList.get(0).posZ);
            Vec3 dst=new Motion3D(list.get(0),5,true).getPosVec();
            attack((EntityLivingBase) list.get(0), damage);
            list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
            for(Entity e:list)
            {
                NetworkManager.sendMdRayEffectTo(str,dst,(EntityPlayerMP)e);
            }
            ballList.get(0).setDead();
            ballList.remove(0);
        }
    }

    public float getMaxDistance(){return range;}
}
