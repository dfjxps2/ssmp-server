package com.seaboxdata.auth.common;

public enum ApplicationNameEnum {

    MDS_SERVER("http://MDS-SERVER"),
    DS_SERVER("http://DS-SERVER"),
    AUTH_SERVER("http://DS-SERVER"),
    DQS_SERVER("http://DQS-SERVER");

    private final String name;

    private ApplicationNameEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
