package com.thegalos.mathkef.activities;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.thegalos.mathkef.objects.Figure;
import com.thegalos.mathkef.objects.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Size;


public class Journey extends AppCompatActivity {

    boolean isCorrect;
    boolean isPlayerTurn;
    boolean addition;
    boolean subtraction;
    boolean multiply;
    boolean division;
    boolean timerCounter = true;
    boolean regularQuestions = true;
    boolean isBossNow = false;
    boolean playerLost = false;
    boolean bossSetup = false;
    private boolean playerLeftFlag = false;

    public static float screenRatioX;



    int screenWidth;
    int questionerSpeed;
    int bossSpeed;
    int bossQuestions;
    int questionerX;
    int bossX;
    int tempTimer;
    int bossQuestionsTemp;
    int counterQuestions;
    int score = 0;
    int counterBossQuestions = 0 ;
    int counterRightBossQuestion= 0;
    int lifeLeft = 4;
    int tempSpeed = 350;
    int modulo = 2000;
    int playerNum = 0;
    static final int loadingScreenTime = 4000;
    private int time = 9000;

    String playerId;
    String roomName;
    String gameType;
    String isClicked;
    String playerIdentification;
    String playerNames = "";

    ImageView backgroundOne;
    ImageView backgroundTwo;
    ImageView ivPlayer0;
    ImageView ivPlayer1;
    ImageView ivPlayer2;
    ImageView ivPlayer3;
    ImageView ivHeart3;
    ImageView ivHeart2;
    ImageView ivHeart1;
    ImageView ivBoss,ivBoss1,ivBoss2,ivBoss3;
    ImageView ivQuestioner;
    ImageView ivBrokenHeart;
    ImageView ivLightning;
    ImageView ivBossLightning;

    TextView tvScore;
    TextView tvTimeTOStart;
    TextView tvQuestion;
    TextView tvPlayerTurn;
    TextView tvTimerJourney;
    TextView btnYes;
    TextView btnNo;

    AnimationDrawable adPlayer0;
    AnimationDrawable adPlayer1;
    AnimationDrawable adPlayer2;
    AnimationDrawable adPlayer3;

    AnimationDrawable adQuestioner;
    AnimationDrawable adBoss,adBoss1,adBoss2,adBoss3;
    AnimationDrawable adLightning;
    AnimationDrawable adBossLightning;
    Animation shakeAnim;
    AlphaAnimation alphaAnimation;
    ValueAnimator animator;

    DatabaseReference isClickedRef;
    DatabaseReference turnRef;
    DatabaseReference playerLeftRef;
    DatabaseReference randomNumRef;
    ValueEventListener isClickedListener;
    ValueEventListener turnListener;
    ValueEventListener playerLeftListener;

    List<String> trueQuestionsList;
    List<String> falseQuestionsList;
    List<String> tempQList;
    List<Integer> randomQuestionList;
    List<Integer> randomFalseOrTrueList;

    List<Figure> figureList;

    Queue<Player> playersTurnQueue;
    ConstraintLayout main;
    CountDownTimer countDownTimer;

    Timer timer = new Timer();
    Handler handler = new Handler();

    CountDownTimer afkTimer;

    SharedPreferences sp;

    MediaPlayer mediaPlayer;

