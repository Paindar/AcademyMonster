package cn.paindar.academymonster.ability;

import cn.academy.core.event.BlockDestroyEvent;
import cn.academy.core.util.Plotter;
import cn.academy.vanilla.vecmanip.skill.IVec;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
import static cn.lambdalib.util.generic.RandUtils.RNG;
import static cn.lambdalib.util.generic.RandUtils.rangef;
import static codechicken.nei.NEIClientConfig.world;

/**
 * Created by voidcl on 2017/3/20.
 */
public class AIGroundShock extends BaseSkill{
    private final float maxDistance;
    private float damage;
    private final int flyHeight=5;
    private int headDamage=0;
    private boolean canBreakBlock;
    private float dropRate;
    private float groundBreakProb=0.3f;
    private float ySpeed;
    public AIGroundShock(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(60,20,exp),exp,"vecmanip.ground_shock");
        maxDistance=lerpf(5,12,exp);
        damage=lerpf(2,8,exp);
        canBreakBlock=AMConfig.getBoolean("am.skill.GroundShock.destroyBlock",true);
        dropRate = lerpf(0.3f, 1.0f, exp);
        ySpeed= rangef(0.6f, 0.9f) * lerpf(0.8f, 1.3f,exp);
    }

    public float getMaxDistance()
    {
        return maxDistance;
    }

    private void breakWithForce(int x, int y, int z, boolean drop)
    {
        World world=speller.worldObj;
        Block block = world.getBlock(x, y, z);
        if ( canBreakBlock && !MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, x, y, z)))
        {
            if(block.getBlockHardness(world, x, y, z) >=0)
            {
                if (block != Blocks.farmland && !block.getMaterial().isLiquid())
                {
                    if (drop && RNG.nextFloat() < dropRate)
                    {
                        block.dropBlockAsItemWithChance(world, x, y, z, world.getBlockMetadata(x, y, z), 1.0f, 0);
                    }
                    world.setBlock(x, y, z, Blocks.air);
                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), .5f, 1f);
                }
            }
        }
    }

    public void spell()
    {
        Vec3 planeLook = speller.getLookVec().normalize();

        Plotter plotter = new Plotter((int)Math.floor(speller.posX),(int)Math.floor(speller.posY) - 1,
                (int)Math.floor(speller.posZ), planeLook.xCoord, 0, planeLook.zCoord);

        int iter = (int)maxDistance;

        List<IVec> dejavu_blocks = new ArrayList<>();
        List<Entity> dejavu_ent    = new ArrayList<>();

        Vec3 rot = VecUtils.copy(planeLook);
        rot.rotateAroundY(90);

        Map<Vec3,Float> deltas = new HashMap<Vec3,Float>()
        {
            {
                put(Vec3.createVectorHelper(0,0,0),0f);
                put(rot,0.7f);
                put(VecUtils.multiply(rot,-1),0.7f);
                put(VecUtils.multiply(rot,2),0.3f);
                put(VecUtils.multiply(rot,-2),0.3f);
            }
        };

        Predicate<Entity> selector = EntitySelectors.living().and(EntitySelectors.exclude(speller));
        World world = speller.worldObj;
        while (iter > 0)
        {
            int[] next = plotter.next();
            int x=next[0], y=next[1], z=next[2];

            iter -= 1;
            for (Map.Entry<Vec3,Float> entry: deltas.entrySet())
            {
                Vec3 delta=entry.getKey();
                Float prob=entry.getValue();


                IVec pt = new IVec((int)Math.floor(x + delta.xCoord),(int)Math.floor(y + delta.yCoord), (int)Math.floor(z + delta.zCoord));
                Block block = world.getBlock(pt.x(), pt.y(), pt.z());

                if (RNG.nextDouble() < prob)
                {
                    if (block != Blocks.air && !dejavu_blocks.contains(pt))
                    {
                        dejavu_blocks.add(pt);
                        if (block instanceof BlockStone)
                        {
                            world.setBlock(pt.x(), pt.y(), pt.z(), Blocks.cobblestone);
                        } else if (block instanceof BlockGrass)
                        {
                            world.setBlock(pt.x(), pt.y(), pt.z(), Blocks.dirt);
                        }
                    }
                    if (RNG.nextDouble() < groundBreakProb)
                    {
                        breakWithForce(x, y, z, false);
                    }

                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(pt.x() - 0.2, pt.y() - 0.2, pt.z() - 0.2, pt.x() + 1.4, pt.y() + 2.2, pt.z() + 1.4);
                    List<Entity> entities = WorldUtils.getEntities(world, aabb, selector);
                    for (Entity entity : entities)
                    {
                        if (!dejavu_ent.contains(entity))
                        {
                            dejavu_ent.add(entity);
                            attack((EntityLivingBase) entity, damage);
                            entity.motionY = ySpeed;
                        }
                    }
                }
            }
            for(int i=1;i<=3;i++)
                breakWithForce(x, y + i, z, false);
        }

        super.spell();
        List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
        IVec[] vecs=new IVec[dejavu_blocks.size()];
        for(int i=0;i<dejavu_blocks.size();i++)
        {
            vecs[i]=dejavu_blocks.get(i);
        }

        for(Entity e:list)
        {
            NetworkManager.sendGroundShockEffectTo(vecs,(EntityPlayerMP) e);
        }

    }
}
