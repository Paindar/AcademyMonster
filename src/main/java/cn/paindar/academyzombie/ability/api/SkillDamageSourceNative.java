package cn.paindar.academyzombie.ability.api;

import cn.paindar.academyzombie.ability.BaseAbility;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;

/**
 * Created by Paindar on 2017/2/10.
 */
public class SkillDamageSourceNative extends EntityDamageSource {

    public final BaseAbility skill;

    public SkillDamageSourceNative(EntityLivingBase speller, BaseAbility skill) {
        super("am_skill", speller);
        this.skill = skill;
    }

    // Chat display
    @Override
    public IChatComponent func_151519_b(EntityLivingBase target) {
        return new ChatComponentTranslation("death.attack.ac_skill",
                target.getCommandSenderName(),
                this.damageSourceEntity.getCommandSenderName(),
                skill.getSkillName());
    }

}
