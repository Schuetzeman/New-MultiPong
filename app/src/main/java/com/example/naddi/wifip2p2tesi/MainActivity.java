package com.example.naddi.wifip2p2tesi;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.bcpg.SymmetricEncDataPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    int backView = R.layout.activity_main;
    Database myDb;
    Button btnOnOff, btnDiscover, btnSend,btnCripto, btnConver, buttonplay, btnply, buttonserver, buttonjoin;
    ListView  listView, conversList, joinList;
    TextView read_msg_box, connectionsStatus;
    EditText writeMsg;
    WifiManager wifimanager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChanel;
    WifiDirectBroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    static  final int MESSAGE_READ =1;
    ServerClass serverClass;
    ClientClass clientClass;
    public static SendReceive sendReceive;
    Button btnChatConnect;
    ListView chatMex;
    EditText mexText;
    ListView chatList;
    public static Cript cript;
    String currentMacConnect;
    String currentNameConnect;
    private static final String TAG ="DEBUGINGER";
    public static boolean IsHost;
    public static boolean IsReady=false;
    boolean HasClicktJoin=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cript = new Cript();
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OnCreate: Neue Instanz der App Geöffnet");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);




        backView = R.layout.activity_main;
        setContentView(R.layout.activity_main);

        myDb = new Database(this);
        sendReceive = new SendReceive(MESSAGE_READ,handler);

        checkPermissions();

    }


    //-------------------------Handler der die Nachrichten Empfängt----------------------------------------
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {


                case MESSAGE_READ:
                    //Nachrichten Empfangen
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Log.i(TAG, "Handler_Message_Read: Nachricht Empfangen: " + tempMsg);
                    filterservice(tempMsg);
            }
            return true;
        }
    });

    public void filterservice(String nachricht) {

        Log.i(TAG, "Filterservice: Neue Nachricht Empfangen: " + nachricht);

        //Filterservice für globale übergabe parameter:

        //Filter für All Start
        if (nachricht.equals("All_start")) {
            Log.i(TAG, "Filterservice_Parameter: All_start wurde empfangen");
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        }
        //Filter für errore
        else if (nachricht.equals("errore")) {
            Log.i(TAG, "Filterservice_Parameter: errore wurde empfangen");

        }
        //Filter für Letsegooo
        else if (nachricht.equals("Letsegooo")) {
            Log.i(TAG, "Filterservice_Parameter: Letsegooo wurde empfangen");
            IsReady = true;

        }

        //Filter ob was auch immer
        else if(nachricht.equals("hallole")){
            Log.i(TAG, "Filterservice_Parameter: hallole aus dem komischen filter kahm an");
            WifiP2pConfig config = new WifiP2pConfig();
            System.out.println(currentMacConnect); //zuordnen eines empfangsfeldes
            setContentView(R.layout.chat);
            chatWork();
            try{
                updateChatMex();
            }catch (Exception e){}
        }
        //Filter für Comand Parameter
        else{

            //Bilden eines Teilstrings der den Command enthält
            //Alle Commands müssen 6 zeichen enthalten
            String command;
            command=nachricht.substring(0,6);
            Log.i(TAG, "Filterservice: Command wurde gebildet: "+command);

            //Filterservice für alle commands

            //Filterservice für GtwMsg
            if(command.equals("GtwMsg")) {
                Log.i(TAG, "Filterservice_Command: GateWayMassage empfangen");

                //Ermitteln der Zielhandyposition
                int ziel_handy_pos=Integer.parseInt(nachricht.substring(6,nachricht.lastIndexOf("*")));

                if(ziel_handy_pos<1){
                    GameView.circle.Point_Scored('r');
                }
                if(ziel_handy_pos>GameView.amountPlayers){
                    GameView.circle.Point_Scored('l');
                }
                if(ziel_handy_pos==GameView.thisScreen.HandyPosition){
                    GameView.circle.CurrentHandy = GameView.thisScreen.HandyPosition;
                    //GameView.circle.xpos=Float.parseFloat(nachricht.substring(nachricht.lastIndexOf("*")+1,nachricht.lastIndexOf(">")));
                    GameView.circle.ypos=Float.parseFloat(nachricht.substring(nachricht.lastIndexOf(">")+1,nachricht.lastIndexOf("<"))) * GameView.thisScreen.adjustedHeight;
                    GameView.circle.standardxspeed=Float.parseFloat(nachricht.substring(nachricht.lastIndexOf("<")+1,nachricht.lastIndexOf("#")));
                    GameView.circle.standardyspeed=Float.parseFloat(nachricht.substring(nachricht.lastIndexOf("#")+1,nachricht.length()));
                    if(GameView.circle.standardxspeed < 0) GameView.circle.xpos = GameView.thisScreen.width;
                    else GameView.circle.xpos = 0;

                }else{
                    sendToSendRecive(nachricht);
                }

                //Filterservice für NewBallMessage
            }if(command.equals("NBAMsg")){
                Log.i(TAG, "Filterservice_Command: New Ball Massage empfangen");
                int Positionee =Integer.parseInt(nachricht.substring(6,nachricht.length()));
                if(GameView.thisScreen.HandyPosition == Positionee){
                    GameView.circle.CurrentHandy = GameView.thisScreen.HandyPosition;
                    GameView.circle.xpos = 450;
                    GameView.circle.ypos = 900;
                    GameView.circle.standardyspeed = 3;
                    GameView.circle.standardradius = 10;
                    if(GameView.thisScreen.HandyPosition == 1) GameView.circle.standardxspeed = 6;
                    if(GameView.thisScreen.HandyPosition == GameView.amountPlayers) GameView.circle.standardxspeed = -6;
                }
            }

            //Filterservice SoftwareAndroid_Dimension
            if(command.equals("Sa_Dim")){
                Log.i(TAG, "Filterservice_Command: SoftwareAndroid_Dimension empfangen");

                String Sa_width=nachricht.substring(6,nachricht.lastIndexOf(">"));
                String Sa_height=nachricht.substring(nachricht.lastIndexOf(">")+1,nachricht.lastIndexOf("#"));
                String Sa_density=nachricht.substring(nachricht.lastIndexOf("#")+1,nachricht.lastIndexOf("<"));
                String Sa_position=nachricht.substring(nachricht.lastIndexOf("<")+1,nachricht.length());
                Log.i(TAG, "Filterservice_Command: SoftwareAndroid_Dimension: Sa_width "+Sa_width);
                Log.i(TAG, "Filterservice_Command: SoftwareAndroid_Dimension: Sa_height "+Sa_height);
                Log.i(TAG, "Filterservice_Command: SoftwareAndroid_Dimension: Sa_density "+Sa_density);
                Log.i(TAG, "Filterservice_Command: SoftwareAndroid_Dimension: Sa_position " +Sa_position);
                GameView.screen[Integer.parseInt(Sa_position) - 1].width = Float.parseFloat(Sa_width);
                GameView.screen[Integer.parseInt(Sa_position) - 1].height = Float.parseFloat(Sa_height);
                GameView.screen[Integer.parseInt(Sa_position) - 1].density = Float.parseFloat(Sa_density);
                GameView.screen[Integer.parseInt(Sa_position) - 1].HandyPosition = Integer.parseInt(Sa_position);
            }
        }
    }

    public static void sendToSendRecive(String input){
        Log.i(TAG, "SendRecive: Sende Nachricht: "+input);
        String temp =new String();
        sendReceive.write(input.getBytes());
        temp=null;
        System.gc();


    }




    private void exqListener() {
        btnOnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifimanager.isWifiEnabled()){
                    wifimanager.setWifiEnabled(false);
                    btnOnOff.setText("WIFI ACCESSO");
                }else{
                    wifimanager.setWifiEnabled(true);
                    btnOnOff.setText("WIFI SPENTO");
                }
            }
        });
        btnDiscover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChanel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionsStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionsStatus.setText("Discovery start fail");
                    }
                });
            }
        });

        buttonplay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        buttonjoin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChanel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionsStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionsStatus.setText("Discovery start fail");
                    }
                });


                setContentView(R.layout.client);
                HasClicktJoin=true;
                joinList=findViewById(R.id.peerListViewC);

                joinList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        final WifiP2pDevice device = deviceArray[i];
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.groupOwnerIntent = 0;  //Less probability to become the GO


                        config.deviceAddress = device.deviceAddress;




                        mManager.connect(mChanel,config,new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(),"connected to"+device.deviceName+"  mac"+device.deviceAddress,Toast.LENGTH_SHORT).show();
                                currentMacConnect = device.deviceAddress;
                                currentNameConnect = device.deviceName;
                            }
                            @Override
                            public void onFailure(int i) {
                                Toast.makeText(getApplicationContext(),"not connected",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });


            }
        });

        buttonserver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.createGroup(mChanel, new WifiP2pManager.ActionListener(){
                    @Override
                    public void onSuccess() {
                        connectionsStatus.setText("Host Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionsStatus.setText("fail");
                    }
                });






                setContentView(R.layout.host);
                WifiP2pConfig config = new WifiP2pConfig();
                config.groupOwnerIntent = 15;

            }
        });




        //-----------------------------START CONNECTION----------------------------------------------------
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                //hier einstellen ob host oder nicht


                config.deviceAddress = device.deviceAddress;




                mManager.connect(mChanel,config,new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"connected to"+device.deviceName+"  mac"+device.deviceAddress,Toast.LENGTH_SHORT).show();
                        currentMacConnect = device.deviceAddress;
                        currentNameConnect = device.deviceName;
                    }
                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"not connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //--------------------invio------------------------------------
