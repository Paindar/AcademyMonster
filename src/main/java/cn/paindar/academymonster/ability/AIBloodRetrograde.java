package cn.paindar.academymonster.ability;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;


import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
/**
 * Created by voidcl on 2017/3/16.
 */
public class AIBloodRetrograde extends BaseSkill {

    private float damage;
    private float exp;
    public AIBloodRetrograde(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(40,20,exp),exp,"vecmanip.bloodretrograde");
        this.exp=exp;
        damage=lerpf(20,30,exp);
    }



    public void spell()
    {
        MovingObjectPosition result=Raytrace.traceLiving(speller,2,EntitySelectors.living());
        EntityLivingBase target=null;
        if(result!=null&&result.typeOfHit==MovingObjectPosition.MovingObjectType.ENTITY)
        {
            target=(EntityLivingBase)result.entityHit;
        }
        if(target!=null&&!isSkillInCooldown())
        {
            attackIgnoreArmor(target,damage);
            float speed=target.getAIMoveSpeed();
            System.out.println(speed);

            target.setAIMoveSpeed(lerpf(speed,speed*0.08f,exp));
            List<Entity> list=WorldUtils.getEntities(speller,25,EntitySelectors.player());
            for(Entity e:list)
            {
                NetworkManager.sendFleshRippingEffectTo(target,(EntityPlayerMP)e);
            }
        }else
        {
            return ;
        }
        super.spell();
    }

}
