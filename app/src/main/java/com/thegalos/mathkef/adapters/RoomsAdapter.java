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

import com.bumptech.glide.Glide;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.objects.Room;

import java.util.List;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyRoomsViewHolder> {

    private final Context context;
    private final List<Room> roomsList;
    private myRoomsListener listener;

    public interface myRoomsListener {
        void onRoomsListener(int position);
    }

    public void setListener(myRoomsListener listener) {
        this.listener = listener;
    }


    public RoomsAdapter(Context context, List<Room> scoreList) {
        this.context = context;
        this.roomsList = scoreList;
    }

    public class MyRoomsViewHolder extends RecyclerView.ViewHolder{

        final TextView tvRoomName;
        final TextView tvNumOfPlayers;
        final ImageView ivLocked;
        final CardView cardView;


        MyRoomsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvNumOfPlayers = itemView.findViewById(R.id.tvNumOfPlayers);
            ivLocked = itemView.findViewById(R.id.ivLocked);
            cardView = itemView.findViewById(R.id.cardRoomList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRoomsListener(getAdapterPosition());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyRoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rooms_list,parent,false);
        return new MyRoomsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRoomsViewHolder holder, int position) {
        Room room = roomsList.get(position);
        String str = context.getResources().getString(R.string.players_count) + room.getNumOfPlayers();
        holder.tvNumOfPlayers.setText(str);
        holder.tvRoomName.setText(room.getRoomName());
        str = room.getStatus();
        if (str != null) {
            if (str.equals("Close"))
                Glide.with(context).load(R.drawable.vector_lock).into(holder.ivLocked);
            else if (str.equals("Open"))
                Glide.with(context).load(R.drawable.vector_lock_open).into(holder.ivLocked);
        }

        if (position % 3 == 0)
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_green));
        if (position % 3 == 1)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_purple1));
        if (position % 3 == 2)
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pastel_sand));

    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }
}