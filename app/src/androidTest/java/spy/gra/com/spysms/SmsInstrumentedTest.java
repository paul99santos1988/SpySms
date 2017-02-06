package spy.gra.com.spysms;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import spy.gra.com.spysms.service.SmsService;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SmsInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        Intents.init();
    }

    @Test
    public void clickingFab_shouldStartEmailClient() {
        onView(withId(R.id.fab)).perform(click());
        intended(hasAction(equalTo(Intent.CATEGORY_APP_EMAIL)));
    }

    @Test
    public void servicesStartedOnInit() throws TimeoutException {
        context.startService(new Intent(context, SmsService.class));
        assertTrue(SmsService.started);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}