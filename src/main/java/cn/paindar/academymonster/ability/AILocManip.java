package cn.paindar.academymonster.ability;

import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.helper.BlockPos;
import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.entity.EntityMagManipBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/6/4.
 */
public class AILocManip extends BaseSkill
{
    Map<EntityMagManipBlock,Double> list=new HashMap<>();
    private int time=0;
    private float damage;
    private double lastX,lastY,lastZ;
    private boolean canDestroyBlock= AMConfig.getBoolean("am.skill.LocManip.destroyBlock",true);
    public AILocManip(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(600,300,exp),exp,"teleporter.loc_manip");
        damage=lerpf(7,10,exp);
    }
    public void spell()
    {
        World world=speller.worldObj;
        if(world.isRemote || !canSpell())
            return; //w.getTileEntity(x,y,z)==null &&
        double range=3;
        isChanting=true;
        double rad=0,part=Math.acos(1.0-9.0/2/(range*range));
        lastX=speller.posX;
        lastY=speller.posY;
        lastZ=speller.posZ;
        List<BlockPos> list=WorldUtils.getBlocksWithin(speller, 7, (int)lerpf(25,100,getSkillExp()), BlockSelectors.filNormal,(world1, x, y, z, block) -> world1.getBlock(x,y,z).getBlockHardness(world1,x,y,z)>0);
        for(BlockPos pos:list)
        {
            EntityMagManipBlock entity=  new EntityMagManipBlock(speller, damage,this);
            if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, pos.x,pos.y, pos.z)) &&canDestroyBlock)
            {

                entity.setBlock(pos.getBlock());
                world.setBlockToAir(pos.x,pos.y, pos.z);
            }
            else
                entity.setBlock(Blocks.ice);
            entity.setPosition(pos.x + .5, pos.y + .5, pos.z + .5);
            world.spawnEntityInWorld(entity);
            entity.setPlaceFromServer(false);
            entity.radium=rad;
            this.list.put(entity,range);
            rad+=part;
            if(rad>=2*Math.PI)
            {
                part=Math.acos(1-9.0/2/(range*range));
                rad= RandUtils.rangef(0,(float)part);
                range+=2;
            }
        }
    }

    @Override
    public void onTick()
    {
        if(!isChanting)
            return;
        time++;
        double tx=0,ty=0,tz=0;
        if(speller==null || speller.isDead)
            time=250;
        else
        {
            if (time % 3 != 0)
                return;
            tx = speller.posX - lastX;
            ty = speller.posY - lastY;
            tz = speller.posZ - lastZ;
            lastX = speller.posX;
            lastY = speller.posY;
            lastZ = speller.posZ;
        }
        List<EntityMagManipBlock> tmpList = new ArrayList<>();
        for(Map.Entry<EntityMagManipBlock,Double> pair:list.entrySet())
        {
            EntityMagManipBlock entity=pair.getKey();
            if(entity.isDead)
            {
                tmpList.add(entity);
                continue;
            }
            if(time>=240)
            {
                entity.setPlaceFromServer(true);
                if(canDestroyBlock)
                    entity.stopMoveTo();
                else
                    entity.setDead();
                tmpList.add(entity);
                continue;
            }
            entity.radium=(entity.radium+0.13)%(6.26);
            double rad=entity.radium;
            Vec3 end=Vec3.createVectorHelper(speller.posX+Math.sin(rad)*pair.getValue(),speller.posY+4,speller.posZ+Math.cos(rad)*pair.getValue());
            entity.setPosition(entity.posX+tx,entity.posY+ty,entity.posZ+tz);
            entity.setMoveTo(end.xCoord,end.yCoord,end.zCoord);
        }
        for(EntityMagManipBlock entity:tmpList)
        {
            list.remove(entity);
        }
    }


}
