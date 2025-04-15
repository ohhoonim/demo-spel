package com.ohhoonim.demo_spel.inventor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Society {
    private String name;

    public static final String ADVISORS = "advisors";
    public static final String PRESIDENT = "president";

    private List<Inventor> members = new ArrayList<>();
    private Map<String, Inventor> officers = new HashMap<>();

    public List<Inventor> getMembers() {
        return members;
    }

    public Map<String, Inventor> getOfficers() {
        return officers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMember(String name) {
        return members.stream()
                .filter(m -> m.getName().equals(name)).findFirst()
                .isPresent();
    }
}
