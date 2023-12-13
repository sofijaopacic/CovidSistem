package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.Test;

public class Klijent implements Runnable {
		
	ServerComm		server;
	BufferedReader 	konzola;
	Korisnik 	prijavljeniKorisnik;
	
	
	public static void main(String[] args) 
	{
		Klijent		k = new Klijent();
		
		k.run();
		
		System.out.println("-- Kraj rada --");

	}
			
	public void run()
	{
		String	izbor;
				
		try 
		{
			konzola = new BufferedReader(new InputStreamReader(System.in));
			server = new ServerComm();
			prijavljeniKorisnik = new Korisnik();
			
			do {
				
				printHeader("Dobrodosli u COVID SISTEM1");
				
				printLine("1. Prijava");
				printLine("2. Registracija");
				printLine("");
				printLine("0. Kraj rada");
			
				izbor = readLine();
			
				switch (izbor) 
				{
					case "1" 	:	loginMenu();
									break;

					case "2" 	: 	registrationMenu();
									break;
					   											
					case "0" 	: 	server.sendMessage("/exit");
									break;

					default		:	System.out.println("Nepostojeci izbor");
									break;
				}
			} while(!izbor.equals("0")); 

		} 
		catch (IOException e) {
			printLine("Server nije dostupan.");
			printLine(e.getMessage());
		}

	}	
	
	private void loginMenu()  
	{
		String			username, password;
		boolean 		loginOk = false;

		try {
			while(!loginOk) {
				
				printHeader("PRIJAVA NA SISTEM");
				
				username = inputField("Unesite korisnicko ime");				
				password = inputField("Unesite lozinku");
				
				if(username.isEmpty() || password.isEmpty())
					return;
				
				if(server.sendMessage("loginUser", username, password))
				{
					loginOk = true;
					
					prijavljeniKorisnik.fromString(server.getResult(0));
										
					if(prijavljeniKorisnik.administrator)
						administratorMenu();
					else
						testMenu();
				}
				else
				{
					printLine("Pogresno korisnicko ime ili lozinka. \nDa li zelite da pokusate ponovo (D-da, ostalo-ne) ?");

					if(!readLine().toLowerCase().equals("d")) 
						loginOk = true;
				}
			}
			
		} catch (IOException e) {
			printLine(e.getMessage());
		}
	}
	
	private void registrationMenu() 
	{
		boolean unosOk = false;
			
		try 
		{
			printHeader("REGISTRACIJA");
			
			while(!unosOk) 
			{
				prijavljeniKorisnik.username = inputField("Unesite korisnicko ime");
					
				unosOk = !server.sendMessage("checkUsername", prijavljeniKorisnik.username);

				if(!unosOk) {
					printLine("Ovo korisnicko ime je zauzeto, pokusajte drugo");
				}
			}
			
			unosOk=false;				
			while (!unosOk) 
			{
				prijavljeniKorisnik.lozinka = inputField("Unesite lozinku");
				
				unosOk = prijavljeniKorisnik.lozinka.length() > 2;
				
				if(!unosOk) {
					printLine("Lozinka mora imati vise od 3 karaktera");
				}
			}
			
			unosOk=false;		
			while (!unosOk) 
			{
				prijavljeniKorisnik.ime = inputField("Unesite vase ime");
				
				unosOk = !prijavljeniKorisnik.ime.isEmpty();
				
				if(!unosOk) {
					printLine("Morate uneti ime");
				}
			}
				
			unosOk=false;		
			while (!unosOk) 
			{
				prijavljeniKorisnik.prezime = inputField("Unesite vase prezime");
				
				unosOk = !prijavljeniKorisnik.prezime.isEmpty();
				if(!unosOk) {
					printLine("Morate uneti prezime");
				}
			}
				
			unosOk=false;	
			while (!unosOk) 
			{
				prijavljeniKorisnik.pol = inputField("Unesite vas pol (M/Z)");
				
				unosOk = (prijavljeniKorisnik.pol.equalsIgnoreCase("m") || prijavljeniKorisnik.pol.equalsIgnoreCase("z"));
				if(!unosOk) {
					printLine("Pol mora biti M ili Z");
				}
			}
				
			unosOk=false;	
			while (!unosOk) 
			{
				prijavljeniKorisnik.email = inputField("Unesite vas email");
				
				
				Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(prijavljeniKorisnik.email);
				
				unosOk = matcher.matches();
				if(!unosOk) {
					printLine("Neispravna email adresa");
				}
			}
				
			if(server.sendMessage("saveUser", prijavljeniKorisnik.toString())) 
			{
				printLine("Uspesna registracija");

				testMenu();
			}
						
		} catch (IOException e) {
			printLine("Greska pri unosu: " + e.getMessage());
		}
	}
//
// ADMINISTRATOR
//
	private void administratorMenu() 
	{
		String izbor;
		
		// odmah moraju podaci o novim 
		pocetakAdmin();
		
		try {
			do {
				printHeader("ADMINISTRATOR");
				printSayHello(prijavljeniKorisnik);				

				printLine("1. Svi korisnici");
				printLine("2. Pozitivni");
				printLine("3. Negativni");
				printLine("4. Pod nadzorom");
				printLine("5. Statistika");
				printLine("");
				printLine("0. Odjavi se");
				
				izbor = readLine();
				
				switch (izbor) 
				{
					case "1" 	: 	listaKorisnika();
						   			break;
					case "2" 	:	listaPozitivnih();
									break; 
					case "3" 	:	listaNegativnih();
									break; 
					case "4" 	:	listaNadziranih();
									break; 
					case "5" 	:	statistika();
									break; 
					case "0" 	: 	break;
	
					default		:	System.out.println("Nepostojeci izbor");
									break;
				}
			} while (!izbor.equals("0"));
			
		} catch (Exception e) {
			printLine(e.getMessage());
		}
	}
	
