package com.thegalos.mathkef.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.activities.SplashScreen;

public class Profile extends Fragment {

    String journeyStr = "0";
    String lastSvrStr = "0";
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        final TextView tvJourneyScore = view.findViewById(R.id.tvJourneyScore);
        final TextView tvLastSvrScore = view.findViewById(R.id.tvLastSvrScore);
        ImageView ivUserPhoto = view.findViewById(R.id.ivUserPhoto);
        TextView logout = view.findViewById(R.id.tvLogout);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvUserName.setText(user.getDisplayName());
            if (user.getPhotoUrl() != null)
                Glide.with(this).load(user.getPhotoUrl()).into(ivUserPhoto);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("gameType").remove("JourneyScore").remove("LastSurvivorScore").remove("roomName").apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finish();
            }
        });

        int sum = sp.getInt("JourneyScore", 0);
        if (sum != 0) {
            journeyStr = "Highest score: " + sum;
            tvJourneyScore.setText(journeyStr);
        }
        sum = sp.getInt("Last_SurvivorScore", 0);
        if (sum != 0) {
            lastSvrStr = "Highest Score: " + sum;
            tvLastSvrScore.setText(lastSvrStr);
        }
        sum = sp.getInt("Last_SurvivorQuestions", 0);

    }
}