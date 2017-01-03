package flutterwave.com.raveApp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import flutterwave.com.rave.Components.RaveDialog;
import flutterwave.com.rave.models.RaveData;

public class MainActivity extends AppCompatActivity {

    private Button raveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        raveButton = (Button)findViewById(R.id.rave_btn);

        raveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coke);
                RaveData raveData = new RaveData(
                        bitmap,
                        "Shawarma and Coke",
                        "Shawarma and Coke for kenny",
                        1400.00,
                        "FLWPUBK-b06794b2e72d2cf13215b220b04f314b-X",
                        "FLWSECK-d4c03911a1c2b2db5fbd39d8ca8c2825-X",
                        "kehinde.a.shittu@gmail.com",
                        "rave-dash-1483447695"
                );
                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
                rave.show();
            }
        });
    }
}
