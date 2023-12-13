package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class LoginLogs 
{
	private				String username = null;
	ArrayList<LoginLog> podaci = new ArrayList<>();
	
	public LoginLogs(String	 name)
	{
		username = name;
		load();
	}
	
	public void getLastLogin(KorisnikServer korisnik)
	{
		LoginLog	log;
		
		// ako ima podataka
		if(podaci.size() > 0)
		{
			// uzmi podatke poslednjeg prijavljivanja
			log = podaci.get(podaci.size() - 1);
			
			korisnik.setDatumPoslednjePrijave(log.datum); 
		}
	}

	public void add(KorisnikServer korisnik)
	{
		try {
			
			LoginLog		log = new LoginLog();
			
			log.username = korisnik.username;
			log.datum = Helper.StringFromDate(new GregorianCalendar());

//			podaci.add(log);

			FileWriter fp = new FileWriter("Log.txt", true);
			
			fp.append(log.serialize());

			fp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// ucitava prijavljivanja samo odredjenog/prijavljenog korisnika
	public void load() 
	{	
		String 		podatak;
		LoginLog 	log;
		
		try {
			BufferedReader fr = new BufferedReader(new FileReader("log.txt"));
			while(true) 
			{
				podatak = fr.readLine();

				if(podatak==null || podatak.equals("")) break;
				
				log = new LoginLog();
				log.serialize(podatak);
				
				if(log.username.equals(username))
					podaci.add(log);
				else
					log = null;
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
