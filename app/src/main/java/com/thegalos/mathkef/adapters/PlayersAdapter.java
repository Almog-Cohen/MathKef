package com.thegalos.mathkef.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thegalos.mathkef.R;
import com.thegalos.mathkef.objects.Player;

import java.util.List;


public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.MyPlayerViewHolder> {

    private final Context context;
    private final List<Player> playerList;

    public PlayersAdapter(Context context, List<Player> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    public class MyPlayerViewHolder extends RecyclerView.ViewHolder{

        final TextView getPlayerName;
        final ImageView ivPlayerFigure;
        final CardView cardView;


        MyPlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            getPlayerName = itemView.findViewById(R.id.tvPlayerName);
            ivPlayerFigure = itemView.findViewById(R.id.ivPlayerFigure);
            cardView = itemView.findViewById(R.id.cardPlayers);
        }
    }

    @NonNull
    @Override
    public MyPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_players,parent,false);
        return new MyPlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPlayerViewHolder holder, int position) {
        Player PlayerObject = playerList.get(position);

        holder.getPlayerName.setText(PlayerObject.getPlayerName());
//        holder.getPlayerName.setGravity(Gravity.CENTER);
        holder.getPlayerName.setTextSize(26);
        if (PlayerObject.getPlayerFigure() != null) {
            switch (PlayerObject.getPlayerFigure()) {
                case "Dog":
                    holder.ivPlayerFigure.setImageResource(R.drawable.bdog80);
                    break;
                case "Dragon":
                    holder.ivPlayerFigure.setImageResource(R.drawable.dragon80);
                    break;
                case "Giraffe":
                    holder.ivPlayerFigure.setImageResource(R.drawable.giraffe00);
                    break;
                case "Knight":
                    holder.ivPlayerFigure.setImageResource(R.drawable.knight80);
                    break;
                case "empty":
                    holder.ivPlayerFigure.setImageDrawable(null);
            }
        }


        if (position % 3 == 0)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_green));
        if (position % 3 == 1)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_purple1));
        if (position % 3 == 2)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_light_pink));

    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }
}