	private void pocetakAdmin() 
	{
		Korisnik	korisnik = new Korisnik();
		
		if(server.sendMessage("listPositive"))
		{
			printHeader("LISTA POZITIVNIH");
			
			for(int i=0; i<server.getResults().size(); i++)
			{
				korisnik.fromString(server.getResult(i));
				printLine(korisnik.getFullName() + ", " + korisnik.email);
			}
			
			if(server.getResults().size()==0)
				printLine("Nema pozitivnih osoba.");
			
		}
		
		String			s;
		int provera=0;
		
		
		if(server.sendMessage("getMonitored"))
		{
			printHeader("LISTA OSOBA POD NADZOROM");
			
			if(server.getResults().size()==0)
				printLine("Nema nadziranih osoba.");
			else
			{
				for(int i=0; i<server.getResults().size(); i+=2)
				{
					korisnik.fromString(server.getResult(i));

					s = korisnik.getFullName() + ", " + korisnik.email;
				
					if(server.getResult(i+1).equals("true"))
					{
						s += " - POTREBNO TESTIRANJE";
						printLine(s);
						provera++;
					}
				}
			
				if(provera==0)
				{
					printLine("Ima osoba pod nadzorom ali im on nije istekao!");
				}
			}


			pressAnyButton();
		}
		
	}

	private void statistika() 
	{
		if(server.sendMessage("getStatistics"))
		{
			printHeader("STATISTIKA");
			
			printLine("Testitanja - " + String.valueOf(server.getResult(0)));
			printLine("Pozitivnih - " + String.valueOf(server.getResult(1)));
			printLine("Negativnih - " + String.valueOf(server.getResult(2)));
			printLine("Nadziranih - " + String.valueOf(server.getResult(3)));

			pressAnyButton();
		}
		
	}

	private void listaNadziranih()  
	{
		String			s;
		Korisnik	korisnik = new Korisnik();
		
		if(server.sendMessage("getMonitored"))
		{
			printHeader("LISTA OSOBA POD NADZOROM");
			
			for(int i=0; i<server.getResults().size(); i+=2)
			{
				korisnik.fromString(server.getResult(i));

				s = korisnik.getFullName() + ", " + korisnik.email;
				
				if(server.getResult(i+1).equals("true"))
					s += " - POTREBNO TESTIRANJE";
				
				printLine(s);
			}

			if(server.getResults().size()==0)
				printLine("Nema nadziranih osoba.");

			pressAnyButton();
		}
		
	}


