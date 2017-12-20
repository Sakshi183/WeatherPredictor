package com.example.android.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String s = name.getText().toString();
                    s = s.trim();
                    //Log.v("Name",s);
                    if (s.length()==0) {
                        Toast.makeText(MainActivity.this, "Please enter your name ", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            //Log.v("Name passed:",s);

                        Intent i = new Intent(MainActivity.this, MapsActivity.class);

                        Bundle bundle = new Bundle();

                        bundle.putString("NAME", name.getText().toString());
                        //Log.v("Name display")
                        //Add the bundle to the intent
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                }


        });




    }

}
