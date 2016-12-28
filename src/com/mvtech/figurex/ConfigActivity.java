package com.mvtech.figurex;

import com.mvtech.structures.Motion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfigActivity extends Activity {
	private final String TAG = "Config";
	
	private Motion mMotion = null;
	
	private ImageView mIvIcon = null;
	private TextView mTvTitle = null;
	private TextView mTvMotionNo = null;
	
	private Button mBtnCancel = null;
	private Button mBtnOk = null;
	
	private Spinner mSpSensorType = null;
	private EditText mEtSensorValue = null;
	
	private EditText mEtMotor1Init = null;
	private EditText mEtMotor1Start = null;
	private EditText mEtMotor1Stop = null;
	private EditText mEtMotor1Delay = null;
	private EditText mEtMotor1Hold = null;
	private EditText mEtMotor1Repeat = null;

	private EditText mEtMotor2Init = null;
	private EditText mEtMotor2Start = null;
	private EditText mEtMotor2Stop = null;
	private EditText mEtMotor2Delay = null;
	private EditText mEtMotor2Hold = null;
	private EditText mEtMotor2Repeat = null;

	private EditText mEtMotor3Init = null;
	private EditText mEtMotor3Start = null;
	private EditText mEtMotor3Stop = null;
	private EditText mEtMotor3Delay = null;
	private EditText mEtMotor3Hold = null;
	private EditText mEtMotor3Repeat = null;

	private EditText mEtMotor4Init = null;
	private EditText mEtMotor4Start = null;
	private EditText mEtMotor4Stop = null;
	private EditText mEtMotor4Delay = null;
	private EditText mEtMotor4Hold = null;
	private EditText mEtMotor4Repeat = null;

	private Spinner mSpSoundType = null;
	private EditText mEtSoundDelay = null;
	
	private EditText mEtLedBlink = null;
	private EditText mEtLedDelay = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.config);

        // 이전 액티비티로부터 넘어온 데이터를 꺼낸다.
		mMotion = (Motion)getIntent().getSerializableExtra("MotionObject");
		
		setupViews();
		setValues();
		setupButtons();
       
        
        // 백그라운드 레이아웃(액티비티의 루트 레이아웃)을 레퍼런스 한다.
//        LinearLayout llBackground = (LinearLayout)findViewById(R.id.main_back);
         
        // 이전 액티비티로부터 받아온 색상을 배경색으로 세팅한다.
//        llBackground.setBackgroundColor(color);
         
        // 다이얼로그를 생성해, 색상 스트링과 설명을 출력한다.