	private void listaPozitivnih() 
	{
		Korisnik	korisnik = new Korisnik();
		
		if(server.sendMessage("listPositive"))
		{
			printHeader("LISTA POZITIVNIH");
			
			for(int i=0; i<server.getResults().size(); i++)
			{
				korisnik.fromString(server.getResult(i));
				printLine(korisnik.getFullName() + ", " + korisnik.email);
			}
			
			if(server.getResults().size()==0)
				printLine("Nema pozitivnih osoba.");
			pressAnyButton();
		}
		
	}

	private void listaNegativnih() 
	{
		Korisnik	korisnik = new Korisnik();
		
		if(server.sendMessage("listNegative"))
		{
			printHeader("LISTA NEGATIVNIH");
			
			for(int i=0; i<server.getResults().size(); i++)
			{
				korisnik.fromString(server.getResult(i));
				printLine(korisnik.getFullName() + ", " + korisnik.email);
			}

			if(server.getResults().size()==0)
				printLine("Nema negativnih osoba.");

			pressAnyButton();;
		}
		
	}
	
	
	private void listaKorisnika() 
	{
		String			str;;
		Test		test = new Test(null);
		Korisnik	korisnik = new Korisnik();
		
		if(server.sendMessage("listUsers"))
		{
			printHeader("LISTA KORISNIKA");
			
			for(int i=0; i<server.getResults().size(); i+=2)
			{
				korisnik.fromString(server.getResult(i));
				
				str = server.getResult(i+1);
				if(str.equals("123456789")) // korisnik nije testiran - nema podataka
				{
					str = "(nema podataka)";
				}
				else
				{
					test.fromString(str);
					if(test.isMonitored())
						str = "POD NADZOROM ";
					else
						str = test.isPositive() ? "POZITIVAN" : "NEGATIVAN";
					
				}
				printLine(str + " - " + korisnik.getFullName() + ", " + korisnik.email);
			}

			pressAnyButton(); 
		}
		
	}
//
// USER
//
	private void testMenu() {
		
		String izbor;
		
		 try {
			 do {
				 printHeader("GLAVNI MENI");
					//ovde mu ispisujem stanje ako je pre bio logovan i sve one podatke
				 printSayHello(prijavljeniKorisnik);				
				 
				 if(server.sendMessage("testNeeded"))
					 printLine("Pod nadzorom ste, isteklo je vreme da uradite test !!\n");
				 
				 printLine("1. Test samoprocene");
				 printLine("2. PCR test");
				 printLine("3. Brzi test");
				 printLine("4. Uvid u vase podatke");
				 printLine("");
				 printLine("0. Odjavi se");
				
				izbor = readLine();
				
				switch (izbor) 
				{
					case "1" 	: 	testSamoprocena();
						   			break;
						   			
					case "2" 	: 	pcrTest();
		   							break;
		   			
					case "3"  :  	brziTest(null);
									break;
		   							
					case "4" 	:	uvidPodaci();
									break;
									
					case "0" 	: 	break;
	
					default		:	printLine("Nepostojeci izbor");
									break;
				}
			} while (!izbor.equals("0"));
			
		} catch (Exception e) {
			printLine(e.getMessage());
		}
	}
		
	private void brziTest(Test test) 
	{
		if(test == null)
			test = new Test(prijavljeniKorisnik.username);
		
		if(server.sendMessage("getSpeedyTest"))
		{
			test.brziTest = Integer.parseInt(server.getResult(0));

			printLine("\nRezultat brzog testa: " + (test.brziTest == 1 ? "POZITIVAN" : "NEGATIVAN"));					
			
			server.sendMessage("saveTest", test.toString());
		}
		else
			printLine("Nije dozvoljeno testiranje, moguce je uraditi smao jedan test u danu.");					
			
		pressAnyButton();
		
	}

