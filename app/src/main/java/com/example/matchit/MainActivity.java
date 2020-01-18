package com.example.matchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView logo;
    private Button playBtn;
    private RadioGroup timeLimitRg;
    private RadioButton timeLimitRb;
    private RadioGroup sizeLimitRg;
    private RadioButton sizeLimitRb;
    private RadioGroup pairRg;
    private RadioButton pairRb;
    private Button disclaimerBtn;
    private TextView disclaimerTv;
    private Animation fromTop;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        setupOnClickEvents();
    }
    private void initComponent() {
        playBtn = findViewById(R.id.playBtn);
        logo = findViewById(R.id.logoIv);
        timeLimitRg = findViewById(R.id.timeRGroup);
        sizeLimitRg = findViewById(R.id.SizeRGroup);
        pairRg = findViewById(R.id.pairsRg);
        disclaimerBtn = findViewById(R.id.disclaimerBtn);
        disclaimerTv = findViewById(R.id.disclaimerTv);

        intent = new Intent(MainActivity.this, GameActivity.class);

        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        logo.setAnimation(fromTop);
    }
    private void setupOnClickEvents() {
        setUpPlayBtnClickEvent();
        setupDisclaimerEvent();

    }

    private void setupDisclaimerEvent() {
        disclaimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disclaimerTv.setVisibility(disclaimerTv.getVisibility() == View.VISIBLE ?  View.GONE :  View.VISIBLE);
            }
        });
    }

    private void setUpPlayBtnClickEvent() {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGame();
            }
        });
    }

    private void goToGame(){
        getRadioButtonsInfo();

        if(radioBtnNotFilled()) {
            Toast.makeText(this,"Please select one option for all choices", Toast.LENGTH_SHORT).show();
            return;
        }

        fillIntent();
        startActivity(intent);
    }

    private void fillIntent() {

        int size = Integer.parseInt((String)sizeLimitRb.getText());
        int time = Integer.parseInt((String)timeLimitRb.getText());
        String pair = (String)pairRb.getText();

        intent.putExtra("SIZE", size);
        intent.putExtra("TIME", time);
        intent.putExtra("PAIR", pair.equals("pair") ? 2 : 4);
    }

    private boolean radioBtnNotFilled() {
        return timeLimitRb == null || sizeLimitRb == null || pairRb == null;
    }

    private void getRadioButtonsInfo() {
        int selectedId = timeLimitRg.getCheckedRadioButtonId();
        timeLimitRb = (RadioButton) findViewById(selectedId);
        selectedId = sizeLimitRg.getCheckedRadioButtonId();
        sizeLimitRb = (RadioButton) findViewById(selectedId);
        selectedId = pairRg.getCheckedRadioButtonId();
        pairRb = (RadioButton) findViewById(selectedId);
    }
}
