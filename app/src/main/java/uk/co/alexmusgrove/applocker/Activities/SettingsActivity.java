package uk.co.alexmusgrove.applocker.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        Context context = getApplicationContext();
        CardView resetpassword_card = this.findViewById(R.id.resetpassword_view);

        resetpassword_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordFragment dialog = passwordFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialog.show(fragmentManager,"passwordFragment");
            }
        });
    }

    //Overriding methods for options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.main_item){
            Toast.makeText(this, "Main Menu Button", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
