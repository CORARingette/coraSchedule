/**
 * 
 */
package ice;

/**
 * @author andrewmcgregor
 *
 */
public class IceDataGlobal {

	public enum IceDataType {
		CLASSIC, SWERK
	}

	public static IceData instance_sg;

	public static IceData getInstance() {
		return instance_sg;
	}

	public static void makeIceData(IceDataType dataType) {
		if (dataType == IceDataType.CLASSIC) {
			instance_sg = new IceDataClassic();
		} else {
			instance_sg = new IceDataSwerk();
		}
	}
}
