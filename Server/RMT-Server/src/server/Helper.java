package server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Helper {
	
	public static String StringFromDate(GregorianCalendar datum)
	{
		DateFormat formatDatuma = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		
		return formatDatuma.format(datum.getTime());
	}

	public static GregorianCalendar DateFromString(String datumText)
	{
		GregorianCalendar	 datum = null;
		
		try {
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			
			datum = new GregorianCalendar();

			datum.setTime(df.parse(datumText));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		return datum;
	}
}
