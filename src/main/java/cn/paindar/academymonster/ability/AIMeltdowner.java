package cn.paindar.academymonster.ability;

import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.network.NetworkManager;
import cn.paindar.academymonster.events.RayShootingEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
import static cn.lambdalib.util.generic.VecUtils.*;

/**
 * Created by Paindar on 2017/3/12.
 */
public class AIMeltdowner extends BaseSkill
{
    private static final double STEP = 0.5;
    private float damage;
    private float range =2;
    private int maxIncrement = 16;
    private int tick=0;
    public AIMeltdowner(EntityLivingBase speller,float exp)
    {
        super(speller, (int)lerpf(800, 600, exp), exp,"meltdowner.meltdowner");
        maxIncrement=(int)lerpf(12,25,exp);
        damage=lerpf(7, 60, exp);
    }


    public float getMaxDistance(){return maxIncrement;}

    private void destroyBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if(hardness < 0)
            return ;
        if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, x, y, z)))
        {
            if(block.getMaterial() != Material.air) {
                block.dropBlockAsItemWithChance(world, x, y, z,
                        world.getBlockMetadata(x, y, z), 0.05f, 0);
            }
            world.setBlockToAir(x, y, z);
        }
    }

    private List<Entity> selectTargets(EntityLivingBase entity, double incr_)
    {
        Motion3D motion = new Motion3D(entity, true).move(0.1).normalize();
        float yaw = -MathUtils.PI_F * 0.5f - motion.getRotationYawRadians(),
                pitch = motion.getRotationPitchRadians();
        Vec3 start = motion.getPosVec();
        Vec3 slope = motion.getMotionVec();

        Vec3 vp0 = VecUtils.vec(0, 0, 1);
        vp0.rotateAroundZ(pitch);
        vp0.rotateAroundY(yaw);

        Vec3 vp1 = VecUtils.vec(0, 1, 0);
        vp1.rotateAroundZ(pitch);
        vp1.rotateAroundY(yaw);
        Vec3 v0 = add(start, add(multiply(vp0, -range), multiply(vp1, -range))),
                v1 = add(start, add(multiply(vp0, range), multiply(vp1, -range))),
                v2 = add(start, add(multiply(vp0, range), multiply(vp1, range))),
                v3 = add(start, add(multiply(vp0, -range), multiply(vp1, range))),
                v4 = add(v0, multiply(slope, incr_)),
                v5 = add(v1, multiply(slope, incr_)),
                v6 = add(v2, multiply(slope, incr_)),
                v7 = add(v3, multiply(slope, incr_));
        AxisAlignedBB aabb = WorldUtils.minimumBounds(v0, v1, v2, v3, v4, v5, v6, v7);

        Predicate<Entity> areaSelector = target -> {
            Vec3 dv = subtract(vec(target.posX, target.posY, target.posZ), start);
            Vec3 proj = dv.crossProduct(slope);
            return !target.equals(entity) && proj.lengthVector() < range * 1.2;
        };
        return WorldUtils.getEntities(speller.worldObj, aabb, EntitySelectors.everything().and(areaSelector));

    }

    private void spellLightgun()
    {
        EntityLivingBase lastEntity=speller;
        World world=speller.worldObj;
        double incr_=maxIncrement;

        /* Apply Entity Damage */
        {
            boolean reflectCheck = true;
            List<Vec3> paths = new ArrayList<>();
            Vec3 pos=Vec3.createVectorHelper(lastEntity.posX, lastEntity.posY +lastEntity.getEyeHeight(),
                    lastEntity.posZ);
            paths.add(pos);
            paths.add(add(pos, multiply(lastEntity.getLookVec(), incr_)));
            while (reflectCheck) {
                reflectCheck=false;
                if(incr_<=0)
                    break;
                List<Entity> targets = selectTargets(lastEntity, incr_);
                targets.sort(Comparator.comparingDouble(lastEntity::getDistanceSqToEntity));
                for (Entity e : targets) {
                    if (e instanceof EntityLivingBase) {
                        RayShootingEvent event = new RayShootingEvent(speller, (EntityLivingBase) e, incr_);
                        boolean result = MinecraftForge.EVENT_BUS.post(event);
                        incr_=event.range;
                        if (!result)
                            attack((EntityLivingBase) e, damage);
                        else {
                            incr_ -= (e.getDistanceToEntity(lastEntity));
                            paths.remove(paths.size()-1);
                            pos=Vec3.createVectorHelper(e.posX, e.posY +e.getEyeHeight(), e.posZ);
                            paths.add(pos);
                            paths.add(add(pos,multiply(e.getLookVec(), incr_)));
                            lastEntity = (EntityLivingBase) e;
                            reflectCheck=true;
                            break;
                        }
                    } else {
                        e.setDead();
                    }
                }
            }

            {
                int index=0;
                Vec3 str=paths.get(index), end=paths.get(index+1), dir=subtract(end, str);
                float yaw = (float)(-MathUtils.PI_F * 0.5f -Math.atan2(dir.xCoord, dir.zCoord)),
                        pitch = (float) -Math.atan2(dir.yCoord, Math.sqrt(dir.xCoord * dir.xCoord + dir.zCoord * dir.zCoord));
                Vec3 vp0 = VecUtils.vec(0, 0, 1);
                vp0.rotateAroundZ(pitch);
                vp0.rotateAroundY(yaw);

                Vec3 vp1 = VecUtils.vec(0, 1, 0);
                vp1.rotateAroundZ(pitch);
                vp1.rotateAroundY(yaw);
                incr_=1;
                for(int i=1;i<=maxIncrement;i++)
                {
                    if(incr_>=dir.lengthVector()||i==maxIncrement)
                    {
                        incr_=1;
                        index++;
                        if(!speller.worldObj.isRemote)
                        {
                            List<Entity> list=WorldUtils.getEntities(speller, 40, EntitySelectors.player());
                            for(Entity e:list)
                            {
                                for(int j=0;j<paths.size()-1;j++)
                                    NetworkManager.sendMeltdownerEffectTo(speller,str, end, (EntityPlayerMP)e);
                            }
                        }
                        if(index==paths.size()-1)
                            break;
                        str=paths.get(index);
                        end=paths.get(index+1);
                        dir=subtract(end, str);
                        yaw = (float)(-MathUtils.PI_F * 0.5f -Math.atan2(dir.xCoord, dir.zCoord));
                        pitch = (float) -Math.atan2(dir.yCoord, Math.sqrt(dir.xCoord * dir.xCoord + dir.zCoord * dir.zCoord));
                        vp0 = VecUtils.vec(0, 0, 1);
                        vp0.rotateAroundZ(pitch);
                        vp0.rotateAroundY(yaw);

                        vp1 = VecUtils.vec(0, 1, 0);
                        vp1.rotateAroundZ(pitch);
                        vp1.rotateAroundY(yaw);
                    }
                    if(AMConfig.getBoolean("am.skill.Meltdowner.destroyBlock",true)) {
                        Vec3 cur=add(str,multiply(dir.normalize(),incr_));
                        for (double s = -range; s <= range; s += STEP) {
                            for (double t = -range; t <= range; t += STEP) {
                                double rr = range * RandUtils.ranged(0.9, 1.1);
                                if (s * s + t * t > rr * rr)
                                    continue;
                                pos = VecUtils.add(cur,
                                        VecUtils.add(
                                                VecUtils.multiply(vp0, s),
                                                VecUtils.multiply(vp1, t)));
                                destroyBlock(world, (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
                            }
                        }
                    }
                    incr_++;
                }
            }

        }
    }

    @Override
    protected void onTick()
    {
        if(!isChanting)
            return;
        if(speller.isDead)
        {
            isChanting=false;
            tick=0;
            super.spell();
        }

         tick++;
         if(isInterf())
         {
             isChanting=false;
             tick=0;
             super.spell();
         }
        if(tick>=60)
        {
            spellLightgun();
            isChanting=false;
            super.spell();
            tick=0;
        }
    }

    @Override
    public void spell()
    {
        if(!canSpell())
            return;
        tick=0;
        isChanting=true;
        speller.worldObj.playSoundAtEntity(speller,"academymonster:md_charge",1,1);


    }
}