//        alert(title, no);

	}
    /**
     * 자체적으로 만든 알럿다이얼로그 생성 메소드
     * 
     * @param title 타이틀
     * @param message 메시지
     */
	private void setupViews() {
		
		mIvIcon = (ImageView)findViewById(R.id.iv_icon);
		mTvTitle = (TextView)findViewById(R.id.tv_title_1);
		mTvMotionNo = (TextView)findViewById(R.id.tv_motion_no);
		
		mSpSensorType = (Spinner)findViewById(R.id.sp_sensor_type);
		mEtSensorValue = (EditText)findViewById(R.id.et_sensor_value);
		
		mEtMotor1Init = (EditText)findViewById(R.id.et_motor1_init);
		mEtMotor1Start = (EditText)findViewById(R.id.et_motor1_start);
		mEtMotor1Stop = (EditText)findViewById(R.id.et_motor1_stop);
		mEtMotor1Delay = (EditText)findViewById(R.id.et_motor1_delay);
		mEtMotor1Hold = (EditText)findViewById(R.id.et_motor1_hold);
		mEtMotor1Repeat = (EditText)findViewById(R.id.et_motor1_repeat);

		mEtMotor2Init = (EditText)findViewById(R.id.et_motor2_init);
		mEtMotor2Start = (EditText)findViewById(R.id.et_motor2_start);
		mEtMotor2Stop = (EditText)findViewById(R.id.et_motor2_stop);
		mEtMotor2Delay = (EditText)findViewById(R.id.et_motor2_delay);
		mEtMotor2Hold = (EditText)findViewById(R.id.et_motor2_hold);
		mEtMotor2Repeat = (EditText)findViewById(R.id.et_motor2_repeat);

		mEtMotor3Init = (EditText)findViewById(R.id.et_motor3_init);
		mEtMotor3Start = (EditText)findViewById(R.id.et_motor3_start);
		mEtMotor3Stop = (EditText)findViewById(R.id.et_motor3_stop);
		mEtMotor3Delay = (EditText)findViewById(R.id.et_motor3_delay);
		mEtMotor3Hold = (EditText)findViewById(R.id.et_motor3_hold);
		mEtMotor3Repeat = (EditText)findViewById(R.id.et_motor3_repeat);

		mEtMotor4Init = (EditText)findViewById(R.id.et_motor4_init);
		mEtMotor4Start = (EditText)findViewById(R.id.et_motor4_start);
		mEtMotor4Stop = (EditText)findViewById(R.id.et_motor4_stop);
		mEtMotor4Delay = (EditText)findViewById(R.id.et_motor4_delay);
		mEtMotor4Hold = (EditText)findViewById(R.id.et_motor4_hold);
		mEtMotor4Repeat = (EditText)findViewById(R.id.et_motor4_repeat);
		
		mSpSoundType = (Spinner)findViewById(R.id.sp_sound_type);
		mEtSoundDelay = (EditText)findViewById(R.id.et_sound_delay);
		
		mEtLedBlink = (EditText)findViewById(R.id.et_led_blink);
		mEtLedDelay = (EditText)findViewById(R.id.et_led_delay);
	}
	
	private void setupButtons() {
		mBtnOk = (Button)findViewById(R.id.btnOk);
        mBtnOk.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getValues();
				Intent intent = new Intent();
				intent.putExtra("MotionObject", mMotion);

				setResult(RESULT_OK, intent);
				finish();
			}
		});

		mBtnCancel = (Button)findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
	}
	
	private void setValues() {
		
		try {
			switch( mMotion.no) {
			case 0:
				mIvIcon.setImageResource(R.drawable.connect);
				break;
			case 1:
				mIvIcon.setImageResource(R.drawable.disconnect);
				break;
			case 2:
				mIvIcon.setImageResource(R.drawable.battery_low);
				break;
			case 3:
				mIvIcon.setImageResource(R.drawable.vibration);
				break;
			default:
				mIvIcon.setImageResource(R.drawable.action);
				break;
			}
			mTvTitle.setText(mMotion.title);
			mTvMotionNo.setText(""+mMotion.no);
			mSpSensorType.setSelection(mMotion.Sensor.type);
			mEtSensorValue.setText(String.valueOf(mMotion.Sensor.value));
			

			mEtMotor1Init.setText(String.valueOf(mMotion.Motors[0].angle_init));
			mEtMotor1Start.setText(String.valueOf(mMotion.Motors[0].angle_start));
			mEtMotor1Stop.setText(String.valueOf(mMotion.Motors[0].angle_stop));
			mEtMotor1Delay.setText(String.valueOf(mMotion.Motors[0].delay_time));
			mEtMotor1Hold.setText(String.valueOf(mMotion.Motors[0].hold_time));
			mEtMotor1Repeat.setText(String.valueOf(mMotion.Motors[0].repeat_count));
	
			mEtMotor2Init.setText(String.valueOf(mMotion.Motors[1].angle_init));
			mEtMotor2Start.setText(String.valueOf(mMotion.Motors[1].angle_start));
			mEtMotor2Stop.setText(String.valueOf(mMotion.Motors[1].angle_stop));
			mEtMotor2Delay.setText(String.valueOf(mMotion.Motors[1].delay_time));
			mEtMotor2Hold.setText(String.valueOf(mMotion.Motors[1].hold_time));
			mEtMotor2Repeat.setText(String.valueOf(mMotion.Motors[1].repeat_count));
	
			mEtMotor3Init.setText(String.valueOf(mMotion.Motors[2].angle_init));
			mEtMotor3Start.setText(String.valueOf(mMotion.Motors[2].angle_start));
			mEtMotor3Stop.setText(String.valueOf(mMotion.Motors[2].angle_stop));
			mEtMotor3Delay.setText(String.valueOf(mMotion.Motors[2].delay_time));
			mEtMotor3Hold.setText(String.valueOf(mMotion.Motors[2].hold_time));
			mEtMotor3Repeat.setText(String.valueOf(mMotion.Motors[2].repeat_count));
	
			mEtMotor4Init.setText(String.valueOf(mMotion.Motors[3].angle_init));
			mEtMotor4Start.setText(String.valueOf(mMotion.Motors[3].angle_start));
			mEtMotor4Stop.setText(String.valueOf(mMotion.Motors[3].angle_stop));
			mEtMotor4Delay.setText(String.valueOf(mMotion.Motors[3].delay_time));
			mEtMotor4Hold.setText(String.valueOf(mMotion.Motors[3].hold_time));
			mEtMotor4Repeat.setText(String.valueOf(mMotion.Motors[3].repeat_count));
	
			mSpSoundType.setSelection(mMotion.Sound.index);
			mEtSoundDelay.setText(String.valueOf(mMotion.Sound.delay_time));
			
			mEtLedBlink.setText(String.valueOf(mMotion.Led.blink));
			mEtLedDelay.setText(String.valueOf(mMotion.Led.delay_time));		
			
		}
		catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getValues() {
		mMotion.Sensor.type = (byte) mSpSensorType.getSelectedItemPosition(); 
		mMotion.Sensor.value = Byte.parseByte(mEtSensorValue.getText().toString());
		
		mMotion.Motors[0].angle_init = Byte.parseByte( mEtMotor1Init.getText().toString());
		mMotion.Motors[0].angle_start = Byte.parseByte(mEtMotor1Start.getText().toString());
		mMotion.Motors[0].angle_stop = Byte.parseByte( mEtMotor1Stop.getText().toString());
		mMotion.Motors[0].delay_time = Byte.parseByte( mEtMotor1Delay.getText().toString());
		mMotion.Motors[0].hold_time = Byte.parseByte( mEtMotor1Hold.getText().toString());
		mMotion.Motors[0].repeat_count = Byte.parseByte( mEtMotor1Repeat.getText().toString());

		mMotion.Motors[1].angle_init = Byte.parseByte( mEtMotor2Init.getText().toString());
		mMotion.Motors[1].angle_start = Byte.parseByte(mEtMotor2Start.getText().toString());
		mMotion.Motors[1].angle_stop = Byte.parseByte( mEtMotor2Stop.getText().toString());
		mMotion.Motors[1].delay_time = Byte.parseByte( mEtMotor2Delay.getText().toString());
		mMotion.Motors[1].hold_time = Byte.parseByte( mEtMotor2Hold.getText().toString());
		mMotion.Motors[1].repeat_count = Byte.parseByte( mEtMotor2Repeat.getText().toString());

		mMotion.Motors[2].angle_init = Byte.parseByte( mEtMotor3Init.getText().toString());
		mMotion.Motors[2].angle_start = Byte.parseByte(mEtMotor3Start.getText().toString());
		mMotion.Motors[2].angle_stop = Byte.parseByte( mEtMotor3Stop.getText().toString());
		mMotion.Motors[2].delay_time = Byte.parseByte( mEtMotor3Delay.getText().toString());
		mMotion.Motors[2].hold_time = Byte.parseByte( mEtMotor3Hold.getText().toString());
		mMotion.Motors[2].repeat_count = Byte.parseByte( mEtMotor3Repeat.getText().toString());

		mMotion.Motors[3].angle_init = Byte.parseByte( mEtMotor4Init.getText().toString());
		mMotion.Motors[3].angle_start = Byte.parseByte(mEtMotor4Start.getText().toString());
		mMotion.Motors[3].angle_stop = Byte.parseByte( mEtMotor4Stop.getText().toString());
		mMotion.Motors[3].delay_time = Byte.parseByte( mEtMotor4Delay.getText().toString());
		mMotion.Motors[3].hold_time = Byte.parseByte( mEtMotor4Hold.getText().toString());
		mMotion.Motors[3].repeat_count = Byte.parseByte( mEtMotor4Repeat.getText().toString());

		mMotion.Sound.index = (byte) mSpSoundType.getSelectedItemPosition();
		mMotion.Sound.delay_time = Byte.parseByte( mEtSoundDelay.getText().toString());
		
		mMotion.Led.blink =  Byte.parseByte( mEtLedBlink.getText().toString());
		mMotion.Led.delay_time = Byte.parseByte( mEtLedDelay.getText().toString());	
	}
}