/*
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMsg.getText().toString();
               // Toast.makeText(getApplicationContext(),msg.getBytes().toString(),Toast.LENGTH_SHORT).show();
                try{
                    sendToSendRecive(msg.getBytes());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
*/
        btnCripto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Cript x = new Cript();
                //System.out.println(x.prikey);
                //System.out.println(x.pubkey);
            }
        });



        btnConver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.conversazioni);
                conversList = findViewById(R.id.convList);
                conversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cursor peers = myDb.getPeers();
                        int x=0;
                        while(peers.moveToNext()){
                            if (i == x){
                                currentMacConnect = peers.getString(peers.getColumnIndex("mac"));
                                break;
                            }
                        }
                        setContentView(R.layout.chat);
                        System.out.println(currentMacConnect);

                        chatWork();
                        updateChatMex();

                    }
                });



                Cursor peers = myDb.getPeers();
                String[] list = new String[peers.getCount()];
                int i =0;
                //final String mac;
                String name;

                while(peers.moveToNext()){
                    final String mac = peers.getString(peers.getColumnIndex("mac"));
                    name= peers.getString(peers.getColumnIndex("name"));
                    System.out.println(mac+" "+ name);
                    list[i] = mac+" "+ name;
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (getApplicationContext(), (android.R.layout.simple_list_item_1),list) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                conversList.setAdapter(adapter);

            }
        });
    }


    private void initialWork() {
        btnConver = findViewById(R.id.conversazioni);
        btnCripto = findViewById(R.id.cripto);
        btnOnOff = findViewById(R.id.onOff);
        btnDiscover = findViewById(R.id.discover);
        //   btnSend = findViewById(R.id.sendButton);
        buttonplay = findViewById(R.id.playButton);
        listView =  findViewById(R.id.peerListView);
        buttonserver = findViewById(R.id.server);
        buttonjoin = findViewById(R.id.join);



        //   read_msg_box =  findViewById(R.id.readMsg);
        connectionsStatus= findViewById(R.id.connectionStatus);
        // writeMsg = findViewById(R.id.writeMsg);
        wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChanel = mManager.initialize(this,getMainLooper(),null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager,mChanel,this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }


    private void chatWork(){
        btnChatConnect = findViewById(R.id.reconnectionButton);
        chatMex = findViewById(R.id.chatList);
        mexText = findViewById(R.id.mexText);
        btnSend = findViewById(R.id.sendChat);
        chatList = findViewById(R.id.chatList);
        btnply=findViewById(R.id.Ply);

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mexText.getText().toString();
                // Toast.makeText(getApplicationContext(),msg.getBytes().toString(),Toast.LENGTH_SHORT).show();
                try{

//SENDE BEFEL... HIER...JA GENAU HIER
                    // String msgcript = cript.encript(msg); //Verschlüsselt ie nachricht
                    String msgcript=msg;
                    sendToSendRecive(msgcript); //senden
                    mexText.setText("");
                    myDb.insertMessage(currentMacConnect,"me: "+msg);
                    updateChatMex();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btnply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){







                String msg="All_start";
                // String msgcript = cript.encript(msg); //Verschlüsselt ie nachricht
                String msgcript=msg;
                sendToSendRecive(msgcript); //senden

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);

            }
        });
    }


    WifiP2pManager.PeerListListener peerListListener= new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNameArray= new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray[index]= device.deviceAddress+"  "+device.deviceName;
                    deviceArray[index]=device;

                    myDb.insertPeers(device.deviceAddress,device.deviceName);
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        return view;
                    }
                };

                listView.setAdapter(adapter);
                if(HasClicktJoin)joinList.setAdapter(adapter);
            }
            if (peers.size()==0){
                Toast.makeText(getApplicationContext(),"no device found",Toast.LENGTH_SHORT).show();
            }

        }
    };





    //---------------------------------------LISTENER DELLA CONNESSIONE----------------------------------------------------------
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

            final InetAddress groupOwnwerAndres =wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                IsHost=true;
                connectionsStatus.setText("host");
                //sendReceive = new SendReceive(MESSAGE_READ,handler);
                serverClass=new ServerClass(sendReceive);

                serverClass.start();
                Toast.makeText(getApplicationContext(),"host",Toast.LENGTH_SHORT).show();
                // serverClass.run();


            }else if (wifiP2pInfo.groupFormed){
                IsHost=false;
                // sendReceive = new SendReceive(MESSAGE_READ,handler);

                connectionsStatus.setText("client");

                clientClass = new ClientClass(groupOwnwerAndres,sendReceive);
                clientClass.start();
                Toast.makeText(getApplicationContext(),"client",Toast.LENGTH_SHORT).show();
            }

            Handler handler = new Handler();

