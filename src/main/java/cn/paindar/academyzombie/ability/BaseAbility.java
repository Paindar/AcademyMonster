package cn.paindar.academyzombie.ability;

import cn.academy.ability.api.Category;
import cn.academy.ability.api.CategoryManager;
import cn.academy.ability.api.Controllable;
import cn.lambdalib.annoreg.core.Registrant;
import cn.lambdalib.annoreg.mc.RegInitCallback;
import cn.lambdalib.s11n.nbt.NBTS11n;
import cn.lambdalib.s11n.network.NetworkS11n;
import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academyzombie.core.AcademyZombie;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/9.
 */

public abstract class BaseAbility
{
    private int maxCooldown;
    protected int remainCooldown=0;
    private float skillExp;
    protected boolean isChanting=false;
    protected Vec3 targetPos;
    public BaseAbility(int maxCooldown,float abilityExp)
    {
        this.maxCooldown=maxCooldown;
        this.skillExp=abilityExp;
        FMLCommonHandler.instance().bus().register(this);
    }

    public float getSkillExp(){return skillExp;}

    protected int getMaxCooldown(){return maxCooldown;}

    public boolean isSkillInCooldown(){return remainCooldown!=0;}

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        if(remainCooldown>0)
            remainCooldown--;
    }

    public void spell()
    {
        remainCooldown=maxCooldown;
    }


}
