package com.example.naddi.wifip2p2tesi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.net.wifi.p2p.WifiP2pInfo;
import android.widget.Toast;

import com.example.naddi.wifip2p2tesi.MainActivity;

import java.util.logging.Handler;


public class GameView extends View  {

    public static int amountPlayers;
    public static int scoreLeft = 0;
    public static int scoreRight = 0;
    float zwischenfloat;
    public static Circle circle;
    public static Screen thisScreen;
    public static Screen[] screen;
    Screen saveScreen;
    Paddle paddle;
    Paint paint;
    private static final String TAG ="DEBUGINGER";
    boolean firstime = true;
    int dummy = 0;



    public GameView(Context context) {
        super(context);


        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        thisScreen = new Screen();
        thisScreen.getOwnHandyTask();
        thisScreen.getOwnHandyDimensions();
        thisScreen.getAmountPlayers();
        thisScreen.getOwnHandyPosition();
        thisScreen.adjustedHeight = thisScreen.height;

        String msgcript;

        /*
        msgcript = ("Bliblablublubblub"); //Verschlüsselt ie nachricht
        //thisScreen.HandyPosition--;
        MainActivity.sendToSendRecive(msgcript); //senden
*/


        if(thisScreen.HandyTask == 'h'){
            screen = new Screen[amountPlayers];
            for(int i = 0; i < amountPlayers; i++) screen[i] = new Screen();
        }

        if(thisScreen.HandyTask == 'j'){
            //thisScreen.HandyPosition++;MainActivity.cript.encript
            msgcript = ("Sa_Dim" + String.valueOf(thisScreen.width) + ">" + String.valueOf(thisScreen.height) + "#" + String.valueOf(thisScreen.density) + "<" + String.valueOf(thisScreen.HandyPosition)); //Verschlüsselt ie nachricht
            Log.i(TAG, "GameView: Sa_Dim wird gesendet: " + msgcript);
            //thisScreen.HandyPosition--;
            MainActivity.sendToSendRecive(msgcript); //senden
        }

        Log.i(TAG, "GameView: Objekt Circle initialisiert");



        for(int i = 0; i < 900000; i++) dummy++;



        circle = new Circle();
        circle.xpos = 450;
        circle.ypos = 400;
        circle.standardxspeed = 6;
        circle.standardyspeed = 3;
        circle.standardradius = 10;
        circle.radius = circle.standardradius * thisScreen.density;
        circle.xspeed = circle.standardxspeed * thisScreen.density;
        circle.yspeed = circle.standardyspeed * thisScreen.density;

        paddle = new Paddle();
        if(thisScreen.HandyPosition == 1){
            paddle.xdistance = 80 * thisScreen.density;
            paddle.length = 100 * thisScreen.density;
            paddle.width = 10 * thisScreen.density;
            paddle.ypos = thisScreen.height/2;
            paddle.adjust = 50 * thisScreen.density;
            paddle.xpos = paddle.xdistance;
        }

        if(thisScreen.HandyPosition == amountPlayers){
            paddle.xdistance = 80 * thisScreen.density;
            paddle.length = 100 * thisScreen.density;
            paddle.width = 10 * thisScreen.density;
            paddle.ypos = thisScreen.height/2;
            paddle.adjust = 50 * thisScreen.density;
            paddle.xpos = thisScreen.width - paddle.xdistance;
        }


        circle.CurrentHandy = 1;

 //Verschlüsselt ie nachricht
        MainActivity.sendToSendRecive("Letsegooo"); //senden

        for(int i = 0; i < 900000; i++) dummy--;

    }


    class Circle {
        float xpos;
        float ypos;
        float standardxspeed;
        float standardyspeed;
        float xspeed;
        float yspeed;
        float standardradius;
        float radius;
        int CurrentHandy;

        public void move(){
            xpos += xspeed;
            ypos += yspeed;
        }

        public void getSpecificValues(){
            xspeed = standardxspeed * thisScreen.density;
            yspeed = standardyspeed * thisScreen.density;
            radius = standardradius * thisScreen.density;
        }

