package cn.paindar.academymonster.ability;

import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
/**
 * Created by voidcl on 2017/3/19.
 */
public class AIShiftTeleport extends BaseSkill{
    private float exp;
    private float maxdistance;
    public AIShiftTeleport(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(40,20,exp),exp,"teleporter.shift_tp");
        this.exp=exp;
        maxdistance=lerpf(3,10,getSkillExp());
    }


    public float getMaxdistance()
    {
        return maxdistance;
    }
    public void spell()
    {
        MovingObjectPosition result=Raytrace.traceLiving(speller,2,EntitySelectors.living());
        EntityLivingBase target=null;
        if(result!=null&&result.typeOfHit==MovingObjectPosition.MovingObjectType.ENTITY)
        {
            target=(EntityLivingBase)result.entityHit;
        }

        if(isSkillInCooldown()||target==null)
            return;
        int ix=(int)target.posX+RandUtils.nextInt(1)-RandUtils.nextInt(1);int iy=(int)target.posY+1;int iz=(int)target.posZ;
        World world=speller.worldObj;
        Block b=world.getBlock((int)Math.floor(speller.posX),(int)Math.floor(speller.posY)-1,(int)Math.floor(speller.posZ));
        if(b.isCollidable()&&!b.getMaterial().isReplaceable()&&b.getBlockHardness(world,(int)Math.floor(speller.posX),(int)Math.floor(speller.posY)-1,(int)Math.floor(speller.posZ))>=0)
        {
            world.setBlock(ix, iy, iz, b);
            world.setBlockToAir((int) speller.posX, (int) speller.posY -1, (int) speller.posZ);
            List<Entity> list = WorldUtils.getEntities(speller, 25, EntitySelectors.player());
            for (Entity e : list) {
                NetworkManager.sendSoundTo("tp.tp", target, .5f, (EntityPlayerMP) e);
            }

        }else
        {
            return;
        }
        super.spell();
    }
}
