package cn.paindar.academymonster.core.support.tile;

import cn.academy.ability.block.TileAbilityInterferer;
import cn.academy.crafting.block.BlockAbilityInterferer;
import cn.lambdalib.util.mc.WorldUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

/**
 * Created by Paindar on 2017/3/23.
 */
public class AbilityInterfManager
{
    private Set<Entity> affectEntity=new LinkedHashSet<>();
    private int maxTick=5;
    private int tick=0;
    public static AbilityInterfManager instance=new AbilityInterfManager();
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if(tick++==maxTick)
        {
            update();
            tick=0;
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event)
    {
        if(event.placedBlock instanceof BlockAbilityInterferer)
        {
            int pos[]={event.x,event.y,event.z};
            AMWorldData data=AMWorldData.get(event.world);
            data.set.add(pos);
            data.markDirty();
        }
    }

    private void update()
    {
        WorldServer[] worlds= DimensionManager.getWorlds();
        affectEntity.clear();
        for(WorldServer world:worlds)
        {
            if(world==null)
                continue;
            Set<int[]> set=AMWorldData.get(world).set;
            for(int[] pos:set)
            {
                TileEntity tile=world.getTileEntity(pos[0],pos[1],pos[2]);
                if(tile instanceof TileAbilityInterferer)
                {
                    TileAbilityInterferer tAI=(TileAbilityInterferer)tile;
                    if(tAI.enabled())
                    {
                        List<Entity> list= WorldUtils.getEntities(tAI,tAI.range(),(Entity e)->((e instanceof IMob)&& !e.isDead));
                        affectEntity.addAll(list);
                    }
                }
                else
                    set.remove(pos);
            }
        }
    }

    public boolean find(Entity e){return affectEntity.contains(e);}

}
