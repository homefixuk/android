package com.homefix.tradesman.model;

import com.samdroid.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 6/15/2016.
 */

public class Problem {

    private String name, description;
    private long time;
    private List<Part> potentialParts;

    public Problem() {
        super();
    }

    public String getName() {
        return Strings.returnSafely(name);
    }

    public String getDescription() {
        return Strings.returnSafely(description);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public List<Part> getPotentialParts() {
        if (potentialParts == null) potentialParts = new ArrayList<>();

        return potentialParts;
    }

    public void setTime(long time) {
        this.time = time;
    }

    final private static List<Problem> M_PROBLEMs = new ArrayList<>();

    public synchronized static List<Problem> getProblemTypes() {
        return M_PROBLEMs;
    }

    public static List<String> getProblemTypeNames() {
        List<String> names = new ArrayList<>();

        List<Problem> types = getProblemTypes();

        for (int i = 0, len = types.size(); i < len; i++) names.add(types.get(i).getName());

        return names;
    }

}
