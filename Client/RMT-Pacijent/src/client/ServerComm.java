package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerComm {
	private Socket soketZaKom = null; 
	private PrintStream izlazni = null;
	private BufferedReader ulazni = null;
	
	private List<String>	rezultat = new ArrayList<String>();
	
	public ServerComm() throws IOException
	{
		int port = 3334;

		soketZaKom = new Socket("localhost",port);
		izlazni = new PrintStream(soketZaKom.getOutputStream());
		ulazni = new BufferedReader(new InputStreamReader(soketZaKom.getInputStream()));
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		soketZaKom.close();
	}

	public boolean sendMessage(String command, String... params) 
	{
		izlazni.println(command);
	
		for(int i=0;i<params.length;i++)
		{
			izlazni.println(params[i]);
		}
		
		return getMessage();
	}
	
	public boolean getMessage() 
	{
		String			poruka;

		rezultat.clear();

		try {
			
			poruka = ulazni.readLine();
			
			if(poruka != null)
			{
				
				if(poruka.equals("true"))
				{				
					while(!(poruka = ulazni.readLine()).equals("//END MESSAGE//"))
					{	
						rezultat.add(poruka);
					}
					return true;
				}
				else 
					ulazni.readLine();
			}
			
		} catch (IOException e) {
			System.out.println("Greska u komunikaciji");
			System.out.println(e.getMessage());
		}
		
		return false;
	}
	
	public List<String> getResults()
	{
		return rezultat;
	}

	public String getResult(int index)
	{
		return rezultat.get(index);
	}

}
