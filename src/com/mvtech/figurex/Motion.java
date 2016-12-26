package com.mvtech.figurex;

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

		
}
