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
				conn = DriverManager.getConnection("jdbc:sqlite:/home/andrew/Documents/SQLiteStudio/CarRental2");
				
				System.out.println("CONNECTED TO CARRENTAL DATABASE\n");
				 // Call Sub-Menu //
				sc.nextLine();
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
	
	
	public static void addCustomer(Connection conn, PreparedStatement pstat) {
		System.out.println("Enter your full name.");
		String name = sc.nextLine();
		System.out.println("Enter your address");
		String address = sc.nextLine();
		System.out.println("Enter state.");
		String state = sc.nextLine();
		
		try {
		String custmax = "SELECT MAX(c_custid) FROM customer";
		pstat = conn.prepareStatement(custmax);
		ResultSet rs = pstat.executeQuery();
		int custid = rs.getInt("MAX(c_custid)");
		custid++;
		
		String rentmax = "SELECT MAX(c_rentalid) FROM customer";
		pstat = conn.prepareStatement(rentmax);
		rs = pstat.executeQuery();
		int rentalid = rs.getInt("MAX(c_rentalid)");
		rentalid++;
		
		String sqlIn = "insert into customer(c_custid, c_name, c_rentalid, c_address, c_state) " + 
				"  values(?,?,?,?,?)";
		pstat = conn.prepareStatement(sqlIn);
		pstat.setInt(1,custid);
		pstat.setString(2,name);			
		pstat.setInt(3,rentalid);
		pstat.setString(4,address);
		pstat.setString(5,state);
		
		
		pstat.executeUpdate();
		System.out.println("Table updated");
		
		
		}
		catch(SQLException e) {}
	}
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
	 *
	 * 
	 */
	
	
	public static void subMenu(Connection conn, PreparedStatement pstat) {
		int ch;
		String retName;
		
		System.out.println("\n1: New Customer \n2: Returning Customer");
		ch = sc.nextInt();
		sc.nextLine();
		if(ch == 1) {
			addCustomer(conn,pstat);
		}
		
		if (ch == 2) {
			System.out.println("Enter Your Full Name:");
			
			retName = sc.nextLine();
		}
		
		do {
			System.out.println("Choose an option below.");
			System.out.println("\n1: Reserve  \n2: Search  \n3: Cancel Reservation  \n4: Billing  \n0: Back To Main Menu  ");
			ch = sc.nextInt();
			
			switch(ch) {
			case 1:
				reservation(conn, pstat);
				break;

			case 2:
				search(conn,pstat);
				break;
				
			case 3: 
				cancelRes(conn, pstat);
				break;
			case 4:
				billing(conn, pstat);
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
	 */
	public static void reservation(Connection conn, PreparedStatement pstat) {
		int lID;
		boolean flag = false;
		System.out.println("Which location would you like to pick up at(id #)? ");
		do {
			
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
			sc.nextLine();
			if(lID == 0)
				return;			
			if(lID < 0 || lID > 10)
				System.out.println("Invalid ID choose again");
			else
				flag = true;			
			
		}while(flag != true);
		
		try {
			
			// Print all vehicles from location (lID)
			String sql = "SELECT v_vehicleid,v_year,v_make, v_model,v_price,res_locationid FROM vehicle,reservation WHERE res_locationid = " + lID +
				" AND res_vehicleid = v_vehicleid";
			pstat = conn.prepareStatement(sql);
			ResultSet rs = pstat.executeQuery();
			
			System.out.println("before while loop");
			
			while(rs.next()) {
				System.out.print("\nid:" + rs.getInt("v_vehicleid") + " " + rs.getString("v_year") + " " + rs.getString("v_make") + 
						" " + rs.getString("v_model") + " $" + rs.getString("v_price") + " at location id " + rs.getInt("res_locationid"));						
			}
			System.out.println();
			System.out.println("Select vehicle id to begin reservation or enter -1 to go back");

			int vid = sc.nextInt();
			sc.nextLine();
			if(vid == -1)
				return;

			System.out.println("What day would you like to pick up(mm-dd)? " );
			String pickupDate = sc.nextLine();
			
			System.out.println("How many days would you like to rent for? ");
			int returnDate = sc.nextInt();
			sc.nextLine();
			
		
								
			// Variables to store max
			int resid,rentalid,custid;
			
			
			// Query for reservationid max and increment++
			String resmax = "SELECT MAX(res_reservationid) FROM reservation";
			pstat = conn.prepareStatement(resmax);
			rs = pstat.executeQuery();
			resid = rs.getInt("MAX(res_reservationid)");
			resid++;
			
			
			
			// Query for MAX rentalid and increment++
			String rentmax = "SELECT MAX(res_rentalid) FROM reservation";
			pstat = conn.prepareStatement(rentmax);
			rs = pstat.executeQuery();
			rentalid = rs.getInt("MAX(res_rentalid)");
			rentalid++;
			
			// Query for MAX custid and increment++
			String custmax = "SELECT MAX(res_custid) FROM reservation";
			pstat = conn.prepareStatement(custmax);
			rs = pstat.executeQuery();
			custid = rs.getInt("MAX(res_custid)");
			custid++;
			
			
			//sc.nextLine();	
			System.out.println("After CUSTMAX BEFORE UPDATE ");
			//pstat = conn.prepareStatement(sqlIn);
			String sqlIn = "insert into reservation(res_reservationid, res_rentalid, res_locationid, res_pickup, " + 
					" res_custid, res_vehicleid) values(?,?,?,?,?,?)";
			pstat = conn.prepareStatement(sqlIn);
			pstat.setInt(1,resid);
			pstat.setInt(2,rentalid);			
			pstat.setInt(3,lID);
			pstat.setString(4,pickupDate);
			pstat.setInt(5,custid);
			pstat.setInt(6,vid);
			
			pstat.executeUpdate();
			System.out.println("Reservation Made Successfully");
			System.out.println("Reservation ID: " + resid);
			System.out.println("Pickup date:" + pickupDate);
			System.out.println("At location " + lID);

			// ----------------------- PRINT OUT INFO RESID VEHICLE DATE TO PICK UP ---------------
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
			sc.nextLine();
			int lID = 0;
			switch(ch) {
			//  CASE 1 Print All vehicles from this year	
			case 1:				
				try {
					System.out.print("Enter the year(yyyy):");
					//sc.nextLine();
					String year = sc.nextLine();
					
					String sqlY = "SELECT res_locationid, v_year, v_make, v_model, v_price FROM vehicle,reservation" +
							" WHERE v_year = '" + year + "' AND v_vehicleid = res_vehicleid ORDER BY v_year";
					pstat = conn.prepareStatement(sqlY);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						lID = rs.getInt("res_locationid");
						System.out.print("id." + lID + " " + rs.getString("v_year") + " " + rs.getString("v_make") + " " + rs.getString("v_model") + 
								" $" + rs.getDouble("v_price"));
						
						//System.out.print(" " + lID);
						System.out.println();
					}
					
				
					System.out.println("Select vehicle id to begin reservation or enter -1 to go back");
					int vid = sc.nextInt();
					sc.nextLine();
					if(vid == -1)
						break;

					System.out.println("What day would you like to pick up(mm-dd)? " );
					String pickupDate = sc.nextLine();
					
					System.out.println("How many days would you like to rent for? ");
					int returnDate = sc.nextInt();
					sc.nextLine();
					
					
					//pstat.executeQuery();
					
										
					// Variables to store max
					int resid,rentalid,custid;
					
					
					// Query for reservationid max and increment++
					String resmax = "SELECT MAX(res_reservationid) FROM reservation";
					pstat = conn.prepareStatement(resmax);
					rs = pstat.executeQuery();
					resid = rs.getInt("MAX(res_reservationid)");
					resid++;
					//System.out.println(resid);
					
					
					// Query for MAX rentalid and increment++
					String rentmax = "SELECT MAX(res_rentalid) FROM reservation";
					pstat = conn.prepareStatement(rentmax);
					rs = pstat.executeQuery();
					rentalid = rs.getInt("MAX(res_rentalid)");
					rentalid++;
					
					// Query for MAX custid and increment++
					String custmax = "SELECT MAX(res_custid) FROM reservation";
					pstat = conn.prepareStatement(custmax);
					rs = pstat.executeQuery();
					custid = rs.getInt("MAX(res_custid)");
					custid++;
					
					lID = 2;
					//sc.nextLine();	
					
					//pstat = conn.prepareStatement(sqlIn);
					String sqlIn = "insert into reservation(res_reservationid, res_rentalid, res_locationid, res_pickup, " + 
							" res_custid, res_vehicleid) values(?,?,?,?,?,?)";
					pstat = conn.prepareStatement(sqlIn);
					pstat.setInt(1,resid);
					pstat.setInt(2,rentalid);			
					pstat.setInt(3,lID);
					pstat.setString(4,pickupDate);
					pstat.setInt(5,custid);
					pstat.setInt(6,vid);
					
					pstat.executeUpdate();
					System.out.println("Reservation Made Successfully");
					System.out.println("Reservation ID: " + resid);
					System.out.println("Pickup date:" + pickupDate);
					System.out.println("At location " + lID);

					// ----------------------- PRINT OUT INFO RESID VEHICLE DATE TO PICK UP ---------------
					
				}
				catch(SQLException e) {}
				break;
				
			// CASE 2 Print All vehicles with this Make name
			case 2:
				
				try {
					System.out.println("Enter the Make(Ford..): ");
					
					String in = sc.nextLine();
					
					String sqlM = "SELECT v_vehicleid, v_year, v_make, v_model, v_price,res_locationid FROM vehicle,reservation" +
							" WHERE v_make = '" + in + "' AND v_vehicleid = res_vehicleid ORDER BY v_make";
					pstat = conn.prepareStatement(sqlM);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						// May need to add spaces " " ?						
						System.out.print("id." + rs.getInt("v_vehicleid") + " " + rs.getString("v_year") + " " + rs.getString("v_make") + 
								" " + rs.getString("v_model") + " $" + rs.getDouble("v_price") + " location id." + rs.getInt("res_locationid"));
						System.out.println();
					}
					System.out.println("Select vehicle id to begin reservation or enter -1 to go back");
					int vid = sc.nextInt();
					sc.nextLine();
					if(vid == -1)
						break;

					System.out.println("What day would you like to pick up(mm-dd)? " );
					String pickupDate = sc.nextLine();
					
					System.out.println("How many days would you like to rent for? ");
					int returnDate = sc.nextInt();
					sc.nextLine();
					
				
										
					// Variables to store max
					int resid,rentalid,custid;
					
					
					// Query for reservationid max and increment++
					String resmax = "SELECT MAX(res_reservationid) FROM reservation";
					pstat = conn.prepareStatement(resmax);
					rs = pstat.executeQuery();
					resid = rs.getInt("MAX(res_reservationid)");
					resid++;				
					
					
					// Query for MAX rentalid and increment++
					String rentmax = "SELECT MAX(res_rentalid) FROM reservation";
					pstat = conn.prepareStatement(rentmax);
					rs = pstat.executeQuery();
					rentalid = rs.getInt("MAX(res_rentalid)");
					rentalid++;
					
					// Query for MAX custid and increment++
					String custmax = "SELECT MAX(res_custid) FROM reservation";
					pstat = conn.prepareStatement(custmax);
					rs = pstat.executeQuery();
					custid = rs.getInt("MAX(res_custid)");
					custid++;
					
					
					//sc.nextLine();	
					System.out.println("After CUSTMAX BEFORE UPDATE ");
					//pstat = conn.prepareStatement(sqlIn);
					String sqlIn = "insert into reservation(res_reservationid, res_rentalid, res_locationid, res_pickup, " + 
							" res_custid, res_vehicleid) values(?,?,?,?,?,?)";
					pstat = conn.prepareStatement(sqlIn);
					pstat.setInt(1,resid);
					pstat.setInt(2,rentalid);			
					pstat.setInt(3,lID);
					pstat.setString(4,pickupDate);
					pstat.setInt(5,custid);
					pstat.setInt(6,vid);
					
					pstat.executeUpdate();
					System.out.println("Reservation Made Successfully");
					System.out.println("Reservation ID: " + resid);
					System.out.println("Pickup date:" + pickupDate);
					System.out.println("At location " + lID);
				}
				catch(SQLException e) {}
				break;
			


			// CASE 3 Print all Vehicles with this MODEL name			
			case 3:	
				
				try {
					System.out.println("Enter the Model(Viper..): ");
					
					String in = sc.nextLine();
					
					String sqlMod = "SELECT v_vehicleid, v_year, v_make, v_model, v_price, res_locationid FROM vehicle,reservation" +
							" WHERE v_model = '" + in + "' AND v_vehicleid = res_vehicleid ORDER BY v_model";
					pstat = conn.prepareStatement(sqlMod);
					ResultSet rs = pstat.executeQuery();
					while(rs.next()) {
						// May need to add spaces " " ?	
						System.out.print("id." + rs.getInt("v_vehicleid") + " " + rs.getString("v_year") +  " " + rs.getString("v_make") + 
								" " + rs.getString("v_model") + " $" + rs.getDouble("v_price") + " location id." + rs.getInt("res_locationid"));
						System.out.println();
					}

					System.out.println("Select vehicle id to begin reservation or enter -1 to go back");

					int vid = sc.nextInt();
					sc.nextLine();
					if(vid == -1)
						break;

					System.out.println("What day would you like to pick up(mm-dd)? " );
					String pickupDate = sc.nextLine();
					
					System.out.println("How many days would you like to rent for? ");
					int returnDate = sc.nextInt();
					sc.nextLine();
					
				
										
					// Variables to store max
					int resid,rentalid,custid;
					
					
					// Query for reservationid max and increment++
					String resmax = "SELECT MAX(res_reservationid) FROM reservation";
					pstat = conn.prepareStatement(resmax);
					rs = pstat.executeQuery();
					resid = rs.getInt("MAX(res_reservationid)");
					resid++;
					
					
					
					// Query for MAX rentalid and increment++
					String rentmax = "SELECT MAX(res_rentalid) FROM reservation";
					pstat = conn.prepareStatement(rentmax);
					rs = pstat.executeQuery();
					rentalid = rs.getInt("MAX(res_rentalid)");
					rentalid++;
					
					// Query for MAX custid and increment++
					String custmax = "SELECT MAX(res_custid) FROM reservation";
					pstat = conn.prepareStatement(custmax);
					rs = pstat.executeQuery();
					custid = rs.getInt("MAX(res_custid)");
					custid++;
					
					
					//sc.nextLine();	
					System.out.println("After CUSTMAX BEFORE UPDATE ");
					//pstat = conn.prepareStatement(sqlIn);
					String sqlIn = "insert into reservation(res_reservationid, res_rentalid, res_locationid, res_pickup, " + 
							" res_custid, res_vehicleid) values(?,?,?,?,?,?)";
					pstat = conn.prepareStatement(sqlIn);
					pstat.setInt(1,resid);
					pstat.setInt(2,rentalid);			
					pstat.setInt(3,lID);
					pstat.setString(4,pickupDate);
					pstat.setInt(5,custid);
					pstat.setInt(6,vid);
					
					pstat.executeUpdate();
					System.out.println("Reservation Made Successfully");
					System.out.println("Reservation ID: " + resid);
					System.out.println("Pickup date:" + pickupDate);
					System.out.println("At location " + lID);
				}
				catch(SQLException e) {}
				break;

			//  CASE 4 Print location to display and wait for input.
			// Is there a different way to display and get input, through sql?
			// Then query to print all vehicles from that location.	
			case 4:
				
				//int lID;
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
					String sql = "SELECT v_vehicleid,v_year,v_make, v_model,v_price,res_locationid FROM vehicle,reservation WHERE res_locationid = " + lID +
						" AND res_vehicleid = v_vehicleid";
					pstat = conn.prepareStatement(sql);
					ResultSet rs = pstat.executeQuery();
					
					System.out.println("before while loop");
					
					while(rs.next()) {
						System.out.print("\nid:" + rs.getInt("v_vehicleid") + " " + rs.getString("v_year") + " " + rs.getString("v_make") + 
								" " + rs.getString("v_model") + " $" + rs.getString("v_price") + " at location id " + rs.getInt("res_locationid"));						
					}
					System.out.println();
					System.out.println("Select vehicle id to begin reservation or enter -1 to go back");

					int vid = sc.nextInt();
					sc.nextLine();
					if(vid == -1)
						break;

					System.out.println("What day would you like to pick up(mm-dd)? " );
					String pickupDate = sc.nextLine();
					
					System.out.println("How many days would you like to rent for? ");
					int returnDate = sc.nextInt();
					sc.nextLine();
					
				
										
					// Variables to store max
					int resid,rentalid,custid;
					
					
					// Query for reservationid max and increment++
					String resmax = "SELECT MAX(res_reservationid) FROM reservation";
					pstat = conn.prepareStatement(resmax);
					rs = pstat.executeQuery();
					resid = rs.getInt("MAX(res_reservationid)");
					resid++;
					
					
					
					// Query for MAX rentalid and increment++
					String rentmax = "SELECT MAX(res_rentalid) FROM reservation";
					pstat = conn.prepareStatement(rentmax);
					rs = pstat.executeQuery();
					rentalid = rs.getInt("MAX(res_rentalid)");
					rentalid++;
					
					// Query for MAX custid and increment++
					String custmax = "SELECT MAX(res_custid) FROM reservation";
					pstat = conn.prepareStatement(custmax);
					rs = pstat.executeQuery();
					custid = rs.getInt("MAX(res_custid)");
					custid++;
					
					
					
					String sqlIn = "insert into reservation(res_reservationid, res_rentalid, res_locationid, res_pickup, " + 
							" res_custid, res_vehicleid) values(?,?,?,?,?,?)";
					pstat = conn.prepareStatement(sqlIn);
					pstat.setInt(1,resid);
					pstat.setInt(2,rentalid);			
					pstat.setInt(3,lID);
					pstat.setString(4,pickupDate);
					pstat.setInt(5,custid);
					pstat.setInt(6,vid);
					
					pstat.executeUpdate();
					System.out.println("Reservation Made Successfully");
					System.out.println("Reservation ID: " + resid);
					System.out.println("Pickup date:" + pickupDate);
					System.out.println("At location " + lID);

					
					
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
	public static void cancelRes(Connection conn, PreparedStatement pstat) {
		sc.nextLine();
		try {
			System.out.println("Please Enter Your Name " );
			String name = sc.nextLine();
			String sql = "SELECT c_name, res_pickup, l_address, l_state  FROM customer,reservation,location" +
					" WHERE c_name = '" + name +"' AND c_custid = res_custid AND res_locationid = l_locationid";
			
		
			pstat = conn.prepareStatement(sql);
			
			System.out.println("Before while");
			ResultSet rs = pstat.executeQuery();
			System.out.println("After execute query");
			while(rs.next()) {
				System.out.print(rs.getString("c_name") + rs.getString("res_pickup") + rs.getString("l_address") + rs.getString("l_state"));
				System.out.println();
			}
				System.out.println("Would You Like To Cancel This Reservation? y/n");
				String cancel = sc.nextLine();
				if(cancel == "y") {
					
					String sql1 = "DELETE FROM reservation WHERE res_reservationid = ?";
					pstat = conn.prepareStatement(sql1);
					pstat.setInt(1, 102);
					pstat.executeUpdate();
					System.out.println("Reservation Canceled.");
				}
				else
					subMenu(conn,pstat);
			
		}
			catch(SQLException e) {}
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
			sc.nextLine();
			try {
				System.out.println("Please Enter Your Name " );
				String name = sc.nextLine();
				
				String sql = "SELECT c_name, res_pickup, b_daysrented, (v_price * b_daysrented) AS total  FROM customer,reservation,billing,vehicle " +
						" WHERE c_name = '" + name + "' AND b_custid = c_custid AND res_custid = c_custid GROUP BY c_name";
				
				pstat = conn.prepareStatement(sql);
				
				ResultSet rs = pstat.executeQuery();
				System.out.println("Before While");
				while(rs.next()) {
					System.out.println("Name: " + rs.getString("c_name") + "  Reservation Date: " + rs.getString("res_pickup") +  "  Days Rented: "+ 
							rs.getString("b_daysrented") + "  Total Due: " + rs.getDouble("total"));
					}
				//String sql1 = "UPDATE vehicle " + "SET v_avail = 'Yes' WHERE ...";
			}
			catch(SQLException e) {}
		}
}