package uk.co.alexmusgrove.applocker.Activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Services.appService;

public class lockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        Intent intent = getIntent();
        generateAppDetails(intent.getStringExtra("packageName"));

        final Button button = findViewById(R.id.authenticate_button);
        final EditText editText = findViewById(R.id.password_editText);

        button.setOnClickListener(v -> {
            
            Intent unlockintent = new Intent(this, appService.class);
            unlockintent.putExtra("unlockedApp", intent.getStringExtra("packageName"));
            finish();
            startService(unlockintent);
        });
    }

    public void generateAppDetails (String packageName) {
        PackageManager packageManager = getPackageManager();

        try {
            ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageName, 0);

            Drawable icon = packageManager.getApplicationIcon(app);
            String name = (String) packageManager.getApplicationLabel(app);

            ImageView appIcon = (ImageView) findViewById(R.id.appicon_imageview);
            TextView appLabel = (TextView) findViewById(R.id.appname_textview);

            appIcon.setImageDrawable(icon);
            appLabel.setText(name);

        } catch (PackageManager.NameNotFoundException e) {
            Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }

    }
}
