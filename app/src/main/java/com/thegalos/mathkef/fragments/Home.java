package com.thegalos.mathkef.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thegalos.mathkef.R;

import me.ibrahimsn.lib.SmoothBottomBar;

public class Home extends Fragment {

    Context context;
    public Home() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        Button lastSurvivorBtn = view.findViewById(R.id.btnLastSurvivor);
        Button btnJourney = view.findViewById(R.id.btnJourney);
        ImageView ivHomeBoss = view.findViewById(R.id.ivHomeBoss);
        ImageView ivHomeDog = view.findViewById(R.id.ivHomeDog);
        ImageView ivHomeKnight = view.findViewById(R.id.ivHomeKnight);
        ImageView ivHomeQuestioner = view.findViewById(R.id.ivHomeQuestioner);
        AnimationDrawable adBoss;
        AnimationDrawable adDog;
        AnimationDrawable adKnight;
        AnimationDrawable adQuestioner;
        adBoss = (AnimationDrawable) ivHomeBoss.getDrawable();
        adDog = (AnimationDrawable) ivHomeDog.getDrawable();
        adKnight = (AnimationDrawable) ivHomeKnight.getDrawable();
        adQuestioner = (AnimationDrawable) ivHomeQuestioner.getDrawable();
        adBoss.start();
        adDog.start();
        adKnight.start();
        adQuestioner.start();
        TextView tvSlogan = view.findViewById(R.id.tvUser);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String str = getString(R.string.welcome)+ " " + user.getDisplayName();
            tvSlogan.setText(str);
        }


        lastSurvivorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("gameType", "Last_Survivor").apply();
                Fragment fragment = getParentFragmentManager().findFragmentByTag("Home");
                if (fragment != null) {
                    getParentFragmentManager().beginTransaction().remove(fragment).add(R.id.mainLayout, new RoomsList(), "RoomsList").commit();
                    SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
                    smoothBottomBar.setVisibility(View.GONE);
                }

            }
        });

        btnJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("gameType", "Journey").apply();
                Fragment fragment = getParentFragmentManager().findFragmentByTag("Home");
                if (fragment != null) {
                    getParentFragmentManager().beginTransaction().remove(fragment).add(R.id.mainLayout, new RoomsList(), "RoomsList").commit();
                    SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
                    smoothBottomBar.setVisibility(View.GONE);
                }
            }
        });
    }
}