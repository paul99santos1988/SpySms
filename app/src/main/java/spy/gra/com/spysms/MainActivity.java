package spy.gra.com.spysms;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import spy.gra.com.spysms.receiver.IncomingSmsReceiver;
import spy.gra.com.spysms.service.SmsService;

/**
 * Main class that registers Receiver and Sms Service to listen to incoming sms
 */
public class MainActivity extends AppCompatActivity implements IncomingSmsReceiver.ShowDialogInterface {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private IncomingSmsReceiver receiver;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Register receiver and service
        receiver = new IncomingSmsReceiver();
        IntentFilter filter = new IntentFilter(Constants.SMS_RECEIVED);
        registerReceiver(receiver, filter);
        receiver.registerReceiver(this);
        startService(new Intent(this, SmsService.class));

        checkForPermissions();
        loadUI();
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }

    private void loadUI() {
        // Load float button that will open email client
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserEmailClient();
            }
        });
    }

    private void openUserEmailClient() {
        Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void smsDetails(String from, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New SMS from: " + from);
            builder
                    .setMessage(message)
                    .setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.getLocalizedMessage();
        }

        Snackbar.make(fab, "New Sms from " + from + "\n " + message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.GREEN)
                .show();

        Toast.makeText(getApplicationContext(), "New Sms from " + from + "\n " + message, Toast.LENGTH_LONG).show();
    }
}