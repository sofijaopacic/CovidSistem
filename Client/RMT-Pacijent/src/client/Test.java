package client;

import java.util.GregorianCalendar;

public class Test 
{
	String		username;
	String 		odgovori[];
	int			brziTest;
	int			pcrTest;
	
	private GregorianCalendar 	datum;
	
	public Test(String usernameText)
	{
		odgovori = new String("       ").split("");
		datum = new GregorianCalendar();
		
		brziTest = 0;
		pcrTest = 0;
		
		username = usernameText;
	}

	public String toString()
	{
		return 	username + ";" + 
				String.join("", odgovori) + ";" + 
				String.valueOf(brziTest) + ";" + 
				String.valueOf(pcrTest) + ";" + 
				getDatum();
	}

	public void fromString(String podaci)
	{
		String niz[];
		
		niz = podaci.split(";");

		username = niz[0];
		odgovori = niz[1].split("");
		brziTest = Integer.parseInt(niz[2]);
		pcrTest = Integer.parseInt(niz[3]);
		setDatum(niz[4]);		
	}
		
	public void setSamotestiranje(int index, boolean odgovor)
	{
		odgovori[index] = odgovor ? "D" : "N";
	}


	public int getBrojPozitivnihOdgovora()
	{
		int		rez = 0;
		
// prvi je blanko, znaci nije bilo samotestiranja, vrati -1
		if(odgovori[0].equals(" "))
			return -1;
		
		for(int i=0; i<odgovori.length; i++)
		{
			if(odgovori[i].equals("D"))
				rez++;
		}
		return rez;
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
	
	public String getDatum()
	{	
		return Helper.StringFromDate(datum);
	}

	public void setDatum(String datumText)
	{	
		datum = Helper.DateFromString(datumText);
	}
}
