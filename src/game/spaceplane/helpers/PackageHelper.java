package game.spaceplane.helpers;

import java.util.List;

public class PackageHelper {
	public static byte[] MakeByteArray(List<Byte> byteList){
		byte [] result = new byte[byteList.size()];
		
		for(int i = 0; i < byteList.size(); i++){
			result[i] = byteList.get(i);
		}
		
		return result;
	}
}
