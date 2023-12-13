package server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
	String 				username;
	GregorianCalendar 	datum;
	String 				odgovori[];
	int 				brziTest;
	int 				pcrTest;
	
	public Test()
	{
		odgovori = new String("       ").split("");
		datum = new GregorianCalendar();
		
		brziTest = 0;
		pcrTest = 0;
	}

	public boolean isPositive()
	{
		return (brziTest == 1) || (pcrTest == 1);
	}
	
	public boolean isMonitored()
	{
		int		odgovorDa = 0;
		String	s = String.join("", odgovori);
		
		for(int i=0; i<s.length(); i++)
		{
			if(s.charAt(i) == 'D')
				odgovorDa++;
		}
		return (odgovorDa < 2) && (pcrTest + brziTest == 0);
	}

	public boolean isSelfTested()
	{
		return odgovori[0]!=" ";
	}
	
	
	
	public String toString()
	{
		return 	username + ";" + 
				String.join("", odgovori) + ";" + 
				String.valueOf(brziTest) + ";" + 
				String.valueOf(pcrTest) + ";" +		
		   		getDatum(); 
	}

	public String serialize()
	{
		return 	toString() + "\n"; 
	}
	
	public void serialize(String podaci)
	{
		String niz[];
		
		niz = podaci.split(";");

		username = niz[0];
		odgovori = niz[1].split("");
		brziTest = Integer.parseInt(niz[2]);
		pcrTest = Integer.parseInt(niz[3]);
		setDatum(niz[4]);		
	}
	
	public String getDatum()
	{	
		return Helper.StringFromDate(datum);
	}
	
	public void setDatum(String datumText)
	{	
		datum = Helper.DateFromString(datumText);
	}
}
