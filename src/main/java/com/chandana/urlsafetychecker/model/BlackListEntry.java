package com.chandana.urlsafetychecker.model;

import jakarta.persistence.*;

@Entity
public class BlackListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String value;

    @Enumerated(EnumType.STRING)
    private BlackListType type;

    public Long getId() {
        return Id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BlackListType getType() {
        return type;
    }

    public void setType(BlackListType type) {
        this.type = type;
    }
}
