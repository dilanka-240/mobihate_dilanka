package app.dilanka.mobihate;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;

import app.dilanka.mobihate.repository.GrayscaleRepository;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat grayscaleSwitch;
    private GrayscaleRepository grayscaleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grayscaleSwitch = findViewById(R.id.grayscaleSwitch);
        grayscaleRepository = new GrayscaleRepository();
        
        grayscaleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean success = grayscaleRepository.applyGrayscale(isChecked, this);
            if (!success) {
                Toast.makeText(this, "Permission Denied! Run ADB command to enable.", Toast.LENGTH_LONG).show();
                grayscaleSwitch.setChecked(!isChecked);
            }
        });
    }
}
