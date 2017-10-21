package com.bugfuzz.android.projectwalrus.device.chameleonmini;

import android.os.AsyncTask;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bugfuzz.android.projectwalrus.R;
import com.bugfuzz.android.projectwalrus.device.CardDevice;
import com.bugfuzz.android.projectwalrus.device.CardDeviceManager;

import java.io.IOException;

public class ChameleonMiniActivity extends AppCompatActivity {
    public static final String EXTRA_DEVICE = "com.bugfuzz.android.projectwalrus.device.chameleonmini.ChameleonMiniActivity.EXTRA_DEVICE";

    private ChameleonMiniDevice chameleonMiniDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chameleon_mini);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        CardDevice cardDevice = CardDeviceManager.INSTANCE.getCardDevices().get(
                getIntent().getIntExtra(EXTRA_DEVICE, -1));
        if (cardDevice == null) {
            finish();
            return;
        }
        try {
            chameleonMiniDevice = (ChameleonMiniDevice) cardDevice;
        } catch (ClassCastException e) {
            finish();
            return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.settings, new ChameleonMiniActivity.SettingsFragment())
                .commit();


        ((TextView) findViewById(R.id.version)).setText("Retrieving...");

        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return chameleonMiniDevice.getVersion();
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String version) {
                ((TextView) findViewById(R.id.version)).setText(version != null ? version : "(Unable to determine)");
            }
        }).execute();

    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_chameleon_mini);
        }
    }

}
