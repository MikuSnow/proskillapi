package com.sucy.skill.hook;

import mc.promcteam.engine.NexEngine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.hook.MythicMobsHook
 */
public class MythicMobsHook {

    public static void taunt(final LivingEntity target, final LivingEntity source, final double amount) {
        NexEngine.get().getMythicMobs().taunt(target, source, amount);
    }

    public static boolean isMonster(final LivingEntity target) {
        return NexEngine.get().getMythicMobs().isMythicMob(target);
    }

    public static String getMythicMobId(Entity entity) {
        return NexEngine.get().getMythicMobs().getMythicNameByEntity(entity);
    }
}
