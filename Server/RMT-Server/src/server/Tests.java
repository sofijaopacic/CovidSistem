package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tests {
	private List<Test> testovi = new ArrayList<>();
	
	public Tests()
	{
		load();
	}
	
	private void addTest(Test test)
	{
		testovi.add(test);
	}
	
	public Test getLastTest(String username) 
	{
		Test 		test;
		
		for(int i=testovi.size()-1; i>=0; i--) 
		{
			test = testovi.get(i);
			
			if (test.username.equals(username)) 
				return test;
		}
		
		return null; 
	}

	public Test getLastPCR(String username) 
	{
		Test 		test;
		
		for(int i=testovi.size()-1; i>=0; i--) 
		{
			test = testovi.get(i);
			
			if (test.username.equals(username) && (test.pcrTest > 0)) 
				return test;
		}
		
		return null; 
	}

	public List<Test> getTests(String username) 
	{
		List<Test> rezultat = new ArrayList<>();		
		Test 		test;
		
		for(int i=0; i<testovi.size(); i++) 
		{
			test = testovi.get(i);
			
			if (test.username.equals(username)) 
			{
				rezultat.add(test);
			}
		}
		
		return rezultat; 
	}

	public boolean save(Test test)
	{
		try {
			FileWriter fp = new FileWriter("testovi.txt", true);
	
			fp.append(test.serialize());
			
			fp.close();
			
			addTest(test);
			
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void load()
	{
		String 		podatak;
		Test 		test;
		
		try {
			BufferedReader fr = new BufferedReader(new FileReader("testovi.txt"));
			while(true) 
			{
				podatak = fr.readLine();

				if(podatak==null || podatak.equals("")) break;
				
				test = new Test();
				
				test.serialize(podatak);
				
				addTest(test);
			}
			fr.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public List<Test> getList()
	{
		return testovi;
	}
}
