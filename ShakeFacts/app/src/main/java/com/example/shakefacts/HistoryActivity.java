package com.example.shakefacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Toast;

//import com.example.shakefacts.databinding.ActivityMainBinding;
import com.example.shakefacts.db.FactListDataBase;
import com.example.shakefacts.db.FactListItem;

public class HistoryActivity extends AppCompatActivity {

    private FactListDataBase factListDatabase;
    //private ActivityHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

//        factListDatabase = Room.databaseBuilder(
//                this,
//                FactListDataBase.class,
//                "factlist_db")
//                .fallbackToDestructiveMigration()
//                .build();
//

    }
}