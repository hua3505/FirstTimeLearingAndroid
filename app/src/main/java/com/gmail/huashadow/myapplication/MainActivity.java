package com.gmail.huashadow.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.gmail.huashadow.myapplication.ticwear.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.gmail.huashadow.myapplication.MESSAGE";
    public final static String SAVED_MESSAGE = "com.gmail.huashadow.myapplication.SAVED_MESSAGE";
    public final static String EXTRA_PHOTO = "com.gmail.huashadow.myapplication.PHOTO";
    public final  static int PICK_CONTACT_REQUEST = 1;
    public final  static int TAKE_PHOTO_REQUEST = 2;

    private  ShareActionProvider mShareActionProvider;
    private String mMessage = "";
    private long mExitTime = 0;
    private String mCurrentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //createDeskShortCut();
    }

    public void createDeskShortCut2() {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.title_activity_photo));
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, getShortCutIcon());
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        getApplicationContext().sendBroadcast(shortCutIntent);
    }

    public void createDeskShortCut() {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Intent intent = new Intent(getApplicationContext(), DisplayMessageActivity.class);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        //shortCutIntent.putExtra("duplicate", "true");
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.title_activity_display_message));
        //shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, getShortCutIcon());
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        getApplicationContext().sendBroadcast(shortCutIntent);
    }

    Bitmap getShortCutIcon() {
        Bitmap bitmap = Bitmap.createBitmap(148, 148, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(74, 74, 50, paint);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //menuItem.getActionProvider()导致crash，不知为何
        //MenuItem menuItem = menu.findItem(R.id.action_share);
        //mShareActionProvider = (ShareActionProvider)menuItem.getActionProvider();


        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setShareIntent(Intent intent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                openSearch();
                return  true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_opengps:
                openGPS();
                return true;
            case R.id.action_send_saved:
                sendMessage(mMessage);
                return true;
            case R.id.action_open_url:
                openUrl("http://www.baidu.com");
                return true;
            case R.id.action_pick_contact:
                pickContact();
                return true;
            case R.id.action_take_photo:
                takePhoto();
                return true;
            case R.id.action_more_apps:
                openMoreApps();
                return true;
            case R.id.action_create_shortcut:
                createDeskShortCut2();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void  onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SAVED_MESSAGE, mMessage);
        savedInstanceState.putString(PhotoActivity.SAVED_PATH, mCurrentPhotoPath);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public  void onRestoreInstanceState(Bundle savedInstanceState) {
        mMessage = savedInstanceState.getString(SAVED_MESSAGE);
        mCurrentPhotoPath = savedInstanceState.getString(PhotoActivity.SAVED_PATH);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
                    exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_CONTACT_REQUEST:
                if (RESULT_OK == resultCode) {
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    cursor.moveToFirst();
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    sendMessage("Phone Number: " + phoneNumber);
                }
                break;
            case TAKE_PHOTO_REQUEST:
                if (RESULT_OK == resultCode) {
                    galleryAddPic();
                    Intent intent = new Intent(this, PhotoActivity.class);
                    intent.putExtra(EXTRA_PHOTO, mCurrentPhotoPath);
                    startActivity(intent);
                }
                break;
        }
    }

    public void exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, R.string.exit_while_repeat, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else {
            finish();
        }
    }

    public void sendMessage(String message) {
        //for test SharedPreferences
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString(getString(R.string.saved_message), message);
//        editor.commit();

        SharedPreferences readableSharedPref = getPreferences(MODE_WORLD_READABLE);
        SharedPreferences.Editor editor1 = readableSharedPref.edit();
        editor1.putString(getString(R.string.saved_message2), message);
        editor1.commit();

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private String getEditorText() {
        EditText editText = (EditText)findViewById(R.id.edit_message);
        int width = editText.getWidth();
        String message = editText.getText().toString();
        mMessage = message;
        return message;
    }

    public void onBtnSend(View view) {
        sendMessage(getEditorText());
    }

    public void onBtnSendWith(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getEditorText());
        sendIntent.setType("text/plain");
        //startActivity(sendIntent);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)));
        //setShareIntent(sendIntent);
    }

    public void openSearch() {
        sendMessage(getResources().getString(R.string.comming_soon));
    }

    public void openSettings() {
       sendMessage(getResources().getString(R.string.comming_soon));
    }

    public void openGPS() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void openUrl(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        List activities = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        }

//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
    }

    public void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    private void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {

            }

            if (photoFile != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST);
            }
        }
    }

    private void openMoreApps() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:magic+watchface")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=magic+watchface")));
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
