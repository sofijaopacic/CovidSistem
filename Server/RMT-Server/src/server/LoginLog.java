package server;


public class LoginLog {
	String	username;
	String	datum;
	
	public void serialize(String podaci)
	{
		String niz[];
		
		niz = podaci.split(";");

		username = niz[0];
		datum = niz[1];
	}
	
	public String serialize()
	{
		return 	username + ";" + 
		   		datum + "\n"; 
	}
	
}
