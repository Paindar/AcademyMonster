package cn.paindar.academymonster.ability.api;

import cn.paindar.academymonster.ability.BaseSkill;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

/**
 * Created by Paindar on 2017/2/10.
 */
public class SkillDamageSourceNative extends EntityDamageSource {

    public final BaseSkill skill;

    public SkillDamageSourceNative(EntityLivingBase speller, BaseSkill skill) {
        super("am_skill", speller);
        this.skill = skill;
    }

    // Chat display
    @Override
    public IChatComponent func_151519_b(EntityLivingBase target) {
        return new ChatComponentTranslation("death.attack.ac_skill",
                target.getCommandSenderName(),
                this.damageSourceEntity.getCommandSenderName(),
                StatCollector.translateToLocal(skill.getUnlocalizedSkillName()));
    }

}
