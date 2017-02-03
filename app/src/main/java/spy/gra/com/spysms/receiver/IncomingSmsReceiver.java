package spy.gra.com.spysms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Receiver that listens for incoming sms
 */
public class IncomingSmsReceiver extends BroadcastReceiver {

    private ShowDialogInterface callback;

    public void onReceive(Context context, Intent intent) {

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            String messageBody = "no data";
            String messageFrom = "no data";
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                 messageBody = smsMessage.getMessageBody();
                 messageFrom = smsMessage.getDisplayOriginatingAddress();
            }

            if(callback != null) {
                callback.smsDetails(messageFrom, messageBody);
            }
        }
    }

    public void registerReceiver(ShowDialogInterface receiverCallback) {
        this.callback = receiverCallback;
    }

    // Show different UI elements to notifying the user of an incoming SMS
    public interface ShowDialogInterface {
        void smsDetails(String from, String message);
    }
}