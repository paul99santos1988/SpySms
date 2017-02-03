package spy.gra.com.spysms;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowSmsManager;
import org.robolectric.util.ServiceController;

import java.util.ArrayList;
import java.util.List;

import spy.gra.com.spysms.receiver.IncomingSmsReceiver;
import spy.gra.com.spysms.service.SmsService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.robolectric.Shadows.shadowOf;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Config(constants = BuildConfig.class, sdk = 25, shadows = {ShadowSmsManager.class})
@RunWith(RobolectricTestRunner.class)
public class SmsUnitTest {

    private IncomingSmsReceiver receiver;
    private Application application;
    private ServiceController<SmsService> controller;

    private final Intent receiveSMSIntent = new Intent("android.permission.RECEIVE_SMS");
    private final Intent sendSMSIntent = new Intent("android.permission.SEND_SMS");
    private final Intent readSMSIntent = new Intent("android.permission.READ_SMS");
    private final Intent writeSMSIntent = new Intent("android.permission.WRITE_SMS");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        application = RuntimeEnvironment.application;
        receiver = mock(IncomingSmsReceiver.class);
        controller = Robolectric.buildService(SmsService.class);
    }

    @Test
    public void isAppServiceRegisteredInAndroid() {
        try {
            boolean found = false;
            List<PInfo> allPackages = getInstalledApps(false);

            for (PInfo packageName : allPackages) {
                PackageInfo data = application.getPackageManager().getPackageInfo(
                        packageName.name, PackageManager.GET_RECEIVERS);

                if (data != null) {
                    if (packageName.name.equals("spy.gra.com.spysms")) {
                        assertEquals("spy.gra.com.spysms", packageName.name);
                        found = true;
                    }
                }
            }
            assertTrue(found);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    class PInfo {
        private String name;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<>();
        List<PackageInfo> packs = application.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            if ((!getSysPackages) && (packs.get(i).versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.name = packs.get(i).packageName;
            res.add(newInfo);
        }
        return res;
    }

    @Test
    public void isSmsSent() {
        String message = "Test messsage";
        String phoneNumber = "0040752284577";
        sendSMS(phoneNumber, message);

        ShadowSmsManager shadowSmsManager = shadowOf(SmsManager.getDefault());
        ShadowSmsManager.TextSmsParams lastSentTextMessageParams = shadowSmsManager.getLastSentTextMessageParams();

        assertEquals(phoneNumber, lastSentTextMessageParams.getDestinationAddress());
        assertEquals(message, lastSentTextMessageParams.getText());
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Test
    public void onReceivingAnyIntentShouldStartService() throws Exception {
        Intent expectedService = new Intent(application, SmsService.class);
        Intent actionIntent = new Intent();
        actionIntent.setAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        receiver.onReceive(application, actionIntent);

        assertEquals(expectedService.getComponent(), controller.getIntent().getComponent());
    }

    @Test
    public void testBroadcastReceiverRegistered() {
        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();
        assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = IncomingSmsReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        // False if not found
        Assert.assertTrue(receiverFound);
    }
}