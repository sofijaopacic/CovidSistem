package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.KorisnikServer;

public class Korisnici {
	
	private List<KorisnikServer> korisnici = new ArrayList<>();
	
	public Korisnici() {
		load();
	}
	

	public KorisnikServer findUser(String username) 
	{
		KorisnikServer korisnik;
		
		for(int i=0; i<korisnici.size(); i++) 
		{
			korisnik = korisnici.get(i);
			
			if (korisnik.username.equals(username)) {
				return korisnik;
			}
		}
		
		return null;
	}

	public boolean checkUsername(String username) 
	{
		KorisnikServer korisnik;
		
		for(int i=0; i<korisnici.size(); i++) 
		{
			korisnik = korisnici.get(i);
			
			if (korisnik.username.equals(username)) {
				return true;
			}
		}
		
		return false;
	}

	public KorisnikServer login(String username, String lozinka) 
	{
		KorisnikServer korisnik;
		
		for(int i=0; i<korisnici.size(); i++) 
		{
			korisnik = korisnici.get(i);
			
			if (korisnik.username.equals(username) && korisnik.lozinka.equals(lozinka)) 
			{
				LoginLogs	logs = new LoginLogs(username);
				
				logs.getLastLogin(korisnik);
				
				logs.add(korisnik);
				
				return korisnik;
			}
		}
		
		return null; 
	}
	
	
	
	public boolean save(KorisnikServer korisnik) {
		
		try {
			FileWriter fp = new FileWriter("korisnici.txt", true);
	
			fp.append(korisnik.serialize());

			fp.close();
			
			korisnici.add(korisnik);
			
			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
		
		
	}
	
	public void load() 
	{	
		String 		podatak;
		KorisnikServer 	korisnik;
		
		try {
			BufferedReader fr = new BufferedReader(new FileReader("korisnici.txt"));
			while(true) 
			{
				podatak = fr.readLine();

				if(podatak==null || podatak.equals("")) break;
				
				korisnik = new KorisnikServer();
				
				korisnik.serialize(podatak);
				
				korisnici.add(korisnik);
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public List<KorisnikServer> getList()
	{
		return korisnici;
	}
	
}
