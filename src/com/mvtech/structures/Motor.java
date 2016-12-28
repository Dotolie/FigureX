package com.mvtech.structures;

import java.io.Serializable;

public class Motor implements Serializable {
	public byte angle_init;
	public byte angle_start;
	public byte angle_stop;
	public byte delay_time;
	public byte hold_time;
	public byte repeat_count;
	
	public Motor() {}
	
	public Motor( int init, int start, int stop, int delay, int hold, int repeat) {
		this.angle_init = (byte)init;
		this.angle_start = (byte)start;
		this.angle_stop = (byte)stop;
		this.delay_time = (byte)delay;
		this.hold_time = (byte)hold;
		this.repeat_count = (byte)repeat;
	}
}
