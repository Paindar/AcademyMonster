package cn.paindar.academymonster.ability;

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
 * Created by Paindar on 2017/6/4.
 */
public class AIElectronCurtains extends BaseSkill
{
    private double maxDamage;
    private double maxDistance;
    private int maxAmounts;
    public AIElectronCurtains(EntityLivingBase speller,float exp)
    {
        super(speller, (int)lerpf(40,120,exp), exp, "meltdowner.electron_curtain");
        maxDamage=lerpf(6,12,exp);
        maxDistance=lerpf(7,15,exp);
        maxAmounts=(int)lerpf(8,20,exp);
    }
    public double getMaxDistance(){return maxDistance;}

    @Override
    public void spell()
    {
        if(canSpell())
        {
            double part=2.0*Math.PI/maxAmounts;
            if(speller.worldObj.isRemote)
                return;
            for(int i=0;i<maxAmounts;i++)
            {
                EntityMdBallNative ball = new EntityMdBallNative(speller,(int)lerpf(20,5,getSkillExp()),(float)Math.cos(part*i)*1.23f,(float)Math.sin(part*i)*1.23f, ball1 ->{
                    Vec3 str= VecUtils.vec(ball1.posX, ball1.posY, ball1.posZ),
                            end=VecUtils.vec(ball1.posX+(ball1.posX-speller.posX)*getMaxDistance()/1.23,0,ball1.posZ+(ball1.posZ-speller.posZ)*getMaxDistance()/1.23);
                    MovingObjectPosition trace = Raytrace.perform(speller.worldObj,str,end
                            , EntitySelectors.exclude(speller).and(EntitySelectors.living()));
                    if (trace != null && trace.entityHit != null)
                    {
                        attack((EntityLivingBase) trace.entityHit,(float)maxDamage);
                    }
                    List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                    for(Entity e:list)
                    {
                        NetworkManager.sendMdRayEffectTo(str,end,(EntityPlayerMP)e);
                    }
                }) ;
                speller.worldObj.spawnEntityInWorld(ball);
            }
            super.spell();
        }
    }


}
