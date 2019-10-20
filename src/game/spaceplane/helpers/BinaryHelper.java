package game.spaceplane.helpers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryHelper {
	//Makes a 2-byte big endian array from a short
	public static byte[] makeBytesBigEndian(short s){
		ByteBuffer bb = ByteBuffer.allocate(2);
		
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putShort(s);
		
		return bb.array();
	}
	
	//Makes a short from a 2-byte big endian array
	public static short makeShortBigEndian(byte[] b){
		ByteBuffer bb = ByteBuffer.wrap(b);
		
		bb.order(ByteOrder.BIG_ENDIAN);
		
		return bb.getShort();
	}
	
	public static byte[] makeBytesLittleEndian(short s){
		ByteBuffer bb = ByteBuffer.allocate(2);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort(s);
		
		return bb.array();
	}
	
	public static short makeShortLittleEndian(byte[] b){
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		return bb.getShort();
	}
	
//	public static byte[] makeBytesLittleEndian(float f){
//		ByteBuffer bb = ByteBuffer.allocate(4);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
//		bb.putFloat(f);
//		
//		return bb.array();
//	}
//	
//	public static float makeFloatLittleEndian(byte[] b){
//		ByteBuffer bb = ByteBuffer.wrap(b);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
//		
//		return bb.getFloat();
//	}
	
	public static byte[] makeBytesLittleEndian(double d){
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putDouble(d);
		
		return bb.array();
	}
	
	public static double makeDoubleLittleEndian(byte[] b){
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		return bb.getDouble();
	}
}