//Dieser kauz hatt alles zerstört glaube ich
            handler.postDelayed(new Runnable() {
                public void run() {
                    try{
                        Log.i(TAG, "Temporare nachricht... glaube diser handler hatte alles zerstoert");

                        String snd = "hallole";
                        sendToSendRecive(snd);
                    }catch (Exception e){
                        Log.i(TAG, "Temporare nachricht... glaube diser handler hatte alles zerstoert_FEHLER");
                        Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, 5000);

            // setContentView(R.layout.chat);
            //  chatWork();
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    private void updateChatMex(){
        Cursor mexs = myDb.getMessages(currentMacConnect);
        String[] list = new String[mexs.getCount()];

        int i = 0;
        while(mexs.moveToNext()){
            final String mex = mexs.getString(mexs.getColumnIndex("mex"));
            list[i]= mex;
            i++;

        }
        final Myadapter adapter = new Myadapter(this,list);
        chatList.setAdapter(adapter);
        chatList.setTranscriptMode(chatList.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatList.setSelection(adapter.getCount() - 1);
            }
        });

    }

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};


    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);

            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    public void sendingereinger(){
        String msg="hallo";
        String msgcript = cript.encript(msg); //Verschlüsselt ie nachricht
        sendToSendRecive(msgcript); //senden
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "android.permission.ACCESS_COARSE_LOCATION" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted

                initialWork();
                exqListener();
                break;
        }
    }
}
