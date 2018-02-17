package com.bugfuzz.android.projectwalrus.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.bugfuzz.android.projectwalrus.R;
import com.bugfuzz.android.projectwalrus.data.Card;
import com.bugfuzz.android.projectwalrus.data.CardData;
import com.bugfuzz.android.projectwalrus.data.DatabaseHelper;
import com.bugfuzz.android.projectwalrus.data.QueryUtils;
import com.bugfuzz.android.projectwalrus.device.CardDevice;
import com.bugfuzz.android.projectwalrus.util.GeoUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.VIBRATOR_SERVICE;

public class BulkReadCardsThread extends Thread {

    private static final String NOTIFICATION_CHANNEL_ID = "bulk_read_cards";
    private static final int BASE_NOTIFICATION_ID = 0;

    private static Map<Integer, BulkReadCardsThread> runningInstances = new HashMap<>();
    private static int nextID;

    private final Context context;

    private final CardDevice cardDevice;
    private final Class<? extends CardData> cardDataClass;
    private final Card cardTemplate;

    private final int id;

    private boolean stop;

    private DatabaseHelper databaseHelper;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location currentBestLocation;

    public BulkReadCardsThread(Context context, CardDevice cardDevice,
                               Class<? extends CardData> cardDataClass, Card cardTemplate) {
        this.context = context;
        this.cardDevice = cardDevice;
        this.cardDataClass = cardDataClass;
        this.cardTemplate = cardTemplate;

        id = nextID++;
    }

    @Override
    public void run() {
        runningInstances.put(id, this);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Bulk Read Cards", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Background bulk reading operations");
            notificationManager.createNotificationChannel(channel);
        }

        CardDevice.Metadata deviceMetadata =
                cardDevice.getClass().getAnnotation(CardDevice.Metadata.class);

        Intent stopIntent = new Intent(context, StopBroadcastReceiver.class);
        stopIntent.setAction(StopBroadcastReceiver.ACTION_STOP);
        stopIntent.putExtra(StopBroadcastReceiver.EXTRA_ID, id);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // TODO: icon
                        .setContentTitle("Bulk reading cards")
                        .setContentText("From " + deviceMetadata.name())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                deviceMetadata.icon()))
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setOngoing(true)
                        .setProgress(0, 0, true)
                        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop",
                                PendingIntent.getBroadcast(context, id, stopIntent, 0));

        notificationManager.notify(BASE_NOTIFICATION_ID + id, builder.build());

        // TODO: fix in OrmLiteBaseAppCompatActivity too (use class ver)
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        if (currentBestLocation == null ||
                                GeoUtils.isBetterLocation(location, currentBestLocation))
                            currentBestLocation = location;
                    }
                }
            };

            Looper.prepare();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
        } catch (SecurityException ignored) {
        }

        try {
            readCards();
        } finally {
            if (locationCallback != null)
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);

            OpenHelperManager.releaseHelper();

            notificationManager.cancel(BASE_NOTIFICATION_ID + id);

            runningInstances.remove(id);
        }
    }

    private void readCards() {
        try {
            cardDevice.readCardData(cardDataClass, new CardDevice.CardDataSink() {
                private int numRead = 0;
                private CardData lastCardData;

                @Override
                public void onCardData(CardData cardData) {
                    if (cardData.equals(lastCardData))
                        return;
                    lastCardData = cardData;

                    Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                    if (vibrator != null)
                        vibrator.vibrate(300);

                    // TODO: clone the template instead of modifying
                    String origName = cardTemplate.name;

                    cardTemplate.setCardData(cardData);
                    if (currentBestLocation != null) {
                        cardTemplate.cardLocationLat = currentBestLocation.getLatitude();
                        cardTemplate.cardLocationLng = currentBestLocation.getLongitude();
                    } else
                        cardTemplate.cardLocationLat = cardTemplate.cardLocationLng = null;
                    cardTemplate.name = origName + " (" + ++numRead + ")";

                    databaseHelper.getCardDao().create(cardTemplate);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(
                            new Intent(QueryUtils.ACTION_WALLET_UPDATE));

                    cardTemplate.name = origName;
                }

                @Override
                public boolean wantsMore() {
                    return !stop;
                }
            });
        } catch (IOException exception) {
            Toast.makeText(context, "Failed while bulk reading cards: " + exception.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static class StopBroadcastReceiver extends BroadcastReceiver {

        private static final String ACTION_STOP = "com.bugfuzz.android.projectwalrus.ui.BulkReadCardsThread$StopBroadcastReceiver.ACTION_STOP";

        private static final String EXTRA_ID = "com.bugfuzz.android.projectwalrus.ui.BulkReadCardsThread$StopBroadcastReceiver.EXTRA_ID";

        @Override
        public void onReceive(Context context, Intent intent) {
            BulkReadCardsThread thread = runningInstances.get(intent.getIntExtra(EXTRA_ID, -1));
            if (thread != null)
                thread.stop = true;
        }
    }
}
