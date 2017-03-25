package cn.paindar.academymonster.core.support.tile;

import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Paindar on 2017/3/23.
 */
public class AMWorldData extends WorldSavedData
{
    private static String TAG= AcademyMonster.MODID;
    Set<int[]> set=new LinkedHashSet<>();
    public static AMWorldData get(World world)
    {
        WorldSavedData data = world.perWorldStorage.loadData(AMWorldData.class, TAG);
        if (data == null)
        {
            data = new AMWorldData(TAG);
            world.perWorldStorage.setData(TAG, data);
        }
        return (AMWorldData) data;
    }

    public AMWorldData(String name)
    {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        int size=nbt.getInteger("AMtAIM_size");
        for(int i=0;i<size;i++)
        {
            int[] pos=nbt.getIntArray(String.valueOf(i));
            set.add(pos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        int i=0;
        for(int[] value:set)
        {
            nbt.setIntArray(String.valueOf(i++),value);
        }
        nbt.setInteger("AMtAIM_size",i);
    }
}
