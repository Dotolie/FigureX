package com.mvtech.figurex;

import java.text.DateFormat;
import java.util.Date;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class Controller {
	private final String TAG="Controller";
    
	private final long SCAN_PERIOD = 10000;
    private int mRssi = -100;
    public static final int UART_PROFILE_READY = 10;
    public static final int UART_PROFILE_CONNECTED = 20;
    public static final int UART_PROFILE_DISCONNECTED = 21;

    
    public static int mState = UART_PROFILE_DISCONNECTED;
	
    private boolean mScanning = false;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;	

	public Context mContext;
	Handler mHandler = null;
	
	public boolean init(Context context) {
		mContext = context;
		service_init();
		
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
            Log.e(TAG, "no adaptor");
            return false;
        }

        mHandler = new Handler();
        
        Thread th = new Thread(new Runnable() {
        	@Override
        	public void run() {
        		while(true) {

        			if( mService != null ) {
        				scanLeDevice(true);
        				break;
        			}

        			try {
        				Thread.sleep(1000);
        			}
        			catch(InterruptedException e ) {
        				e.printStackTrace();
        			}
        		}
        		Log.e(TAG, "loop out for th");
        	}
        });
        th.start();
        
        return true;
	}
	
	public void close() {
		try {
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(UARTStatusChangeReceiver);
		} catch (Exception ignore) {
			Log.e(TAG, ignore.toString());
		}
		mContext.unbindService(mServiceConnection);
		mState = UART_PROFILE_DISCONNECTED;
		
		Log.e(TAG,  " close ");
	}
	
	public void service_init() {
        Intent bindIntent = new Intent(mContext, UartService.class);
        mContext.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
  
        LocalBroadcastManager.getInstance(mContext).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }	
    

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			String name = device.getName();
			if ( name != null && device.getName().equals("Nordic_UART")) {
				scanLeDevice(false);
				String address = device.getAddress();

				mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
				mService.connect(address);
				mRssi = rssi;
				Log.e(TAG,"mDevice=" + mDevice + ", mac=" + device.getAddress() + " mser=" + mService + ", rssi=" + rssi);

			}            	   
       }
    };    
    
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
    
    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
        		mService = ((UartService.LocalBinder) rawBinder).getService();
        		Log.d(TAG, "onServiceConnected mService= " + mService);
        		if (!mService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                }

        }

        public void onServiceDisconnected(ComponentName classname) {
       ////     mService.disconnect(mDevice);
        		mService = null;
        }
    };
    
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
           //*********************//
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				((MainActivity)mContext).runOnUiThread( new Runnable() {
					@Override
					public void run() {
						String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
						((MainActivity)mContext).mTvDeviceName.setText(mDevice.getName());
						((MainActivity)mContext).mTvMac.setText("["+mDevice.getAddress()+"]");
						((MainActivity)mContext).mTvRssi.setText(String.valueOf(mRssi));

						Log.d(TAG, "UART_CONNECT_MSG");
					}
				});
				
				mState = UART_PROFILE_CONNECTED;
			}
           
          //*********************//
			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				((MainActivity)mContext).runOnUiThread( new Runnable() {
					@Override
					public void run() {
						String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
						((MainActivity)mContext).mTvDeviceName.setText("None");
						((MainActivity)mContext).mTvMac.setText("[--:--:--:--:--:--]");
						((MainActivity)mContext).mTvRssi.setText("-90");	
						
						Log.d(TAG, "UART_DISCONNECT_MSG");
						mService.close();
					}
				});
				
				mState = UART_PROFILE_DISCONNECTED;
			}            
          
          //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
             	 mService.enableTXNotification();
            }
          //*********************//
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);

				try {
					String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
					String msg = new String(txValue, 0, txValue.length);
					Log.i(TAG, " RX(" + txValue.length + ")=" + msg);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
	           //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
				Log.e(TAG, " uart disconnect");
            	mService.disconnect();
            }
        }

		private void runOnUiThread(Runnable runnable) {
			// TODO Auto-generated method stub
			
		}
    };    
    
    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    String byteArrayToDec(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%d ", b));
        return sb.toString();
    }
    
    public void runMotion( final byte[] datas ) {

    	if( mState == UART_PROFILE_CONNECTED ) {
	    	mService.writeRXCharacteristic(datas);
	    	Log.i(TAG, "execute motin="+byteArrayToDec(datas) );
    	}
    	else {
    		Log.e(TAG,  "doesn't exceut run msg, connection lose !");
    	}
    }
    
    public void sendConfig(final byte datas[]) {
    	int nRest = datas.length;
    	int nLen;
    	boolean bRet;
    	
    	if( mState == UART_PROFILE_CONNECTED ) {
	    	while( nRest > 0 ) {
	    		if( (nRest-20) > 0 ) {
	    			nLen = 20;    			
	    		}
	    		else {
	    			nLen = nRest;
	    		}
	    		byte[] packet = new byte[nLen];
	    		System.arraycopy(datas, datas.length - nRest, packet, 0, nLen);
	    		mService.writeRXCharacteristic(packet);
	    		nRest = nRest - nLen;
	    	}

			Log.i(TAG, "config motin=" + byteArrayToDec(datas));
    	}
    	else {
    		Log.e(TAG,  "doesn't send config, connection lose !");
    	}
    }
}
