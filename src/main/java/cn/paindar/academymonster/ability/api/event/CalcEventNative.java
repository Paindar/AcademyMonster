package cn.paindar.academymonster.ability.api.event;

import cn.academy.ability.api.event.CalcEvent;
import cn.paindar.academymonster.ability.BaseSkill;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Paindar on 2017/2/10.
 */
public class CalcEventNative<T> extends Event {
    public T value;

    public static <T> T calc(CalcEvent<T> evt) {
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.value;
    }

    public static class NativeCalcEvent<T> extends CalcEvent<T> {

        public final EntityLivingBase player;

        public NativeCalcEvent(EntityLivingBase _player, T initial) {
            super(initial);
            player = _player;
        }

    }

    public static class SkillAttack extends CalcEventNative.NativeCalcEvent<Float> {

        public final BaseSkill skill;
        public final Entity target;

        public SkillAttack(EntityLivingBase player, BaseSkill _skill, Entity _target, float initial) {
            super(player, initial);
            skill = _skill;
            target = _target;
        }

    }


}
