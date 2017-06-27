package com.wewins.facelibrary.utils;

/**
 * Created by minyuchun on 2017/6/16.
 */

public class Person {
    private String imgPath;
    private String personName;

    public Person() {
    }

    public Person(String imgPath, String personName) {
        this.imgPath = imgPath;
        this.personName = personName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "imgPath='" + imgPath + '\'' +
                ", personName='" + personName + '\'' +
                '}';
    }
}
