package com.mvtech.figurex;

import java.io.Serializable;

public class Sensor implements Serializable {
	public byte type;
	public byte value;
	
	public Sensor() {}
	
	public Sensor( int type, int value ) {
		this.type = (byte)type;
		this.value = (byte)value;
	}
}
