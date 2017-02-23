package cn.paindar.academymonster.ability;

import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.effect.EffectManager;
import cn.paindar.academymonster.entity.EntityMineRayNative;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import static cn.lambdalib.util.generic.MathUtils.lerp;
import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/19.
 */
public class AIMineRay extends BaseSkill
{

    private float maxDist;
    private EntityMineRayNative rayEffect;
    private int maxTime;
    private float spd;
    private float remainHardness;
    private int x=0,y=0,z=0;
    private int effectId;
    public AIMineRay(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(10,5,exp), exp,"MineRay");
        maxDist=lerpf(7,15,exp);
        maxTime=(int)lerp(5,10,exp);
        spd=lerpf(0.3f,0.7f,exp);
    }

    public float getMaxDistance()
    {
        return maxDist;
    }

    public void spell()
    {
        if(isSkillInCooldown()|| isChanting)
            return;
        isChanting=true;
        rayEffect = new EntityMineRayNative(speller,maxDist);
        effectId= EffectManager.addEffect(rayEffect);
        speller.worldObj.spawnEntityInWorld(rayEffect);
        super.spell();
    }

    @Override
    protected void onTick()
    {
        if(!isChanting || rayEffect==null||rayEffect.isDead|| speller.isDead||maxTime<=0)
        {
            stop();
            return ;
        }
        maxTime--;

        MovingObjectPosition result = Raytrace.traceLiving(speller, maxDist, EntitySelectors.nothing());
        if(result!=null)
        {
            int tx=result.blockX,ty=result.blockY,tz=result.blockZ;
            Block block=speller.worldObj.getBlock(tx,ty,tz);
            if(x!=tx||y!=ty||z!=tz)
            {
                remainHardness=block.getBlockHardness(speller.worldObj,tx,ty,tz);
                x=tx;y=ty;z=tz;
                if (remainHardness < 0) remainHardness = Float.MAX_VALUE;
            }
            else
            {
                if(remainHardness<0)
                {
                    World world=speller.worldObj;
                    if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, tx, ty, tz)))
                    {
                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), .5f, 1f);
                        block.dropBlockAsItemWithChance(world, x, y, z, world.getBlockMetadata(x, y, z),0.5f,0);
                        world.setBlock(x, y, z, Blocks.air);
                        x=y=z=-1;
                    }
                }
                else
                    remainHardness-=spd;
            }
        }
    }

    public void stop()
    {
        isChanting=false;
        EffectManager.removeEffect(effectId);
        if(rayEffect!=null)
            rayEffect.setDead();

    }
}
