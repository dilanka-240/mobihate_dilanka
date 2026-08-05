package app.dilanka.mobihate.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;

import app.dilanka.mobihate.MainActivity;

public class GrayService extends AccessibilityService {

    private static boolean running = false;
    private static WindowManager windowManager;
    private static View overlayView;

    public static boolean isRunning() {
        return running;
    }

    public static void updateIntensity(int percent){
        if(overlayView == null){
            return;
        }
        int alpha = (int) (255 * (percent / 100f));
        overlayView.setBackgroundColor(Color.argb(alpha, 30, 30,30));
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int intensity = intent != null ? intent.getIntExtra("intensity", 70) : 70;
        startForeground(1, buildNotification());
        showOverlay(intensity);
        running = true;
        return START_STICKY;
    }

    private void showOverlay(int intensity) {
        if (overlayView != null) return;

        overlayView = new View(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(overlayView, params);
        updateIntensity(intensity);
    }

    private Notification buildNotification() {
        String channelId = "gray_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Grayscale Filter", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        PendingIntent pi = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Grayscale Filter Active")
                .setContentText("Tap to adjust")
                .setSmallIcon(android.R.drawable.ic_menu_manage)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){}

    @Override
    public void onInterrupt(){}

    @Override
    public void onDestroy() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
        running = false;
        super.onDestroy();
    }

}
