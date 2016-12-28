package com.mvtech.structures;

import java.io.Serializable;


public class Motion implements Serializable {
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

	public byte[] getBytes() {
    	int index = 0;
    	byte[] datas = new byte[31];
    	
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
