package com.todoapp;

/**
 * Created by User on 05.09.2015.
 */
public enum Subject {
    RUSSIAN("Russian"),
    SAKHA("Sakha"),
    PHYSICS("Physics"),
    LITERATURE("Literature"),
    GEOGRAPHY("Geography"),
    HISTORY("History"),
    IT("IT"),
    Algebra("Algebra"),
    DRAWING("Drawing"),
    ENGLISH("English"),
    BIOLOGY("Biology"),
    CHEMISTRY("Chemistry"),
    ECONOMICS_E("Economics (E)"),
    SAKHA_CULTURE("Sakha Culture"),
    COMMENTARY("Commentary");

    private final String localeName;

    Subject(String name) {
        this.localeName = name;
    }

    public String getLocaleName() {
        return localeName;
    }
}
