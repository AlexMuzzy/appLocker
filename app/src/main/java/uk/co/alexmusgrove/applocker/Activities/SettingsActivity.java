package uk.co.alexmusgrove.applocker.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        CardView resetpassword_card = this.findViewById(R.id.resetpassword_view);
        resetpassword_card.setOnClickListener(v -> {
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager,"passwordFragment");
        });
    }

    ShareActionProvider shareActionProvider = null;
    //Overriding methods for options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_share);
        ShareActionProvider shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent send = new Intent();
        send.setAction(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, AppSQLiteDBHelper.getAllApps(getApplicationContext()).size() + " Apps are locked to appLocker");
        send.setType("text/plain");
        if(shareAction != null) {
            shareAction.setShareIntent(send);
        }

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.main_item){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            return true;
        }
        if(item.getItemId()==R.id.user_guide_item){
            Intent userGuideIntent = new Intent(this, UserGuideActivity.class);
            startActivity(userGuideIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
