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
                        "FLWPUBK-dd90ea51702e8c6206be12f8d17bb849",
                        "FLWSECK-b4cb9aa9f1a80c55927ad90cd70bf90a",
                        "kehinde.a.shittu@gmail.com",
                        "FLW-TXREF-PB-1481725499667-RND_76"
                );
                RaveDialog rave = new RaveDialog(MainActivity.this, raveData);
                rave.show();
            }
        });
    }
}
