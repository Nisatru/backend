package com.WWI16AMA.backend_api.Member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public enum Office {
    FLUGWART("Flugwart"),
    IMBETRIEBSKONTROLLTURMARBEITEND("ImBetriebskontrollturmArbeitend"),
    KASSIERER("Kassierer"),
    SYSTEMADMINISTRATOR("Systemadministrator"),
    VORSTANDSVORSITZENDER("Vorstandsvorsitzender");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;

    Office(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title.toUpperCase();
    }

    public String title() {
        return title;
    }
}

