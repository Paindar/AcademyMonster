package cn.paindar.academymonster.ability;

import cn.academy.vanilla.electromaster.skill.ArcGen;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/17.
 */
public class AIArcGen extends BaseSkill
{

    private float damage;
    private float range ;
    private float prob;
    private float slowdown;
    public AIArcGen(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(40,20,exp), exp,"electromaster.arc_gen");
        damage=lerpf(5,9,exp);
        range=lerpf(6,15,exp);
        prob=lerpf(0,0.6f,exp);
        slowdown=exp>0.5?lerpf(0,0.8f,exp-0.5f):0;
    }

    public float getMaxDistance(){return range;}

    private Vec3 getDest(EntityLivingBase speller){return Raytrace.getLookingPos(speller, range).getLeft();}

    public void spell()
    {
        if(isSkillInCooldown())
            return;
        World world=speller.worldObj;
        MovingObjectPosition result=Raytrace.traceLiving(speller, range, EntitySelectors.living(), BlockSelectors.filNormal);
        if(result!=null)
        {
            if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                if (result.entityHit instanceof EntityLivingBase)
                {
                    EntityLivingBase target = (EntityLivingBase) result.entityHit;
                    attack(target, damage);
                    if (RandUtils.nextDouble() <= slowdown)
                    {
                        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1));
                    }
                }
            }
            else if(getSkillExp()>=0.4 &&  result.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int x=result.blockX,y=result.blockY,z=result.blockZ;
                Block block=world.getBlock(x,y,z);
                if (RandUtils.ranged(0, 1) < prob)
                {
                    if (world.getBlock(x, y + 1, z) == Blocks.air) {
                        world.setBlock(x, y + 1, z, Blocks.fire, 0, 0x03);
                    }
                }
            }
        }
        List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
        for(Entity e:list)
        {
            NetworkManager.sendArcGenTo(speller,range,(EntityPlayerMP)e);
        }
        super.spell();
    }
}
