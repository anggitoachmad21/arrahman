package id.latenight.creativepos.util;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import id.latenight.creativepos.MainActivity;
import id.latenight.creativepos.R;
import pub.devrel.easypermissions.EasyPermissions;

public class MyApplication extends Application implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface
{
    private final String TAG = MainActivity.class.getSimpleName();
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private BluetoothAdapter mAdapeter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService mService = new BluetoothService(this, new BluetoothHandler(this));
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private boolean isPrinterReady = false;

    private static MyApplication sInstance;
    private Context context;
    private SessionManager sessionManager;

    public static MyApplication getApplication() {
        return sInstance;
    }


    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions((Activity) context, "You need bluetooth permission", RC_BLUETOOTH, params);
            return;
        }
        sInstance = this;
    }

    public void setupBluetoothConnection(String address)
    {
        // Either setup your connection here, or pass it in
        mDevice = mService.getDevByMac(address);
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            mService.connect(mDevice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocket getCurrentBluetoothConnection()
    {
        return mSocket;
    }

    public int isConnected() {
        return mService.getState();
    }

    public void sendMessage(String header, String body) {
        if (!mService.isAvailable()) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth");
            return;
        }
        if (isPrinterReady) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            byte[] sendData = new byte[0];
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(URI.API_IMAGE_BILL).getContent());
                PrintPic pg = new PrintPic();
                pg.initCanvas(400);
                pg.initPaint();
                pg.drawImage(0, 0, bitmap);
                sendData = pg.printDraw();
            } catch (IOException e) {
                // Log exception
            }

            mService.write(sendData);
            mService.write(PrinterCommands.SELECT_FONT_A);
            mService.write(PrinterCommands.ESC_ALIGN_CENTER);
            mService.sendMessage(header, "");
            mService.write(PrinterCommands.ESC_ALIGN_LEFT);
            mService.sendMessage(body, "");
            mService.write(PrinterCommands.FEED_LINE);
            mService.write(PrinterCommands.FEED_LINE);
            openCashDrawer();
        } else {
            if (mService.isBTopen()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_connect_with_printer), Toast.LENGTH_LONG).show();
            } else {
                requestBluetooth();
            }
        }
    }

    public void dailyReport(String header, String body) {
        if (!mService.isAvailable()) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth");
            return;
        }
        if (isPrinterReady) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            byte[] sendData = new byte[0];
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(URI.API_IMAGE_BILL).getContent());
                PrintPic pg = new PrintPic();
                pg.initCanvas(400);
                pg.initPaint();
                pg.drawImage(0, 0, bitmap);
                sendData = pg.printDraw();
            } catch (IOException e) {
                // Log exception
            }

            mService.write(sendData);
            mService.write(PrinterCommands.SELECT_FONT_A);
            mService.write(PrinterCommands.ESC_ALIGN_CENTER);
            mService.sendMessage(header, "");
            mService.write(PrinterCommands.ESC_ALIGN_LEFT);
            mService.sendMessage(body, "");
            mService.write(PrinterCommands.FEED_LINE);
            mService.write(PrinterCommands.FEED_LINE);
        } else {
            if (mService.isBTopen()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_connect_with_printer), Toast.LENGTH_LONG).show();
            } else {
                requestBluetooth();
            }
        }
    }

    public void testPrint(String body) {
        if (!mService.isAvailable()) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth");
            return;
        }
        if (isPrinterReady) {
            mService.write(PrinterCommands.SELECT_FONT_A);
            mService.write(PrinterCommands.ESC_ALIGN_CENTER);
            mService.write(PrinterCommands.ESC_ALIGN_LEFT);
            mService.sendMessage(body, "");
            mService.write(PrinterCommands.FEED_LINE);
            mService.write(PrinterCommands.FEED_LINE);
        } else {
            if (mService.isBTopen()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_connect_with_printer), Toast.LENGTH_LONG).show();
            } else {
                requestBluetooth();
            }
        }
    }

    public boolean openCashDrawer() {
        try {
            byte[] bytes = intArrayToByteArray(new int[]{27, 112, 0, 50, 250});
            mService.write(bytes);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Open drawer error", e);
            return false;
        }
    }

    private byte[] intArrayToByteArray(int[] Iarr) {
        byte[] bytes = new byte[Iarr.length];
        for (int i = 0; i < Iarr.length; i++) {
            bytes[i] = (byte) (Iarr[i] & 0xFF);
        }
        return bytes;
    }

    public void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        Log.e("DEBUG", "Terhubung dengan perangkat");
    }

    @Override
    public void onDeviceConnecting() {
        Log.e("DEBUG", "Sedang menghubungkan...");
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        Log.e("DEBUG", "Koneksi perangkat terputus");
        setupBluetoothConnection(sessionManager.getPrinter());
    }

    @Override
    public void onDeviceUnableToConnect() {
        Log.e("DEBUG", "Tidak dapat terhubung ke perangkat");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {

    }
}