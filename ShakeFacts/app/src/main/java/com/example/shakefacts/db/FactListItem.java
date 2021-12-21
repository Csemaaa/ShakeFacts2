package com.example.shakefacts.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FactList")
public class FactListItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int itemID;
    private String itemName;
    private String factName;

    public int getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getFactName() {
        return factName;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setFactName(String factName) {
        this.factName = factName;
    }

    @Override
    public String toString() {
        return "FactListItem{" +
                "itemID=" + itemID +
                ", itemName='" + itemName + '\'' +
                ", factName='" + factName + '\'' +
                '}';
    }
}
