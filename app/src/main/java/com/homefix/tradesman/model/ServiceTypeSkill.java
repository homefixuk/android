package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class ServiceTypeSkill extends BaseModel {

    private Problem serviceType;
    private Skill skill;
    private int level;

    public Problem getServiceType() {
        return serviceType;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
}
