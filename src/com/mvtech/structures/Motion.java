package com.mvtech.structures;

import java.io.Serializable;


public class Motion implements Serializable {
	public final static byte REQ_GREETING 	= 0x00;
	public final static byte RSP_GREETING 	= 0x01;
	public final static byte REQ_RESET 		= 0x02;
	public final static byte RSP_RESET 		= 0x03;
	public final static byte REQ_VERSION 	= 0x04;
	public final static byte RSP_VERSION	= 0x05;
	public final static byte REQ_MOTION		= 0x06;
	public final static byte RSP_MOTION		= 0x07;
	public final static byte REQ_SET_CONFIG	= 0x08;
	public final static byte RSP_SET_CONFIG	= 0x09;
	public final static byte REQ_GET_CONFIG	= 0x0A;
	public final static byte RSP_GET_CONFIG	= 0x0B;
	
	public int no;
	public String title;

	public Sensor Sensor;
	public Motor[] Motors;
	public Sound Sound;
	public Led Led;
	public boolean checked = false;
	

	public Motion() { }
	
	public Motion(int no, String title, Sensor sensor, Motor[] motors, Sound sound, Led led   ){
		this.no = no;
		this.title = title;
		
		this.Sensor = sensor;
		this.Motors = motors;
		this.Sound = sound;
		this.Led = led;
	}

	public byte[] getConfig() {
    	int index = 0;
    	byte[] datas = new byte[32];
    	
    	datas[index++] = REQ_SET_CONFIG;
    	datas[index++] = (byte) this.no;
    	datas[index++] = this.Sensor.type;
    	datas[index++] = this.Sensor.value;
    	for(int i=0;i<4;i++) {
    		datas[index++] = this.Motors[i].angle_init;
    		datas[index++] = this.Motors[i].angle_start;
    		datas[index++] = this.Motors[i].angle_stop;
    		datas[index++] = this.Motors[i].delay_time;
    		datas[index++] = this.Motors[i].hold_time;
    		datas[index++] = this.Motors[i].repeat_count;  		
    	}
    	datas[index++] = this.Sound.index;
    	datas[index++] = this.Sound.delay_time;
    	datas[index++] = this.Led.blink;
    	datas[index++] = this.Led.delay_time;
    	
    	return datas;		
	}
}
