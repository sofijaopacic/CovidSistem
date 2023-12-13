package client;
import java.util.GregorianCalendar;

import client.Korisnik;

public class Korisnik {

	String 				username;
	String 				lozinka;
	String 				ime;
	String 				prezime;
	String 				pol;
	String 				email;
	boolean 			administrator;
	
	private GregorianCalendar 	datumPoslednjePrijave;

	public Korisnik()
	{
		datumPoslednjePrijave = new GregorianCalendar();
		administrator = false;
	}	
	
	public String getFullName()
	{
		return ime + " " + prezime;
	}
	
	public String toString() 
	{
		return username + ";" + 
			   lozinka + ";" + 
			   ime + ";" + 
			   prezime + ";" + 
			   pol + ";" + 
			   email + ";" + 
			   getDatumPoslednjePrijave() + ";" + 
			   administrator;
	}

	public void fromString(String podaci) 
	{
		String niz[];
		
		niz = podaci.split(";");

		username = niz[0];
		lozinka = niz[1];
		ime = niz[2];
		prezime = niz[3];
		pol = niz[4];
		email = niz[5];		
		setDatumPoslednjePrijave(niz[6]);		
		administrator = niz[7].equalsIgnoreCase("true") ? true : false;		
	}

	public String getDatumPoslednjePrijave()
	{	
		return Helper.StringFromDate(datumPoslednjePrijave);
	}

	public void setDatumPoslednjePrijave(String datum)
	{	
		datumPoslednjePrijave = Helper.DateFromString(datum);
	}
}
