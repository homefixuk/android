package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceTypeSkill {

    Problem service_type;
    Skill skill;
    int level;

    public Problem getService_type() {
        return service_type;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
}
