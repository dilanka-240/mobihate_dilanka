package app.dilanka.mobihate;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;

import app.dilanka.mobihate.services.GrayService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toggleBtn = findViewById(R.id.btnToggle);
        Button bedTimeBtn = findViewById(R.id.btnOpenBedTime);
        SeekBar intensityBar = findViewById(R.id.intensityBar);
        TextView label = findViewById(R.id.label);

        // open gray filter
        toggleBtn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, 100);
                return;
            }

            if (GrayService.isRunning()) {
                stopService(new Intent(this, GrayService.class));
                toggleBtn.setText("Enable Grayscale");
            } else {
                startService(new Intent(this, GrayService.class));
                toggleBtn.setText("Disable Grayscale");
            }
        });

        // sending to bedtime on/off settings
        bedTimeBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.google.android.apps.wellbeing",
                    "com.google.android.apps.wellbeing.settings.ui.bedtime.BedTimeActivity"
            ));
            try{
                startActivity(intent);
            }catch(Exception e){
                Intent wellBeingIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.wellbeing");
                if(wellBeingIntent != null){
                    startActivity(wellBeingIntent);
                }else{
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });

        intensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser){
                label.setText("Intensity:" + progress + "%");
                if (GrayService.isRunning() && fromUser) {

                    GrayService.updateIntensity(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }

        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Button toggleBtn = findViewById(R.id.btnToggle);
        toggleBtn.setText(GrayService.isRunning() ? "Disable Grayscale" : "Enable Grayscale");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this, "Overlay permission required", Toast.LENGTH_LONG).show();
            }
        }
    }
}
