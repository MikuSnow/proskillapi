package com.sucy.skill.dynamic.condition;

import org.bukkit.entity.LivingEntity;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.condition.Ground
 */
public class GroundCondition extends ConditionComponent {
    private static final String type = "type";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final boolean onGround     = target.isOnGround();
        final boolean wantOnGround = settings.getString(type, "on ground").equalsIgnoreCase("on ground");
        return onGround == wantOnGround;
    }

    @Override
    public String getKey() {
        return "ground";
    }
}
