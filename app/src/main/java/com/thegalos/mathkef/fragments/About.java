package com.thegalos.mathkef.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thegalos.mathkef.R;

public class About extends Fragment {

    public About() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView ivLogos = view.findViewById(R.id.ivLogos);
        final AnimationDrawable ivLogoDrawable = (AnimationDrawable) ivLogos.getDrawable();
        ivLogoDrawable.start();
    }
}