package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIPenetrateTeleport;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/2/10.
 */
public class EntityAcademySkeleton extends EntitySkeleton
{

    public List<BaseAbility> abilityList=new ArrayList<>();
    public EntityAcademySkeleton(World world) {
        super(world);
        abilityList.add(new AIElectronBomb(this,1));
        for(BaseAbility skill:abilityList)
        {
            if(skill instanceof AIElectronBomb)
                this.tasks.addTask(4,new EntityAIElectronBomb(this,(AIElectronBomb)skill));
            else if (skill instanceof AIPenetrateTeleport)
                this.tasks.addTask(5,new EntityAIPenetrateTeleport(this,(AIPenetrateTeleport)skill));
        }
    }

}
