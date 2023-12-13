package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import server.KorisnikServer;
import server.Korisnici;

public class ServerKlijent extends Thread {
	
	final int PCR_SECONDS_DELAY = 10;		// na svakih deset sekundi se menja status PCR testa
	final int MONITOR_MINUTES_DELAY = 1;	// posle 1 minuta, nadzirani moraju da urade test

	Socket 			soketZaKom = null;
	BufferedReader 	ulazni = null;
	PrintStream 	izlazni = null;
	
	Korisnici 		korisnici = new Korisnici();
	KorisnikServer	prijavljeniKorisnik = null;
	Tests 			tests = new Tests();
	
	Test			pcr = null;
	
	public class PCRTask extends TimerTask {
	    @Override
	    public void run() 
	    {
	    	if(pcr == null) 
	    	{
	    		pcr = new Test();
	    		pcr.username = prijavljeniKorisnik.username;
	    	}
	    	else
	    		if(++pcr.pcrTest == 3) // 
	    		{
	    			
	    			pcr.pcrTest = (int)((Math.random() * 10) + 1) %2 + 1;
	    			
	    			tests.save(pcr);
	    			
	    			pcr = null; 
	    			
	    			cancel();
	    		}
	    }
	}
	
	public ServerKlijent(Socket soket) {
		this.soketZaKom = soket;
	}
	
