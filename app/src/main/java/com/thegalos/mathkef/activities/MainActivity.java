package com.thegalos.mathkef.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.fragments.About;
import com.thegalos.mathkef.fragments.HighScores;
import com.thegalos.mathkef.fragments.Home;
import com.thegalos.mathkef.fragments.Profile;
import com.thegalos.mathkef.objects.MultiChoiceQuestion;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    FirebaseUser user;
    SmoothBottomBar smoothBottomBar;
    ConstraintLayout layout;
    DatabaseReference needUpdateRef;
    ValueEventListener needUpdateListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        needUpdateRef = FirebaseDatabase.getInstance().getReference().child("NeedUpdate");
        needUpdateListener = needUpdateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue(Boolean.class)) {
                        Snackbar snackbar = Snackbar.make(layout, R.string.forced_firebase_update, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        saveQuestionsLocally();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });


        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HighScores");
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("Journey"))
                        sp.edit().putInt("JourneyScore", snapshot.child("Journey").getValue(Integer.class)).apply();

                    if (snapshot.hasChild("Last_Survivor"))
                        sp.edit().putInt("Last_SurvivorScore", snapshot.child("Last_Survivor").getValue(Integer.class)).apply();

                    if (snapshot.hasChild("Last_SurvivorQuestions"))
                        sp.edit().putInt("Last_SurvivorQuestions", snapshot.child("Last_SurvivorQuestions").getValue(Integer.class)).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flAppFragment, new About(), "About").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flAppFragment, new Home(), "Home").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flAppFragment, new HighScores(), "HighScores").commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flAppFragment, new Profile(), "Profile").commit();
                        break;
                }
                return false;
            }
        });
    }

    /**
     *  Called upon login or qListReady value change to false
     *  function will save all questions from Firebase to Shared-Preferences
     */
    private void saveQuestionsLocally() {
        DatabaseReference questionsRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> sumTrueQuestionsList = new ArrayList<>();
                    List<String> subTrueQuestionsList = new ArrayList<>();
                    List<String> mulTrueQuestionsList = new ArrayList<>();
                    List<String> divTrueQuestionsList = new ArrayList<>();
                    List<String> sumFalseQuestionsList = new ArrayList<>();
                    List<String> subFalseQuestionsList = new ArrayList<>();
                    List<String> mulFalseQuestionsList = new ArrayList<>();
                    List<String> divFalseQuestionsList = new ArrayList<>();

                    List<MultiChoiceQuestion> sumMultiChoiceQuestionList = new ArrayList<>();
                    List<MultiChoiceQuestion> subMultiChoiceQuestionList = new ArrayList<>();
                    List<MultiChoiceQuestion> mulMultiChoiceQuestionList = new ArrayList<>();
                    List<MultiChoiceQuestion> divMultiChoiceQuestionList = new ArrayList<>();
                    for (int i = 1; i < 201; i++) {
                        sumTrueQuestionsList.add(dataSnapshot.child("Yes_No").child("Addition").child("True").child(String.valueOf(i)).getValue(String.class));
                        sumFalseQuestionsList.add(dataSnapshot.child("Yes_No").child("Addition").child("False").child(String.valueOf(i)).getValue(String.class));

                        subTrueQuestionsList.add(dataSnapshot.child("Yes_No").child("Subtraction").child("True").child(String.valueOf(i)).getValue(String.class));
                        subFalseQuestionsList.add(dataSnapshot.child("Yes_No").child("Subtraction").child("False").child(String.valueOf(i)).getValue(String.class));

                        mulTrueQuestionsList.add(dataSnapshot.child("Yes_No").child("Multiply").child("True").child(String.valueOf(i)).getValue(String.class));
                        mulFalseQuestionsList.add(dataSnapshot.child("Yes_No").child("Multiply").child("False").child(String.valueOf(i)).getValue(String.class));

                        divTrueQuestionsList.add(dataSnapshot.child("Yes_No").child("Division").child("True").child(String.valueOf(i)).getValue(String.class));
                        divFalseQuestionsList.add(dataSnapshot.child("Yes_No").child("Division").child("False").child(String.valueOf(i)).getValue(String.class));

                        MultiChoiceQuestion sum = new MultiChoiceQuestion();
                        sum.setQuestion(dataSnapshot.child("Multi_Choice").child("Addition").child(String.valueOf(i)).child("Question").getValue(String.class));
                        sum.setCorrect(String.valueOf(dataSnapshot.child("Multi_Choice").child("Addition").child(String.valueOf(i)).child("Answers").child("Correct").getValue(Long.class)));
                        sum.setWrong1(String.valueOf(dataSnapshot.child("Multi_Choice").child("Addition").child(String.valueOf(i)).child("Answers").child("Wrong1").getValue(Long.class)));
                        sum.setWrong2(String.valueOf(dataSnapshot.child("Multi_Choice").child("Addition").child(String.valueOf(i)).child("Answers").child("Wrong2").getValue(Long.class)));
                        sum.setWrong3(String.valueOf(dataSnapshot.child("Multi_Choice").child("Addition").child(String.valueOf(i)).child("Answers").child("Wrong3").getValue(Long.class)));
                        sumMultiChoiceQuestionList.add(sum);

                        MultiChoiceQuestion sub = new MultiChoiceQuestion();
                        sub.setQuestion(dataSnapshot.child("Multi_Choice").child("Subtraction").child(String.valueOf(i)).child("Question").getValue(String.class));
                        sub.setCorrect(String.valueOf(dataSnapshot.child("Multi_Choice").child("Subtraction").child(String.valueOf(i)).child("Answers").child("Correct").getValue(Long.class)));
                        sub.setWrong1(String.valueOf(dataSnapshot.child("Multi_Choice").child("Subtraction").child(String.valueOf(i)).child("Answers").child("Wrong1").getValue(Long.class)));
                        sub.setWrong2(String.valueOf(dataSnapshot.child("Multi_Choice").child("Subtraction").child(String.valueOf(i)).child("Answers").child("Wrong2").getValue(Long.class)));
                        sub.setWrong3(String.valueOf(dataSnapshot.child("Multi_Choice").child("Subtraction").child(String.valueOf(i)).child("Answers").child("Wrong3").getValue(Long.class)));
                        subMultiChoiceQuestionList.add(sub);

                        MultiChoiceQuestion mul = new MultiChoiceQuestion();
                        mul.setQuestion(dataSnapshot.child("Multi_Choice").child("Multiply").child(String.valueOf(i)).child("Question").getValue(String.class));
                        mul.setCorrect(String.valueOf(dataSnapshot.child("Multi_Choice").child("Multiply").child(String.valueOf(i)).child("Answers").child("Correct").getValue(Long.class)));
                        mul.setWrong1(String.valueOf(dataSnapshot.child("Multi_Choice").child("Multiply").child(String.valueOf(i)).child("Answers").child("Wrong1").getValue(Long.class)));
                        mul.setWrong2(String.valueOf(dataSnapshot.child("Multi_Choice").child("Multiply").child(String.valueOf(i)).child("Answers").child("Wrong2").getValue(Long.class)));
                        mul.setWrong3(String.valueOf(dataSnapshot.child("Multi_Choice").child("Multiply").child(String.valueOf(i)).child("Answers").child("Wrong3").getValue(Long.class)));
                        mulMultiChoiceQuestionList.add(mul);

                        MultiChoiceQuestion div = new MultiChoiceQuestion();
                        div.setQuestion(dataSnapshot.child("Multi_Choice").child("Division").child(String.valueOf(i)).child("Question").getValue(String.class));
                        div.setCorrect(String.valueOf(dataSnapshot.child("Multi_Choice").child("Division").child(String.valueOf(i)).child("Answers").child("Correct").getValue(Long.class)));
                        div.setWrong1(String.valueOf(dataSnapshot.child("Multi_Choice").child("Division").child(String.valueOf(i)).child("Answers").child("Wrong1").getValue(Long.class)));
                        div.setWrong2(String.valueOf(dataSnapshot.child("Multi_Choice").child("Division").child(String.valueOf(i)).child("Answers").child("Wrong2").getValue(Long.class)));
                        div.setWrong3(String.valueOf(dataSnapshot.child("Multi_Choice").child("Division").child(String.valueOf(i)).child("Answers").child("Wrong3").getValue(Long.class)));
                        divMultiChoiceQuestionList.add(div);
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(sumTrueQuestionsList);
                    sp.edit().putString("sumTrueQuestionsList", json).apply();

                    json = gson.toJson(subTrueQuestionsList);
                    sp.edit().putString("subTrueQuestionsList", json).apply();

                    json = gson.toJson(mulTrueQuestionsList);
                    sp.edit().putString("mulTrueQuestionsList", json).apply();

                    json = gson.toJson(divTrueQuestionsList);
                    sp.edit().putString("divTrueQuestionsList", json).apply();

                    json = gson.toJson(sumFalseQuestionsList);
                    sp.edit().putString("sumFalseQuestionsList", json).apply();

                    json = gson.toJson(subFalseQuestionsList);
                    sp.edit().putString("subFalseQuestionsList", json).apply();

                    json = gson.toJson(mulFalseQuestionsList);
                    sp.edit().putString("mulFalseQuestionsList", json).apply();

                    json = gson.toJson(divFalseQuestionsList);
                    sp.edit().putString("divFalseQuestionsList", json).apply();

                    json = gson.toJson(sumMultiChoiceQuestionList);
                    sp.edit().putString("sumMultiChoiceQuestionsList", json).apply();

                    json = gson.toJson(subMultiChoiceQuestionList);
                    sp.edit().putString("subMultiChoiceQuestionList", json).apply();

                    json = gson.toJson(mulMultiChoiceQuestionList);
                    sp.edit().putString("mulMultiChoiceQuestionList", json).apply();

                    json = gson.toJson(divMultiChoiceQuestionList);
                    sp.edit().putString("divMultiChoiceQuestionList", json).apply();

                    sp.edit().putBoolean("qListReady", true).apply();

                    Snackbar snackbar = Snackbar.make(layout, R.string.questions_from_firebase_saved, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });

    }

    /**
     *  Initialize object used in class
     */
    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        layout = findViewById(R.id.mainLayout);
        smoothBottomBar = findViewById(R.id.bottomBar);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.flAppFragment, new Home(), "Home").commit();
        boolean questionList = (sp.getBoolean("qListReady", false));
        if (!questionList) {
            saveQuestionsLocally();
        }
    }

    /**
     * Closing Firebase listener to prevent app crash
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        needUpdateRef.removeEventListener(needUpdateListener);
    }
}