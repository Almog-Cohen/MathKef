package com.thegalos.mathkef.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.adapters.RoomsAdapter;
import com.thegalos.mathkef.objects.Room;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class RoomsList extends Fragment {

    public RoomsList() { }

    Context context;
    SharedPreferences sp;

    String playerName;
    String roomName;
    String playerId;
    String gameStatus;
    String gameType;

    boolean addition;
    boolean subtraction;
    boolean multiply;
    boolean division;

    RecyclerView rvRooms;
    Button btnCreateRoom;
    RoomsAdapter roomsAdapter;
    TextView tvGameRoomList;
    TextView tvEmpty;
    ImageView ivBackRoomsList;
    DatabaseReference databaseReference;
    DatabaseReference roomStatusRef;
    List<Room> roomList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rooms_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                moveHome();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        init(view);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Room room = new Room();
                room.setRoomName(snapshot.getKey());
                room.setNumOfPlayers(String.valueOf(snapshot.child("Players").getChildrenCount()));

                room.setStatus(snapshot.child("Game_Status").getValue(String.class));
                roomList.add(room);
                roomsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int i = 0;
                for (Room room : roomList) {
                    if (room.getRoomName().equals(snapshot.getKey()))
                        break;
                    i += 1;
                }
                Room room = new Room();
                room.setRoomName(snapshot.getKey());
                room.setNumOfPlayers(String.valueOf(snapshot.child("Players").getChildrenCount()));

                room.setStatus(snapshot.child("Game_Status").getValue(String.class));
                roomList.set(i, room);
                roomsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (Room room : roomList) {
                    if (room.getRoomName().equals(snapshot.getKey()))
                        break;
                    i += 1;
                }
                roomList.remove(i);
                roomsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateRoom.setEnabled(false);
                final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        btnCreateRoom.setEnabled(true);
                    }
                });
                dialog.setContentView(R.layout.dialog_select_operations);

                final ImageView ivPlusSign = dialog.findViewById(R.id.ivPlusSign);
                final ImageView ivMinusSign = dialog.findViewById(R.id.ivMinusSign);
                final ImageView ivMultiplySign = dialog.findViewById(R.id.ivMultiplySign);
                final ImageView ivDivisionSign = dialog.findViewById(R.id.ivDivisionSign);
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                addition = sp.getBoolean("addition", false);
                if (addition)
                    ivPlusSign.setImageResource(R.drawable.operator_plus_selected);

                subtraction = sp.getBoolean("Subtraction", false);
                if (subtraction)
                    ivMinusSign.setImageResource(R.drawable.operator_minus_selected);

                multiply = sp.getBoolean("Multiply", false);
                if (multiply)
                    ivMultiplySign.setImageResource(R.drawable.operator_multiply_selected);

                division = sp.getBoolean("Division", false);
                if (division)
                    ivDivisionSign.setImageResource(R.drawable.operator_division_selected);

                ivPlusSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addition) {
                            addition = false;
                            ivPlusSign.setImageResource(R.drawable.operator_plus);
                        } else {
                            addition = true;
                            ivPlusSign.setImageResource(R.drawable.operator_plus_selected);
                        }
                    }
                });

                ivMinusSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subtraction) {
                            subtraction = false;
                            ivMinusSign.setImageResource(R.drawable.operator_minus);
                        } else {
                            subtraction = true;
                            ivMinusSign.setImageResource(R.drawable.operator_minus_selected);
                        }
                    }
                });
                ivMultiplySign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (multiply) {
                            multiply = false;
                            ivMultiplySign.setImageResource(R.drawable.operator_multiply);
                        } else {
                            multiply = true;
                            ivMultiplySign.setImageResource(R.drawable.operator_multiply_selected);
                        }
                    }
                });

                ivDivisionSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (division) {
                            division = false;
                            ivDivisionSign.setImageResource(R.drawable.operator_division);
                        } else {
                            division = true;
                            ivDivisionSign.setImageResource(R.drawable.operator_division_selected);
                        }
                    }
                });

                TextView tvPlay = dialog.findViewById(R.id.tvPlay);
                TextView tvCancel = dialog.findViewById(R.id.tvCancel);

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnCreateRoom.setEnabled(true);
                        dialog.dismiss();
                    }
                });

                tvPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!addition && !subtraction && !multiply && !division)
                            Toast.makeText(context, "You must select at least one operator", Toast.LENGTH_SHORT).show();
                        else {

                            sp.edit().putBoolean("addition", addition).apply();
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType)
                                    .child(roomName).child("Operators").child("Addition").setValue(addition);


                            sp.edit().putBoolean("Subtraction", subtraction).apply();
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType)
                                    .child(roomName).child("Operators").child("Subtraction").setValue(subtraction);


                            sp.edit().putBoolean("Multiply", multiply).apply();
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType)
                                    .child(roomName).child("Operators").child("Multiply").setValue(multiply);


                            sp.edit().putBoolean("Division", division).apply();
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType)
                                    .child(roomName).child("Operators").child("Division").setValue(division);

                            dialog.dismiss();

                            //Create new room and join the player
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Name").setValue(playerName);
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host").setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Figure").setValue("empty");

                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Game_Status").setValue("Open");
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Figures").child("Dog").setValue(false);
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Figures").child("Dragon").setValue(false);
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Figures").child("Giraffe").setValue(false);
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Figures").child("Knight").setValue(false);
                            sp.edit().putString("roomName", roomName).apply();
                            getParentFragmentManager().beginTransaction().replace(R.id.mainLayout, new GameRoom(),"GameRoom").commit();
                        }
                    }
                });
                dialog.show();
            }
        });

        ivBackRoomsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveHome();
            }
        });

        //Join Game Room
        roomsAdapter.setListener(new RoomsAdapter.myRoomsListener() {
            @Override
            public void onRoomsListener(int position) {
                roomName = roomList.get(position).getRoomName();
                roomStatusRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Game_Status");
                roomStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            gameStatus = dataSnapshot.getValue(String.class);
                            if (gameStatus.equals("Open")) {
                                sp.edit().putString("roomName", roomName).apply();
                                getParentFragmentManager().beginTransaction().replace(R.id.mainLayout, new GameRoom(), "GameRoom").commit();
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Host").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Name").setValue(playerName);
                                FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomName).child("Players").child(playerId).child("Figure").setValue("empty");
                            } else {
                                final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_end_game);

                                TextView tvDialogEndTitle = dialog.findViewById(R.id.tvDialogEndTitle);
                                TextView tvGameOverText = dialog.findViewById(R.id.tvGameOverText);
                                TextView tvGameOverOK = dialog.findViewById(R.id.tvGameOverOK);

                                tvDialogEndTitle.setText(R.string.room_is_locked);
                                tvGameOverText.setText(R.string.please_choose_another_room);
                                tvGameOverOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    /**
     *  Initialize object used in class
     */
    private void init(View view) {
        context = getContext();
        ivBackRoomsList =view.findViewById(R.id.ivBackRoomsList);
        rvRooms = view.findViewById(R.id.rvRooms);
        btnCreateRoom = view.findViewById(R.id.btnCreateRoom);
        tvGameRoomList = view.findViewById(R.id.tvGameRoomList);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        roomName = playerName;
        gameType = sp.getString("gameType", "Journey");
        if (gameType.equals("Journey"))
            tvGameRoomList.setText(R.string.journey);
        else
            tvGameRoomList.setText(R.string.last_survivor);

        roomList = new ArrayList<>();
        rvRooms.setHasFixedSize(true);
        rvRooms.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        roomsAdapter = new RoomsAdapter(context, roomList);
        rvRooms.setAdapter(roomsAdapter);
        roomsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (roomsAdapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvRooms.setVisibility(View.GONE);

                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvRooms.setVisibility(View.VISIBLE);
                }
            }
        });

        if (user != null) {
            playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType);
    }


    /**
     * Called when pressing back button or back arrow switch to home fragment
     */
    public void moveHome() {
        Fragment fragment = getParentFragmentManager().findFragmentByTag("RoomsList");
        if (fragment != null) {
            getParentFragmentManager().beginTransaction().remove(fragment).add(R.id.flAppFragment, new Home(), "Home").commit();
            SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
            smoothBottomBar.setVisibility(View.VISIBLE);
        }
    }
}