        public void checkHitbox() {
           /*
            if (xpos > screen.width - radius || xpos < radius)
                xspeed *= -1;*/
           //unwichtig
            //if (ypos > thisScreen.height - radius - thisScreen.offset || ypos < radius + thisScreen.offset) standardyspeed *= -1;

            if(ypos > thisScreen.height - radius - thisScreen.offset && standardyspeed > 0) standardyspeed *= -1;

            if(ypos < radius + thisScreen.offset && standardyspeed < 0 ) standardyspeed *= -1;


            if(xpos >= thisScreen.width && standardxspeed > 0 && CurrentHandy != amountPlayers) {
                CurrentHandy++;
                xpos = 0;
            }

            if(xpos >= thisScreen.width + radius && standardxspeed > 0 && CurrentHandy == amountPlayers){
                scoreLeft++;
                xpos = 450;
                ypos = 900;
                standardxspeed = 6;
                standardyspeed = 3;
            }


            if(xpos < 0 && standardxspeed < 0 && CurrentHandy != 1){
                CurrentHandy--;
                xpos = thisScreen.width;
            }

            if(xpos < - radius && standardxspeed < 0 && CurrentHandy == 1){
                scoreRight++;
                xpos = 450;
                ypos = 900;
                standardxspeed = 6;
                standardyspeed = 3;
            }

            if (thisScreen.HandyPosition == 1){
                //--------------------------------------
                //if(xpos >= screen[CurrentHandy - 1].width - radius && standardxspeed > 0) standardxspeed *= -1;
                //--------------------------------------
                if(xpos - radius <= paddle.xpos + paddle.width && xpos - radius >= paddle.xpos - paddle.width && ypos >= paddle.ypos - paddle.length/2 && ypos <= paddle.ypos + paddle.length/2 && standardxspeed < 0) standardxspeed *= -1;
            }
            if (thisScreen.HandyPosition == amountPlayers){
                //--------------------------------------
                //if(xpos <= radius) standardxspeed *= -1;
                //--------------------------------------
                if(xpos + radius >= paddle.xpos - paddle.width && xpos + radius <= paddle.xpos + paddle.width && ypos >= paddle.ypos - paddle.length && ypos <= paddle.ypos + paddle.length && standardxspeed > 0) standardxspeed *= -1;
            }
        }

        public void sendPos(){
        }

        public void getPosX(float wert){
            xpos = wert;
            Log.i(TAG, "Circle: getPosX hat Empfangen: "+String.valueOf(wert));
        }

        public void getPosY(float wert){
            ypos = wert;
            Log.i(TAG, "Circle: getPosY hat Empfangen: "+String.valueOf(wert));
        }

        public void Point_Scored(char input){
            if(input == 'l'){
                scoreLeft++;
                MainActivity.sendToSendRecive("NBAMsg" + String.valueOf(circle.CurrentHandy));
            }
            if(input == 'r'){
                scoreRight++;
                 //Verschlüsselt ie nachricht
                MainActivity.sendToSendRecive("EoPMsg" + String.valueOf(circle.CurrentHandy));
            }
        }

    }


    class Screen {
        float width;
        float height;
        float density;
        float adjustedHeight;
        float offset;
        int HandyPosition;
        char HandyTask;

        public void getHandyPosition(){

        }

        public void getHandyDimensions(){
            //_----------------------------------
            width = Resources.getSystem().getDisplayMetrics().widthPixels;
            height = Resources.getSystem().getDisplayMetrics().heightPixels;
            //height = 900;
            density =  getResources().getDisplayMetrics().density;
            //---------------------------
        }

        public void sendHandyPosition(){

        }

        public void sendHandyDimensions(){

        }

        public void getOwnHandyPosition(){
            if(HandyTask == 'h') HandyPosition = 1;
            if(HandyTask == 'j') HandyPosition = amountPlayers;
        }

        public void getOwnHandyDimensions(){
            width = Resources.getSystem().getDisplayMetrics().widthPixels;
            height = Resources.getSystem().getDisplayMetrics().heightPixels;
            density =  getResources().getDisplayMetrics().density;

            /*
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
            */

            //width = 1070;
            //height = 2100;
        }

        public void getOwnHandyTask(){
            if(MainActivity.IsHost) HandyTask = 'h';
            else HandyTask = 'j';
        }

        public void getAmountPlayers(){
            amountPlayers = 2;
        }

    }

    class Paddle{
        float xdistance;
        float xpos;
        float ypos;
        float length;
        float width;
        float adjust;
    }


    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        if(thisScreen.HandyTask == 'h' && firstime == true){
            firstime = false;
            Log.i(TAG, "Draw: hat den Initialisirungslauf erreicht ");
            screen[thisScreen.HandyPosition - 1].width = thisScreen.width;
            screen[thisScreen.HandyPosition - 1].height = thisScreen.height;
            screen[thisScreen.HandyPosition - 1].density = thisScreen.density;
            screen[thisScreen.HandyPosition - 1].HandyPosition = thisScreen.HandyPosition;
            screen[thisScreen.HandyPosition - 1].HandyTask = 'h';

            //saveScreen = new Screen();

            /*
            for(int i = 0; i < amountPlayers - 1; i++){
                saveScreen.getHandyDimensions();
                saveScreen.getHandyPosition();
                saveScreen.HandyPosition = amountPlayers;

                screen[saveScreen.HandyPosition - 1].width = saveScreen.width;
                screen[saveScreen.HandyPosition - 1].height = saveScreen.height;
                screen[saveScreen.HandyPosition - 1].density = saveScreen.density;
                screen[saveScreen.HandyPosition - 1].HandyPosition = saveScreen.HandyPosition;
                screen[saveScreen.HandyPosition - 1].HandyTask = 'j';
            }
            */
            zwischenfloat = 9999;
            for(int i = 0; i < amountPlayers; i++){
                if(screen[i].height / screen[i].density < zwischenfloat){
                    zwischenfloat = screen[i].height / screen[i].density;
                    //zwischenspeicher = i;
                }
            }
            for(int i = 0; i < amountPlayers; i++){
                screen[i].adjustedHeight = zwischenfloat * screen[i].density;
                screen[i].offset = (screen[i].height - screen[i].adjustedHeight)/2;

                //_-------------------------------------

                //SENDEN VON OFFSET UND HEIGHT

                //_-------------------------------------
            }
            thisScreen.offset = screen[thisScreen.HandyPosition - 1].offset;
        }



