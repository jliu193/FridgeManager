package com.example.fridgemanager;

import java.time.LocalDate;

public class ItemDomainObject extends ItemPersistentObject {

    private Integer id;

    public ItemDomainObject(Integer id, String name, Integer count, LocalDate expiredDate) {
        super(name, count, expiredDate);
        this.id = id;
    }

    @Override
    public String toString() {
        return "ItemDomainObject{" +
                "id='" + id + '\'' +
                ", " + super.toString() +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return super.getName();
    }

    public Integer getCount() {
        return super.getCount();
    }

    public LocalDate getExpiredDate() {
        return super.getExpiredDate();
    }

}
