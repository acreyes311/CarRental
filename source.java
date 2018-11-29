package CarRental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.*;

public class source {
	public static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstat = null;
		menu(conn,pstat);

	}
	
	
	/*
	 * menu options: 
	 * 1: connect ->goto submenu
	 * 2: Disconnect
	 * 0: exit
	 * 
	 */
	
	public static void menu(Connection conn, PreparedStatement pstat) {
			
		//pstat = conn.prepareStatement();
		int ch;
		do {
			System.out.println("\n\n1:Connect to CarRental System \n2:Disconnect \n0:Exit");
			
			ch = sc.nextInt();
			switch (ch) {
			//Case 1 Connects user to DB and calls Sub-Menu	
			case 1:
				try {
				conn = DriverManager.getConnection("jdbc:sqlite:/home/andrew/Documents/SQLiteStudio/CarRental");
				
				System.out.println("CONNECTED TO CARRENTAL DATABASE\n");
				 // Call Sub-Menu //
				subMenu(conn,pstat);
				
				}
				catch(SQLException e) {}
				break;
			
			// Case 2 Disconnects User From DB
			case 2:
				try {
				conn.close();
				System.out.println("DISCONNECTED");
				}
				catch(SQLException e) {}
				
				break;
				
			case 3:
				//getInput(pstat,conn);
				break;
				
			}	
		}while (ch != 0);
		System.out.println("EXITING");
		
		
	}
	
	
	//public static void addCustomer
	/*
	 *- subMenu will first ask if new or return customer.
	 *- if new -> get info -> create customer (function?)
	 *- else get name and proceed
	 *- next menu will ask for what they want to do
	 *	- Reserve
	 *	- Search/ Cars Available
	 *  - 
	 *	- Exit
	 * 
	 * TODO: Fix All Queries
	 * 
	 */
	
	
	public static void subMenu(Connection conn, PreparedStatement pstat) {
		int ch;
		String newName, retName, address;
		
		System.out.println("\n1: New Customer \n2: Returning Customer");
		ch = sc.nextInt();
		if(ch == 1) {
			System.out.println("Enter Your Full Name followed by your Address");
			
			newName = sc.nextLine();
			//System.out.println("Enter Your Address:");
			address = sc.nextLine();
		}
		
		if (ch == 2) {
			System.out.println("Enter Your Full Name:");
			retName = sc.nextLine();
		}
		
		do {
			System.out.println("Choose an option below.");
			System.out.println("\n1:Reserve  \n2: Vehicles by Location  \n3: Search  \n0: Back To Main Menu  ");
			ch = sc.nextInt();
			
			switch(ch) {
			case 1:
				reservation(conn, pstat);
				break;

			case 2:
				search(conn,pstat);
				break;
				
			case 3: 
				//cancelRes(conn, pstat);
				break;
			case 4:
				//billing(conn, pstat);
				//
				break;
			}
		}while(ch!= 0);
	}
	
	
	/* - reservation will ask for input on:
	 * 		1. location
	 * 		2. pickup date
	 *  	3. return date
	 *  	4. Car Type
	 *  
	 * - Query out list of cars available from location fitting date and passenger constraints
	 * - Take input / NAME/ l_id /
	 * - Print out reservation
	 * 
	 * TODO: - Fix Location Display
	 * 		 - Fix All queries
	 */
	public static void reservation(Connection conn, PreparedStatement pstat) {
		int lID;
		boolean flag = false;
		System.out.println("Which location would you like to pick up at(id #)? ");
		do {
			// ***** NEED TO FIX DISPLAY ******
			System.out.println(
					"1	3 Roxbury Place			309-892-3092		IL\n" + 
					"2	3 Orin Terrace			228-781-3694		MS\n" + 
					"3	55481 Loftsgordon Court	916-229-5953		CA\n" + 
					"4	0999 Ridgeway Point		307-124-5700		WY\n" + 
					"5	1614 Anderson Avenue	704-828-5125		NC\n" + 
					"6	51 Caliangt Park		816-722-4896		MO\n" + 
					"7	935 Paget Plaza			606-997-5229		KY\n" + 
					"8	052 Linden Avenue		518-812-4920		NY\n" + 
					"9	3 Anderson Parkway		402-505-1603		NE\n" + 
					"10	807 Pearson Drive		405-506-0323		OK\n" +
					"0 Exit\n");
			
			lID = sc.nextInt();
			if(lID == 0)
				return;			
			if(lID < 0 || lID > 10)
				System.out.println("Invalid ID choose again");
			else
				flag = true;			
			
		}while(flag != true);
		
		System.out.println("What day would you like to pick up(mm-dd)? " );
		String pickupDate = sc.nextLine();
		
		//System.out.println("What day would you like to return(mm-dd)? ");
		System.out.println("How many days would you like to rent for? ");
		String returnDate = sc.nextLine();
		
		System.out.println("These vehicles are available for rent on " + pickupDate + "\n");
		
		int vch;	//vehicle choice
		
		// *******  NEED VEHICLE ID FOR SEARCH *********
		// *******  LINK BETWEEN V_ID and L_ID *********
		try {
			String sql = "SELECT v_id,v_year,v_make, v_model,v_price FROM vehicle WHERE l_id = other_lid AND ";
			pstat = conn.prepareStatement(sql);
			ResultSet rs = pstat.executeQuery();
			
			while(rs.next()) {
				System.out.println("id no. " + rs.getInt("v_id") + rs.getString("v_year") + rs.getString("v_make") + 
						rs.getString("v_model") + rs.getString("v_price"));
				
			}
			
			
			System.out.println("Choose a Vehicle or -1 to go back");
			vch = sc.nextInt();
			if(vch == -1)
				return;
			
			// NOW RESERVE ? add to reservation, get reservation id
			
		}
		catch (SQLException e) {}
	}	
	
	
	/*
	 *  - search() runs queries based on user input ?
	 *  // how many criteria? year + model + make ? = A lot of input/work
	 */
	public static void search(Connection conn, PreparedStatement pstat) {
		int ch;
		do {		
			
			System.out.println("Choose criteria number to search by." );
			System.out.println("1:Year \n2:Make \n3:Model \n4:location \n0:Go Back");
			ch = sc.nextInt();
			
			switch(ch) {
			//  CASE 1 Print All vehicles from this year	
			case 1:				
				try {
					System.out.print("Enter the year(yyyy):");
					sc.nextLine();
					String in = sc.nextLine();
					
					String sqlY = "SELECT v_year, v_make, v_model, v_price FROM vehicle" +
							" WHERE v_year = '" + in + "' ORDER BY v_year";
					pstat = conn.prepareStatement(sqlY);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						//System.out.println("Inside rs.next");
						System.out.print(rs.getString("v_year") + rs.getString("v_make") + rs.getString("v_model") + rs.getDouble("v_price"));
						System.out.println();
					}

					
					/********************************************************
					System.out.println("Select vehicle id or enter -1 to go back");
					int vid = sc.nextInt();
					if(vid = -1)
						return;
					else do what
						- Print: You have selected this (print.vehicle) would you like to reserve
									* leads to even more user input, Maybe just take directly to reservation after choosing vid
						- call to reservation
							- if not go back to list					
					 ****************************************************/
				}
				catch(SQLException e) {}
				break;
				
			// CASE 2 Print All vehicles with this Make name
			case 2:
				
				try {
					System.out.print("Enter the Make: ");
					sc.nextLine();
					String in = sc.nextLine();
					
					String sqlM = "SELECT v_year, v_make, v_model, v_price FROM vehicle" +
							" WHERE v_make = '" + in + "' ORDER BY v_make";
					pstat = conn.prepareStatement(sqlM);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						// May need to add spaces " " ?						
						System.out.print(rs.getString("v_year") + rs.getString("v_make") + rs.getString("v_model") + rs.getDouble("v_price"));
						System.out.println();
					}
					/********************************************************
					System.out.println("Select vehicle id or enter -1 to go back");
					int vid = sc.nextInt();
					if(vid = -1)
						return;
					else do what
						- Print: You have selected this (print.vehicle) would you like to reserve
									* leads to even more user input, Maybe just take directly to reservation after choosing vid
						- call to reservation
							- if not go back to list					
					 ****************************************************/
				}
				catch(SQLException e) {}
				break;
			


			// CASE 3 Print all Vehicles with this MODEL name			
			case 3:				
				try {
					System.out.print("Enter the Model: ");
					sc.nextLine();
					String in = sc.nextLine();
					
					String sqlMod = "SELECT v_year, v_make, v_model, v_price FROM vehicle" +
							" WHERE v_model = '" + in + "' ORDER BY v_model";
					pstat = conn.prepareStatement(sqlMod);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						// May need to add spaces " " ?	
						System.out.print(rs.getString("v_year") + rs.getString("v_make") + rs.getString("v_model") + rs.getDouble("v_price"));
						System.out.println();
					}

					/********************************************************
					System.out.println("Select vehicle id or enter -1 to go back");
					int vid = sc.nextInt();
					if(vid = -1)
						return;
					else do what
						- Print: You have selected this (print.vehicle) would you like to reserve
									* leads to even more user input, Maybe just take directly to reservation after choosing vid
						- call to reservation
							- if not go back to list					
					 ****************************************************/
				}
				catch(SQLException e) {}
				break;

			//  CASE 4 Print location to display and wait for input.
			// Is there a different way to display and get input, through sql?
			// Then query to print all vehicles from that location.	
			case 4:
				
				int lID;
				boolean flag = false;
				System.out.println("Which location would you like to display? Enter id # ");
				do {
					// ***** NEED TO FIX DISPLAY ******
					System.out.println (
							"1	3 Roxbury Place			309-892-3092		IL\n" + 
							"2	3 Orin Terrace			228-781-3694		MS\n" + 
							"3	55481 Loftsgordon Court	916-229-5953		CA\n" + 
							"4	0999 Ridgeway Point		307-124-5700		WY\n" + 
							"5	1614 Anderson Avenue	704-828-5125		NC\n" + 
							"6	51 Caliangt Park		816-722-4896		MO\n" + 
							"7	935 Paget Plaza			606-997-5229		KY\n" + 
							"8	052 Linden Avenue		518-812-4920		NY\n" + 
							"9	3 Anderson Parkway		402-505-1603		NE\n" + 
							"10	807 Pearson Drive		405-506-0323		OK\n" +
							"0 Exit\n");
					
					lID = sc.nextInt();
					if(lID == 0)
						return;			
					if(lID < 0 || lID > 10)
						System.out.println("Invalid ID choose again");
					else
						flag = true;			
					
				}while(flag != true);
				try {
					// Print all vehicles from location (lID)
					String sqlL = "SELECT * FROM location" +
							" WHERE l_id = '" + lID + "' ORDER BY v_model";
					pstat = conn.prepareStatement(sqlL);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						// Fix spacing
						System.out.print(rs.getString("v_year") + rs.getString("v_make") + rs.getString("v_model") + rs.getDouble("v_price"));
						System.out.println();
					}
					/********************************************************
					System.out.println("Select vehicle id or enter -1 to go back");
					int vid = sc.nextInt();
					if(vid = -1)
						return;
					else do what
						- Print: You have selected this (print.vehicle) would you like to reserve
									* leads to even more user input, Maybe just take directly to reservation after choosing vid
						- call to reservation
							- if not go back to list					
					 ****************************************************/
				}
				catch(SQLException e) {}
				break;
				
			
			}
		}while(ch != 0);
		
		
	}
	/** 
	 * - return() asks for Vid,Odom, Dropoff location
	 * - update vehicle
	 */
	public static void return(Connection conn, PreparedStatement pstat) {
		
	}

	/**
	 * - billing()
	 * - Input
	 * 		- Get Customer Name
	 * - Search DB for most recent rental or all of customers transactions?
	 * - Display info to customer
	 * - Update Vehicle Count +1
	 */
	public static void billing(Connection conn, PreparedStatement pstat) {

	}
}