package flutterwave.com.raveApp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.common.collect.Lists;

import java.util.Map;

import flutterwave.com.rave.Components.RaveDialog;
import flutterwave.com.rave.models.RaveData;
import flutterwave.com.rave.utils.RaveAuthModel;

public class MainActivity extends AppCompatActivity {

    private Button raveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        raveButton = (Button)findViewById(R.id.rave_btn);

//         vbvssecure
//
//        raveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RaveData raveData = new RaveData.Builder(
//                        "Shawarma and Coke",
//                        "Shawarma and Coke for kenny",
//                        1400.00,
//                        "FLWPUBK-b06794b2e72d2cf13215b220b04f314b-X",
//                        "FLWSECK-d4c03911a1c2b2db5fbd39d8ca8c2825-X",
//                        "kehinde.a.shittu@gmail.com",
//                        "rave-dash-1483447695",
//                        RaveAuthModel.VBV_SECURECODE)
//                        .build();
//                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
//                rave.show();
//            }
//        });

        //no auth
//
//        raveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RaveData raveData = new RaveData.Builder(
//                        "Shawarma and Coke",
//                        "Shawarma and Coke for kenny",
//                        1400.00,
//                        "FLWPUBK-bcdc25e5a478ee7e465050b8d0c843f2-X",
//                        "FLWSECK-a4e9c2480ddf7565a62c1a02c9be3252-X",
//                        "kehinde.a.shittu@gmail.com",
//                        "rave-checkout-1485605746",
//                        RaveAuthModel.NOAUTH)
//                        .build();
//                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
//                rave.show();
//            }
//        });

//        // random debit
//
//        raveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RaveData raveData = new RaveData.Builder(
//                        "Shawarma and Coke",
//                        "Shawarma and Coke for kenny",
//                        10000.0,
//                        "FLWPUBK-e8fdf9cf4d34c2084b040b5f8b214844-X",
//                        "FLWSECK-fc2285159317c22ffaf3c811a68bc430-X",
//                        "kehinde.a.shittu@gmail.com",
//                        "rave-checkout-1485623235",
//                        RaveAuthModel.RANDOM_DEBIT)
//                        .build();
//                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
//                rave.show();
//            }
//        });


// pin
        raveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RaveData raveData = new RaveData.Builder(
                        "Shawarma and Coke",
                        "Shawarma and Coke for kenny",
                        1400.00,
                        "FLWPUBK-6c011cdb12f0c81e332424b061047e7b-X",
                        "FLWSECK-9796e060383fecbe90f984dfa9e2e1df-X",
                        "kehinde.a.shittu@gmail.com",
                        "rave-checkout-1485623235",
                        RaveAuthModel.PIN)
                        .build();
                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
                rave.show();
            }
        });
    }
}
