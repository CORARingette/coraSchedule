package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateTimeUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeFullDateFromDateAndTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd:HHmmss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, 4);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		Date testDate = calendar.getTime();
		String actual = formatter.format(DateTimeUtils.makeFullDateFromDateAndTime(testDate, "9:30"));
		String expected = "20170512:093000";
		Assert.assertEquals(expected, actual);
		actual = formatter.format(DateTimeUtils.makeFullDateFromDateAndTime(testDate, "12:30"));
		expected = "20170512:123000";
		Assert.assertEquals(expected, actual);
		actual = formatter.format(DateTimeUtils.makeFullDateFromDateAndTime(testDate, "23:30"));
		expected = "20170512:233000";
		Assert.assertEquals(expected, actual);

	}

}
