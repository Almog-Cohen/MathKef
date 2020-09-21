package com.thegalos.mathkef.fragments;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.adapters.ScoresAdapter;
import com.thegalos.mathkef.objects.Score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores extends Fragment {

    Context context;

    public HighScores() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_high_scores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        ImageView ivHighScores = view.findViewById(R.id.ivHighScores);
        final RecyclerView rvScoreJourney = view.findViewById(R.id.rvScoreJourney);
        final RecyclerView rvScoreLastSurvivor = view.findViewById(R.id.rvScoreLastSurvivor);
        final AnimationDrawable ivLogoDrawable = (AnimationDrawable) ivHighScores.getDrawable();
        ivLogoDrawable.start();

        final List<Score> journeyScoreList = new ArrayList<>();
        final List<Score> lastSurvivorScoreList = new ArrayList<>();

        DatabaseReference dbJourney = FirebaseDatabase.getInstance().getReference().child("HighScores").child("Journey");
        DatabaseReference dbLastSurvivor = FirebaseDatabase.getInstance().getReference().child("HighScores").child("Last_Survivor");

        Query queryJourney = dbJourney.orderByChild("Score").limitToLast(5);
        queryJourney.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer score = snapshot.child("Score").getValue(Integer.class);
                        String playerNames = snapshot.child("Names").getValue(String.class);
                        Score scoreObject = new Score();
                        scoreObject.setScore(score);
                        scoreObject.setPlayerNames(playerNames);
                        journeyScoreList.add(scoreObject);
                    }
                }

                Collections.reverse(journeyScoreList);
                rvScoreJourney.setHasFixedSize(true);
                rvScoreJourney.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                final ScoresAdapter feedAdapter = new ScoresAdapter(context, journeyScoreList);
                rvScoreJourney.setAdapter(feedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        Query queryLastSurvivor = dbLastSurvivor.orderByChild("score").limitToLast(5);
        queryLastSurvivor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Integer score = snapshot.child("Score").getValue(Integer.class);
                    Integer questionAnswered = snapshot.child("QuestionAnswered").getValue(Integer.class);
                    String playerNames = snapshot.child("Name").getValue(String.class);
                    Score scoreObject = new Score();
                    scoreObject.setScore(score);
                    scoreObject.setPlayerNames(playerNames);
                    scoreObject.setQuestionAnswered(questionAnswered);
                    lastSurvivorScoreList.add(scoreObject);
                }
                Collections.reverse(lastSurvivorScoreList);
                rvScoreLastSurvivor.setHasFixedSize(true);
                rvScoreLastSurvivor.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                final ScoresAdapter feedAdapter = new ScoresAdapter(context, lastSurvivorScoreList);
                rvScoreLastSurvivor.setAdapter(feedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}