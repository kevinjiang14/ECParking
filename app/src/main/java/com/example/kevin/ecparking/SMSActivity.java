package com.example.kevin.ecparking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

public class SMSActivity extends AppCompatActivity {
    private String twilioNumber = "2012926798";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // SMS Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS}, 77 );
        }

        Button submitbutton = (Button)findViewById(R.id.submit);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitResult();
            }
        });
    }

    public void SubmitResult(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(twilioNumber, null, "Gay", null, null);
    }
}
