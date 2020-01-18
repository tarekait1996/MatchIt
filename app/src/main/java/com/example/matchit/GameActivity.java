package com.example.matchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    private class Card {
        int position;
        ImageView image;
        public Card(ImageView img, int position){
            this.image = img;
            this.position = position;
        }
    }

    private Button mRetryBtn;
    private Button mMainMenuBtn;
    private GridView mGv;
    private ImageView mResultIv;
    private TextView mResultTv;
    private TextView mPointsTv;
    private TextView mTimeTv;
    private LinearLayout dashboardLl;

    private Stack<Card> clickedStack = new Stack<>();
    private List<Integer> cards;
    private int[] drawableImages;

    private int score;
    private int difficulty_pairs;
    private int MAX_SCORE;
    private int difficulty_selectedNumb;
    private int time;
    private int counter;

    CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initComponent();
        setupClickEvents();
    }
    private void initComponent() {
        mRetryBtn = findViewById(R.id.replayBtn);
        mGv = findViewById(R.id.gridViewGame);
        mResultIv = findViewById(R.id.congratsIv);
        mResultTv = findViewById(R.id.congratsTxt);
        mTimeTv = findViewById(R.id.timeTv);
        dashboardLl = findViewById(R.id.dashboardLl);
        mPointsTv = findViewById(R.id.scoreTv);
        mMainMenuBtn = findViewById(R.id.mainMenuBtn);

        Intent intent = getIntent();
        difficulty_selectedNumb = intent.getIntExtra("PAIR", 2);
        time = intent.getIntExtra("TIME", 30);
        difficulty_pairs = intent.getIntExtra("SIZE", 16)/difficulty_selectedNumb;

        drawableImages = new int[]{R.drawable.pikachu, R.drawable.venusaur, R.drawable.charizard,
                R.drawable.blastoise,R.drawable.chimchar, R.drawable.piplup, R.drawable.turtwig,
                R.drawable.gengar, R.drawable.luxray, R.drawable.garbodor, R.drawable.misdreavus,
                R.drawable.froakie};

        MAX_SCORE = difficulty_pairs*difficulty_selectedNumb;

        setupGame();
    }

    private void setupGame() {

        setupGameVisibility();
        setupCards();
        updateScore();
        startTimer();
    }

    private void setupGameVisibility() {
        mGv.setVisibility(View.VISIBLE);
        mRetryBtn.setVisibility(View.GONE);
        mMainMenuBtn.setVisibility(View.GONE);
        mResultTv.setVisibility(View.GONE);
        mResultIv.setVisibility(View.GONE);
        dashboardLl.setVisibility(View.VISIBLE);
    }

    private void setupCards() {
        score = 0;
        cards = new ArrayList<>();

        setupImageAdapter();
        fillCardList(drawableImages, cards);
    }

    private void setupImageAdapter() {
        ImageAdapter imageAdapter = new ImageAdapter(this, MAX_SCORE);
        mGv.setAdapter(imageAdapter);
    }

    private void fillCardList(int[] drawableImages, List<Integer> cards) {
        for(int j =0; j < difficulty_selectedNumb; j++)
            for(int i =0; i < difficulty_pairs; i++)
                cards.add(drawableImages[i]);
        Collections.shuffle(cards);
    }

    private void updateScore() {
        mPointsTv.setText(String.valueOf(score));
    }

    private void startTimer() {
        counter = time;

        mCountDownTimer = new CountDownTimer(counter*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimeTv.setText("0:" + checkDigit(counter));

                if (millisUntilFinished < 10000){
                    mTimeTv.setTextColor(Color.RED);
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(200);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    mTimeTv.startAnimation(anim);
                }
                counter--;
            }

            public void onFinish() {
                setupEndScreen(false);
            }

        };
        mCountDownTimer.start();
    }


    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void setupClickEvents() {
        setupRetryClickEvent();
        setupGridClickEvents();
        setupMainMenuBtnClickEvent();
    }

    private void setupMainMenuBtnClickEvent() {
        mMainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupRetryClickEvent() {

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupGame();

            }
        });
    }

    private void setupGridClickEvents() {
        mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                onCardClicked(i, l, view, adapterView);
            }
        });
    }

    private void onCardClicked(final int position, long itemID, final View view, AdapterView<?> adapterView) {

        if(clickedStack.empty()){
            openCard(position, view);
            clickedStack.push(new Card((ImageView)view, position));
        }
        else if (clickedStack.peek().position == position){
            ((ImageView)view).setImageResource(R.drawable.back);
            clickedStack.pop();
        }
        else {
            openCard(position, view);
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    determineAction(position, view);
                }
            },500);


        }
    }

    private void determineAction(int position, View view) {
        if(cards.get(position).equals(cards.get(clickedStack.peek().position))){

            clickedStack.push(new Card((ImageView)view, position));



            if(clickedStack.size() == difficulty_selectedNumb){
                Toast.makeText(getApplicationContext(), "Good Job!", Toast.LENGTH_SHORT).show();
                while(!clickedStack.isEmpty()) {
                    ImageView temp = clickedStack.pop().image;
                    score++;
                    updateScore();
                    temp.setVisibility(View.GONE);
                }
                if(score == MAX_SCORE){
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                    setupEndScreen(true);
                }
            }

        }
        else {
            clickedStack.push(new Card((ImageView)view, position));
            Toast.makeText(getApplicationContext(), "Oh no, keep trying!", Toast.LENGTH_SHORT).show();
            while(!clickedStack.isEmpty()) {
                ImageView temp = clickedStack.pop().image;
                temp.setImageResource(R.drawable.back);
            }
        }
    }



    private void setupEndScreen(boolean won) {

        mCountDownTimer.cancel();
        mTimeTv.setTextColor(Color.BLACK);
        mTimeTv.clearAnimation();

        mGv.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.VISIBLE);
        mMainMenuBtn.setVisibility(View.VISIBLE);
        mResultTv.setVisibility(View.VISIBLE);
        mResultIv.setVisibility(View.VISIBLE);
        dashboardLl.setVisibility(View.GONE);


        if(won){
            mResultIv.setImageResource(R.drawable.win);
            mResultTv.setText(R.string.winText);
        }
        else {
            mResultIv.setImageResource(R.drawable.faint);
            mResultTv.setText(R.string.lossText);
        }
    }

    private void openCard(int position, View view){
        ((ImageView)view).setImageResource(cards.get(position));
    }

}
