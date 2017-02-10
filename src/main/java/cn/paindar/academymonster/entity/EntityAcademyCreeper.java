package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.AIBodyIntensify;
import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIBodyIntensify;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIPenetrateTeleport;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/2/10.
 */
public class EntityAcademyCreeper extends EntityCreeper
{
    float explosionRadius;
    public List<BaseAbility> abilityList=new ArrayList<>();
    public EntityAcademyCreeper(World world)
    {
        super(world);
        explosionRadius=3;
        abilityList.add(new AIBodyIntensify(this,1));
        for(BaseAbility skill:abilityList)
        {
            if(skill instanceof AIElectronBomb)
                this.tasks.addTask(4,new EntityAIElectronBomb(this,(AIElectronBomb)skill));
            else if (skill instanceof AIPenetrateTeleport)
                this.tasks.addTask(5,new EntityAIPenetrateTeleport(this,(AIPenetrateTeleport)skill));
            else if (skill instanceof AIBodyIntensify)
                this.tasks.addTask(5,new EntityAIBodyIntensify(this,(AIBodyIntensify)skill));
        }
    }

//    public void func_146077_cc()
//    {
//        if (!this.worldObj.isRemote)
//        {
//            boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
//
//            if (this.getPowered())
//            {
//                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(this.explosionRadius * 2), flag);
//            }
//            else
//            {
//                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, flag);
//            }
//
//            this.setDead();
//        }
//    }
}
