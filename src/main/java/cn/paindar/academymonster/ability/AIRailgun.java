package cn.paindar.academymonster.ability;

import cn.academy.ability.api.AbilityPipeline;
import cn.academy.core.entity.EntityRayBase;
import cn.academy.core.event.BlockDestroyEvent;
import cn.academy.core.util.Plotter;
import cn.academy.core.util.RangedRayDamage;
import cn.academy.vanilla.electromaster.skill.Railgun;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.ai.EntityCoinThrowingNative;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
import static cn.lambdalib.util.generic.VecUtils.*;

/**
 * Created by Paindar on 2017/2/11.
 */
public class AIRailgun extends BaseAbility
{
    static final double STEP = 0.5;
    private float damage;
    private float range =2;
    public int maxIncrement = 25;
    private EntityCoinThrowingNative coin;
    public AIRailgun(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(800, 600, exp), exp);
        maxIncrement=(int)lerpf(12,25,exp);
        damage=lerpf(20, 50, exp);
    }
    public float getMaxDistance(){return maxIncrement;}

    private void destroyBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if(hardness < 0)
            hardness = 233333;
        if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, x, y, z)))
        {
            if(block.getMaterial() != Material.air) {
                block.dropBlockAsItemWithChance(world, x, y, z,
                        world.getBlockMetadata(x, y, z), 0.05f, 0);
            }
            world.setBlockToAir(x, y, z);
        }
    }

    private void spellRailgun()
    {
        Motion3D motion=new Motion3D(speller, true).move(0.1).normalize();
        float yaw = -MathUtils.PI_F * 0.5f - motion.getRotationYawRadians(),
                pitch = motion.getRotationPitchRadians();
        Set<int[]> processed = new HashSet<>();
        World world=speller.worldObj;
        Vec3 start = motion.getPosVec();
        Vec3 slope = motion.getMotionVec();

        Vec3 vp0 = VecUtils.vec(0, 0, 1);
        vp0.rotateAroundZ(pitch);
        vp0.rotateAroundY(yaw);

        Vec3 vp1 = VecUtils.vec(0, 1, 0);
        vp1.rotateAroundZ(pitch);
        vp1.rotateAroundY(yaw);


        /* Apply Entity Damage */
        {
            Vec3 v0 = add(start, add(multiply(vp0, -range), multiply(vp1, -range))),
                    v1 = add(start, add(multiply(vp0, range), multiply(vp1, -range))),
                    v2 = add(start, add(multiply(vp0, range), multiply(vp1, range))),
                    v3 = add(start, add(multiply(vp0, -range), multiply(vp1, range))),
                    v4 = add(v0, multiply(slope, maxIncrement)),
                    v5 = add(v1, multiply(slope, maxIncrement)),
                    v6 = add(v2, multiply(slope, maxIncrement)),
                    v7 = add(v3, multiply(slope, maxIncrement));
            AxisAlignedBB aabb = WorldUtils.minimumBounds(v0, v1, v2, v3, v4, v5, v6, v7);

            Predicate<Entity> areaSelector = target -> {
                Vec3 dv = subtract(vec(target.posX, target.posY, target.posZ), start);
                Vec3 proj = dv.crossProduct(slope);
                return proj.lengthVector() < range * 1.2;
            };
            List<Entity> targets = WorldUtils.getEntities(speller.worldObj, aabb, EntitySelectors.everything().and(areaSelector).and(EntitySelectors.exclude(speller)));

            for (Entity e : targets)
            {
                if (e instanceof EntityLivingBase)
                {
                    attack((EntityLivingBase) e, damage);
                } else
                {
                    e.setDead();
                }
            }
        }
        //TODO
        if(true)
        {
            for(int i=1;i<maxIncrement;i++)
            {
                Vec3 str=VecUtils.add(start,VecUtils.multiply(slope,i));
                for (double s = -range; s <= range; s += STEP)
                {
                    for (double t = -range; t <= range; t += STEP)
                    {
                        double rr = range * RandUtils.ranged(0.9, 1.1);
                        if (s * s + t * t > rr * rr)
                            continue;
                        Vec3 pos = VecUtils.add(str,
                                VecUtils.add(
                                        VecUtils.multiply(vp0, s),
                                        VecUtils.multiply(vp1, t)));
                        destroyBlock(world,(int) pos.xCoord,(int) pos.yCoord,(int) pos.zCoord);
                    }
                }
            }
        }
        if(!speller.worldObj.isRemote)
        {
            List<Entity> list=WorldUtils.getEntities(speller, 40, EntitySelectors.player());
            for(Entity e:list)
            {
                NetworkManager.sendRailgunEffectTo(speller,maxIncrement,(EntityPlayerMP)e);
            }
        }
    }
    public void spell()
    {
        if(isSkillInCooldown())
            return;
        super.spell();
        coin=new EntityCoinThrowingNative(speller);
        speller.worldObj.spawnEntityInWorld(coin);
    }

    @SubscribeEvent
    @Override
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        super.onServerTick(event);
        if(coin==null || coin.isDead||speller.isDead)
        {
            return;
        }
        if(coin.getProgress()>0.9)
        {
            coin.setDead();
            spellRailgun();
        }
    }

    @Override
    public String getSkillName()
    {
        return Railgun.getDisplayName();
    }
}
