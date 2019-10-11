package schedule;

import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IceSpreadsheetTest {

	IceSpreadsheet iss = null;
	SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");

	@Before
	public void setUp() throws Exception {
		iss = IceSpreadsheet.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetShareTeam() {
		try {
//			String actual = iss.getShareTeam(formater.parse("2018/10/20"), "15:00", "St Laurent", "U14B Moodie");
//			Assert.assertEquals("U12 Reg Kingstone", actual);
//			actual = iss.getShareTeam(formater.parse("2018/10/20"), "08:00", "Grandmaitre", "U12 Prov Blue Stass");
//			Assert.assertEquals("U12 Reg Peltzer", actual);
//			actual = iss.getShareTeam(formater.parse("2018/10/21"), "19:30", "Walkley", "U12 Prov Red Purves");
//			Assert.assertEquals("U16C Hart", actual);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testListTeams()
	{
		iss.dump();
	}

}
