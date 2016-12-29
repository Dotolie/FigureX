package com.mvtech.figurex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.mvtech.structures.Led;
import com.mvtech.structures.Motion;
import com.mvtech.structures.Motor;
import com.mvtech.structures.Sensor;
import com.mvtech.structures.Sound;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG = "FigureX";

    private static final int REQUEST_ENABLE_BT 		= 2;
    private static final int REQUEST_CODE2CONFIG 	= 3;
	
	
	private CustomArrayAdapter mCustomArrayAdaptor = null;
	private ArrayList<Motion> mMotionList = null;
	private Button mBtnAdd = null;
	private Button mBtnDeletes = null;
	private Button mBtnSave = null;
	private TextView mTvActionItems = null;
	private ListView mListView = null;
	
	private int mCount = 0;
	private int mPosition = 0;
	

    public TextView mTvDeviceName = null;
    public TextView mTvMac = null;
    public TextView mTvRssi = null;

    public ProgressBar mPgScanning = null;
    
    private Controller mController = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        	Toast.makeText(this, "BLE is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		 
		
		setLayout();
        setViews();
        
        mController = new Controller();
        if( mController.init(this) == false ) {
        	finish();
        	return;
        }
        mMotionList = xmlParser();
        
//      BaseAdapter 연결
//      mListView.setAdapter(new CustomBaseAdapter(this, mCareList));
        
        // ArrayAdapter 연결
        mCustomArrayAdaptor = new CustomArrayAdapter(this, R.layout.list_row, mMotionList);
        mListView.setAdapter( mCustomArrayAdaptor );
        
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				
				runMotion(position);
				
			}
		});
        
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if( position >= 4 ) {
					mCustomArrayAdaptor.remove(mCustomArrayAdaptor.getItem(position));
					mCustomArrayAdaptor.notifyDataSetChanged();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCount = mMotionList.size();
							mTvActionItems.setText(""+mCount);	
						}
					});

					Toast.makeText(
							getApplicationContext(), 
							"Delete Motion = " + position,
							Toast.LENGTH_SHORT
							).show();					
				}
				return true;
			}
		});
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    private void setViews() {
    	mBtnAdd = (Button)findViewById(R.id.btn_add);
    	mBtnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNewMotion();
			}

		});
    	
    	
    	mBtnDeletes = (Button)findViewById(R.id.btn_deletes);
    	mBtnDeletes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCount = mMotionList.size();
				if( mCount>4) {
					for( int i=mCount-1;i>=4;i--) {
						if( mMotionList.get(i).checked) {
							mCustomArrayAdaptor.remove(mCustomArrayAdaptor.getItem(i));
							mListView.clearChoices();
						}
					}
					
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCount = mMotionList.size();
							mTvActionItems.setText(""+mCount);
						}
					});
					
					mCustomArrayAdaptor.notifyDataSetChanged();
				}
			}
		});
    	
    	
    	mBtnSave = (Button)findViewById(R.id.btn_save);
    	mBtnSave.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveConfig();
			}
		});
    	
    	mTvActionItems = (TextView)findViewById(R.id.tv_actionItems);
    	
    	mTvDeviceName = (TextView)findViewById(R.id.tv_devicename);
    	mTvMac = (TextView)findViewById(R.id.tv_mac);
    	mTvRssi = (TextView)findViewById(R.id.tv_rssi);
    	
    	mPgScanning = (ProgressBar)findViewById(R.id.pg_scanning);
    }
    
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch( requestCode ) {
		case REQUEST_CODE2CONFIG:
			if( resultCode == RESULT_OK) {
				Motion motion = (Motion)data.getSerializableExtra("MotionObject");
				Motion tmp = mMotionList.set(mPosition, motion);
				Log.d(TAG, "received intent OK");
			}
			else {
				Log.d(TAG,  "received intent CANCEL");
			}
			break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        default:
            Log.e(TAG, "wrong request code");
            break;			
		}
	}

    
    private ArrayList<Motion> xmlParser() {
    	ArrayList<Motion> arrayList = new ArrayList<Motion>();
    	
    	try {
    		String dirPath = getConfigPath();

        	InputStream is = new FileInputStream(dirPath + "motions.xml");
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    		XmlPullParser parser = factory.newPullParser();
    		parser.setInput(new InputStreamReader(is, "UTF-8"));
    		int eventType = parser.getEventType();
    		   
    		int nMotors = 0;
    		boolean bOnSound = false;
    		boolean bOnSensor = false;
    		boolean bOnMotor = false;
    		boolean bOnLed = false;
    		
    		Motion motion = null;
    		Sensor sensor = null;
    		Motor[] motors = null;
    		Motor motor = null;
    		Sound sound = null;
    		Led led = null;
    		
    		while(eventType != XmlPullParser.END_DOCUMENT ) {
    			switch( eventType ) {
    			case XmlPullParser.START_TAG:
    				String startTag = parser.getName();
    				
    				//--------------------------------------------------------
    				if( startTag.equals("motion")) {
    					Sensor _sensor = new Sensor(5,10);
    					Motor[] _motors = { 
    							new Motor(1,0,9,100,50,5),
    							new Motor(2,0,9,100,50,5),
    							new Motor(3,0,9,100,50,5),
    							new Motor(4,0,9,100,50,5)
    							};
    					Sound _sound = new Sound(3, 100);
    					Led _led = new Led(5, 20);
    					
    					motion = new Motion(
    							0,
    							"auto generation", 
    							_sensor,
    							_motors,
    							_sound,
    							_led
    							);
    					String no = parser.getAttributeValue(0);
    					motion.no = Integer.parseInt( no );
    				}
    				if( startTag.equals("name")) {
    					motion.title = parser.nextText();
    				}
    				//--------------------------------------------------------
    				if( startTag.equals("sensor")) {
    					sensor = new Sensor();
    					bOnSensor = true;
    				}
    				if( bOnSensor && startTag.equals("type")) {
   						sensor.type = Byte.parseByte( parser.nextText() );    						
    				}
    				//--------------------------------------------------------
    				if( startTag.equals("motor")) {
    					if( nMotors == 0) {
    						motors = new Motor[4];
    					}
    					motor = new Motor();
    					bOnMotor = true;
    				}
    				if( bOnMotor && startTag.equals("init")) {
    					motor.angle_init = Byte.parseByte( parser.nextText());
    				}
    				if( bOnMotor && startTag.equals("start")) {
    					motor.angle_start = Byte.parseByte( parser.nextText());
    				}
    				if( bOnMotor && startTag.equals("stop")) {
    					motor.angle_stop = Byte.parseByte( parser.nextText());
    				}
    				if( bOnMotor && startTag.equals("delay")) {
    					motor.delay_time = Byte.parseByte( parser.nextText());
    				}
    				if( bOnMotor && startTag.equals("hold")) {
    					motor.hold_time = Byte.parseByte( parser.nextText());
    				}    				
    				if( bOnMotor && startTag.equals("repeat")) {
    					motor.repeat_count = Byte.parseByte( parser.nextText());
    				}    				
    				//--------------------------------------------------------
    				if( startTag.equals("sound")) {
    					sound = new Sound();
    					bOnSound = true;
    				}
    				if( bOnSound && startTag.equals("index")) {
    					sound.index = Byte.parseByte( parser.nextText());
    				}
    				if( bOnSound && startTag.equals("delay")) {
    					sound.delay_time = Byte.parseByte( parser.nextText());
    				}
    				//--------------------------------------------------------
    				if( startTag.equals("led")) {
    					led = new Led();
    					bOnLed = true;
    				}
    				if( bOnLed && startTag.equals("blink")) {
    					led.blink = Byte.parseByte( parser.nextText());
    				}
    				if( bOnLed && startTag.equals("delay")) {
    					led.delay_time = Byte.parseByte( parser.nextText());
    				}
    				break;
    				
    			case XmlPullParser.END_TAG:
    				String endTag = parser.getName();
    				
    				//--------------------------------------------------------
    				if( endTag.equals("motion")) {
    					motion.Motors = motors;
    					arrayList.add(motion);
    					nMotors = 0;
    				}
    				//--------------------------------------------------------
    				if( endTag.equals("sensor")) {
    					motion.Sensor = sensor;
    					bOnSensor = false;
    				}    				
    				//--------------------------------------------------------
    				if( endTag.equals("motor")) {
    					motors[nMotors++] = motor;
    					bOnMotor = false;
    				}    				
    				//--------------------------------------------------------
    				if( endTag.equals("sound")) {
    					motion.Sound = sound;
    					bOnSound = false;
    				}    				
    				//--------------------------------------------------------
    				if( endTag.equals("led")) {
    					motion.Led = led;
    					bOnLed = false;
    				}    				
    				//--------------------------------------------------------
    				break;
    			}
    			eventType = parser.next();    			
    		}
    	}
    	catch( XmlPullParserException e ) {
    		e.printStackTrace();
    	}
    	catch( UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    	catch( IOException e) {
    		e.printStackTrace();
    	}
    	
    	return arrayList;
    }

    private String getConfigPath() {
    	AssetManager assetManager = getAssets();
    	InputStream in = null;
    	OutputStream out = null;
    	
		String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/XML/";
		File file = new File(dirPath);
		
		if(!file.exists())
			file.mkdirs();
		
		File xmlFile = new File(dirPath + "motions.xml");
		if(!xmlFile.exists()) {
			try {
				in = assetManager.open("motions_default.xml");
				out = new FileOutputStream(dirPath + "motions.xml");
				byte[] buffer = new byte[1024];
				int read;
				while( (read = in.read(buffer)) != -1 ) {
					out.write(buffer,0,read);
				}
				in.close();
				out.flush();
				out.close();
				in=null;
				out= null;
			}
			catch( Exception e) {
				e.printStackTrace();
			}
		}

		return dirPath;
    }
    
    private void addNewMotion() {
		Sensor sensor = new Sensor(0,0);
		Motor[] motors = { 
				new Motor(0,-5,5,100,50,5),
				new Motor(0,-4,4,100,40,4),
				new Motor(0,-3,3,100,30,3),
				new Motor(0,-2,2,100,20,2)
				};
		Sound sound = new Sound(1, 100);
		Led led = new Led(1, 20);

		mCount = mMotionList.size();
		Motion motion = new Motion(
				mCount,
				"User Motion="+mCount, 
				sensor,
				motors,
				sound,
				led
				);
		
		mMotionList.add( motion );
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCount = mMotionList.size();
				mTvActionItems.setText(""+mCount);	
			}
		});
		
		mCustomArrayAdaptor.notifyDataSetChanged();
    }
    
    public void editMotion(int position) {
		mPosition = position;				
		Motion motionObject = mMotionList.get(position);
		
		Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
		intent.putExtra("MotionObject", motionObject);
		
		startActivityForResult(intent, REQUEST_CODE2CONFIG);
    }
    
    public void runMotion(int position) {
		byte[] datas;
		Motion motion = mMotionList.get(position);
		datas = new byte[1];
		datas[0] = (byte) motion.no;
		mController.runMotion(datas);

		Toast.makeText(
				this, 
				"Motion(" + position+") Execute!",
				Toast.LENGTH_SHORT
				).show();

    }
    
    public void sendConfig(int position) {
		byte[] datas;
		Motion motion = mMotionList.get(position);
		datas = motion.getBytes();
		mController.sendConfig(datas);					

		Toast.makeText(
				this, 
				"Send Motion(" + position+") Config ",
				Toast.LENGTH_SHORT
				).show();

    }
    
    private void saveConfig() {
		String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/XML/";
		File file = new File(dirPath);
		
		if(!file.exists())
			file.mkdirs();
		
		File xmlFile = new File(dirPath + "motions.xml");
    	try {
    		xmlFile.createNewFile();
    	}
    	catch( IOException e) {
    		e.printStackTrace();
    	}
    	
    	FileOutputStream os = null;
    	try {
    		os = new FileOutputStream( xmlFile );
    	}
    	catch( FileNotFoundException e ) {
    		e.printStackTrace();
    	}
    	
    	XmlSerializer serializer = Xml.newSerializer();
    	try {
    		serializer.setOutput(os , "UTF-8");
    		serializer.startDocument(null, true);
    		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    		serializer.startTag(null, "motions");
    		
    		for(int i=0;i<mMotionList.size();i++ ) {
    			Motion motion = mMotionList.get(i);
    			serializer.startTag(null, "motion");
    			serializer.attribute(null, "id", String.valueOf(i));
    			
	    			serializer.startTag(null, "name");
	    			serializer.text(motion.title);
	    			serializer.endTag(null, "name");
	
	    			
	    			serializer.startTag(null, "sensor");
	    				serializer.startTag(null, "type");
	    				serializer.text(String.valueOf(motion.Sensor.type));
	    				serializer.endTag(null, "type");
	
	    				serializer.startTag(null, "value");
	    				serializer.text(String.valueOf(motion.Sensor.value));
	    				serializer.endTag(null, "value");
	    			serializer.endTag(null, "sensor");    
	    			
	    			int nMotors = motion.Motors.length;
	    			for(int j=0;j<nMotors;j++) {
		    			serializer.startTag(null, "motor");
		    			serializer.attribute(null, "id", String.valueOf(j));
		    				serializer.startTag(null, "init");
		    				serializer.text(String.valueOf(motion.Motors[j].angle_init));
		    				serializer.endTag(null, "init");
		
		    				serializer.startTag(null, "start");
		    				serializer.text(String.valueOf(motion.Motors[j].angle_start));
		    				serializer.endTag(null, "start");

		    				serializer.startTag(null, "stop");
		    				serializer.text(String.valueOf(motion.Motors[j].angle_stop));
		    				serializer.endTag(null, "stop");

		    				serializer.startTag(null, "delay");
		    				serializer.text(String.valueOf(motion.Motors[j].delay_time));
		    				serializer.endTag(null, "delay");

		    				serializer.startTag(null, "hold");
		    				serializer.text(String.valueOf(motion.Motors[j].hold_time));
		    				serializer.endTag(null, "hold");

		    				serializer.startTag(null, "repeat");
		    				serializer.text(String.valueOf(motion.Motors[j].repeat_count));
		    				serializer.endTag(null, "repeat");

	    				serializer.endTag(null, "motor");    	    				
	    			}

    			serializer.startTag(null, "sound");
    				serializer.startTag(null, "index");
    				serializer.text(String.valueOf(motion.Sound.index));
    				serializer.endTag(null, "index");

    				serializer.startTag(null, "delay");
    				serializer.text(String.valueOf(motion.Sound.delay_time));
    				serializer.endTag(null, "delay");
    			serializer.endTag(null, "sound");    
    			
    			serializer.startTag(null, "led");
					serializer.startTag(null, "blink");
					serializer.text(String.valueOf(motion.Led.blink));
					serializer.endTag(null, "blink");
	
					serializer.startTag(null, "delay");
					serializer.text(String.valueOf(motion.Led.delay_time));
					serializer.endTag(null, "delay");
				serializer.endTag(null, "led");    
	    			
    			
        		serializer.endTag(null, "motion");
    			
    		}
    		
    		serializer.endTag(null, "motions");
    		serializer.endDocument();
    		serializer.flush();
    		os.close();
    		
    		Toast.makeText(
    				this, 
    				"complete saving, motions.xml",
    				Toast.LENGTH_SHORT
    				).show();
    		
    	}
    	catch( Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    
    private void setLayout(){
    	mListView = (ListView) findViewById(R.id.lv_list);
    }	
    
    @Override
    public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");

		try {
			mController.close();
			mController = null;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
 
    }

    @Override
    public void onBackPressed() {
//        if (mController.mState == Controller.UART_PROFILE_CONNECTED) {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(startMain);
//            Toast.makeText(this, 
//            		"running in background.\nDisconnect to exit",
//            		Toast.LENGTH_SHORT).show();
//        }
//        else 
		{
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.popup_title)
					.setMessage(R.string.popup_message)
					.setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).setNegativeButton(R.string.popup_no, null).show();
		}
    }
        
}
