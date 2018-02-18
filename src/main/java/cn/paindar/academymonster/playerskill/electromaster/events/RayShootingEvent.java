package cn.paindar.academymonster.playerskill.electromaster.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;

public class RayShootingEvent extends Event {
    public EntityLivingBase source;
    public EntityLivingBase target;
    public double damage;
    public RayShootingEvent(EntityLivingBase source, EntityLivingBase target, double damage){
        this.source=source;
        this.target=target;
        this.damage=damage;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }
}
