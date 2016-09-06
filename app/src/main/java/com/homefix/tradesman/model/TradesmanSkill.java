package com.homefix.tradesman.model;

/**
 * Created by samuel on 6/15/2016.
 */

public class TradesmanSkill extends BaseModel {

    private Tradesman tradesman;
    private Skill skill;
    private int level;

    public Tradesman getTradesman() {
        return tradesman;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
}
