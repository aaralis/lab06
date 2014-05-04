package com.example.gamelogic;

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.sql.*;


///////////////////////////// Mutlithreaded Server /////////////////////////////

public class ScoreServer
{
   final static int port = 5000;

   static void printUsage() {
   	System.out.println("In another window type:");
   	System.out.println("telnet sslabXX.cs.purdue.edu " + port);
	System.out.println("INSERT-SCORE|name|score|time");
	System.out.println("PRINT-SCORES");
   }

   public static void main(String[] args )
   {  
      try
      {  
         printUsage();
         int i = 1;
         ServerSocket s = new ServerSocket(port);
	 while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Spawning " + i);
            Runnable r = new ThreadedHandler(incoming);
            Thread t = new Thread(r);
            t.start();
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }
}

/**
   This class handles the client input for one server socket connection. 
*/
class ThreadedHandler implements Runnable
{ 
   public ThreadedHandler(Socket i)
   { 
      incoming = i; 
   }

   public static Connection getConnection() throws SQLException, IOException
   {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream("database.properties");
      props.load(in);
      in.close();
      String drivers = props.getProperty("jdbc.drivers");
      if (drivers != null)
        System.setProperty("jdbc.drivers", drivers);
      String url = props.getProperty("jdbc.url");
      String username = props.getProperty("jdbc.username");
      String password = props.getProperty("jdbc.password");

      System.out.println("url="+url+" user="+username+" password="+password);
 
      /*MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setUser(username);
      dataSource.setPassword(password);
      dataSource.setServerName(url);*/

      //return DriverManager.getConnection( url, username, password);
     //return DriverManager.getConnection( "jdbc:mysql://sslab01.cs.purdue.edu:3456/Lab06", "root", "lab06");
      return DriverManager.getConnection( "jdbc:mysql://127.0.0.1:3456/Lab06", "root", "lab06");
   }


   void insertScore(String name, int score, float time) {

      Connection conn=null;
      try
      {
			conn = getConnection();
		        Statement stat1 = conn.createStatement();
			
			ResultSet result = stat1.executeQuery("SELECT MAX(id) FROM score");
			result.next();
			int maxVal = Integer.parseInt(result.getString(1)) + 1;
			
			result.close();
			
			Statement stat2 = conn.createStatement();
			int value = stat2.executeUpdate("INSERT INTO score "
					+ "VALUES (" 
					+ maxVal + ", '" 
					+ name + "', " 
					+ score + ", " 
					+ time + ")");
			
			System.out.println("Name: " + name + ", Score: " + score + ", Time: " + time + " should be inserted");
      }
      catch (Exception e) {
      	System.out.println("Something broke");
      	System.out.println(e.toString());
      }
      finally
      {
		try {
	         if (conn!=null) conn.close();
		}
		catch (Exception e) {
		}
      }

      printScores(0);
   }

   void printScores(int time) {

      Connection conn=null;
      try
      {
	conn = getConnection();
	Statement stat = conn.createStatement();

	ResultSet result = null;

	if(time >= 1)
	{
		result = stat.executeQuery("SELECT name, moves, time FROM score "
				+ "ORDER BY time ASC LIMIT 10");
	}
	else
	{
		result = stat.executeQuery("SELECT name, moves, time FROM score "
				+ "ORDER BY moves ASC LIMIT 10");
	}
			
	int counter = 1;
	while(result.next())
	{
		
		System.out.print(counter + ": ");
		System.out.print(result.getString(1) + "|");
		System.out.print(result.getString(2) + "|");
		System.out.print(result.getString(3));
		//System.out.print(result.getString(4));
		System.out.println("");

		counter++;
	}

	result.close();
      }
      catch (Exception e) {
	System.out.println(e.toString());
      }
      finally
      {
	try {
         if (conn!=null) conn.close();
	}
	catch (Exception e) {
	}
      }
   }   

   void handleRequest( InputStream inStream, OutputStream outStream) {
        Scanner in = new Scanner(inStream);         
        PrintWriter out = new PrintWriter(outStream, 
                                      true /* autoFlush */);

	// Get parameters of the call
	String request = in.nextLine();

	System.out.println("Request="+request);

	String requestSyntax = "Syntax: COMMAND|NAME|SCORE|TIME";

	try {
		// Get arguments.
		// The format is COMMAND|USER|PASSWORD|OTHER|ARGS...
		String [] args = request.split("\\|");

		// Print arguments
		for (int i = 0; i < args.length; i++) {
			System.out.println("Arg "+i+": "+args[i]);
		}

		// Get command and password
		String command = args[0];
		String name = "";
		int score = 500;
		float time = 500;

		if(args.length >= 3)
		{
			name = args[1];
			score = Integer.parseInt(args[2]);
			time = Float.parseFloat(args[3]);
		}

		// Do the operation
		if (command.equals("INSERT-SCORE")) {
			insertScore(name, score, time);
		}
		else if (command.equals("PRINT-SCORES")) {
			printScores(0);
		}
		
	}
	catch (Exception e) {		
		System.out.println(requestSyntax);
		out.println(requestSyntax);

		System.out.println(e.toString());
		out.println(e.toString());
	}
   }

   public void run() {  
      try
      {  
         try
         {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
	    handleRequest(inStream, outStream);

         }
      	 catch (IOException e)
         {  
            e.printStackTrace();
         }
         finally
         {
            incoming.close();
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   private Socket incoming;
}

