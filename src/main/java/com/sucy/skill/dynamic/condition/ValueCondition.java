/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.ValueCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class ValueCondition extends ConditionComponent {
    private static final String KEY = "key";
    private static final String MIN = "min-value";
    private static final String MAX = "max-value";

    @Override
    public String getKey() {
        return "value";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        return test(caster, level, null) && executeChildren(caster, level, targets, force);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final String key  = settings.getString(KEY);
        final double min  = parseValues(caster, MIN, level, 1);
        final double max  = parseValues(caster, MAX, level, 999);
        final Object data = DynamicSkill.getCastData(caster).get(key);

        if (data != null) {
            double value = (Double) data;
            return value >= min && value <= max;
        }

        return false;
    }
}
