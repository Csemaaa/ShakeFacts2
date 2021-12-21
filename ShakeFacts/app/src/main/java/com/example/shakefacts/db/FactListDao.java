package com.example.shakefacts.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface FactListDao {
    @Insert
    public void insertNewItem(FactListItem fli);
    @Query("SELECT * FROM FactList")
    public LiveData<List<FactListItem>> getAllItems();
    @Query("DELETE FROM FactList")
    public void clearDatabase();
    @Delete
    public void deleteItem(FactListItem fli);
}
