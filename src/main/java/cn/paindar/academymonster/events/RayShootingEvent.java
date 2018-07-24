package cn.paindar.academymonster.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;

/**
 * Post by AIRailgun and AIMeltdowner.
 * This event will be post when any entity is going to attack by one of these skill.
 * This event is cancelable, which means this entity reflect attack.
 * Its range is how far skill can extend.
 */
public class RayShootingEvent extends Event {
    public final EntityLivingBase source;
    public final EntityLivingBase target;
    public double range;
    public RayShootingEvent(EntityLivingBase source, EntityLivingBase target, double range){
        this.source=source;
        this.target=target;
        this.range=range;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }
}
