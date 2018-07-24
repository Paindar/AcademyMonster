package cn.paindar.academymonster.playerskill;

import cn.academy.ability.api.Category;
import cn.academy.ability.api.Controllable;
import cn.academy.ability.api.Skill;
import cn.academy.vanilla.ModuleVanilla;
import cn.academy.vanilla.electromaster.CatElectromaster;
import cn.academy.vanilla.electromaster.skill.Railgun$;
import cn.academy.vanilla.meltdowner.CatMeltdowner;
import cn.academy.vanilla.meltdowner.skill.Meltdowner$;
import cn.paindar.academymonster.core.AcademyMonster;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

import static cn.academy.vanilla.electromaster.CatElectromaster.thunderBolt;

public class HookLoader {
    public static final Skill railgun_ = cn.paindar.academymonster.playerskill.electromaster.Railgun.instance;
    public static final Skill meltdowner_ = cn.paindar.academymonster.playerskill.meltdowner.Meltdowner.instance;
    public static void init()
    {
        railgun_.setPosition(164, 59);
        railgun_.setParent(thunderBolt);

        if(removeSkill(CatElectromaster.railgun)) {
            ModuleVanilla.electromaster.addSkill(railgun_);
        }

        if(removeSkill(CatMeltdowner.meltdowner)) {
            meltdowner_.setPosition(115, 40);
            setParentSkill(meltdowner_, CatMeltdowner.scatterBomb);
            //meltdowner.addSkillDep(lightShield, 0.8f);
            if(setParentSkill(CatMeltdowner.mineRayBasic, meltdowner_) &&
                    setParentSkill(CatMeltdowner.rayBarrage, meltdowner_) &&
                    setParentSkill(CatMeltdowner.jetEngine, meltdowner_) &&
                    setParentSkill(CatMeltdowner.mineRayLuck, meltdowner_)){
                ModuleVanilla.meltdowner.addSkill(meltdowner_);
            }
            else{
                AcademyMonster.log.error("Failed to replace meltdowner's skill.");
                ModuleVanilla.meltdowner.addSkill(Meltdowner$.MODULE$);
                setParentSkill(CatMeltdowner.mineRayBasic, Meltdowner$.MODULE$);
                setParentSkill(CatMeltdowner.rayBarrage, Meltdowner$.MODULE$);
                setParentSkill(CatMeltdowner.jetEngine, Meltdowner$.MODULE$);
                setParentSkill(CatMeltdowner.mineRayLuck, Meltdowner$.MODULE$);
            }
        }

    }

    private static boolean removeSkill(Skill skill){
        Field field;
        Category category = skill.getCategory();
        try {
            field = Category.class.getDeclaredField("skillList");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
        List<Skill> skillList=null;
        try {
            skillList = (List<Skill>) field.get(category);
            skillList.remove(skill);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        try {
            field = Category.class.getDeclaredField("ctrlList");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
        try {
            List<Controllable> ctrlList = (List<Controllable>) field.get(category);
            ctrlList.clear();
            Field idF=Skill.class.getDeclaredField("id"),
                contF = Controllable.class.getDeclaredField("id");
            idF.setAccessible(true);
            contF.setAccessible(true);
            for (int i=0;i<skillList.size();i++){
                Skill s = skillList.get(i);
                idF.set(s, i);
                contF.set(s, i);
                ctrlList.add(s);
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean setParentSkill(Skill child, Skill parent)
    {
        try {
            Field field;
            field = Skill.class.getDeclaredField("parent");
            field.setAccessible(true);
            field.set(child, parent);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
