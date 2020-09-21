package com.thegalos.mathkef.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.mathkef.R;
import com.thegalos.mathkef.activities.Journey;
import com.thegalos.mathkef.activities.Last_survivor;
import com.thegalos.mathkef.adapters.PlayersAdapter;
import com.thegalos.mathkef.objects.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GameRoom extends Fragment {

    public GameRoom() {
    }

    Context context;

    String playerId;
    String roomPath;
    String gameType;
    String tempFigure = "";

    boolean owner;
    boolean addition;
    boolean subtraction;
    boolean multiply;
    boolean division;
    boolean isLocked = false;

    int playersCounter=0;
    int operatorsCounter=0;

    ImageView ivBackGameRoom;
    ImageView ivFigureDragon;
    ImageView ivFigureGiraffe;
    ImageView ivFigureKnight;
    ImageView ivFigureDog;
    ImageView ivRoomLock;

    TextView tvConnectedPlayers;
    TextView tvInstructionSelect;
    TextView tvRoomName;
    TextView tvSelectedOperators;
    TextView tvLockRoom;

    SharedPreferences sp;
    FirebaseUser user;

    DatabaseReference playersListRef;
    DatabaseReference ownerRef;
    DatabaseReference gameStatusRef;
    DatabaseReference figureRef;

    ValueEventListener figureListener;
    ValueEventListener playerListListener;
    ValueEventListener gameStatusListener;

    PlayersAdapter playersAdapter;
    List<Player> playersList;
    RecyclerView rvPlayers;
    Button btnStartGame;

    AnimationDrawable adDragon;
    AnimationDrawable adGiraffe;
    AnimationDrawable adKnight;
    AnimationDrawable adDog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_room, container, false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        init(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                exitRoomDialog(owner);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        playersListRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Players");
        playerListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    playersList.clear();
                    playersCounter=0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        playersCounter++;
                        Player player = new Player();
                        player.setPlayerName(snapshot.child("Name").getValue(String.class));
                        player.setPlayerUID(snapshot.getKey());
                        player.setPlayerFigure(snapshot.child("Figure").getValue(String.class));
                        playersList.add(player);
                        if (playersCounter==4)
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status").setValue("Close");
                    }
                    String str = playersList.size() + getString(R.string.possible_players);
                    tvConnectedPlayers.setText(str);
                    playersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        playersListRef.addValueEventListener(playerListListener);

        ownerRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Players").child(playerId).child("Host");
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    owner = (dataSnapshot.getValue(Boolean.class));
                    if (owner) {
                        btnStartGame.setEnabled(true);
                    } else
                        ivRoomLock.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        DatabaseReference operatorsRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Operators");
        operatorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str = "";
                if (dataSnapshot.exists()) {

                    addition = dataSnapshot.child("Addition").getValue(boolean.class);
                    if (addition) {
                        operatorsCounter++;
                        str = str + "+ ";
                    }
                    subtraction = dataSnapshot.child("Subtraction").getValue(boolean.class);
                    if (subtraction) {
                        operatorsCounter++;
                        str = str + "- ";

                    }
                    multiply = dataSnapshot.child("Multiply").getValue(boolean.class);
                    if (multiply) {
                        operatorsCounter++;
                        str = str + "x ";

                    }
                    division = dataSnapshot.child("Division").getValue(boolean.class);
                    if (division) {
                        operatorsCounter++;
                        str = str + "÷ ";
                    }
                    str = str.substring(0,str.length()-1);
                    tvSelectedOperators.setText(str);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        gameStatusRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status");
        gameStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    switch (dataSnapshot.getValue(String.class)) {
                        case "Started":
                            List<String> playerNamesList = new ArrayList<>();

                            closeListeners();
                            /*
                            playersListRef.removeEventListener(playerListListener);
                            gameStatusRef.removeEventListener(gameStatusListener);
                            figureRef.removeEventListener(figureListener);*/

                            Intent intent = new Intent();
                            if (gameType.equals("Journey"))
                                intent = new Intent(getActivity(), Journey.class);

                            else if (gameType.equals("Last_Survivor"))
                                intent = new Intent(getActivity(), Last_survivor.class);

                            for (Player player : playersList)
                                playerNamesList.add(player.getPlayerName());


                            intent.putExtra("addition", addition);
                            intent.putExtra("sub", subtraction);
                            intent.putExtra("multi", multiply);
                            intent.putExtra("div", division);
                            intent.putExtra("roomName", roomPath);
                            intent.putExtra("gameType", gameType);
                            intent.putExtra("Players_List", (Serializable) playersList);
                            intent.putExtra("Players_Names_List", (Serializable) playerNamesList);
                            startActivity(intent);
                            getActivity().finish();
                            break;

                        case "Open":
                            ivRoomLock.setImageResource(R.drawable.vector_lock_open);
                            tvLockRoom.setVisibility(View.VISIBLE);

                            break;
                        case "Close":
                            ivRoomLock.setImageResource(R.drawable.vector_lock);
                            tvLockRoom.setVisibility(View.INVISIBLE);
                            break;
                        case "Exit":
                            closeListeners();
                           /* playersListRef.removeEventListener(playerListListener);
                            gameStatusRef.removeEventListener(gameStatusListener);
                            figureRef.removeEventListener(figureListener);*/
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).setValue(null);
                            Fragment fragment = getParentFragmentManager().findFragmentByTag("GameRoom");
                            if (fragment != null) {
                                getParentFragmentManager().beginTransaction().remove(fragment).add(R.id.mainLayout, new RoomsList(), "RoomsList").commit();
                                break;
                            }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        };
        gameStatusRef.addValueEventListener(gameStatusListener);

        figureRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Figures");
        figureListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("Dog").getValue(Boolean.class)) {
                        ivFigureDog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        ivFigureDog.setClickable(false);
                    } else {
                        ivFigureDog.setBackground(null);
                        ivFigureDog.setClickable(true);
                    }
                    if (snapshot.child("Dragon").getValue(Boolean.class)) {
                        ivFigureDragon.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        ivFigureDragon.setClickable(false);
                    } else {
                        ivFigureDragon.setBackground(null);
                        ivFigureDragon.setClickable(true);
                    }
                    if (snapshot.child("Giraffe").getValue(Boolean.class)) {
                        ivFigureGiraffe.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        ivFigureGiraffe.setClickable(false);
                    } else {
                        ivFigureGiraffe.setBackground(null);
                        ivFigureGiraffe.setClickable(true);
                    }
                    if (snapshot.child("Knight").getValue(Boolean.class)) {
                        ivFigureKnight.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        ivFigureKnight.setClickable(false);
                    } else {
                        ivFigureKnight.setBackground(null);
                        ivFigureKnight.setClickable(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        figureRef.addValueEventListener(figureListener);

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean figureReady = true;
                for (Player p : playersList) {
                    if (p.getPlayerFigure().equals("empty")) {
                        figureReady = false;
                        break;
                    }
                }

                if (figureReady) {
                    btnStartGame.setEnabled(false);
                    randomQuestionsGenerator();
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status").setValue("Started");
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Players").child(playerId).child("Host").setValue(false);

                } else {
                    final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_end_game);
                    TextView tvDialogEndTitle = dialog.findViewById(R.id.tvDialogEndTitle);
                    TextView tvGameOverText = dialog.findViewById(R.id.tvGameOverText);
                    TextView tvGameOverOK = dialog.findViewById(R.id.tvGameOverOK);
                    tvDialogEndTitle.setText(R.string.choose_figure);
                    tvGameOverText.setText(R.string.must_choose_figure);
                    tvGameOverOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });

        ivBackGameRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitRoomDialog(owner);
            }
        });

        ivFigureDragon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFigure("Dragon");
            }
        });
        ivFigureGiraffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFigure("Giraffe");

            }
        });
        ivFigureKnight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFigure("Knight");

            }
        });
        ivFigureDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFigure("Dog");

            }
        });

        ivRoomLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocked) {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status").setValue("Open");
                    ivRoomLock.setImageResource(R.drawable.vector_lock_open);
                    Snackbar.make(view,"GameRoom is Open", Snackbar.LENGTH_SHORT);
                    btnStartGame.setVisibility(View.INVISIBLE);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status").setValue("Close");
                    ivRoomLock.setImageResource(R.drawable.vector_lock);
                    Snackbar.make(view,"GameRoom is Closed", Snackbar.LENGTH_SHORT);
                    btnStartGame.setVisibility(View.VISIBLE);
                }
                isLocked = !isLocked;
            }
        });
    }
    /**
     *  Initialize object used in class
     */
    private void init(View view) {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        gameType = sp.getString("gameType", "Journey");
        roomPath = sp.getString("roomName", "");
        if (roomPath.equals(""))
            getParentFragmentManager().beginTransaction().replace(R.id.mainLayout, new RoomsList(), "RoomsList").commit();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Click_Answer").setValue("Empty");

        playersList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rvPlayers = view.findViewById(R.id.rvPlayers);
        btnStartGame = view.findViewById(R.id.btnStartGame);
        tvSelectedOperators = view.findViewById(R.id.tvSelectedOperators);
        tvRoomName = view.findViewById(R.id.tvRoomName);
        tvInstructionSelect = view.findViewById(R.id.tvInstructionSelect);
        ivFigureDragon = view.findViewById(R.id.ivFigureDragon);
        ivFigureGiraffe = view.findViewById(R.id.ivFigureGiraffe);
        ivFigureKnight = view.findViewById(R.id.ivFigureKnight);
        ivFigureDog = view.findViewById(R.id.ivFigureDog);
        ivBackGameRoom = view.findViewById(R.id.ivBackGameRoom);
        tvConnectedPlayers = view.findViewById(R.id.tvConnectedPlayers);
        ivRoomLock = view.findViewById(R.id.ivRoomLock);
        tvLockRoom = view.findViewById(R.id.tvLockRoom);

        adDragon = (AnimationDrawable) ivFigureDragon.getDrawable();
        adGiraffe = (AnimationDrawable) ivFigureGiraffe.getDrawable();
        adKnight = (AnimationDrawable) ivFigureKnight.getDrawable();
        adDog = (AnimationDrawable) ivFigureDog.getDrawable();
        adDragon.start();
        adGiraffe.start();
        adKnight.start();
        adDog.start();

        //TODO set R.string.room_name_before hebrew!!!!!
        String str = getString(R.string.room_name_before) + roomPath + getString(R.string.room_name_after);
        tvRoomName.setText(str);

        rvPlayers.setHasFixedSize(true);
        rvPlayers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        playersAdapter = new PlayersAdapter(context, playersList);
        rvPlayers.setAdapter(playersAdapter);



    }

    /**
     * @param figure holds user selected Figure to be set in Firebase
     *  Called when user select his game figure
     */
    private void setFigure(String figure) {
        tvInstructionSelect.setVisibility(View.INVISIBLE);
        if (!tempFigure.equals(""))
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Figures").child(tempFigure).setValue(false);

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Figures").child(figure).setValue(true);
        tempFigure = figure;
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Players").child(playerId).child("Figure").setValue(figure);
    }

    /**
     * @param host different operations depend if called by host
     *  Cam be called by pressing back button or back arrow
     *  Shows user the option to exit room, if host exit all other will be kicked to RoomList fragment
     */
    private void exitRoomDialog(final boolean host) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit_game);
        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogExitTitle);
        TextView tvDialogText = dialog.findViewById(R.id.tvDialogText);
        TextView tvDialogExit = dialog.findViewById(R.id.tvDialogExit);
        TextView tvDialogStay = dialog.findViewById(R.id.tvDialogStay);

        tvDialogTitle.setText(R.string.exit_room);
        if (host)
            tvDialogText.setText(R.string.are_you_sure_you_want_exit_the_room_host);
        else
            tvDialogText.setText(R.string.are_you_sure_you_want_exit_the_room);

        tvDialogStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tvDialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (host) {
                    playersListRef.removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Game_Status").setValue("Exit");
                } else {
                    for (Player player : playersList) {
                        if (player.getPlayerUID().equals(playerId)) {
                            FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Figures").child(player.getPlayerFigure()).setValue(false);
                            break;
                        }
                    }

                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Players").child(user.getUid()).removeValue();
                    getParentFragmentManager().beginTransaction().replace(R.id.mainLayout, new RoomsList(), "RoomsList").commit();
                    closeListeners();
                }
                dialog.dismiss();


            }
        });
        dialog.show();
    }

    /**
     * Depend on selected game type create and store type of question in Firebase
     */
    private void randomQuestionsGenerator() {
        DatabaseReference randomNumbersRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(gameType).child(roomPath).child("Random_Numbers");
        Random rd = new Random();
        int numFalseOrTrue = 0 ,randomNum;

        ArrayList<Integer> randomNumArray = new ArrayList<>();

        for (int i = 0 ; i < (operatorsCounter*200) ; i ++) {
            randomNumArray.add(i+1);
        }

        Collections.shuffle(randomNumArray);
        for (int i= 0; i<100 ; i++) {
            if (gameType.equals("Journey")) {
                numFalseOrTrue = rd.nextInt(2);
            } else if (gameType.equals("Last_Survivor")) {
                numFalseOrTrue = rd.nextInt(4);
            }
            randomNum = randomNumArray.get(0);
            randomNumArray.remove(0);
            randomNumbersRef.child(String.valueOf(randomNum)).setValue(numFalseOrTrue);
        }

    }

    /**
     *  Closing all Firebase listeners to prevent app crash
     */
    private void closeListeners() {
        playersListRef.removeEventListener(playerListListener);
        gameStatusRef.removeEventListener(gameStatusListener);
        figureRef.removeEventListener(figureListener);
    }

}