    KonfettiView journeyKonfetti;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);


        init();

        playerLeftRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players_Count");
        turnRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host");
        isClickedRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer");

        DatabaseReference playerFigureRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Figures");
        playerFigureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Figure figure = new Figure();
                        figure.setFigureName(dataSnapshot.getKey());
                        figure.setPlaying(dataSnapshot.getValue(boolean.class));
                        figureList.add(figure);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        turnListener = turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvPlayerTurn.setText(playersTurnQueue.peek().getPlayerName());
                    isPlayerTurn = (dataSnapshot.getValue(Boolean.class));
                    if (isPlayerTurn) {
                        btnYes.setVisibility(View.VISIBLE);
                        btnNo.setVisibility(View.VISIBLE);

                    } else {
                        btnYes.setVisibility(View.INVISIBLE);
                        btnNo.setVisibility(View.INVISIBLE);
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
                        String charPlayerLeft = null;
                        playerIdentification = dataSnapshot.getValue(String.class);

                        if (playersTurnQueue.peek().getPlayerUID().equals(playerIdentification)) {

                            charPlayerLeft = playersTurnQueue.peek().getPlayerFigure();
                            playersTurnQueue.remove();
                            if (playersTurnQueue.size() > 0) {
                                switch (playersTurnQueue.peek().getPlayerFigure()) {
                                    case "Giraffe":
                                        ivPlayer0.animate().translationX(500).setDuration(500);
                                        break;
                                    case "Knight":
                                        ivPlayer1.animate().translationX(500).setDuration(500);
                                        break;
                                    case "Dragon":
                                        ivPlayer2.animate().translationX(500).setDuration(500);
                                        break;
                                    case "Dog":
                                        ivPlayer3.animate().translationX(500).setDuration(500);
                                        break;
                                }
                            }
                        } else {

                            Player firstPlayer = playersTurnQueue.peek();
                            playersTurnQueue.remove();
                            playersTurnQueue.add(firstPlayer);

                            while (!playersTurnQueue.peek().getPlayerUID().equals(firstPlayer.getPlayerUID())) {
                                Player lastPlayerRemoved = playersTurnQueue.peek();
                                playersTurnQueue.remove();

                                if (!lastPlayerRemoved.getPlayerUID().equals(playerIdentification)) {
                                    playersTurnQueue.add(lastPlayerRemoved);
                                } else {
                                    charPlayerLeft = lastPlayerRemoved.getPlayerFigure();
                                }
                            }
                        }

                        switch (charPlayerLeft) {
                            case "Giraffe":
                                adPlayer0.stop();
                                ivPlayer0.setVisibility(View.INVISIBLE);
                                break;
                            case "Knight":
                                adPlayer1.stop();
                                ivPlayer1.setVisibility(View.INVISIBLE);
                                break;
                            case "Dragon":
                                adPlayer2.stop();
                                ivPlayer2.setVisibility(View.INVISIBLE);
                                break;
                            case "Dog":
                                adPlayer3.stop();
                                ivPlayer3.setVisibility(View.INVISIBLE);
                                break;
                        }






                        if (playersTurnQueue.size() == 0) {
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).setValue(null);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (!playersTurnQueue.peek().getPlayerUID().equals(playerId)) {
            Player firstPlayer = playersTurnQueue.peek();
            playersTurnQueue.remove();
            playersTurnQueue.add(firstPlayer);
            boolean isPlayer = true ;
            while (!playersTurnQueue.peek().getPlayerUID().equals(firstPlayer.getPlayerUID())) {

                if (isPlayer)
                    playerNum++;

                if (playersTurnQueue.peek().getPlayerUID().equals(playerId)) {
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

        isClickedListener = isClickedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    isClicked = (dataSnapshot.getValue(String.class));
                    if (isClicked != null)
                        if (isClicked.equals("Yes")) {
                            isCorrect = randomFalseOrTrueList.get(0) == 0;
                            btnYes.setVisibility(View.VISIBLE);

                            btnYes.startAnimation(shakeAnim);

                        } else if (isClicked.equals("No")) {
                            isCorrect = randomFalseOrTrueList.get(0) == 1;
                            btnNo.setVisibility(View.VISIBLE);
                            btnNo.startAnimation(shakeAnim);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



        shakeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!isBossNow)
                    countDownTimer.cancel();
                tvTimerJourney.setVisibility(View.INVISIBLE);

                if (isCorrect) {


                    adBossLightning.start();
                    ivBossLightning.setVisibility(View.VISIBLE);
                } else {
                    adLightning.start();
                    ivLightning.setVisibility(View.VISIBLE);
                }

                checkAnswer(isCorrect);

                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Empty");
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                btnNo.setVisibility(View.INVISIBLE);
                btnYes.setVisibility(View.INVISIBLE);
                new CountDownTimer(500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {
                        adLightning.stop();
                        ivLightning.setVisibility(View.INVISIBLE);
                        adBossLightning.stop();
                        ivBossLightning.setVisibility(View.INVISIBLE);
                        switchingTurns();
                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        if (playersTurnQueue.size() > 0)
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Game_Status").setValue("Close");

        randomRead();

        setQuestions();

        main.setVisibility(View.GONE);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNo.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("Yes");
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNo.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Click_Answer").setValue("No");
            }
        });

        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimeTOStart.setVisibility(View.VISIBLE);
                tvTimeTOStart.setText(getString(R.string.loading_screen_timer, (int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvTimeTOStart.setVisibility(View.GONE);
                tvScore.setVisibility(View.VISIBLE);
                ivQuestioner.setX(screenWidth + 50);
                ivBoss.setX(screenWidth + 50);
                bossX = screenWidth + 50;
                questionerX = screenWidth + 50;
                main.setVisibility(View.VISIBLE);

                switchingQuestion();
                timer(time);

                for (Figure figure : figureList) {

                    switch (figure.getFigureName()) {

                        case "Giraffe":
                            if (figure.isPlaying()) {
                                ivPlayer0.setVisibility(View.VISIBLE);
                                adPlayer0.start();
                            }
                            break;
                        case "Knight":
                            if (figure.isPlaying()) {
                                ivPlayer1.setVisibility(View.VISIBLE);
                                adPlayer1.start();
                            }
                            break;
                        case "Dragon":
                            if (figure.isPlaying()) {
                                ivPlayer2.setVisibility(View.VISIBLE);
                                adPlayer2.start();
                            }

                            break;
                        case "Dog":
                            if (figure.isPlaying()) {
                                ivPlayer3.setVisibility(View.VISIBLE);
                                adPlayer3.start();
                            }
                            break;
                    }
                }

                switch (playersTurnQueue.peek().getPlayerFigure()) {
                    case "Giraffe":
                        ivPlayer0.animate().translationX(500).setDuration(500);
                        break;
                    case "Knight":
                        ivPlayer1.animate().translationX(500).setDuration(500);
                        break;
                    case "Dragon":
                        ivPlayer2.animate().translationX(500).setDuration(500);
                        break;
                    case "Dog":
                        ivPlayer3.animate().translationX(500).setDuration(500);
                        break;
                }

                animator.start();
                adBoss.start();
                adQuestioner.start();
                ivQuestioner.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        moveObjects("Regular");
                                    }
                                });
                            }
                        }, 0, 20);
                    }
                }, 0);
            }
        }.start();
    }

    /**
     *  Loading random numbers list generated in GameRoom class into using Firebase single event listener
     */
    private void randomRead() {

        randomNumRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Random_Numbers");
        randomNumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        randomFalseOrTrueList.add(dataSnapshot.getValue(Integer.class));
                        randomQuestionList.add(Integer.valueOf(dataSnapshot.getKey()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    /**
     *  Check first position in number in randomFalseOrTrueList user would be given a question
     *  True if 0 is given, False if 1 is given
     */
    private void switchingQuestion() {
        if (randomFalseOrTrueList.get(0) == 0) {
            tvQuestion.setText(trueQuestionsList.get(randomQuestionList.get(0)));
        } else if (randomFalseOrTrueList.get(0) == 1) {
            tvQuestion.setText(falseQuestionsList.get(randomQuestionList.get(0)));
        }

    }


    /**
     * @param who Which enemy is asking questions, either Questioner or Boss
     * Depend on value in who the object will move on players screen
     */
    public void moveObjects(String who) {

        if (who.equals("Regular")) {
            questionerX -= questionerSpeed;
            ivQuestioner.setX(questionerX);
            adQuestioner.start();
            ivBoss.setVisibility(View.INVISIBLE);
            ivBoss1.setVisibility(View.INVISIBLE);
            ivBoss2.setVisibility(View.INVISIBLE);
            ivBoss3.setVisibility(View.INVISIBLE);
            ivQuestioner.setVisibility(View.VISIBLE);
            adPlayer0.start();
            adPlayer1.start();
            adPlayer2.start();
            adPlayer3.start();

        } else if (who.equals("Boss")) {
            bossX -= bossSpeed;

            switch (counterRightBossQuestion) {
                case 0:
                    ivBoss.setX(bossX);
                    ivBoss1.setVisibility(View.INVISIBLE);
                    ivBoss2.setVisibility(View.INVISIBLE);
                    ivBoss3.setVisibility(View.INVISIBLE);
                    ivBoss.setVisibility(View.VISIBLE);
                    adBoss.start();
                    break;

                case 1:
                    ivBoss.setVisibility(View.INVISIBLE);
                    ivBoss2.setVisibility(View.INVISIBLE);
                    ivBoss3.setVisibility(View.INVISIBLE);

                    adBoss.stop();
                    ivBoss1.setX(bossX);
                    ivBoss1.setVisibility(View.VISIBLE);
                    adBoss1.start();
                    break;

                case 2:
                    ivBoss.setVisibility(View.INVISIBLE);
                    ivBoss1.setVisibility(View.INVISIBLE);
                    ivBoss3.setVisibility(View.INVISIBLE);
                    adBoss1.stop();
                    ivBoss2.setX(bossX);
                    ivBoss2.setVisibility(View.VISIBLE);
                    adBoss2.start();
                    break;

                case 3:
                    adBoss2.stop();
                    ivBoss.setVisibility(View.INVISIBLE);
                    ivBoss1.setVisibility(View.INVISIBLE);
                    ivBoss2.setVisibility(View.INVISIBLE);
                    ivBoss3.setX(bossX);
                    ivBoss3.setVisibility(View.VISIBLE);
                    adBoss3.start();
                    break;
            }
            ivQuestioner.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * @param correct Boolean holds whether the answer is correct or not
     * if correct add score, check if boss setup needed and play sound
     * if wrong lower player life and call checkHearts Function
     */
    private void checkAnswer(boolean correct) {
        if (correct) {
            mediaPlayer = MediaPlayer.create(Journey.this, R.raw.magic2);
            mediaPlayer.start();
            if (isBossNow) {
                counterRightBossQuestion++;
                if (counterRightBossQuestion == 4) {
                    if (lifeLeft == 4) {
                        score += 200;
                        tvScore.setText(String.valueOf(score));
                    } else if (lifeLeft == 3) {
                        lifeLeft += 1;
                        ivHeart3.setImageResource(R.drawable.heart);
                        Toast.makeText(this, "Life added", Toast.LENGTH_SHORT).show();
                    } else if (lifeLeft == 2) {
                        lifeLeft += 1;
                        ivHeart2.setImageResource(R.drawable.heart);
                        Toast.makeText(this, "Life added", Toast.LENGTH_SHORT).show();
                    } else if (lifeLeft == 1) {
                        lifeLeft += 1;
                        ivHeart1.setImageResource(R.drawable.heart);
                        Toast.makeText(this, "Life added", Toast.LENGTH_SHORT).show();
                    }
                }

                score += 200;
            } else {
                score += 100;
            }
            tvScore.setText(String.valueOf(score));
            if (score % modulo == 0 ) {
                regularQuestions = false;
                bossSetup = true;
            }

        } else {

            mediaPlayer = MediaPlayer.create(Journey.this, R.raw.magic);
            mediaPlayer.start();
            checkHearts();
        }

        if (lifeLeft == 0)
            return;

        if (regularQuestions)
            questionerX = screenWidth + 50;
        else
            counterBossQuestions++;


        if (counterBossQuestions == 5) {
            timer.cancel();
            isBossNow = false;
            regularQuestions = true;
            mediaPlayer = MediaPlayer.create(Journey.this, R.raw.santa_laugh);
            adBoss.stop();
            time = tempTimer;
            questionerX = screenWidth + 50;
            changeSpeed(tempSpeed);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ivBoss.getLayoutParams().height = (int) getResources().getDimension(R.dimen.image_view_height);
                            ivBoss.getLayoutParams().width = (int) getResources().getDimension(R.dimen.image_view_width);
                            moveObjects("Regular");

                        }
                    });
                }
            }, 0, 20);
        }

        if (bossSetup) {

            timer.cancel();
            countDownTimer.cancel();
            tempTimer = time;
            counterBossQuestions = 1;

            time = 27000;
            changeSpeed(1500);

            counterRightBossQuestion = 0;
            bossX = screenWidth-30;
            adPlayer0.start();
            adPlayer1.start();
            adPlayer2.start();
            adPlayer3.start();
            animator.resume();
            isBossNow = true;
            ivBoss.setX(bossX);
            adQuestioner.stop();
            ivBoss.setVisibility(View.VISIBLE);
            ivQuestioner.setVisibility(View.INVISIBLE);
            adBoss.start();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            moveObjects("Boss");
                        }
                    });
                }
            }, 0, 20);
            bossSetup = false ;
            timer(time);
        }
    }

    /**
     *  called when question answer is incorrect
     *  Life is lowered and if life equal to zero game will end by calling endGame Function
     */
    public void checkHearts() {

        lifeLeft--;
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { ivBrokenHeart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) { ivBrokenHeart.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        if (lifeLeft == 0)
            endGame();

        else if (lifeLeft == 1) {
            ivBrokenHeart.startAnimation(alphaAnimation);
            ivHeart1.setImageResource(R.drawable.grayheart); // if the first heart appears

        } else if (lifeLeft == 2) {
            ivBrokenHeart.startAnimation(alphaAnimation);
            ivHeart2.setImageResource(R.drawable.grayheart); // if the first heart appears

        } else if (lifeLeft == 3) {
            ivBrokenHeart.startAnimation(alphaAnimation);
            ivHeart3.setImageResource(R.drawable.grayheart); // if the first heart appears
        }

        Toast.makeText(this, "life left: " + lifeLeft, Toast.LENGTH_SHORT).show();
    }

    /**
     *  called when game end no life left
     *  stop all animations and hide unneeded objects
     *  save score to Firebase database and to Shared-Preferences if needed
     */
    private void endGame() {
        playerLost = true ;
        timer.cancel();
        countDownTimer.cancel();
        mediaPlayer = MediaPlayer.create(Journey.this, R.raw.evil_laugh);
        mediaPlayer.start();

        animator.cancel();
        adPlayer0.stop();
        adPlayer1.stop();
        adPlayer2.stop();
        adPlayer3.stop();
        adQuestioner.stop();
        adBoss.stop();
        ivPlayer0.setVisibility(View.INVISIBLE);
        ivPlayer1.setVisibility(View.INVISIBLE);
        ivPlayer2.setVisibility(View.INVISIBLE);
        ivPlayer3.setVisibility(View.INVISIBLE);
        ivQuestioner.setVisibility(View.INVISIBLE);
        ivBoss.setVisibility(View.INVISIBLE);

        int userScore = sp.getInt("JourneyScore", 0);
        String str = getString(R.string.no_more_life_score_is) + score;
        if (userScore < score) {
            str = getString(R.string.congrats_new_high_score) + score;
            journeyKonfetti.build()
                    .addColors(getResources().getColor(R.color.gool_light_blue), getResources().getColor(R.color.gool_blue), getResources().getColor(R.color.gool_orange))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 10f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(nl.dionsegijn.konfetti.models.Shape.Square.INSTANCE,nl.dionsegijn.konfetti.models.Shape.Circle.INSTANCE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(journeyKonfetti.getWidth()/2f , journeyKonfetti.getHeight()/2f)
                    .streamFor(300, 10000L);

            if (user != null)
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HighScores").child("Journey").setValue(score);
            sp.edit().putInt("JourneyScore", score).apply();
        }

        //Score to Firebase
        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players");
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot nameSnapShot : dataSnapshot.getChildren())
                        playerNames += nameSnapShot.child("Name").getValue(String.class)+ ", ";

                playerNames = playerNames.substring(0, playerNames.length()-2);
                if (playerId.equals(playersTurnQueue.peek().getPlayerUID())) {
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("HighScores").child("Journey").push();
                    dbReference.child("Names").setValue(playerNames);
                    dbReference.child("Score").setValue(score);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_end_game);
        dialog.setCancelable(false);

        TextView tvGameOverText = dialog.findViewById(R.id.tvGameOverText);
        TextView tvGameOverOK = dialog.findViewById(R.id.tvGameOverOK);
        tvGameOverText.setText(str);

        tvGameOverOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Journey.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    /**
     *  Called in game initializing and when player advances through the game to increase game difficulty
     */
    public void changeSpeed(int speed) {
        questionerSpeed = Math.round((float) screenWidth / speed);
        bossSpeed = Math.round((float) screenWidth / speed);
    }

    /**
     * Called when backPress is clicked to show a dialog before exiting game
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
                startActivity(new Intent(Journey.this, MainActivity.class));
                finish();
            }
        });
        dialog.show();

    }

    //TODO ALMOG FILL
    /**
     * Cancel the away from keyboard timer if the player is back to the application
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (afkTimer != null)
            afkTimer.cancel();

    }

    //TODO ALMOG FILL
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

                    startActivity(new Intent(Journey.this, MainActivity.class));
                    finish();
                }
            }.start();
        }
    }

    //TODO ALMOG FILL
    /**
     * When player status is leave/lost/Away from keyboard all the timers and listeners are closed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (afkTimer != null)
            afkTimer.cancel();
        timer.cancel();
        timer = null;
        mediaPlayer.release();
        countDownTimer.cancel();
        closeListeners();
        finish();
    }

    /**
     * called when round timer reached zero or when question is answered
     * ask new question if there are still players and question list is not empty
     * end game if question list is empty
     */
    private void switchingTurns() {
        if (playersTurnQueue.size()>0 && !playerLost && randomFalseOrTrueList.size() != 0) {
            if (isPlayerTurn)
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host").setValue(false);

            randomFalseOrTrueList.remove(0);
            randomQuestionList.remove(0);

            switchingQuestion();
            if(playersTurnQueue.size()>1) {
                switch (playersTurnQueue.peek().getPlayerFigure()) {
                    case "Giraffe":
                        ivPlayer0.animate().translationX(-10).setDuration(500);
                        break;

                    case "Knight":
                        ivPlayer1.animate().translationX(-10).setDuration(500);
                        break;

                    case "Dragon":
                        ivPlayer2.animate().translationX(-10).setDuration(500);
                        break;

                    case "Dog":
                        ivPlayer3.animate().translationX(-10).setDuration(500);
                        break;
                }
            }

            if (!isBossNow) {
                counterQuestions++;
                countDownTimer.cancel();

                if (counterQuestions % 10 == 0 && counterQuestions != 0 && timerCounter) {
                    time = time - 1000;
                    switch (time) {
                        case 8000:
                            tempSpeed = 560;
                            changeSpeed(tempSpeed);
                            break;

                        case 7000:
                            tempSpeed = 490;
                            changeSpeed(tempSpeed);
                            timerCounter = false;
                            break;
                    }
                }
                timer(time);
            }

            Player lastPlayer = playersTurnQueue.peek();
            playersTurnQueue.remove();
            playersTurnQueue.add(lastPlayer);

            if (playersTurnQueue.size()>1 && !playerLeftFlag) {
                switch (playersTurnQueue.peek().getPlayerFigure()) {
                    case "Giraffe":
                        ivPlayer0.animate().translationX(500).setDuration(500);
                        break;
                    case "Knight":
                        ivPlayer1.animate().translationX(500).setDuration(500);
                        break;
                    case "Dragon":
                        ivPlayer2.animate().translationX(500).setDuration(500);
                        break;
                    case "Dog":
                        ivPlayer3.animate().translationX(500).setDuration(500);
                        break;
                }
            }

            playerLeftFlag = false;
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playersTurnQueue.peek().getPlayerUID()).child("Host").setValue(true);

        } else if (randomFalseOrTrueList.size() == 0)
            endGame();
    }

    /**
     *  Closing all Firebase listeners to prevent app crash
     */
    private void closeListeners() {
        if (playerLeftListener != null && playerLeftRef != null)
            playerLeftRef.removeEventListener(playerLeftListener);
        if (turnRef != null && turnListener != null)
            turnRef.removeEventListener(turnListener);
        if (isClickedListener != null && isClickedRef != null)
            isClickedRef.removeEventListener(isClickedListener);

    }

    /**
     *  Get Related Questions from Shared-Preferences depend on host operators selection
     *  Fill True and False list with gathered questions
     */
    private void setQuestions() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String json;

        for (int i = 0 ; i<200 ; i++) {

            if (addition) {
                json = sp.getString("sumTrueQuestionsList", "1 + 1 = 2");
                tempQList = gson.fromJson(json, type);
                trueQuestionsList.add(tempQList.get(i));
                json = sp.getString("sumFalseQuestionsList", "1 + 1 = 3");
                tempQList = gson.fromJson(json, type);
                falseQuestionsList.add(tempQList.get(i));

            } if (subtraction) {
                json = sp.getString("subTrueQuestionsList", "4 - 2 = 2");
                tempQList = gson.fromJson(json, type);
                trueQuestionsList.add(tempQList.get(i));
                json = sp.getString("subFalseQuestionsList", "5 - 1 = 3");
                tempQList = gson.fromJson(json, type);
                falseQuestionsList.add(tempQList.get(i));

            } if (multiply) {
                json = sp.getString("mulTrueQuestionsList", "1 x 2 = 2");
                tempQList = gson.fromJson(json, type);
                trueQuestionsList.add(tempQList.get(i));
                json = sp.getString("mulFalseQuestionsList", "1 x 1 = 3");
                tempQList = gson.fromJson(json, type);
                falseQuestionsList.add(tempQList.get(i));

            } if (division) {
                json = sp.getString("divTrueQuestionsList", "2 / 1 = 2");
                tempQList = gson.fromJson(json, type);
                trueQuestionsList.add(tempQList.get(i));
                json = sp.getString("divFalseQuestionsList", "2 / 1 = 0");
                tempQList = gson.fromJson(json, type);
                falseQuestionsList.add(tempQList.get(i));
            }
        }
    }

    //TODO ALMOG FILL

    /**
     * @param timeCounter int that holds the time for setting the timer
     * Setting new timer
     * When time is over its decrease players life , and calling switching turns function
     */
    private void timer(final int timeCounter) {
        countDownTimer = new CountDownTimer(timeCounter,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimerJourney.setVisibility(View.VISIBLE);
                String str = getString(R.string.time_left_milli) + (millisUntilFinished / 1000);
                tvTimerJourney.setText(str);
            }

            @Override
            public void onFinish() {

                if (isBossNow) {
                    isBossNow = false;
                    regularQuestions = true;
                    mediaPlayer = MediaPlayer.create(Journey.this, R.raw.santa_laugh);
                    adBoss.stop();
                    mediaPlayer.start();
                    time = tempTimer;
                    changeSpeed(tempSpeed);
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    moveObjects("Regular");
                                }
                            });
                        }
                    }, 0, 20);
                }

                checkAnswer(false);
                isBossNow=false;
                switchingTurns();
            }
        }.start();

    }


    /**
     *  Setting information need for game to work (User ID, Screen width, enemy speed, question list)
     *  Initialize object used in class
     *  Initialize game animations
     */
    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            playerId = user.getUid();
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        screenRatioX = 1920f/screenWidth;
        screenWidth = (int) (screenWidth*screenRatioX);

        changeSpeed(710);

        addition = getIntent().getBooleanExtra("addition", false);
        subtraction = getIntent().getBooleanExtra("sub", false);
        division = getIntent().getBooleanExtra("div", false);
        multiply = getIntent().getBooleanExtra("multi", false);


        mediaPlayer = new MediaPlayer();
        randomFalseOrTrueList = new ArrayList<>();
        randomQuestionList = new ArrayList<>();
        trueQuestionsList = new ArrayList<>();
        falseQuestionsList = new ArrayList<>();
        tempQList = new ArrayList<>();
        figureList = new ArrayList<>();
        playersTurnQueue = new LinkedList<>((Collection<? extends Player>) getIntent().getSerializableExtra("Players_List"));

        backgroundOne = findViewById(R.id.ivBackground1);
        backgroundTwo = findViewById(R.id.ivBackground2);
        ivLightning = findViewById(R.id.ivLightning);
        ivBossLightning = findViewById(R.id.ivBossLightning);
        tvScore = findViewById(R.id.tvScore);
        ivHeart3 = findViewById(R.id.ivHeart3);
        ivHeart2 = findViewById(R.id.ivHeart2);
        ivHeart1 = findViewById(R.id.ivHeart1);
        ivBrokenHeart = findViewById(R.id.ivBrokenHeart);
        tvTimeTOStart = findViewById(R.id.tvTimeTOStart);
        tvTimerJourney = findViewById(R.id.tvTimerJourney);
        journeyKonfetti = findViewById(R.id.journeyKonfetti);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvPlayerTurn = findViewById(R.id.player_turn);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        main = findViewById(R.id.question_layout);
        ivPlayer0 = findViewById(R.id.ivGiraffe);
        ivPlayer1 = findViewById(R.id.ivKnight);
        ivPlayer2 = findViewById(R.id.ivDragon);
        ivPlayer3 = findViewById(R.id.ivDog);
        ivQuestioner = findViewById(R.id.ivQuestioner);
        ivBoss = findViewById(R.id.ivBoss);
        ivBoss1 = findViewById(R.id.iv1Boss);
        ivBoss2 = findViewById(R.id.iv2Boss);
        ivBoss3 = findViewById(R.id.iv3Boss);


        tvScore.setText(String.valueOf(score));

        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);


        ivPlayer0.setVisibility(View.INVISIBLE);
        ivPlayer1.setVisibility(View.INVISIBLE);
        ivPlayer2.setVisibility(View.INVISIBLE);
        ivPlayer3.setVisibility(View.INVISIBLE);
        ivBoss1.setVisibility(View.INVISIBLE);
        ivBoss2.setVisibility(View.INVISIBLE);
        ivBoss3.setVisibility(View.INVISIBLE);
        ivBrokenHeart.setVisibility(View.GONE);

        ivLightning.setX(ivPlayer0.getX());
        ivQuestioner.setX(screenWidth + 50);
        ivBoss.setX(screenWidth + 50);

        roomName = getIntent().getStringExtra("roomName");
        gameType = getIntent().getStringExtra("gameType");

        bossQuestions = playersTurnQueue.size();
        bossQuestionsTemp = bossQuestions;

        adPlayer0 = (AnimationDrawable) ivPlayer0.getDrawable();
        adPlayer1 = (AnimationDrawable) ivPlayer1.getDrawable();
        adPlayer2 = (AnimationDrawable) ivPlayer2.getDrawable();
        adPlayer3 = (AnimationDrawable) ivPlayer3.getDrawable();


        adBoss = (AnimationDrawable) ivBoss.getDrawable();
        adBoss1 = (AnimationDrawable) ivBoss1.getDrawable();
        adBoss2 = (AnimationDrawable) ivBoss2.getDrawable();
        adBoss3 = (AnimationDrawable) ivBoss3.getDrawable();

        adQuestioner = (AnimationDrawable) ivQuestioner.getDrawable();
        adLightning = (AnimationDrawable) ivLightning.getDrawable();
        adBossLightning = (AnimationDrawable) ivBossLightning.getDrawable();

        //FADE IN-OUT ANIMATION
        alphaAnimation = new AlphaAnimation(1.0f, 0.3f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(0);
        alphaAnimation.setRepeatMode(Animation.REVERSE);


        animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
    }
}
