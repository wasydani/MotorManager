package de.blitzkrieg.motormanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import static de.blitzkrieg.motormanager.MainActivity.EXTRA_MESSAGE;

public class Main2Activity extends AppCompatActivity {

    private EditText mTempo;
    private ImageView mSign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mTempo = findViewById(R.id.editText3);
        mSign = findViewById(R.id.oneImView);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(EXTRA_MESSAGE);
        if (EXTRA_MESSAGE != null) {
            mTempo.setText(msg);
        } else {
            mTempo.setText("0.0");
        }




    }

}
