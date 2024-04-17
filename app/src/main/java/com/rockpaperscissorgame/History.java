package com.rockpaperscissorgame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    ArrayList<String> game_id, player_choice, computer_choice, game_result;

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        dbHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        game_id = new ArrayList<>();
        player_choice = new ArrayList<>();
        computer_choice = new ArrayList<>();
        game_result = new ArrayList<>();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, MainActivity.class);
            startActivity(intent);
        });

        displayData();

        MainActivity main = new MainActivity();

    }

    void displayData(){

        Cursor cursor = dbHelper.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }else {
            ArrayList<String> historyList = new ArrayList<>();
            while (cursor.moveToNext()){
                game_id.add(cursor.getString(0));
                player_choice.add(cursor.getString(1));
                computer_choice.add(cursor.getString(2));
                game_result.add(cursor.getString(3));

                String historyEntry = "Match No : " + cursor.getString(0) + "\n"+
                        "Player Choice : " + cursor.getString(1) + "\n" +
                        "Computer Choice : " + cursor.getString(2) + "\n" +
                        "Match Result : " + cursor.getString(3);

                historyList.add(historyEntry);

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);

            ListView historyListView = findViewById(R.id.historyListView);
            historyListView.setAdapter(adapter);
        }
    }
}