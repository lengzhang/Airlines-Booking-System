/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class AirBooking{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public AirBooking(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + AirBooking.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking esql = null;
		
		try{
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new AirBooking (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Passenger");
				System.out.println("2. Book Flight");
				System.out.println("3. Review Flight");
				System.out.println("4. Insert or Update Flight");
				System.out.println("5. List Flights From Origin to Destination");
				System.out.println("6. List Most Popular Destinations");
				System.out.println("7. List Highest Rated Destinations");
				System.out.println("8. List Flights to Destination in order of Duration");
				System.out.println("9. Find Number of Available Seats on a given Flight");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPassenger(esql); break;
					case 2: BookFlight(esql); break;
					case 3: TakeCustomerReview(esql); break;
					case 4: InsertOrUpdateRouteForAirline(esql); break;
					case 5: ListAvailableFlightsBetweenOriginAndDestination(esql); break;
					case 6: ListMostPopularDestinations(esql); break;
					case 7: ListHighestRatedRoutes(esql); break;
					case 8: ListFlightFromOriginToDestinationInOrderOfDuration(esql); break;
					case 9: FindNumberOfAvailableSeatsForFlight(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddPassenger(AirBooking esql){//1
		//Add a new passenger to the database
		int pID = 0;
		String passNum = "";
		String fullName = "";
		String bdate = "";
		String country = "";

		int state = 0;
		do {
			// Get input for Passport Number
			if (state == 0) {
				// Allow user input 3 times
				int counter = 3;
				String input;
				// Create Index for selecting passNum
				try {
					esql.executeUpdate("CREATE INDEX passNum_select ON Passenger USING BTREE (passNum);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				while (counter > 0) {
					System.out.print("\nPlease enter your Passport Number: ");
					try {
						input = in.readLine();
						passNum = input.toUpperCase();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (passNum.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (passNum.length() != 10) {
						System.out.printf("\n\tInvalid Passport Number\n");
						counter--;
						continue;
					}
					// Check passNum exist
					try {
						String query = "";
						query += "SELECT * FROM Passenger WHERE passNum = \'";
						query += input + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							counter--;
							continue;
						}
						else {
							break;
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				// Drop Index for passNum_select
				try {
				esql.executeUpdate("DROP INDEX passNum_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				if (counter > 0) {
					state = 1;
				}
				else {
					state = -1;
				}
			}
			// Get input for First Name
			else if (state == 1) {
				// Allow user input 3 times
				int counter = 3;
				String input;
				while (counter > 0) {
					System.out.print("\nPlease enter your First Name: ");
					try {
						input = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check input for First Name is valid
					int valid = 1;
					for (int i = input.length(); --i >= 0;) {
						if (Character.isDigit(input.charAt(i))) {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					// Store inptu to fullname
					fullName = input.substring(0, 1).toUpperCase() + input.substring(1, input.length()).toLowerCase() + " ";
					break;	
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
			}
			// Get input for Last Name
			else if (state == 2) {
				// Allow user input 3 times
				int counter = 3;
				String input;
				while (counter > 0) {
					System.out.print("\nPlease enter your Last Name: ");
					try {
						input = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check input for First Name is valid
					int valid = 1;
					for (int i = input.length(); --i >= 0;) {
						if (Character.isDigit(input.charAt(i))) {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					// Store inptu to fullname
					fullName += input.substring(0, 1).toUpperCase() + input.substring(1, input.length()).toLowerCase();	
					break;
				}
				if (counter > 0) {
					state = 3;
				}
				else {
					state = -1;
				}
				// Check Full Name
				if (fullName.length() > 24) {
					System.out.printf("\n\tYour full name is too long, please enter again.\n");
					fullName = "";
					state = 1;
				}
			}
			// Get input for Date of Birth
			else if (state == 3) {
				// Allow user input 3 times
				int counter = 3;
				String input;
				while (counter > 0) {
					System.out.print("\nPlease enter your Date of Birth (YYYY-MM-DD): ");
					try {
						input = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (input.length() != 10) {
						System.out.printf("\n\tInvalid format of Data of Birth\n");
						counter--;
						continue;
					}
					// Check input for Data of Birth is valid
					int valid = 1;
					for (int i = input.length(); --i >= 0;) {
						if (!Character.isDigit(input.charAt(i)) && input.charAt(i) != '-') {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					// Store inptu to db
					bdate = input;
					break;
				}
				state = 4;
			}
			// Get input for Country
			else if (state == 4) {
				// Allow user input 3 times
				int counter = 3;
				String input;
				while (counter > 0) {
					System.out.print("\nPlease enter your Country: ");
					try {
						input = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (input.length() > 24) {
						System.out.printf("\n\tYour input is too long\n");
						counter--;
						continue;
					}
					// Check input for Data of Birth is valid
					int valid = 1;
					for (int i = input.length(); --i >= 0;) {
						if (Character.isDigit(input.charAt(i)) && input.charAt(i) != ' ') {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					// Store inptu to db
					country = input;
					break;
				}
				state = 5;
			}
			// Look for the available pid
			else if (state == 5) {
				// Create Index for pID
				try {
					esql.executeUpdate("CREATE INDEX pID_select ON Passenger USING BTREE (pID);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "";
					query += "SELECT pID FROM Passenger;";
					List<List<String>> result =  esql.executeQueryAndReturnResult(query);
					int available_pID = 0;
					for (List<String> row : result) {
						for (String content : row) {
							if (available_pID != Integer.parseInt(content)) {
								break;
							}
							available_pID++;
						}
					}
					pID = available_pID;
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// DROP Index for pID_select
				try {
					esql.executeUpdate("DROP INDEX pID_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 6;
			}
			// Store into the database
			else if (state == 6) {
				try {
					String query = "";
					query = "INSERT INTO passenger (pid, passnum, fullname, bdate, country) VALUES (";
					query += pID + ", \'";
					query += passNum + "\', \'";
					query += fullName + "\', \'";
					query += bdate + "\', \'";
					query += country + "\');";
					esql.executeUpdate(query);
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}while (true);
	}
	
	public static void BookFlight(AirBooking esql){//2
		//Book Flight for an existing customer
		String passNum = "";
		String pID = "";
		String origin = "";
		String destination = "";
		String flightNum = "";
		String departure = "";
		String bookref = "";
		String input;
		int state = 0;
		while (true) {
			// Get input for Passport Number
			if (state == 0) {
				// Create Index for selecting passNum
				try {
					esql.executeUpdate("CREATE INDEX passNum_select ON Passenger USING BTREE (passNum);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				int counter = 3;
				while (counter > 0) {
					System.out.print("\nPlease enter your Passport Number: ");
					try {
						input = in.readLine();
						passNum = input.toUpperCase();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (passNum.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (passNum.length() != 10) {
						System.out.printf("\n\tInvalid Passport Number\n");
						counter--;
						continue;
					}
					// Check passNum exist
					try {
						String query = "";
						query += "SELECT * FROM Passenger WHERE passNum = \'";
						query += passNum + "\';";
						List<List<String>> result = esql.executeQueryAndReturnResult(query);
						// Passport Number exist
						if (result.size() > 0) {
							pID = (result.get(0)).get(0);
							break;
						}
						// Passport Number does not exist
						else {
							System.out.print("\n\tPassport Number does not exist, please add this Passenger first.\n\n");
							counter = 0;
							break;
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				// Drop Index for passNum_select
				try {
				esql.executeUpdate("DROP INDEX passNum_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				if (counter > 0) {
					state = 1;
				}
				else {
					state = -1;
				}
			}
			// Get input for origin
			else if (state == 1) {
				// Create Index for origin
				try {
					esql.executeUpdate("CREATE INDEX origin_select ON Flight USING BTREE (origin);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
					List<List<String>> origin_list =  esql.executeQueryAndReturnResult(query);
					String t = "";
					System.out.printf("\n---------- List of Origins ----------\n");
					for (List<String> row : origin_list) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-------------------------------------\n");
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// Get input for origins
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter your origin: ");
					try {
						origin = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (origin.length() == 0) {
						counter = 0;
						break;
					}
					// Check the origin is in the list or not
					try {
						String query = "SELECT DISTINCT origin FROM Flight WHERE origin = \'";
						query += origin + "\';";
						int rows = esql.executeQuery(query);
						// origin exist
						if (rows > 0) {
							break;
						}
						// origin does not exist
						else {
							System.out.printf("\n\tYour input does not in the list of origin.\n");
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
					
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
				// DROP Index for origin
				try {
					esql.executeUpdate("DROP INDEX origin_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
			// Get input for destination
			else if (state == 2) {
				// Create Index for destination
				try {
					esql.executeUpdate("CREATE INDEX destination_select ON Flight USING BTREE (destination);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
					query += origin + "\' ORDER BY destination;";
					List<List<String>> destination_list =  esql.executeQueryAndReturnResult(query);
					String t = "";
					System.out.printf("\n---------- List of Destination ----------\n");
					for (List<String> row : destination_list) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-----------------------------------------\n");
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// Get input for destination
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter your destination: ");
					try {
						destination = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (destination.length() == 0) {
						counter = 0;
						break;
					}
					// Check the destination is in the list or not
					try {
						String query = "SELECT DISTINCT flightNum FROM Flight WHERE ";
						query += "origin = \'" + origin + "\' and ";
						query += "destination = \'" + destination + "\';";
						List<List<String>> result = esql.executeQueryAndReturnResult(query);
						// origin exist flightNum
						if (result.size() > 0) {
							flightNum = result.get(0).get(0);
							break;
						}
						// origin does not exist
						else {
							System.out.printf("\n\tYour input does not in the list of destination.\n");
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
				if (counter > 0) {
					state = 3;
				}
				else {
					state = -1;
				}
				// DROP Index for origin
				try {
					esql.executeUpdate("DROP INDEX destination_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 3;
			}
			// Get input for departure date
			else if (state == 3) {
				// Allow user input 3 times
				int counter = 3;
				while (counter > 0) {
					System.out.print("\nPlease enter your Date of Departure (YYYY-MM-DD): ");
					try {
						input = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (input.length() != 10) {
						System.out.printf("\n\tInvalid format of Data of Departure\n");
						counter--;
						continue;
					}
					// Check input for Data of Departure is valid
					int valid = 1;
					for (int i = input.length(); --i >= 0;) {
						if (!Character.isDigit(input.charAt(i)) && input.charAt(i) != '-') {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					// Store inptu to departure
					departure = input;
					break;
				}
				if (counter > 0) {
					state = 4;
				}
				else {
					state = -1;
				}
			}
			// Get bookref
			else if (state == 4) {
				// Create Index for bookref
				try {
					esql.executeUpdate("CREATE INDEX bookRef_select ON Booking USING BTREE (bookRef);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}

				String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				while (true) {
					bookref = "";
					Random random = new Random();
					for (int i = 0; i < 10; ++i) {
						bookref += letters.charAt(random.nextInt(26));
					}
					try {
						String query = "SELECT bookRef FROM Booking WHERE bookRef = \'";
						query += bookref + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							continue;
						} else {
							state = 5;
							break;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						state = -1;
						break;
					}
				}
				// DROP Index for bookref
				try {
					esql.executeUpdate("DROP INDEX bookRef_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
			else if (state == 5) {
				try {
					String query = "";
					query = "INSERT INTO Booking (bookRef, departure, flightNum, pID) VALUES (\'";
					query += bookref + "\', \'";
					query += departure + "\', \'";
					query += flightNum + "\', \'";
					query += pID + "\');";
					esql.executeUpdate(query);
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}
	}
	
	public static void TakeCustomerReview(AirBooking esql){//3
		//Insert customer review into the ratings table
		int rID = 0;
		String pID = "", flightNum = "", score = "", comment = "";
		int state = 0;
		while (true) {
			// Get input for passenger ID
			if (state == 0) {
				// Create Index for selecting passNum
				try {
					esql.executeUpdate("CREATE INDEX passNum_select ON Passenger USING BTREE (passNum);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				int counter = 3;
				while (counter > 0) {
					System.out.print("\nPlease enter passenger's Passport Number: ");
					String input;
					try {
						input = in.readLine().toUpperCase();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (input.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (input.length() != 10) {
						System.out.printf("\n\tInvalid Passport Number\n");
						counter--;
						continue;
					}
					// Check passNum exist
					try {
						String query = "";
						query += "SELECT * FROM Passenger WHERE passNum = \'";
						query += input + "\';";
						List<List<String>> result = esql.executeQueryAndReturnResult(query);
						// Passport Number exist
						if (result.size() > 0) {
							pID = (result.get(0)).get(0);
							break;
						}
						// Passport Number does not exist
						else {
							System.out.print("\n\tPassport Number does not exist, please add this Passenger first.\n");
							counter = 0;
							break;
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				// Drop Index for passNum_select
				try {
					esql.executeUpdate("DROP INDEX passNum_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				if (counter > 0) {
					state = 1;
				}
				else {
					state = -1;
				}
			}
			// Check did passanger book any flight.
			else if (state == 1) {
				// Create Index for selecting pID
				try {
					esql.executeUpdate("CREATE INDEX pID_select ON Booking USING BTREE (pID);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				// Check pID in Booking
				try {
					String query = "";
					query += "SELECT pID FROM Booking WHERE pID = \'";
					query += pID + "\';";
					int rows = esql.executeQuery(query);
					// Passport Number exist
					if (rows > 0) {
						state = 2;
					}
					// Passport Number does not exist
					else {
						System.out.print("\n\tThis passenger did not book any flights.\n");
						state = 0;
					}
				}catch(Exception e){
					System.err.println (e.getMessage());
					state = -1;
				}
				try {
					esql.executeUpdate("DROP INDEX pID_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
			// Get input for flightNum
			else if (state == 2) {
				// Create Index for selecting passNum
				try {
					esql.executeUpdate("CREATE INDEX pID_select ON Booking USING BTREE (pID);");
					esql.executeUpdate("CREATE INDEX flightNum_select ON Booking USING BTREE (flightNum);");
					esql.executeUpdate("CREATE INDEX pID_flightNum_select ON Booking USING BTREE (pID, flightNum);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "SELECT flightNum FROM Booking WHERE pID = ";
					query += pID + " and flightnum not in (select flightnum from ratings) ORDER BY flightNum;";
					List<List<String>> flight_list =  esql.executeQueryAndReturnResult(query);
					if (flight_list.size() > 0) {
						System.out.printf("\n---------- Passenger's Flight ----------\n");
						for (List<String> row : flight_list) {
							for (String content : row) {
								System.out.printf("\t%s\n", content);
							}
						}
						System.out.printf("----------------------------------------\n");
					}
					else {
						System.out.printf("\n\tNo flight can be rated.\n\n");
						break;
					}
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the flightNum you want to comment: ");
					try {
						flightNum = in.readLine().toUpperCase();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (flightNum.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT flightNum FROM Booking WHERE pID = ";
						query += pID + " and flightNum = \'";
						query += flightNum + "\' ";
						query += "and flightnum not in (select flightnum from ratings where pid = ";
						query += pID + ");";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						} else {
							System.out.printf("\n\tThis flight is not in the list.\n");
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
				try {
					esql.executeUpdate("DROP INDEX pID_select;");
					esql.executeUpdate("DROP INDEX flightNum_select;");
					esql.executeUpdate("DROP INDEX pID_flightNum_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				if (counter > 0) {
					state = 3;
				}
				else {
					state = -1;
				}
			}
			// Get input for the score
			else if (state == 3) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the score in the range (0,5): ");
					try {
						score = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (score.length() == 0) {
						counter = 0;
						break;
					}
					if (Integer.parseInt(score) > 5 || Integer.parseInt(score) < 0) {
						System.out.printf("\n\tYour input is out of range.\n");
						counter--;
						continue;
					}
					else {
						break;
					}
				}
				if (counter > 0) {
					state = 4;
				}
				else {
					state = -1;
				}
			}
			// Get input for comment
			else if (state == 4) {
				comment = "";
				System.out.printf("\nPlease enter your comment, end by <ENTER>: ");
				try {
					comment = in.readLine();
					state = 5;
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
					state = -1;
				}
			}
			// Get available rID
			else if (state == 5) {
				// Create Index for rID
				try {
					esql.executeUpdate("CREATE INDEX rID_select ON Ratings USING BTREE (rID);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "";
					query += "SELECT rID FROM Ratings ORDER BY rID;";
					List<List<String>> result =  esql.executeQueryAndReturnResult(query);
					int available_rID = 0;
					for (List<String> row : result) {
						for (String content : row) {
							if (available_rID != Integer.parseInt(content)) {
								break;
							}
							available_rID++;
						}
					}
					rID = available_rID;
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// DROP Index for pID_select
				try {
					esql.executeUpdate("DROP INDEX rID_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 6;
			}
			else if (state == 6) {
				try {
					String query = "";
					query = "INSERT INTO Ratings (rID, pID, flightNum, score, comment) VALUES (";
					query += rID + ", ";
					query += pID + ", \'";
					query += flightNum + "\', ";
					query += score + ", \'";
					query += comment + "\');";
					esql.executeUpdate(query);
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}
	}
	
	public static void InsertOrUpdateRouteForAirline(AirBooking esql){//4
		//Insert a new route for the airline
		int state = 0, exist = 0;
		String airId = "", name = "", founded = "", country = "", hub = "";
		while (true) {
			// name
			if (state == 0) {
				try {
					esql.executeUpdate("CREATE INDEX name_select ON Airline USING BTREE (name);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the name of the airline: ");
					try {
						name = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty
					if (name.length() == 0) {
						counter = 0;
						break;
					}
					if (name.length() > 24) {
						System.out.printf("\n\tInput invalid\n");
						counter--;
						continue;
					}
					try {
						String query = "SELECT name FROM Airline WHERE name = \'";
						query += name + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							System.out.printf("\nThis airline is already in list, do you want to updata it?(Y/N) ");
							String input = in.readLine().toUpperCase();
							if (input.charAt(0) == 'Y') {
								exist = 1;
								break;
							}
							else {
								counter--;
							}
						}
						else {
							break;
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
					}
				}
				// DROP Index for name_select
				try {
					esql.executeUpdate("DROP INDEX name_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				if (counter > 0) {
					state = 1;
				} else {
					state = -1;
				}
			}
			// founded Year the airline was founded, always greater than 1900
			else if (state == 1) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the Year the airline was founded: ");
					try {
						founded = in.readLine();
						if (founded.length() == 0) {
							counter = 0;
							break;
						}
						if (founded.length() > 4) {
							System.out.printf("\n\tInput invalid\n");
							counter--;
							continue;
						}
						int y = Integer.parseInt(founded);
						if (y < 1900) {
							System.out.printf("\n\tInput invalid\n");
							counter--;
							continue;
						}
						else {
							break;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.out.printf("\n\tInput invalid\n");
						counter--;
					}
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
			}
			// country
			else if (state == 2) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the Country which the airline operates: ");
					try {
						country = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					if (country.length() == 0) {
						counter = 0;
						break;
					}
					else if (country.length() > 24) {
						System.out.printf("\n\tInput invalid\n");
						counter--;
						continue;
					}
					else {
						break;
					}
				}
				if (counter > 0) {
					state = 3;
				}
				else {
					state = -1;
				}
			}
			// hub
			else if (state == 3) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the Hub airport of the airline: ");
					try {
						hub = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					if (hub.length() == 0) {
						counter = 0;
						break;
					}
					else if (hub.length() > 24) {
						System.out.printf("\n\tInput invalid\n");
						counter--;
						continue;
					}
					else {
						break;
					}
				}
				if (counter > 0) {
					if (exist == 1) {
						state = 4;
					}
					else {
						state = 5;
					}
				}
				else {
					state = -1;
				}
			}
			// Get airId, name exist
			else if (state == 4) {
				try {
					esql.executeUpdate("CREATE INDEX airId_select ON Airline USING BTREE (airId);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}

				try {
					String query = "";
					query += "SELECT airId FROM Airline WHERE name = \'";
					query += name + "\';";
					List<List<String>> result =  esql.executeQueryAndReturnResult(query);
					airId = result.get(0).get(0);
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				
				try {
					esql.executeUpdate("DROP INDEX airId_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 6;
			}
			// Get airId, name does not exist
			else if (state == 5) {
				// Create Index for airId
				try {
					esql.executeUpdate("CREATE INDEX airId_select ON Airline USING BTREE (airId);");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				try {
					String query = "";
					query += "SELECT airId FROM Airline ORDER BY airId;";
					List<List<String>> result =  esql.executeQueryAndReturnResult(query);
					int available_airId = 0;
					for (List<String> row : result) {
						for (String content : row) {
							if (available_airId != Integer.parseInt(content)) {
								break;
							}
							available_airId++;
						}
					}
					airId = "" + available_airId;
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// DROP Index for airId_select
				try {
					esql.executeUpdate("DROP INDEX airId_select;");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 7;
			}
			else if (state == 6) {
				try {
					String query = "";
					query = "UPDATE Airline ";
					query += "SET ";
					query += "founded = " + founded + ", ";
					query += "country = \'" + country + "\', ";
					query += "hub = \'" + hub + "\' ";
					query += "WHERE ";
					query += "name = \'" + name + "\' and ";
					query += "airId = " + airId + ";";
					esql.executeUpdate(query);
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else if (state == 7) {
				try {
					String query = "";
					query = "INSERT INTO Airline (airId, name, founded, country, hub) VALUES (";
					query += airId + ", \'";
					query += name + "\', ";
					query += founded + ", \'";
					query += country + "\', \'";
					query += hub + "\');";
					esql.executeUpdate(query);
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}
	}
	
	public static void ListAvailableFlightsBetweenOriginAndDestination(AirBooking esql) throws Exception{//5
		//List all flights between origin and distination (i.e. flightNum,origin,destination,plane,duration) 
		try {
			esql.executeUpdate("CREATE INDEX origin_select ON Flight USING BTREE (origin);");
			esql.executeUpdate("CREATE INDEX destination_select ON Flight USING BTREE (destination);");
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		String origin = "", destination = "";
		int state = 0;
		while (true) {
			if (state == 0) {
				try {
					String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					String t = "";
					System.out.printf("\n---------- List of Origins ----------\n");
					for (List<String> row : result) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-------------------------------------\n");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 1;
			}
			else if (state == 1) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the origin city: ");
					try {
						origin = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
					if (origin.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT origin FROM Flight WHERE origin = \'";
						query += origin + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						}
						else {
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
			}
			else if (state == 2) {
				try {
					String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
					query += origin + "\' ORDER BY destination;";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					System.out.printf("\n---------- List of destination ----------\n");
					String t = "";
					for (List<String> row : result) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-----------------------------------------\n");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 3;
			}
			else if (state == 3) {
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the destination city: ");
					try {
						destination = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
					if (destination.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT destination FROM Flight WHERE destination = \'";
						query += destination + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						}
						else {
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				if (counter > 0) {
					state = 4;
				}
				else {
					state = -1;
				}
			}
			else if (state == 4) {
				try {
					String query = "SELECT DISTINCT flightNum,origin,destination,plane,duration FROM Flight WHERE origin = \'";
					query += origin + "\' and destination = \'";
					query += destination + "\';";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					//AvailableFlightsBetweenOriginAndDestination
					String temp = "---------- Flights from " + origin + " to " + destination + "----------";
					System.out.printf("\n%s", temp);
					for (List<String> row : result) {
						System.out.printf("\n\tflightNum   =\t%s\n", row.get(0));
						System.out.printf("\torigin      =\t%s\n", row.get(1));
						System.out.printf("\tdestination =\t%s\n", row.get(2));
						System.out.printf("\tplane       =\t%s\n", row.get(3));
						System.out.printf("\tduration    =\t%s\n", row.get(4));
					}
					String t = "";
					for (int i = 0; i < temp.length(); ++i) {
						t += "-";
					}
					System.out.printf("%s\n\n", t);
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}
		try {
			esql.executeUpdate("DROP INDEX origin_select;");
			esql.executeUpdate("DROP INDEX destination_select;");
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListMostPopularDestinations(AirBooking esql){//6
		//Print the k most popular destinations based on the number of flights offered to them (i.e. destination, choices)
		int counter = 3;
		int k_most = 0;
		while (counter > 0) {
			System.out.printf("\nPlease enter the number of Destinations you want to display: ");
			try {
				String input = in.readLine();
				if (input.length() == 0) {
					return;
				}
				k_most = Integer.parseInt(input);
				if (k_most > 0) {
					break;
				}
				else if (k_most < 0) {
					System.out.printf("\n\tYour input is invalid.\n");
					counter--;
					continue;
				}
				else {
					return;
				}
			} catch (Exception e) {
				//TODO: handle exception
				System.out.printf("\n\tYour input is invalid.\n");
				counter--;
				continue;
			}
		}
		if (counter == 0) {
			return;
		}
		// Start Timer
		long startTime = System.currentTimeMillis();
		try {
			String query = "SELECT destination, Count (destination) ";
			query += "FROM Flight ";
			query += "GROUP BY destination ";
			query += "ORDER BY COUNT (destination) ";
			query += "desc limit ";
			query += k_most + ";";
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			System.out.printf("   ---------------------------------------------\n");
			System.out.printf("        Destination     | The Number of Flights \n");
			System.out.printf("   ---------------------+-----------------------\n");
			for (List<String> row : result) {
				System.out.printf("    %s    |          %s\n", row.get(0), row.get(1));
			}
			System.out.printf("   ---------------------------------------------\n");
			System.out.printf("    (%d rows)\n", result.size());
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		// End Timer		
		long endTime = System.currentTimeMillis();
		System.out.printf("\n\tRuntime: %d ms\n\n", (endTime - startTime));
	}
	
	public static void ListHighestRatedRoutes(AirBooking esql){//7
		//List the k highest rated Routes (i.e. Airline Name, flightNum, Avg_Score)
		int counter = 3;
		int k_most = 0;
		while (counter > 0) {
			System.out.printf("\nPlease enter the number of Routes you want to display: ");
			try {
				String input = in.readLine();
				if (input.length() == 0) {
					return;
				}
				k_most = Integer.parseInt(input);
				if (k_most > 0) {
					break;
				}
				else if (k_most < 0) {
					System.out.printf("\n\tYour input is invalid.\n");
					counter--;
					continue;
				}
				else {
					return;
				}
			} catch (Exception e) {
				//TODO: handle exception
				System.out.printf("\n\tYour input is invalid.\n");
				counter--;
				continue;
			}
		}
		if (counter == 0) {
			return;
		}
		// Start Timer
		long startTime = System.currentTimeMillis();
		try {
			String query = "SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, avg(R.score) ";
			query += "FROM Flight F, Ratings R,Airline A ";
			query += "WHERE F.flightNum = R.flightNum AND A.airId=F.airId ";
			query += "GROUP BY A.name, F.flightNum, F.origin, F.destination, F.plane ";
			query += "ORDER BY avg(R.score) ";
			query += "desc limit ";
			query += k_most + ";";
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			System.out.printf("\n        Airline name       | Flight Number |      Origin      |    Destination    |    Plane Type    |  Score\n");
			System.out.printf(" --------------------------+---------------+------------------+-------------------+------------------+---------\n");
			for (List<String> row : result) {
				float rate = Float.parseFloat(row.get(5));
				System.out.printf("  %s |    %s   | %s | %s  | %s | %.4f\n", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), rate);
			}
			System.out.printf(" --------------------------------------------------------------------------------------------------------------\n");
			System.out.printf("    (%d rows)\n", result.size());
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		// End Timer		
		long endTime = System.currentTimeMillis();
		System.out.printf("\n    Runtime: %d ms\n\n", (endTime - startTime));
	}
	
	public static void ListFlightFromOriginToDestinationInOrderOfDuration(AirBooking esql){//8
		//List flight to destination in order of duration (i.e. Airline name, flightNum, origin, destination, duration, plane)
		int counter = 3;
		int k_most = 0;
		while (counter > 0) {
			System.out.printf("\nPlease enter the number of Routes you want to display: ");
			try {
				String input = in.readLine();
				if (input.length() == 0) {
					return;
				}
				k_most = Integer.parseInt(input);
				if (k_most > 0) {
					break;
				}
				else if (k_most < 0) {
					System.out.printf("\n\tYour input is invalid.\n");
					counter--;
					continue;
				}
				else {
					return;
				}
			} catch (Exception e) {
				//TODO: handle exception
				System.out.printf("\n\tYour input is invalid.\n");
				counter--;
				continue;
			}
		}
		try {
			esql.executeUpdate("CREATE INDEX origin_select ON Flight USING BTREE (origin);");
			esql.executeUpdate("CREATE INDEX destination_select ON Flight USING BTREE (destination);");
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		String origin = "", destination = "";
		int state = 0;
		while (true) {
			if (state == 0) {
				try {
					String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					String t = "";
					System.out.printf("\n---------- List of Origins ----------\n");
					for (List<String> row : result) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-------------------------------------\n");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 1;
			}
			else if (state == 1) {
				counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the origin city: ");
					try {
						origin = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
					if (origin.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT origin FROM Flight WHERE origin = \'";
						query += origin + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						}
						else {
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
			}
			else if (state == 2) {
				try {
					String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
					query += origin + "\' ORDER BY destination;";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					System.out.printf("\n---------- List of destination ----------\n");
					String t = "";
					for (List<String> row : result) {
						for (String content : row) {
							if (t.length() == 0 || content.charAt(0) != t.charAt(0)) {
								System.out.printf("\t%c:\n", content.charAt(0));
							}
							System.out.printf("\t\t%s\n", content);
							t = content;
						}
					}
					System.out.printf("-----------------------------------------\n");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = 3;
			}
			else if (state == 3) {
				counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the destination city: ");
					try {
						destination = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
					if (destination.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT destination FROM Flight WHERE destination = \'";
						query += destination + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						}
						else {
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
						counter = 0;
						break;
					}
				}
				if (counter > 0) {
					state = 4;
				}
				else {
					state = -1;
				}
			}
			else if (state == 4) {
				// Start Timer
				long startTime = System.currentTimeMillis();
				try {
					String query = "SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, F.duration ";
					query += "FROM Flight F, Airline A ";
					query += "WHERE ";
					query += "F.airId = A.airId and ";
					query += "F.origin = \'"+ origin + "\' and ";
					query += "F.destination = \'"+ destination + "\' ";
					query += "ORDER BY F.duration ";
					query += "DESC LIMIT " + k_most + ";";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					//AvailableFlightsBetweenOriginAndDestination
					System.out.printf("\n          Flights from " + origin + " to " + destination + " in order of Duration\n");
					System.out.printf("--------------------------------------------------------------------------\n");
					System.out.printf("       Airline Name       |  Flight Number  |    Plane Type    | Duration\n");
					System.out.printf("--------------------------+-----------------+------------------+----------\n");
					for (List<String> row : result) {
						System.out.printf(" %s |  %s       | %s | %s\n", row.get(0), row.get(1), row.get(4), row.get(5));
					}
					System.out.printf("--------------------------------------------------------------------------\n");
					System.out.printf("    (%d rows)\n", result.size());
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				// End Timer		
				long endTime = System.currentTimeMillis();
				System.out.printf("\n    Runtime: %d ms\n\n", (endTime - startTime));
				state = -1;
			}
			else {
				break;
			}
		}
		try {
			esql.executeUpdate("DROP INDEX origin_select;");
			esql.executeUpdate("DROP INDEX destination_select;");
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
	}
	
	public static void FindNumberOfAvailableSeatsForFlight(AirBooking esql){//9
		int state = 0;
		String flightNum = "", departure = "";
		while (true) {
			if (state == 0) {
				try {
					String query = "SELECT DISTINCT flightNum ";
					query += "FROM Booking ";
					query += "ORDER BY flightNum;";
					List<List<String>> flight_list =  esql.executeQueryAndReturnResult(query);
					if (flight_list.size() > 0) {
						System.out.printf("\n---- Booked Flights ----\n");
						for (List<String> row : flight_list) {
							for (String content : row) {
								System.out.printf("\t%s\n", content);
							}
						}
						System.out.printf("------------------------\n");
					}
					else {
						System.out.printf("\n\tNo flight is booked.\n\n");
						break;
					}
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				int counter = 3;
				while (counter > 0) {
					System.out.printf("\nPlease enter the flightNum: ");
					try {
						flightNum = in.readLine().toUpperCase();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (flightNum.length() == 0) {
						counter = 0;
						break;
					}
					try {
						String query = "SELECT DISTINCT flightNum ";
						query += "FROM Booking ";
						query += "WHERE flightNum = \'" + flightNum + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						} else {
							System.out.printf("\n\tThis flight is not in the list.\n");
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
				if (counter > 0) {
					state = 1;
				}
				else {
					state = -1;
				}
			}
			else if (state == 1) {
				try {
					String query = "SELECT DISTINCT departure ";
					query += "FROM Booking ";
					query += "WHERE flightNum = \'" + flightNum + "\';";
					List<List<String>> departure_list =  esql.executeQueryAndReturnResult(query);
					if (departure_list.size() > 0) {
						System.out.printf("\n---- Date of Departure ----\n");
						for (List<String> row : departure_list) {
							for (String content : row) {
								System.out.printf("\t%s\n", content);
							}
						}
						System.out.printf("---------------------------\n");
					}
					else {
						System.out.printf("\n\tNo departure date.\n\n");
						break;
					}
				}catch(Exception e){
					System.err.println (e.getMessage());
				}
				// Allow user input 3 times
				int counter = 3;
				while (counter > 0) {
					System.out.print("\nPlease enter the Date of Departure (YYYY-MM-DD): ");
					try {
						departure = in.readLine();
					} catch (Exception e) {
						//TODO: handle exception
						counter = 0;
						break;
					}
					// Empty String
					if (departure.length() == 0) {
						counter = 0;
						break;
					}
					// Check invalid input
					if (departure.length() != 10) {
						System.out.printf("\n\tInvalid format of Data of Departure\n");
						counter--;
						continue;
					}
					// Check input for Data of Departure is valid
					int valid = 1;
					for (int i = departure.length(); --i >= 0;) {
						if (!Character.isDigit(departure.charAt(i)) && departure.charAt(i) != '-') {
							valid = 0;
							break;
						}
					}
					if (valid == 0) {
						System.out.printf("\n\tYour input is invalid\n");
						counter--;
						continue;
					}
					try {
						String query = "SELECT DISTINCT departure ";
						query += "FROM Booking ";
						query += "WHERE flightNum = \'" + flightNum + "\' and ";
						query += "departure = \'" + departure + "\';";
						int rows = esql.executeQuery(query);
						if (rows > 0) {
							break;
						}
						else {
							System.out.printf("\n\tDeparture Data does not exist.\n");
							counter--;
							continue;
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
					break;
				}
				if (counter > 0) {
					state = 2;
				}
				else {
					state = -1;
				}
			}
			else if (state == 2) {
				try {
					// Start Timer
					long startTime = System.currentTimeMillis();
					String query = "SELECT F.flightNum, F.origin, F.destination, B.departure, B.c, F.seats, (F.seats - B.c) ";
					query += "FROM Flight F, ( ";
					query += "SELECT flightNum, departure, count(flightNum) AS c ";
					query += "FROM Booking ";
					query += "WHERE flightNum = \'" + flightNum + "\' AND departure = \'" + departure + "\' ";
					query += "GROUP BY flightNum, departure ";
					query += ") AS B ";
					query += "WHERE F.flightNum = B.flightNum;";
					List<List<String>> result = esql.executeQueryAndReturnResult(query);
					System.out.printf("---------------------------------------------------------------------------------------------------------\n");
					System.out.printf("  Flight  |                  |                   |  Departure  | Booked | Total Number | Total Number of\n");
					System.out.printf("  Number  |      Origin      |    Destination    |    Date     | Seats  |   of Seats   | Available Seats\n");
					System.out.printf("---------------------------------------------------------------------------------------------------------\n");
					for (List<String> rows : result) {
						System.out.printf(" %s   %s    %s    %s     %s         %s             %s\n", rows.get(0), rows.get(1), rows.get(2), rows.get(3), rows.get(4), rows.get(5), rows.get(6));
					}
					System.out.printf("---------------------------------------------------------------------------------------------------------\n\n");
					
					System.out.printf("    (%d rows)\n", result.size());
					// End Timer		
					long endTime = System.currentTimeMillis();
					System.out.printf("\n    Runtime: %d ms\n\n", (endTime - startTime));
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
				state = -1;
			}
			else {
				break;
			}
		}
		
	}
	
}