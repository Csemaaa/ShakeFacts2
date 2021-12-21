package com.example.shakefacts.db;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FactListItem.class}, version = 2, exportSchema = false)
public abstract class FactListDataBase extends RoomDatabase {
    public abstract FactListDao factListDAO();
}

