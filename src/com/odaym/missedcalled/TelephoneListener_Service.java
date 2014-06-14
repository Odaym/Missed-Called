package com.odaym.missedcalled;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelephoneListener_Service extends Service {
	public static final String TAG = "TelephoneListener";
	private long startTime = 0;
	private boolean answered = false;
	private long difference;
	private int UNIQUE_NOTIFICATION_NUMBER = 0;
	private String callingNumber;

	public class TelephoneListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d(TAG, "IDLE");
				if (startTime != 0 && !answered) {
					difference = System.currentTimeMillis() - startTime;
					sendNotification(difference / 1000, callingNumber);
				}
				startTime = 0;
				answered = false;
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d(TAG, "OFFHOOK!");
				answered = true;
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d(TAG, "RINGING!");
				startTime = System.currentTimeMillis();
				callingNumber = incomingNumber;
				break;
			default:
				break;
			}
		}
	}

	public void sendNotification(long timeDifference, String incomingNumber) {
		String constructedContext;
		String contactToDisplay = getResources().getString(R.string.unknownContact);

		contactToDisplay = getContactDisplayNameByNumber(incomingNumber);
		if (contactToDisplay == null) {
			contactToDisplay = incomingNumber;
		}

		Log.d(TAG, contactToDisplay + "");

		if (timeDifference < 1) {
			constructedContext = getResources().getString(R.string.phoneRang0);
		} else if (timeDifference == 1) {
			constructedContext = getResources().getString(R.string.phoneRang1);
		} else
			constructedContext = getResources().getString(R.string.phoneRang) + " "
					+ timeDifference + " " + getResources().getString(R.string.secs);

		Notification n = new Notification.Builder(this)
				.setContentTitle(contactToDisplay).setContentText(constructedContext)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true).build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		UNIQUE_NOTIFICATION_NUMBER++;
		notificationManager.notify(UNIQUE_NOTIFICATION_NUMBER, n);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public String getContactDisplayNameByNumber(String number) {
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		String name = null;

		ContentResolver contentResolver = getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] {
				BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null,
				null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				name = contactLookup.getString(contactLookup
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}

		return name;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelephoneListener customPhoneListener = new TelephoneListener();
		telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}
