package com.rockpaperscissorgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static int userIndex;
    private DatabaseHelper dbHelper;
   private static String selectedHandSign;
    private static final String[] handSigns = {"Rock", "Paper", "Scissors"};
    private int wins = 0;
    private int losses = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView rockImage = findViewById(R.id.rockImage);
        ImageView paperImage = findViewById(R.id.paperImage);
        ImageView scissorsImage = findViewById(R.id.scissorsImage);
        Button playButton = findViewById(R.id.playButton);
        Button historyButton = findViewById(R.id.historyButton);
        TextView playerSelects = findViewById(R.id.playerSelects);
        TextView botSelects = findViewById(R.id.botSelects);
        TextView reset = findViewById(R.id.reset);


        dbHelper = new DatabaseHelper(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rockImage.setOnClickListener(v -> {
             userIndex = 0;
            selectedHandSign = "Rock";
            botSelects.setText(null);
            playerSelects.setText(selectedHandSign);


        });

        paperImage.setOnClickListener(v -> {
             userIndex = 1;
            selectedHandSign = "Paper";
            botSelects.setText(null);
            playerSelects.setText(selectedHandSign);
        });

        scissorsImage.setOnClickListener(v -> {
             userIndex = 2;
            selectedHandSign = "Scissors";
            botSelects.setText(null);
            playerSelects.setText(selectedHandSign);
        });

        playButton.setOnClickListener(v -> {
            playGame();
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(v -> {
            wins = 0;
            losses = 0;
            saveWinsAndLosses(wins,losses);
            TextView lblWin = findViewById(R.id.lblWin);
            lblWin.setText(String.valueOf(wins));

            TextView lblLose = findViewById(R.id.lblLose);
            lblLose.setText(String.valueOf(losses));

        });



    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).cancel();
            }
        }, 1000);
    }

    private void playGame() {
        TextView botSelects = findViewById(R.id.botSelects);
        TextView whoWins = findViewById(R.id.lblWhoWins);
        TextView userVsComputer = findViewById(R.id.userVsComputer);

        String result;

        if (selectedHandSign == null) {
            displayToast("Please select a hand sign first");
            return;
        }

        Random random = new Random();
        int computerIndex = random.nextInt(handSigns.length);
        String computerHandSign = handSigns[computerIndex];

        String userHandSign = handSigns[userIndex];

        int userImage = getImage(userHandSign);
        int computerImage = getImage(computerHandSign);

        ImageView dispHands = findViewById(R.id.dispHands);
        ImageView userHands = findViewById(R.id.userHands);
        ImageView computerHands = findViewById(R.id.computerHands);

        dispHands.setVisibility(View.INVISIBLE);
        userHands.setImageResource(userImage);
        computerHands.setImageResource(computerImage);

        if (selectedHandSign.equals(computerHandSign)) {
            result = "It's a tie!";
            whoWins.setText(result);
        } else if ((selectedHandSign.equals("Rock") && computerHandSign.equals("Scissors")) ||
                (selectedHandSign.equals("Paper") && computerHandSign.equals("Rock")) ||
                (selectedHandSign.equals("Scissors") && computerHandSign.equals("Paper"))) {
            result = "Player Wins!";
            whoWins.setText(result);
            wins++;
            saveWinsAndLosses(wins, losses);
        } else {
            result = "Computer Wins!";
            whoWins.setText(result);
            losses++;
            saveWinsAndLosses(wins, losses);
        }

        botSelects.setText(computerHandSign);

        TextView lblWin = findViewById(R.id.lblWin);
        lblWin.setText(String.valueOf(wins));

        TextView lblLose = findViewById(R.id.lblLose);
        lblLose.setText(String.valueOf(losses));

        String userVbot = "Player : " + selectedHandSign + " VS " + "Bot : " + computerHandSign;
        userVsComputer.setText(userVbot);

        final String playerChoice = selectedHandSign;
        final String computerChoice = computerHandSign;
        final String whoWin = result.trim();


        try{
            dbHelper.addToHistory(playerChoice, computerChoice, whoWin);
        } catch (Exception e){
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        selectedHandSign = null;

    }

    private int getImage(String handSign) {
        int image;
        switch (handSign) {
            case "Rock":
                image = R.drawable.rock1;
                break;
            case "Paper":
                image = R.drawable.paper1;
                break;
            case "Scissors":
                image = R.drawable.scissors1;
                break;
            default:
                image = R.drawable.default_image;
        }
        return image;
    }

    private void saveWinsAndLosses(int wins, int losses) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("wins", wins);
        editor.putInt("losses", losses);
        editor.apply();
    }

    private void loadWinsAndLosses() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        wins = sharedPreferences.getInt("wins", 0);
        losses = sharedPreferences.getInt("losses", 0);
    }


    protected void onResume() {
        super.onResume();
        loadWinsAndLosses();

        TextView lblWin = findViewById(R.id.lblWin);
        lblWin.setText(String.valueOf(wins));

        TextView lblLose = findViewById(R.id.lblLose);
        lblLose.setText(String.valueOf(losses));

    }



}