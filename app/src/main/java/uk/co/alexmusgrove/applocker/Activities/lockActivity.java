package uk.co.alexmusgrove.applocker.Activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.alexmusgrove.applocker.Preferences.settingsPreferences;
import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Services.appService;

public class lockActivity extends AppCompatActivity {

    private static final String TAG = "uk.co.alexmusgrove.applocker.Activities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_lock);
        Log.i(TAG, "password: " + settingsPreferences.getPassword(this));

        Intent intent = getIntent();
        generateAppDetails(intent.getStringExtra("packageName"));

        final EditText editText = findViewById(R.id.password_editText);

        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (isPasswordCorrect(editText.getText().toString())){
                            Intent unlockintent = new Intent(getApplicationContext(), appService.class);
                            unlockintent.putExtra("unlockedApp", intent.getStringExtra("packageName"));
                            finish();
                            startService(unlockintent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT);
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }


    /**
     * Name of the package
     * @param packageName
     *
     * generateAppDetails returns all the different UI elements filled with data specific
     * to the application passed through
     */
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

    /**
     *
     * @param password
     * @return returns whether the password entered matches the password set.
     */
    public boolean isPasswordCorrect (String password) {
        String testPassword = settingsPreferences.getPassword(this);
        return password.equals(testPassword);
    }
}