	public void run() 
	{
		String	command;
		
		try {
			ulazni = new BufferedReader(new InputStreamReader(soketZaKom.getInputStream()));
			izlazni = new PrintStream(soketZaKom.getOutputStream());
			
			do
			{
				
				command = readLine();

				switch(command)
				{
					case "loginUser"		:	loginUser();
												break;
											
					case "saveUser"			:	saveUser();
												break;
												
					case "checkUsername"	:	checkUsername();
												break;

					case "saveTest"			:	saveTest();
												break;
												
					case "getTests"			:	getTests();
												break;

					case "listUsers"		:	listUsers();
												break;

					case "listPositive"		:	listPositive();
												break;

					case "listNegative"		:	listNegative();
												break;

					case "sendPCR"			:	sendPCR();
												break;

					case "getSpeedyTest"	:	getSpeedyTest();
												break;

					case "getMonitored"		:	getMonitored();
												break;

					case "testAllowed"		:	testAllowed();
												break;
												
					case "testNeeded"		:	testNeeded();
												break;

					case "getStatistics"	:	getStatistics();
												break;
				}
			}
			while(!command.equalsIgnoreCase("/exit"));
			
			soketZaKom.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void getStatistics()
	{
		Test		test;
		int			brojTestiranja, brojPozitivnih, brojNegativnih, brojPracenja; 
		
		brojTestiranja = brojPozitivnih = brojNegativnih = brojPracenja = 0;

		for(int i=0; i<tests.getList().size(); i++)
		{
			test = tests.getList().get(i);			
				
			brojTestiranja++;
			if(test.isMonitored())
				brojPracenja++;
			else
			{
				if(test.isPositive())
					brojPozitivnih++;
				else
					brojNegativnih++;
			}
		}
		sendLine(true);
		
		sendLine(String.valueOf(brojTestiranja));
		sendLine(String.valueOf(brojPozitivnih));
		sendLine(String.valueOf(brojNegativnih));
		sendLine(String.valueOf(brojPracenja));
		
		endMessage();
	}

	private void getSpeedyTest()
	{
		Test					test;
		GregorianCalendar		today = new GregorianCalendar();
		
		for(int i=tests.getList().size()-1; i>=0; i--)
		{
			test = tests.getList().get(i);			
				
			if(test.username.equals(prijavljeniKorisnik.username) && (test.brziTest > 0))
			{
				if(isSameDate(test.datum, today))
				{
					sendLine(false);
					endMessage();
					return;
				}
			}
		}
		sendLine(true); 
		sendLine(String.valueOf(((int)(Math.random() * 10) + 1) %2 + 1));
		endMessage();
	}

	
	private void testNeeded()
	{
		sendLine(monitoringExceeded(prijavljeniKorisnik.username));
		endMessage();
	}
	
	
	private boolean monitoringExceeded(String username)
	{
		boolean		testNeeded = false;
		Test		test;
		
		test = tests.getLastTest(username);
		
		if((test != null) && (test.isMonitored()))
		{
			GregorianCalendar		today = new GregorianCalendar();
			
			long diff = today.getTimeInMillis() - test.datum.getTimeInMillis();

			testNeeded = TimeUnit.MILLISECONDS.toMinutes(diff) > MONITOR_MINUTES_DELAY;
		}
		
		return testNeeded;
	}
	
	
	private void testAllowed()
	{
		Test					test;
		GregorianCalendar		today = new GregorianCalendar();
		
		for(int i=tests.getList().size()-1; i>=0; i--)
		{
			test = tests.getList().get(i);			
				
			if(test.username.equals(prijavljeniKorisnik.username) && !test.isSelfTested())
			{
				if(isSameDate(test.datum, today))
				{
					sendLine(false);
					endMessage();
					return;
				}
			}
		}

		sendLine(true);
		endMessage();
	}
	
	
	private void getMonitored()
	{
		
		HashMap<String, KorisnikServer>	rezultat = new HashMap<String, KorisnikServer>();
		KorisnikServer					korisnik = null;
		Test						test = null;
		int							i;
		
		sendLine(true);

		for(i=0; i<tests.getList().size(); i++)
		{
			test = tests.getList().get(i);			
			korisnik = korisnici.findUser(test.username);
			
			
			if(test.isMonitored())
			{
				rezultat.put(korisnik.username, korisnik);
			}
			else
			{
				
				if(!rezultat.isEmpty())
					rezultat.remove(korisnik.username);				
			}
		}		
		for(KorisnikServer k : rezultat.values())
		{
			
			sendLine(k.toString());
			
			sendLine(monitoringExceeded(k.username));
		}
		endMessage();
	}

	private void sendPCR() 
	{		
		sendLine(true);
		
		if(pcr == null)
		{
			Test	lastPCR = tests.getLastPCR(prijavljeniKorisnik.username);
			
			sendLine("STATUS");
			
			if((lastPCR != null) && (isSameDate(lastPCR.datum, new GregorianCalendar())))
			{
				sendLine("Rezultat danasnjeg PCR testa je " + (lastPCR.pcrTest == 1 ? "POZITIVAN" : "NEGATIVAN"));
			}
			else
			{
				new Timer().schedule(new PCRTask(), 0, PCR_SECONDS_DELAY*1000);

				sendLine("Na cekanju...");
			}
		}
		
		else
		{
			switch(pcr.pcrTest)
			{
				case	0	:	sendLine("STATUS");
								sendLine("Na cekanju...");
								break;
								
				case	1	:	sendLine("STATUS");
								sendLine("Poslato...");
								break;
								
				case	2	:	sendLine("STATUS");
								sendLine("U obradi...");
								break;
								
				case	3	:	sendLine("RESULT");
								sendLine(String.valueOf(pcr.pcrTest));
								break;
			}
		}
		endMessage();				
	}

	private void listUsers()
	{
		Test			test;
		KorisnikServer		korisnik;
		int				i;

		sendLine(true);
		
		for(i=0; i<korisnici.getList().size(); i++)
		{
			korisnik = korisnici.getList().get(i);
			
			if(!korisnik.administrator)
			{
				sendLine(korisnik.toString());
			
				test = tests.getLastTest(korisnik.username);
				
				
				if(test != null)
					sendLine(test.toString());	
				else
					sendLine("123456789");	
			}
		}		
		endMessage();
	}

	private void listPositive()
	{
		HashMap<String, KorisnikServer>	rezultat = new HashMap<String, KorisnikServer>();
		KorisnikServer					korisnik = null;
		Test						test = null;
		int							i;
		
		sendLine(true);

		for(i=0; i<tests.getList().size(); i++)
		{
			test = tests.getList().get(i);			
			korisnik = korisnici.findUser(test.username);
			
			
			if(test.isPositive())
			{
				rezultat.put(korisnik.username, korisnik);
			}
			else
			{
				
				if(!rezultat.isEmpty())
					rezultat.remove(korisnik.username);				
			}
		}		
		for(KorisnikServer k : rezultat.values())
		{
			sendLine(k.toString());
		}
		endMessage();
	}
	
	

	private void listNegative()
	{
		HashMap<String, KorisnikServer>	rezultat = new HashMap<String, KorisnikServer>();
		KorisnikServer					korisnik;
		Test						test;
		
		sendLine(true);

		for(int i=0; i<tests.getList().size(); i++)
		{
			test = tests.getList().get(i);			
			korisnik = korisnici.findUser(test.username);
			
			if(!test.isPositive() && !test.isMonitored())
			{
				rezultat.put(korisnik.username, korisnik);
			}
			else
			{
				if(!rezultat.isEmpty())
					rezultat.remove(korisnik.username);				
			}
		}		
		for(KorisnikServer k : rezultat.values())
		{
			sendLine(k.toString());
		}
		endMessage();
	}
	
	// vraca listu svih testiranja prijavljenog korisnika
	private void getTests()
	{
		Test		test;
		List<Test>	rezultat;
		
		rezultat = tests.getTests(prijavljeniKorisnik.username);
			
		sendLine(true);

		for(int i=0; i<rezultat.size(); i++)
		{
			test = rezultat.get(i);
			sendLine(test.toString());
		}		
		endMessage();
	}
	
	
	private void saveTest()
	{
		Test	test = new Test();
		
		try {
			test.serialize(readLine());
			
			sendLine(tests.save(test));
								
			endMessage();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// za registraciju
	private void checkUsername()
	{
		String	username;
		
		try {
			username = readLine();
			
			sendLine(korisnici.checkUsername(username));
					
			endMessage();
		
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void saveUser()
	{
		try 
		{
			prijavljeniKorisnik = new KorisnikServer();
			
			prijavljeniKorisnik.fromString(readLine());
			
			sendLine(korisnici.save(prijavljeniKorisnik));
				
			endMessage();
	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void loginUser()
	{
		String		username, password;
		
		try {
			username = readLine();
			password = readLine();
			
			prijavljeniKorisnik = korisnici.login(username, password);
			
			
			if(prijavljeniKorisnik != null) 
			{				
				sendLine(true);
				
				sendLine(prijavljeniKorisnik.toString());
				
				endMessage();
				
				return;
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendLine(false);
		endMessage();
	}
	
	private String readLine() throws IOException
	{
		String	str = ulazni.readLine();
		
		System.out.println("ULAZ : " + str);
		
		return str;
	}

	private void sendLine(String text)
	{
		System.out.println("izlaz : " + text);
		
		izlazni.println(text);
	}

	private void sendLine(boolean text)
	{
		System.out.println("izlaz : " + text);
		
		izlazni.println(text);
	}

	private void endMessage()
	{
		sendLine("//END MESSAGE//");
	}
	
	private boolean isSameDate(GregorianCalendar datum1, GregorianCalendar datum2)
	{
		return (datum1.get(Calendar.YEAR) == datum2.get(Calendar.YEAR)) && 
			   (datum1.get(Calendar.MONTH) == datum2.get(Calendar.MONTH)) && 
			   (datum1.get(Calendar.DAY_OF_MONTH) == datum2.get(Calendar.DAY_OF_MONTH));
	}}
