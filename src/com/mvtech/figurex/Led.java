package com.mvtech.figurex;

import java.io.Serializable;

public class Led implements Serializable {
	public byte blink;
	public byte delay_time;
	
	public Led() {}
	
	public Led( int blink, int delay ) {
		this.blink = (byte)blink;
		this.delay_time = (byte)delay;
	}
}
