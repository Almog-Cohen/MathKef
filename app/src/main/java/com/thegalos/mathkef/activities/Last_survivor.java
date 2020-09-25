package com.thegalos.mathkef.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.objects.MultiChoiceQuestion;
import com.thegalos.mathkef.objects.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Size;

public class Last_survivor extends AppCompatActivity {

    TextView tvName0, tvScore0, tvName1, tvScore1, tvScore2, tvName2, tvName3, tvScore3;
    ImageView ivHeart00, ivHeart01, ivHeart02, ivHeart10, ivHeart11, ivHeart12, ivHeart20, ivHeart21, ivHeart22, ivHeart30, ivHeart31, ivHeart32;
    ImageView ivFigure0, ivFigure1, ivFigure2, ivFigure3, ivTurnFigure;
    AnimationDrawable adTurnFigure;

    Button btnOne, btnTwo, btnThree, btnFour, btnHelp5050;
    ConstraintLayout ConstraintPlayer1, ConstraintPlayer2, ConstraintPlayer3;
    TextView tvQuestionLS, tvTimer, tvQuestionCounter,tvTimeTOStartSurvivor, tvAnswered, tvPlayerTurn;
    LinearLayout linearLayout;


    int playerNum = 0;

    private static final int loadingScreenTime = 4000;

    //Players score
    int player0Score = 0;
    int player1Score = 0;
    int player2Score = 0;
    int player3Score = 0;

    //Players Lives
    int player0Live = 4;
    int player1Live = 4;
    int player2Live = 4;
    int player3Live = 4;

    //Players question answered correct counter
    int player0Counter = 0;
    int player1Counter = 0;
    int player2Counter = 0;
    int player3Counter = 0;

    //Give one hint each to each player

    String questionAnswered;
    Animation shakeAnim;




    //Question operators
    boolean addition, subtraction, multiply, division;

    //Player playing correctly
    private String playerId;
    private String playerLeftId;
    private boolean isPlayerTurn;

    private String roomName;
    private String gameType;

    private String isClicked;

    private String correctQuestion;

    private CountDownTimer countDownTimer;
    CountDownTimer afkTimer;

    private Queue<Player> playersTurnQueue;
    private Queue<Integer> playersNumTurnQueue;
    private List<String> playersNamesList;
    private ArrayList<Player> playersList;


    private List<MultiChoiceQuestion> tempQList;
    private List<MultiChoiceQuestion> MultiChoiceQuestionList;



    private List<Integer> randomQuestionList;
    private List<Integer> randomMultiList;

    SharedPreferences sp;
    FirebaseUser user;

    DatabaseReference playerLeftRef,turnRef,isClickedRef, selectedAnswerRef, randomNumRef;
    ValueEventListener playerLeftListener,turnRefListener,clickedListener, selectedAnswerListener;
    private boolean playerLost = false;

