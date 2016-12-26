package com.mvtech.figurex;

import java.io.Serializable;

public class Sound implements Serializable {
	public byte index;
	public byte delay_time;
	
	public Sound() {}
	
	public Sound(int index, int delay) {
		this.index = (byte)index;
		this.delay_time = (byte)delay;
	}
}