        if(MainActivity.IsReady){

            canvas.drawColor(Color.BLACK);

            canvas.drawRect(0, 0, thisScreen.width, thisScreen.offset, paint);
            canvas.drawRect(0, thisScreen.height - thisScreen.offset, thisScreen.width, thisScreen.height, paint);



            //muss wieder eingefuegt werden:!!!!
            if(thisScreen.HandyPosition == circle.CurrentHandy)canvas.drawCircle(circle.xpos, circle.ypos, circle.radius, paint);
            //----------------------------------------
            //canvas.drawCircle(circle.xpos, circle.ypos, circle.radius, paint);
            //----------------------------------------
            if((thisScreen.HandyPosition == 1 || thisScreen.HandyPosition == amountPlayers) && thisScreen.HandyTask == 'j') canvas.drawRect(paddle.xpos - paddle.width/2, paddle.ypos - paddle.length/2,paddle.xpos + paddle.width/2, paddle.ypos + paddle.length/2, paint);
            if(thisScreen.HandyPosition == 1 && thisScreen.HandyTask == 'h') canvas.drawRect(paddle.xpos - paddle.width/2, paddle.ypos - paddle.length/2,paddle.xpos + paddle.width/2, paddle.ypos + paddle.length/2, paint);
            if(thisScreen.HandyPosition == amountPlayers && thisScreen.HandyTask == 'h') canvas.drawRect(paddle.xpos - paddle.width/2, paddle.ypos - paddle.length/2,paddle.xpos + paddle.width/2, paddle.ypos + paddle.length/2, paint);



            if(thisScreen.HandyPosition == circle.CurrentHandy){
                circle.getSpecificValues();
                canvas.drawCircle(circle.xpos, circle.ypos, circle.radius, paint);
                circle.checkHitbox();
                circle.move();
                Log.i("Dauerrutine: ", "Draw: Auf Handy ist Ball (zeichnen move usw.)" + circle.xpos + " " + circle.ypos);
                if(circle.xpos < 0 + 30 && circle.standardxspeed < 0){
                //Verschlüsselt ie nachricht
                    Log.i(TAG, "Draw: Gateway Message nach links: "+ String.valueOf(thisScreen.HandyPosition - 1) + "*" + String.valueOf(circle.xpos) + ">" + String.valueOf(circle.ypos/thisScreen.adjustedHeight) + "<" + String.valueOf(circle.standardxspeed) + "#" + String.valueOf(circle.standardyspeed));
                    MainActivity.sendToSendRecive("GtwMsg" + String.valueOf(thisScreen.HandyPosition - 1) + "*" + String.valueOf(circle.xpos) + ">" + String.valueOf(circle.ypos/thisScreen.adjustedHeight) + "<" + String.valueOf(circle.standardxspeed) + "#" + String.valueOf(circle.standardyspeed));
                    circle.CurrentHandy--;
                }
                if(circle.xpos > thisScreen.width - 30 && circle.standardxspeed > 0){
                     //Verschlüsselt ie nachricht
                    Log.i(TAG, "Draw: Gateway Message nach rechts: "+ String.valueOf(thisScreen.HandyPosition + 1) + "*" + String.valueOf(circle.xpos) + ">" + String.valueOf(circle.ypos/thisScreen.adjustedHeight) + "<" + String.valueOf(circle.standardxspeed) + "#" + String.valueOf(circle.standardyspeed));
                    MainActivity.sendToSendRecive("GtwMsg" + String.valueOf(thisScreen.HandyPosition + 1) + "*" + String.valueOf(circle.xpos) + ">" + String.valueOf(circle.ypos/thisScreen.adjustedHeight) + "<" + String.valueOf(circle.standardxspeed) + "#" + String.valueOf(circle.standardyspeed));
                    circle.CurrentHandy++;
                }
            }
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if((thisScreen.HandyPosition == 1 || thisScreen.HandyPosition == amountPlayers) && event.getY() < paddle.ypos + paddle.length/2 + paddle.adjust && event.getY() > paddle.ypos - paddle.length/2 - paddle.adjust) {
            paddle.ypos = event.getY();
            if(paddle.ypos < thisScreen.offset + paddle.length/2) paddle.ypos = thisScreen.offset + paddle.length/2;
            if(paddle.ypos > thisScreen.height - thisScreen.offset - paddle.length/2) paddle.ypos = thisScreen.height - thisScreen.offset - paddle.length/2;
        }
        invalidate();
        return true;
    }




}