    KonfettiView lastSurvivorKonfetti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_survivor);

        init();

        clickedListener =  isClickedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    isClicked = (dataSnapshot.getValue(String.class));
                    if (isClicked != null)
                        if (isClicked.equals("Yes")) {
                            checkAnswer(true);
                            if (isPlayerTurn)
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Empty");

                            switchingTurns();
                        } else if (isClicked.equals("No")) {
                            checkAnswer(false);
                            if (isPlayerTurn)
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Empty");
                            switchingTurns();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        playerLeftListener = playerLeftRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (playersTurnQueue.size() > 0) {
                        playerLeftId = dataSnapshot.getValue(String.class);
                        int playerNumLeft = 0;
                        if (playersTurnQueue.peek().getPlayerUID().equals(playerLeftId)) {
                            playersTurnQueue.remove();
                            playerNumLeft = playersNumTurnQueue.peek();
                            playersNumTurnQueue.remove();
                        } else {
                            Player firstPlayer = playersTurnQueue.peek();
                            int firstPlayerNum = playersNumTurnQueue.peek();
                            playersTurnQueue.remove();
                            playersNumTurnQueue.remove();
                            playersTurnQueue.add(firstPlayer);
                            playersNumTurnQueue.add(firstPlayerNum);
                            while (!playersTurnQueue.peek().getPlayerUID().equals(firstPlayer.getPlayerUID())) {

                                Player lastPlayerRemoved = playersTurnQueue.peek();
                                int lastPlayerRemovedNum = playersNumTurnQueue.peek();
                                playersTurnQueue.remove();
                                playerNumLeft = playersNumTurnQueue.peek();
                                playersNumTurnQueue.remove();
                                if (!lastPlayerRemoved.getPlayerUID().equals(playerLeftId)) {
                                    playersTurnQueue.add(lastPlayerRemoved);
                                    playersNumTurnQueue.add(lastPlayerRemovedNum);
                                }
                            }
                        }

                        switch (playerNum) {
                            case 0:
                                switch (playerNumLeft) {
                                    case 1:
                                        ConstraintPlayer1.setVisibility(View.INVISIBLE);
                                        break;
                                    case 2:
                                        ConstraintPlayer2.setVisibility(View.INVISIBLE);
                                        break;
                                    case 3:
                                        ConstraintPlayer3.setVisibility(View.INVISIBLE);
                                        break;

                                }
                                break;

                            case 1:
                                switch (playerNumLeft) {
                                    case 0:
                                        ConstraintPlayer1.setVisibility(View.INVISIBLE);
                                        break;
                                    case 2:
                                        ConstraintPlayer2.setVisibility(View.INVISIBLE);
                                        break;
                                    case 3:
                                        ConstraintPlayer3.setVisibility(View.INVISIBLE);
                                        break;

                                }
                                break;

                            case 2:
                                switch (playerNumLeft) {
                                    case 0:
                                        ConstraintPlayer1.setVisibility(View.INVISIBLE);
                                        break;
                                    case 1:
                                        ConstraintPlayer2.setVisibility(View.INVISIBLE);
                                        break;
                                    case 3:
                                        ConstraintPlayer3.setVisibility(View.INVISIBLE);
                                        break;

                                }
                                break;

                            case 3:
                                switch (playerNumLeft) {
                                    case 0:
                                        ConstraintPlayer1.setVisibility(View.INVISIBLE);
                                        break;
                                    case 1:
                                        ConstraintPlayer2.setVisibility(View.INVISIBLE);
                                        break;
                                    case 2:
                                        ConstraintPlayer3.setVisibility(View.INVISIBLE);
                                        break;
                                }
                                break;
                        }

                        if (playerLeftId.equals(playerId)) {

                            if (playersTurnQueue.size() == 0)
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).setValue(null);
                            closeListeners();

                        } else if (playersTurnQueue.size() == 0) {
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).setValue(null);
                        } else {

                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);
                        }


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        selectedAnswerListener = selectedAnswerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String str = dataSnapshot.child("Player").child("playerName").getValue(String.class) + "\n" + getString(R.string.has_answered) + " " + dataSnapshot.child("Answer").getValue(String.class);
                    tvAnswered.setText(str);
                    tvAnswered.setVisibility(View.VISIBLE);
                    tvAnswered.startAnimation(shakeAnim);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        turnRefListener = turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isPlayerTurn = (dataSnapshot.getValue(boolean.class));
                    if (isPlayerTurn) {
                        btnOne.setVisibility(View.VISIBLE);
                        btnTwo.setVisibility(View.VISIBLE);
                        btnThree.setVisibility(View.VISIBLE);
                        btnFour.setVisibility(View.VISIBLE);
                        btnHelp5050.setVisibility(View.VISIBLE);

                    } else {
                        btnOne.setVisibility(View.INVISIBLE);
                        btnTwo.setVisibility(View.INVISIBLE);
                        btnThree.setVisibility(View.INVISIBLE);
                        btnFour.setVisibility(View.INVISIBLE);
                        btnHelp5050.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        randomNumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    randomQuestionList.add(Integer.valueOf(dataSnapshot.getKey()));
                    randomMultiList.add(dataSnapshot.getValue(Integer.class));
                }
                setQuestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake2);
        shakeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvAnswered.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvAnswered.setVisibility(View.INVISIBLE);
                new CountDownTimer(500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {
                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        //Getting players names
        playersNamesList = new ArrayList<>(getIntent().getStringArrayListExtra("Players_Names_List"));

        //Getting our players turn queue , and setting the host to the first turn
        playersTurnQueue = new LinkedList<>((Collection<? extends Player>) getIntent().getSerializableExtra("Players_List"));
        playersList = new ArrayList<>((Collection<? extends Player>) getIntent().getSerializableExtra("Players_List"));


        if (playersTurnQueue.size()>0)
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);


        //Enable/disable players tables
        switch (playersTurnQueue.size()) {

            case 1:
                ConstraintPlayer1.setVisibility(View.GONE);
                ConstraintPlayer2.setVisibility(View.GONE);
                ConstraintPlayer3.setVisibility(View.GONE);
                break;

            case 2:
                ConstraintPlayer2.setVisibility(View.GONE);
                ConstraintPlayer3.setVisibility(View.GONE);
                break;

            case 3:
                ConstraintPlayer3.setVisibility(View.GONE);
                break;

        }

        playersNumTurnQueue = new LinkedList<>();
        for (int i = 0 ; i < playersTurnQueue.size() ; i ++)
            playersNumTurnQueue.add(i);

        //Mange game layout for players
        if (!playersTurnQueue.peek().getPlayerUID().equals(playerId)) {
            Player firstPlayer = playersTurnQueue.peek();
            playersTurnQueue.remove();
            playersTurnQueue.add(firstPlayer);
            boolean isPlayer = true ;
            while (!playersTurnQueue.peek().getPlayerUID().equals(firstPlayer.getPlayerUID())) {

                if (isPlayer)
                    playerNum++;

                if (playersTurnQueue.peek().getPlayerUID().equals(playerId)) {
                    tvName0.setText(playersTurnQueue.peek().getPlayerName());
                    Player lastPlayer = playersTurnQueue.peek();
                    playersTurnQueue.remove();
                    playersTurnQueue.add(lastPlayer);
                    isPlayer = false;
                } else {
                    Player lastPlayerRemoved = playersTurnQueue.peek();
                    playersTurnQueue.remove();
                    playersTurnQueue.add(lastPlayerRemoved);
                }
            }
        }

        switch (playerNum) {
            case 0:
                tvScore0.setText(String.valueOf(player0Score));
                tvQuestionCounter.setText(String.valueOf(player0Counter));
                tvName0.setText(playersNamesList.get(0));
                switch (playersList.get(0).getPlayerFigure()) {
                    case "Knight":
                        ivFigure0.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure0.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure0.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure0.setImageResource(R.drawable.animation_dog);
                        break;
                }

                if (playersNamesList.size() > 1) {
                    tvName1.setText(playersNamesList.get(1));
                    switch (playersList.get(1).getPlayerFigure()) {
                        case "Knight":
                            ivFigure1.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure1.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure1.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure1.setImageResource(R.drawable.animation_dog);
                            break;
                    }
                    tvScore1.setText(String.valueOf(player1Score));
                }

                if (playersNamesList.size() > 2) {
                    tvName2.setText(playersNamesList.get(2));
                    switch (playersList.get(2).getPlayerFigure()) {
                        case "Knight":
                            ivFigure2.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure2.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure2.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure2.setImageResource(R.drawable.animation_dog);
                            break;
                    }
                }
                tvScore2.setText(String.valueOf(player2Score));


                if (playersNamesList.size() > 3) {
                    tvName3.setText(playersNamesList.get(3));
                    switch (playersList.get(3).getPlayerFigure()) {
                        case "Knight":
                            ivFigure3.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure3.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure3.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure3.setImageResource(R.drawable.animation_dog);
                            break;
                    }
                }
                tvScore3.setText(String.valueOf(player3Score));
                break;

            case 1:
                tvScore0.setText(String.valueOf(player1Score));
                tvQuestionCounter.setText(String.valueOf(player1Counter));
                tvName0.setText(playersNamesList.get(1));
                tvName1.setText(playersNamesList.get(0));
                tvScore1.setText(String.valueOf(player0Score));


                switch (playersList.get(1).getPlayerFigure()) {
                    case "Knight":
                        ivFigure0.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure0.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure0.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure0.setImageResource(R.drawable.animation_dog);
                        break;
                }

                switch (playersList.get(0).getPlayerFigure()) {
                    case "Knight":
                        ivFigure1.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure1.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure1.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure1.setImageResource(R.drawable.animation_dog);
                        break;
                }


                if (playersNamesList.size() > 2) {
                    tvName2.setText(playersNamesList.get(2));
                    switch (playersList.get(2).getPlayerFigure()) {
                        case "Knight":
                            ivFigure2.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure2.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure2.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure2.setImageResource(R.drawable.animation_dog);
                            break;
                    }

                }

                tvScore2.setText(String.valueOf(player2Score));

                if (playersNamesList.size() > 3) {
                    tvName3.setText(playersNamesList.get(3));
                    switch (playersList.get(3).getPlayerFigure()) {
                        case "Knight":
                            ivFigure3.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure3.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure3.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure3.setImageResource(R.drawable.animation_dog);
                            break;
                    }
                }
                tvScore3.setText(String.valueOf(player3Score));
                break;

            case 2:
                tvScore0.setText(String.valueOf(player2Score));
                tvQuestionCounter.setText(String.valueOf(player2Counter));
                tvName0.setText(playersNamesList.get(2));
                switch (playersList.get(2).getPlayerFigure()) {
                    case "Knight":
                        ivFigure0.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure0.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure0.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure0.setImageResource(R.drawable.animation_dog);
                        break;
                }

                tvName1.setText(playersNamesList.get(0));
                tvScore1.setText(String.valueOf(player0Score));
                switch (playersList.get(0).getPlayerFigure()) {
                    case "Knight":
                        ivFigure1.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure1.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure1.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure1.setImageResource(R.drawable.animation_dog);
                        break;
                }

                tvName2.setText(playersNamesList.get(1));
                tvScore2.setText(String.valueOf(player1Score));
                switch (playersList.get(1).getPlayerFigure()) {
                    case "Knight":
                        ivFigure2.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure2.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure2.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure2.setImageResource(R.drawable.animation_dog);
                        break;
                }

                if (playersNamesList.size() > 3) {
                    tvName3.setText(playersNamesList.get(3));
                    switch (playersList.get(3).getPlayerFigure()) {
                        case "Knight":
                            ivFigure3.setImageResource(R.drawable.animation_knight);
                            break;
                        case "Giraffe":
                            ivFigure3.setImageResource(R.drawable.animation_giraffe);
                            break;
                        case "Dragon":
                            ivFigure3.setImageResource(R.drawable.animation_dragon);
                            break;
                        case "Dog":
                            ivFigure3.setImageResource(R.drawable.animation_dog);
                            break;
                    }
                }
                tvScore3.setText(String.valueOf(player3Score));
                break;

            case 3:
                tvScore0.setText(String.valueOf(player3Score));
                tvQuestionCounter.setText(String.valueOf(player3Counter));
                tvName0.setText(playersNamesList.get(3));

                switch (playersList.get(3).getPlayerFigure()) {
                    case "Knight":
                        ivFigure0.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure0.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure0.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure0.setImageResource(R.drawable.animation_dog);
                        break;
                }


                tvScore1.setText(String.valueOf(player0Score));
                tvName1.setText(playersNamesList.get(0));

                switch (playersList.get(0).getPlayerFigure()) {
                    case "Knight":
                        ivFigure1.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure1.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure1.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure1.setImageResource(R.drawable.animation_dog);
                        break;
                }

                tvScore2.setText(String.valueOf(player1Score));
                tvName2.setText(playersNamesList.get(1));

                switch (playersList.get(1).getPlayerFigure()) {
                    case "Knight":
                        ivFigure2.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure2.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure2.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure2.setImageResource(R.drawable.animation_dog);
                        break;
                }

                tvScore3.setText(String.valueOf(player2Score));
                tvName3.setText(playersNamesList.get(2));

                switch (playersList.get(2).getPlayerFigure()) {
                    case "Knight":
                        ivFigure3.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivFigure3.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivFigure3.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivFigure3.setImageResource(R.drawable.animation_dog);
                        break;
                }


//                }
                break;

        }

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered = btnOne.getText().toString();
                if (questionAnswered.equals(correctQuestion)) {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Yes");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("No");
                }
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Answer").setValue(btnOne.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Player").setValue(playersTurnQueue.peek());
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questionAnswered = btnTwo.getText().toString();
                if (questionAnswered.equals(correctQuestion)) {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Yes");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("No");
                }
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Answer").setValue(btnTwo.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Player").setValue(playersTurnQueue.peek());

            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questionAnswered = btnThree.getText().toString();
                if (questionAnswered.equals(correctQuestion)) {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Yes");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("No");
                }
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Answer").setValue(btnThree.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Player").setValue(playersTurnQueue.peek());

            }
        });

        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered = btnFour.getText().toString();
                if (questionAnswered.equals(correctQuestion)) {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Yes");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("No");
                }
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Answer").setValue(btnFour.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected").child("Player").setValue(playersTurnQueue.peek());

            }
        });

        btnHelp5050.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnFour.getText().toString().equals(correctQuestion) || btnThree.getText().toString().equals(correctQuestion)) {
                    btnOne.setVisibility(View.INVISIBLE);
                    btnTwo.setVisibility(View.INVISIBLE);
                }

                if (btnOne.getText().toString().equals(correctQuestion) || btnTwo.getText().toString().equals(correctQuestion)) {
                    btnThree.setVisibility(View.INVISIBLE);
                    btnFour.setVisibility(View.INVISIBLE);
                }
                btnHelp5050.setEnabled(false);
                btnHelp5050.setTextColor(Color.BLACK);
                btnHelp5050.setVisibility(View.INVISIBLE);
            }
        });



        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimeTOStartSurvivor.setText(getString(R.string.loading_screen_timer, (int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvTimeTOStartSurvivor.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                switch (playersTurnQueue.peek().getPlayerFigure()) {
                    case "Knight":
                        ivTurnFigure.setImageResource(R.drawable.animation_knight);
                        break;
                    case "Giraffe":
                        ivTurnFigure.setImageResource(R.drawable.animation_giraffe);
                        break;
                    case "Dragon":
                        ivTurnFigure.setImageResource(R.drawable.animation_dragon);
                        break;
                    case "Dog":
                        ivTurnFigure.setImageResource(R.drawable.animation_dog);
                        break;
                }
                adTurnFigure = (AnimationDrawable) ivTurnFigure.getDrawable();
                adTurnFigure.start();
                timer();
            }
        }.start();
    }

    /**
     * Cancel the away from keyboard timer if the player is back to the application
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (afkTimer != null)
            afkTimer.cancel();

    }

    /**
     * Start away from keyboard timer if the player is not in the application
     * When the time is over the player is removed from the room firebase and switching to home fragment
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (playersTurnQueue.size() > 0) {
            afkTimer = new CountDownTimer(6000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players_Count").setValue(playerId);
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).setValue(null);
                    afkTimer.cancel();
                    countDownTimer.cancel();

                    startActivity(new Intent(Last_survivor.this, MainActivity.class));
                    finish();
                }
            }.start();
        }
    }


    /**
     * When player status is leave/lost/Away from keyboard all the timers and listeners are closed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (afkTimer != null)
            afkTimer.cancel();

        if (countDownTimer != null)
            countDownTimer.cancel();
        closeListeners();
        finish();
    }

    /**
     * Setting the new question on the screen
     * Starting new timer
     * If time is over it calls switching turns function
     */
    //Timer that keeps track of time
    private void timer() {
        MultiChoiceQuestion multiChoiceQuestion = MultiChoiceQuestionList.get(randomQuestionList.get(0));
        tvQuestionLS.setText(multiChoiceQuestion.getQuestion());
        tvQuestionLS.setVisibility(View.VISIBLE);
        correctQuestion = multiChoiceQuestion.getCorrect();

        switch (randomMultiList.get(0)) {
            case 0:
                btnOne.setText(multiChoiceQuestion.getCorrect());
                btnTwo.setText(multiChoiceQuestion.getWrong1());
                btnThree.setText(multiChoiceQuestion.getWrong2());
                btnFour.setText(multiChoiceQuestion.getWrong3());
                break;

            case 1:
                btnOne.setText(multiChoiceQuestion.getWrong1());
                btnTwo.setText(multiChoiceQuestion.getCorrect());
                btnThree.setText(multiChoiceQuestion.getWrong2());
                btnFour.setText(multiChoiceQuestion.getWrong3());
                break;

            case 2:
                btnOne.setText(multiChoiceQuestion.getWrong1());
                btnTwo.setText(multiChoiceQuestion.getWrong2());
                btnThree.setText(multiChoiceQuestion.getCorrect());
                btnFour.setText(multiChoiceQuestion.getWrong3());
                break;

            case 3:
                btnOne.setText(multiChoiceQuestion.getWrong1());
                btnTwo.setText(multiChoiceQuestion.getWrong2());
                btnThree.setText(multiChoiceQuestion.getWrong3());
                btnFour.setText(multiChoiceQuestion.getCorrect());
                break;
        }

        countDownTimer = new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String str = getString(R.string.player_turn_before) + " " + playersTurnQueue.peek().getPlayerName() + " " + getString(R.string.player_turn_after);
                tvPlayerTurn.setText(str);
                str = getString(R.string.time_left_milli) + " " + (millisUntilFinished / 1000);
                tvTimer.setText(str);
            }

            @Override
            public void onFinish() {


                if (!playerLost)
                    checkHearts();

                switchingTurns();
            }
        }.start();
    }

    /**
     * Setting questions by host chosen operators
     */
    //Choose questions by host operators
    private void setQuestions() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<MultiChoiceQuestion>>() {}.getType();
        String json;

        for (int i = 0 ; i<200 ; i++) {
            //Choose questions by host operators
            if (addition) {
                json = sp.getString("sumMultiChoiceQuestionsList", "1 + 1 = 2");
                tempQList = gson.fromJson(json, type);
                MultiChoiceQuestionList.add(tempQList.get(i));
            }

            if (subtraction) {
                json = sp.getString("subMultiChoiceQuestionList", "1 + 1 = 2");
                tempQList = gson.fromJson(json, type);
                MultiChoiceQuestionList.add(tempQList.get(i));
            }

            if (multiply) {
                json = sp.getString("mulMultiChoiceQuestionList", "1 + 1 = 2");
                tempQList = gson.fromJson(json, type);
                MultiChoiceQuestionList.add(tempQList.get(i));
            }

            if (division) {
                json = sp.getString("divMultiChoiceQuestionList", "1 + 1 = 2");
                tempQList = gson.fromJson(json, type);
                MultiChoiceQuestionList.add(tempQList.get(i));
            }
        }

    }

    /**
     *@param isCorrect Boolean holds whether the answer is correct or not
     *  Add score to player who has answer correct or calling the function checkHearts to decrease life if the answer is incorrect
     */
    //Checking if answer is correct
    private void checkAnswer(boolean isCorrect) {
        if (isCorrect) {
//Add score to the player that answered
            switch (playersNumTurnQueue.peek()) {
                case 0:
                    player0Score += 100;
                    player0Counter ++;
                    break;

                case 1:
                    player1Score += 100;
                    player1Counter ++;
                    break;

                case 2:
                    player2Score += 100;
                    player2Counter ++;
                    break;

                case 3:
                    player3Score += 100;
                    player3Counter ++;
                    break;
            }

//Loading score into players layout
            switch (playerNum) {
                case 0:
                    tvScore0.setText(String.valueOf(player0Score));
                    tvQuestionCounter.setText(String.valueOf(player0Counter));
                    tvScore1.setText(String.valueOf(player1Score));
                    tvScore2.setText(String.valueOf(player2Score));
                    tvScore3.setText(String.valueOf(player3Score));
                    break;

                case 1:
                    tvScore0.setText(String.valueOf(player1Score));
                    tvQuestionCounter.setText(String.valueOf(player1Counter));
                    tvScore1.setText(String.valueOf(player0Score));
                    tvScore2.setText(String.valueOf(player2Score));
                    tvScore3.setText(String.valueOf(player3Score));
                    break;

                case 2:
                    tvScore0.setText(String.valueOf(player2Score));
                    tvQuestionCounter.setText(String.valueOf(player2Counter));
                    tvScore1.setText(String.valueOf(player0Score));
                    tvScore2.setText(String.valueOf(player1Score));
                    tvScore3.setText(String.valueOf(player3Score));
                    break;

                case 3:
                    tvScore0.setText(String.valueOf(player3Score));
                    tvQuestionCounter.setText(String.valueOf(player3Counter));
                    tvScore1.setText(String.valueOf(player0Score));
                    tvScore2.setText(String.valueOf(player1Score));
                    tvScore3.setText(String.valueOf(player2Score));
                    break;
            }
        } else checkHearts();
    }

    /**
     *  Decrease player life and checks if player has lost
     */
    //Check lives
    public void checkHearts() {
        switch (playersNumTurnQueue.peek()) {
            case 0:
                player0Live--;
                break;

            case 1:
                player1Live--;
                break;

            case 2:
                player2Live--;
                break;

            case 3:
                player3Live--;
                break;
        }

        switch (playerNum) {
            case 0:

                if (player0Live == 0)
                    checkIsGameOver(player0Score,player0Counter);

                if (player0Live == 1) {
                    ivHeart00.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 2) {
                    ivHeart01.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 3) {
                    ivHeart02.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player1Live == 1) {
                    ivHeart10.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 2) {
                    ivHeart11.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 3) {
                    ivHeart12.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player2Live == 1) {
                    ivHeart20.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 2) {
                    ivHeart21.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 3) {
                    ivHeart22.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player3Live == 1) {
                    ivHeart30.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 2) {
                    ivHeart31.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 3) {
                    ivHeart32.setImageResource(R.drawable.grayheart); // if the first heart appears
                }
                break;

            case 1:

                if (player1Live == 0)
                    checkIsGameOver(player1Score,player1Counter);

                if (player1Live == 1) {
                    ivHeart00.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 2) {
                    ivHeart01.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 3) {
                    ivHeart02.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player0Live == 1) {
                    ivHeart10.setImageResource(R.drawable.grayheart); // if the first heart appears
                } else if (player0Live == 2) {
                    ivHeart11.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 3) {
                    ivHeart12.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player2Live == 1) {
                    ivHeart20.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 2) {
                    ivHeart21.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 3) {
                    ivHeart22.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player3Live == 1) {
                    ivHeart30.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 2) {

                    ivHeart31.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 3) {
                    ivHeart32.setImageResource(R.drawable.grayheart); // if the first heart appears
                }
                break;

            case 2:

                if (player2Live == 0)
                    checkIsGameOver(player2Score,player2Counter);

                if (player2Live == 1) {
                    ivHeart00.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 2) {
                    ivHeart01.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 3) {
                    ivHeart02.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player0Live == 1) {
                    ivHeart10.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 2) {
                    ivHeart11.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 3) {
                    ivHeart12.setImageResource(R.drawable.grayheart); // if the first heart appears
                }



                if (player1Live == 1) {
                    ivHeart20.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 2) {
                    ivHeart21.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 3) {
                    ivHeart22.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player3Live == 1) {
                    ivHeart30.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 2) {
                    ivHeart31.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 3) {
                    ivHeart32.setImageResource(R.drawable.grayheart); // if the first heart appears
                }
                break;

            case 3:

                if (player3Live == 0)
                    checkIsGameOver(player3Score,player3Counter);

                if (player3Live == 1) {
                    ivHeart00.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 2) {
                    ivHeart01.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player3Live == 3) {
                    ivHeart02.setImageResource(R.drawable.grayheart); // if the first heart appears
                }

                if (player0Live == 1) {
                    ivHeart10.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 2) {
                    ivHeart11.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player0Live == 3) {
                    ivHeart12.setImageResource(R.drawable.grayheart); // if the first heart appears
                }



                if (player1Live == 1) {
                    ivHeart20.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 2) {
                    ivHeart21.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player1Live == 3) {
                    ivHeart22.setImageResource(R.drawable.grayheart); // if the first heart appears
                }


                if (player2Live == 1) {
                    ivHeart30.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 2) {
                    ivHeart31.setImageResource(R.drawable.grayheart); // if the first heart appears

                } else if (player2Live == 3) {
                    ivHeart32.setImageResource(R.drawable.grayheart); // if the first heart appears
                }
                break;
        }
    }

    /**
     *  Removing the last question from the list
     *  Cancel the timer and calling a new timer
     *  Switch turn between the players in the queue
     *  Showing who's player turn in the screen
     */
    private void switchingTurns() {
        if (playersTurnQueue.size()>0 &&!playerLost && randomMultiList.size() != 0) {
            countDownTimer.cancel();
            randomQuestionList.remove(0);
            randomMultiList.remove(0);

            if (isPlayerTurn) {
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host").setValue(false);
            }

            //String queue
            Player lastPlayer = playersTurnQueue.peek();
            playersTurnQueue.remove();
            playersTurnQueue.add(lastPlayer);

            //Integer queue
            int lastPlayerNum = playersNumTurnQueue.peek();
            playersNumTurnQueue.remove();
            playersNumTurnQueue.add(lastPlayerNum);
            adTurnFigure.stop();

            switch (playersTurnQueue.peek().getPlayerFigure()) {
                case "Knight":
                    ivTurnFigure.setImageResource(R.drawable.animation_knight);
                    break;
                case "Giraffe":
                    ivTurnFigure.setImageResource(R.drawable.animation_giraffe);
                    break;
                case "Dragon":
                    ivTurnFigure.setImageResource(R.drawable.animation_dragon);
                    break;
                case "Dog":
                    ivTurnFigure.setImageResource(R.drawable.animation_dog);
                    break;
            }
            adTurnFigure = (AnimationDrawable) ivTurnFigure.getDrawable();
            adTurnFigure.start();

            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);
            timer();

        } else if (randomMultiList.size() == 0) {
            switch (playerNum) {
                case 0:
                    checkIsGameOver(player0Score,player0Counter);
                    break;

                case 1:
                    checkIsGameOver(player1Score,player1Counter);
                    break;

                case 2:
                    checkIsGameOver(player2Score,player2Counter);
                    break;

                case 3:
                    checkIsGameOver(player3Score,player3Counter);
                    break;
            }
        }
    }

    /**

     * @param score player score
     * @param questionsAnswered player correct answer count
     *  called when game end no life left
     *  stop all animations and hide unneeded objects
     *  save score to Firebase database and to Shared-Preferences if needed
     */
    //Checking if lives are 0
    private void checkIsGameOver(final int score, final int questionsAnswered) {
        //Finish the game with player id

        playerLost = true ;
        countDownTimer.cancel();
        btnOne.setVisibility(View.GONE);
        btnTwo.setVisibility(View.GONE);
        btnThree.setVisibility(View.GONE);
        btnFour.setVisibility(View.GONE);

        MediaPlayer mediaPlayer = MediaPlayer.create(Last_survivor.this, R.raw.evil_laugh);
        mediaPlayer.start();

        //Getting best score of the player
        int userScore = sp.getInt("Last_SurvivorScore", 0);
        String str;

        if (randomMultiList.size() == 0) {
            str = getString(R.string.game_over_score_is) + " " + score;

        } else {
            str = getString(R.string.no_more_life_score_is) + " " + score;
        }
        if (userScore < score) {
            str = getString(R.string.congrats_new_high_score) + " " + score;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HighScores").child("Last_Survivor").setValue(score);
            sp.edit().putInt("Last_SurvivorScore", score).apply();
            lastSurvivorKonfetti.build()
                    .addColors(getResources().getColor(R.color.gool_light_blue), getResources().getColor(R.color.gool_blue), getResources().getColor(R.color.gool_orange))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 10f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(nl.dionsegijn.konfetti.models.Shape.Square.INSTANCE,nl.dionsegijn.konfetti.models.Shape.Circle.INSTANCE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(lastSurvivorKonfetti.getWidth()/2f , lastSurvivorKonfetti.getHeight()/2f)
                    .streamFor(300, 10000L);
        }

        //Score to Firebase
        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players");
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot nameSnapShot : dataSnapshot.getChildren()) {
                        if (nameSnapShot.getKey().equals(playerId)) {
                            String playerName = nameSnapShot.child("Name").getValue(String.class);
                            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("HighScores").child("Last_Survivor").push();
                            dbReference.child("Name").setValue(playerName);
                            dbReference.child("Score").setValue(score);
                            dbReference.child("QuestionAnswered").setValue(questionsAnswered);
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players_Count").setValue(playerId);
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).setValue(null);

        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_end_game);
        dialog.setCancelable(false);
        TextView tvGameOverText = dialog.findViewById(R.id.tvGameOverText);
        TextView tvGameOverOK = dialog.findViewById(R.id.tvGameOverOK);
        String setText = str + "\n" +getString(R.string.you_have_answered) + " " + questionsAnswered + " " + getString(R.string.questions_correct);
        tvGameOverText.setText(setText);

        tvGameOverOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Last_survivor.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    /**
     * called when backPress is clicked to show a dialog before exiting game
     */
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit_game);
        TextView tvDialogExit = dialog.findViewById(R.id.tvDialogExit);
        TextView tvDialogStay = dialog.findViewById(R.id.tvDialogStay);

        tvDialogStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tvDialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players_Count").setValue(playerId);
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).setValue(null);
                startActivity(new Intent(Last_survivor.this, MainActivity.class));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    /**
     *  Closing all Firebase listeners to prevent app crash
     */
    private void closeListeners() {
        if (playerLeftListener != null && playerLeftRef != null)
            playerLeftRef.removeEventListener(playerLeftListener);
        if (turnRefListener != null && turnRef != null)
            turnRef.removeEventListener(turnRefListener);
        if (clickedListener != null && isClickedRef != null)
            isClickedRef.removeEventListener(clickedListener);
        if (selectedAnswerListener != null && selectedAnswerRef != null)
            selectedAnswerRef.removeEventListener(selectedAnswerListener);
    }

    /**
     *  Setting information need for game to work (User ID, Screen width, enemy speed, question list)
     *  Initialize object used in class
     *  Initialize game animations and layouts
     */
    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        tempQList = new ArrayList<>();
        randomQuestionList = new ArrayList<>();
        randomMultiList = new ArrayList<>();
        MultiChoiceQuestionList = new ArrayList<>();
        playersList = new ArrayList<>();

        roomName = getIntent().getStringExtra("roomName");
        gameType = getIntent().getStringExtra("gameType");
        addition = getIntent().getBooleanExtra("addition",false);
        subtraction = getIntent().getBooleanExtra("sub",false);
        division = getIntent().getBooleanExtra("div",false);
        multiply = getIntent().getBooleanExtra("multi",false);

        isClickedRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer");
        playerLeftRef =  FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players_Count");
        randomNumRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Random_Numbers");
        selectedAnswerRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Selected");
        turnRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host");

        linearLayout = findViewById(R.id.linearLayoutHold);
        lastSurvivorKonfetti = findViewById(R.id.lastSurvivorKonfetti);
        tvTimeTOStartSurvivor = findViewById(R.id.tvTimeToStartSurvivor);
        tvName0 = findViewById(R.id.tvName0);
        tvScore0 = findViewById(R.id.tvScore0);
        ivHeart00 = findViewById(R.id.ivHeart00);
        ivHeart01 = findViewById(R.id.ivHeart01);
        ivHeart02 = findViewById(R.id.ivHeart02);
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        tvName1 = findViewById(R.id.tvName1);
        tvScore1 = findViewById(R.id.tvScore1);
        ivHeart10 = findViewById(R.id.ivHeart10);
        ivHeart11 = findViewById(R.id.ivHeart11);
        ivHeart12 = findViewById(R.id.ivHeart12);
        tvName2 = findViewById(R.id.tvName2);
        tvScore2 =findViewById(R.id.tvScore2);
        ivHeart20 = findViewById(R.id.ivHeart20);
        ivHeart21 = findViewById(R.id.ivHeart21);
        ivHeart22 = findViewById(R.id.ivHeart22);
        tvName3 = findViewById(R.id.tvName3);
        tvScore3 =findViewById(R.id.tvScore3);
        ivHeart30 = findViewById(R.id.ivHeart30);
        ivHeart31 = findViewById(R.id.ivHeart31);
        ivHeart32 = findViewById(R.id.ivHeart32);
        ivFigure0 = findViewById(R.id.ivFigure0);
        ivFigure1 = findViewById(R.id.ivFigure1);
        ivFigure2 = findViewById(R.id.ivFigure3);
        ivFigure3 = findViewById(R.id.ivFigure2);
        ivTurnFigure = findViewById(R.id.ivTurnFigure);
        ConstraintPlayer1 = findViewById(R.id.ConstraintPlayer1);
        ConstraintPlayer2 = findViewById(R.id.ConstraintPlayer2);
        ConstraintPlayer3 = findViewById(R.id.ConstraintPlayer3);
        tvTimer = findViewById(R.id.tvTimer);
        btnOne = findViewById(R.id.btnOne);
        btnTwo = findViewById(R.id.btnTwo);
        btnThree = findViewById(R.id.btnThree);
        btnFour = findViewById(R.id.btnFour);
        btnHelp5050 = findViewById(R.id.btnHelp5050);
        tvQuestionLS = findViewById(R.id.tvQuestionLS);
        tvAnswered = findViewById(R.id.tvAnswered);
        tvPlayerTurn = findViewById(R.id.tvPlayerTurn);
    }
}