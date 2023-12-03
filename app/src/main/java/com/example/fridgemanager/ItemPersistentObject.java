package com.example.fridgemanager;

import java.time.LocalDate;

public class ItemPersistentObject {

    private String name;
    private Integer count;
    private LocalDate expired;

    public ItemPersistentObject(String name, Integer count, LocalDate expired) {
        this.name = name;
        this.count = count;
        this.expired = expired;
    }


    @Override
    public String toString() {
        return "ItemPersistentObject{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", expired=" + expired +
                '}';
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public LocalDate getExpiredDate() {
        return expired;
    }

}
