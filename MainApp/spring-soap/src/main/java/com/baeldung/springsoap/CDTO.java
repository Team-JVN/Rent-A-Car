package com.baeldung.springsoap;

public class CDTO {
    private String name;

    public CDTO(String name) {
        this.name = name;
    }

    public CDTO(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
