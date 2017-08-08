package ivan.ivanmuncher;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;
    //size
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;
    // position
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int pinkX;
    private int pinkY;
    private int blackX;
    private int blackY;

   // Speed
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    //score
    private int score = 0;

    // init class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sound = new SoundPlayer(this);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        box = (ImageView) findViewById(R.id.box);
        orange = (ImageView) findViewById(R.id.chicken);
        pink = (ImageView) findViewById(R.id.ham);
        black = (ImageView) findViewById(R.id.black);

        // get screen size
        WindowManager  wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //

        Log.v("Speed_BOX", boxSpeed+"");
        Log.v("Speed_orange", orangeSpeed+"");
        Log.v("Speed_pink", pinkSpeed+"");
        Log.v("Speed_black", blackSpeed+"");
        // move out side of the screeen
        orange.setX(0-80);
        orange.setY(0-80);
        pink.setX(0-80);
        pink.setY(0-80);
        black.setX(0-80);
        black.setY(0-80);
        scoreLabel.setText("Score : 0");

    }

    public void changeSpeed(){
        if (score < 100){
            boxSpeed = Math.round(screenHeight / 80F);
            orangeSpeed = Math.round(screenHeight /80F);
            pinkSpeed = Math.round(screenHeight / 56F);
            blackSpeed = Math.round(screenHeight /55F);
        } else if (100<= score && score < 200){
            boxSpeed = Math.round(screenHeight / 70F);
            orangeSpeed = Math.round(screenHeight /70F);
            pinkSpeed = Math.round(screenHeight / 40F);
            blackSpeed = Math.round(screenHeight /35F);
        } else if (score > 500){
            boxSpeed = Math.round(screenHeight / 30F);
            orangeSpeed = Math.round(screenHeight /40F);
            pinkSpeed = Math.round(screenHeight / 30F);
            blackSpeed = Math.round(screenHeight /25F);
        }

    }

    public void changePos() {

        changeSpeed();
        hitCheck();
        // orange
        orangeX -= orangeSpeed;
        if(orangeX <0){
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));

        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //black
        blackX -= blackSpeed;
        if(blackX <0){
            blackX = screenWidth + 10;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));

        }
        black.setX(blackX);
        black.setY(blackY);

        //pink
        pinkX -= pinkSpeed;
        if(pinkX <0){
            pinkX = screenWidth + 5000;
            pinkY = (int) Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        if(action_flg == true){
            // touching
            boxY -= 20;
        } else {
            // release
            boxY += 20;
        }
        if (boxY <0) boxY = 0;

        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;
        box.setY(boxY);
        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck() {
        //

        // orange
        int orangeCenterX = orangeX + orange.getWidth() /2;
        int orangeCenterY = orangeY + orange.getHeight() /2;
        // 0 <= orange center x <= boxxwidth
        // boxY <= orangeCenterY <= boxY + boxheight
        if (0 <= orangeCenterX && orangeCenterX <= boxSize &&  boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize){
            score += 10;
            orangeX = -10;
            sound.playHitSound();
        }

        // pink
        int pinkCenterX = pinkX + pink.getWidth() /2;
        int pinkCenterY = pinkY + pink.getHeight() /2;
        if (0 <= pinkCenterX && pinkCenterX <= boxSize &&  boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize){
            score += 30;
            pinkX = -10;
            sound.playHitSound();

        }

        // pink
        int blackCenterX = blackX + black.getWidth() /2;
        int blackCenterY = blackY + black.getHeight() /2;
        if (0 <= blackCenterX && blackCenterX <= boxSize &&  boxY <= blackCenterY && blackCenterY <= boxY + boxSize){
            // timer stop
            timer.cancel();
            timer = null;
            // show res
            Intent intent = new Intent (getApplicationContext(), result.class);
            intent.putExtra("SCORE",score);
            startActivity(intent);
            sound.playOverSound();
        }
    }

    public boolean onTouchEvent(MotionEvent me) {

        if (start_flg == false ){
            start_flg = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            boxY = (int)box.getY();
            // this box is a sqr height + width same
            boxSize = box.getHeight();
            startLabel.setVisibility(View.GONE);
            timer.schedule( new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);

        } else {

            if(me.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }

        }




        return true;
    }

    // disable return
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
