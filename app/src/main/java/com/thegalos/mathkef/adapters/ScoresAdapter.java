package com.thegalos.mathkef.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thegalos.mathkef.R;
import com.thegalos.mathkef.objects.Score;

import java.util.List;


public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.MyScoreViewHolder> {

    private final Context context;
    private final List<Score> scoreList;

    public ScoresAdapter(Context context, List<Score> scoreList) {
        this.context = context;
        this.scoreList = scoreList;
    }

    public class MyScoreViewHolder extends RecyclerView.ViewHolder{

        final TextView tvScore;
        final TextView tvPlayerNames;
        final CardView cardView;


        MyScoreViewHolder(@NonNull View itemView) {
            super(itemView);

            tvScore = itemView.findViewById(R.id.tvHighScore);
            tvPlayerNames = itemView.findViewById(R.id.tvPlayerNames);
            cardView = itemView.findViewById(R.id.cardScore);
        }
    }

    @NonNull
    @Override
    public MyScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_score,parent,false);
        return new MyScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyScoreViewHolder holder, int position) {
        Score scoreObject = scoreList.get(position);
        holder.tvScore.setText(String.valueOf(scoreObject.getScore()));
        holder.tvPlayerNames.setText(scoreObject.getPlayerNames());

        if (position % 5 == 0)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_green));
        if (position % 5 == 1)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_purple1));
        if (position % 5 == 2)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_light_pink));
        if (position % 5 == 3)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_sand));
        if (position % 5 == 4)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_plum));

    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}