	private void testSamoprocena() 
	{
		boolean				odgovor;
		Test			test = new Test(prijavljeniKorisnik.username);
		
		
		if(!server.sendMessage("testAllowed"))
		{
			printLine("Nije dozvoljeno testiranje, moguce je uraditi smao jedan test u danu.");
			pressAnyButton();
			return;
		}
		
		try 
		{
			printHeader("TEST SAMOPROCENE");

			odgovor = getOdgovor("Da li ste putovali van Srbije u okviru 14 dana pre pocetka simptoma (D/N)?");
			test.setSamotestiranje(0, odgovor);
			
			odgovor = getOdgovor("Da li ste bili u kontaku sa zarazenim osobama (D/N) ?");
			test.setSamotestiranje(1, odgovor);
			
			printLine("Sta imate od simptoma ?");
			printSeparator();
			
			odgovor = getOdgovor("Povisena temperatura (D/N) ?"); 
			test.setSamotestiranje(2, odgovor);
			
			odgovor = getOdgovor("Kasalj (D/N) ?");
			test.setSamotestiranje(3, odgovor);
			
			odgovor = getOdgovor("Opsta slabost (D/N) ?");
			test.setSamotestiranje(4, odgovor);
			
			odgovor = getOdgovor("Gubitak cula mirisa (D/N) ?");
			test.setSamotestiranje(5, odgovor);
			
			odgovor = getOdgovor("Gubitak/promena cula ukusa (D/N) ?");
			test.setSamotestiranje(6, odgovor);
			
			int		brojPozitivnihOdgovora = test.getBrojPozitivnihOdgovora();
			
			printLine("\n\n- Odgovoreno sa Da : " + brojPozitivnihOdgovora);
			
			if(brojPozitivnihOdgovora > 1) 
			{				
				brziTest(test);					
				
			}
			else
			{
				server.sendMessage("saveTest", test.toString());
			
				pressAnyButton();
			}
			
		} catch (Exception e) {
		}
		
	}

	private void pcrTest() 
	{
		String			status;
		
		if(server.sendMessage("sendPCR"))
		{	
			status = server.getResult(0);
			
			switch(status)
			{
				case "STATUS"	:	printLine(server.getResult(1));
									break;
									
				case "RESULT"	:	int		pcrResult = Integer.valueOf(server.getResult(1)) == 1 ? 1 : 2;
				
									printLine("Rezultat je " + (pcrResult == 1 ? "POZITIVAN" : "NEGATIVAN"));
									break;
			}

			pressAnyButton();
		}		
	}
	
	private void uvidPodaci() 
	{
		int				odgovorDa;
		String			s;
		Test		test = new Test(null);
		
		if(server.sendMessage("getTests"))
		{
			printHeader("PREGLED TESTIRANJA");
			
			for(int i=0; i<server.getResults().size(); i++)
			{
				test.fromString(server.getResult(i));
				
				s = test.getDatum() + " - ";
				
				// vraca -1 ako nije radjena samoprocena (podaci su od nekog testa), inace broj pozitivnih odgovora
				odgovorDa = test.getBrojPozitivnihOdgovora();

				if(odgovorDa != -1)
					s += "Samotestiranje (Da = " + String.valueOf(odgovorDa) + ") ";

				if(test.isMonitored())
					s += "POD NADZOROM";
				else
				{
					s += test.brziTest > 0 ? "(BRZI TEST) " : "(PCR TEST) ";
					s += test.isPositive() ? "POZITIVAN" : "NEGATIVAN";
				}
				printLine(s);
			}

			pressAnyButton();
		}
	}
//
// helpers
//
	private String inputField(String text) throws IOException
	{
		printLine(text);
		
		return readLine();
	}
	
	private void printLine(String text)
	{
		System.out.println(text);			
	}
	
	private void printSeparator()
	{
		printLine("------------------------------------------------");			
	}

	private void printHeader(String text)
	{
		printSeparator();
		printLine(text);			
		printSeparator();
	}
	
	private void printSayHello(Korisnik korisnik)
	{
		printLine("Korisnik : " + korisnik.getFullName());				
		printLine("Poslednja prijava na sistem " + korisnik.getDatumPoslednjePrijave());
		printSeparator();		
	}
	
	private void pressAnyButton() 
	{
		try {
			inputField("\nPritisnite ENTER za povratak na meni...");
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
	
	private boolean getOdgovor(String text) 
	{
		String odgovor;
		
		try {
			odgovor = inputField(text);
			
			return odgovor.equalsIgnoreCase("D");
		} 
		catch (IOException e) {
//			e.printStackTrace();
		}
		return false;
	}
		
	private String readLine() throws IOException
	{
		String	text = konzola.readLine();
		
		if(text.equalsIgnoreCase("/exit"))
		{
			server.sendMessage("/exit");
			System.out.println("-- Kraj rada --");
			System.exit(0);
		}
		return text;
	}
	
}
