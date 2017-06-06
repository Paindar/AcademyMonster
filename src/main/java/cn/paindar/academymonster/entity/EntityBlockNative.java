package cn.paindar.academymonster.entity;

import cn.academy.core.entity.EntityBlock;
import cn.lambdalib.annoreg.core.Registrant;
import cn.lambdalib.annoreg.mc.RegEntity;
import cn.lambdalib.s11n.network.NetworkMessage;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/6/4.
 */
public class EntityBlockNative extends EntityBlock
{

    public EntityBlockNative()
    {
        super((EntityPlayer)null);
    }

    public EntityBlockNative(World world)
    {
        super(world);
    }

}
