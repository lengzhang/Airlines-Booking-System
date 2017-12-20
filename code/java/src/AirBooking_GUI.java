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

// GUI
import java.awt.Color;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.print.attribute.standard.Destination;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import javax.swing.JComboBox;

import java.awt.TextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import java.awt.Dimension;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class AirBooking_GUI{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public AirBooking_GUI(String dbname, String dbport, String user, String passwd) throws SQLException {
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
				"Usage: " + "java [-classpath <classpath>] " + AirBooking_GUI.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking_GUI esql = null;
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
			
			esql = new AirBooking_GUI (dbname, dbport, user, "");

			esql.AB_GUI(esql);
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	private JFrame mainFrame;
	private JLabel status_label;
	private JPanel mainPanel;
	private JPanel tablePanel;
	
	private JButton[] menu_buttons = new JButton[11];
	private JButton back_to_menu = new JButton("Back");
	private AirBooking_GUI my_esql = null;

	private List<String> indexList = new ArrayList();

	private void Create_Index(String create_query, String drop_query) {
		try {
			my_esql.executeUpdate(create_query);
			indexList.add(drop_query);
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
	}

	private void Drop_Index() {
		while (!indexList.isEmpty()) {
			try {
				my_esql.executeUpdate(indexList.get(0));
				indexList.remove(0);
			} catch (Exception e) {
				//TODO: handle exception
				System.err.println (e.getMessage());
			}
		}
	}

	private void system_end() {
		Drop_Index();
		try{
			if(my_esql != null) {
				System.out.print("Disconnecting from database...");
				my_esql.cleanup ();
				System.out.println("Done\n\nBye !");
			}//end if
			System.exit(0);
		}catch(Exception er){
			// ignored.
		}
	}

	private void AB_GUI(AirBooking_GUI esql){
		my_esql = esql;
		Main_Frame_Initial();
		Set_Menu_Plane();
		mainFrame.add(mainPanel);
		
		// Initializing back to menu button
		back_to_menu.setActionCommand("Back_to_Menu");
		back_to_menu.addActionListener(new ButtonClickListener());

		Main_Frame_Update(null, mainPanel);

		mainFrame.setVisible(true);

		Compoment_Initial();
	}

	private void Main_Frame_Initial() {
		// Main Fram initializes
		mainFrame = new JFrame("Air Booking");
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				system_end();
			}
		});

		// Initial status label
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jp.setBackground(Color.WHITE);
		status_label = new JLabel("Main Menu");
		jp.add(status_label);
		mainFrame.add(jp, BorderLayout.SOUTH);
	}

	private void Main_Frame_Update(JPanel old_panel, JPanel new_panel) {
		if (old_panel != null) {
			mainFrame.remove(old_panel);
		}
		mainFrame.add(new_panel, BorderLayout.CENTER);
		
		// Main Fram update
		mainFrame.revalidate();
		mainFrame.repaint();
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
	}

	private void Set_Menu_Plane() {
		mainPanel = new JPanel(new GridLayout(11,1));
		
		menu_buttons[0] = new JButton("1. Add Passenger");
		menu_buttons[0].setActionCommand("menu_1");
		menu_buttons[1] = new JButton("2. Book Flight");
		menu_buttons[1].setActionCommand("menu_2");
		menu_buttons[2] = new JButton("3. Review Flight");
		menu_buttons[2].setActionCommand("menu_3");
		menu_buttons[3] = new JButton("4. Insert or Update Flight");
		menu_buttons[3].setActionCommand("menu_4");
		menu_buttons[4] = new JButton("5. List Flights From Origin to Destination");
		menu_buttons[4].setActionCommand("menu_5");
		menu_buttons[5] = new JButton("6. List Most Popular Destinations");
		menu_buttons[5].setActionCommand("menu_6");
		menu_buttons[6] = new JButton("7. List Highest Rated Destinations");
		menu_buttons[6].setActionCommand("menu_7");
		menu_buttons[7] = new JButton("8. List Flights to Destination in order of Duration");
		menu_buttons[7].setActionCommand("menu_8");
		menu_buttons[8] = new JButton("9. Find Number of Available Seats on a given Flight");
		menu_buttons[8].setActionCommand("menu_9");
		menu_buttons[9] = new JButton("10. < EXIT >");
		menu_buttons[9].setActionCommand("menu_10");
		menu_buttons[10] = new JButton("Information");
		menu_buttons[10].setActionCommand("information");

		for (JButton t : menu_buttons) {
			t.addActionListener(new ButtonClickListener());
			mainPanel.add(t);
		}
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if (command.equals("menu_1")) {			Choice_One(); }

			else if (command.equals("menu_2")) {	Choice_Two(); }
			
			else if (command.equals("menu_3")) {	Choice_Three(); }
			
			else if (command.equals("menu_4")) {	Choice_Four(); }
			
			else if (command.equals("menu_5")) {	Choice_Five(); }
			
			else if (command.equals("menu_6")) {	Choice_Six(); }
			
			else if (command.equals("menu_7")) {	Choice_Seven(); }
			
			else if (command.equals("menu_8")) {	Choice_Eight(); }
			
			else if (command.equals("menu_9")) {	Choice_Nine(); }
			
			else if (command.equals("menu_10")) {	Choice_Ten(); }

			else if (command.equals("information")) {
				Info_Section();
			}
			
			else if (command.equals("Back_to_Menu")) {
				Drop_Index();
				System.out.printf("Back_to_Menu\n");
				tablePanel.removeAll();
				status_label.setText("Main Menu");
				Main_Frame_Update(tablePanel, mainPanel);
			}
		}
	}

	// Database Parameters
	String passportNum = "";
	String lastName = "";
	String firstName = "";
	String country = "";
	String origin = "";
	String destination = "";
	TextField passport_num_field, last_name_field, first_name_field, country_field, name_field, hub_field;
	
	
	private int lock = 0; // For action listener

	// Date
	String year = "2017", month = "01", day = "01";
	JComboBox<String> year_box, month_box, day_box, origin_box, destination_box;
	JPanel date_panel;



	private void Compoment_Initial() {
		// JTextField
		passport_num_field = new TextField(10);
		passport_num_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (passport_num_field.getText().length() > 10) {
					int c = passport_num_field.getCaretPosition();
					passport_num_field.setText(passport_num_field.getText().substring(0, c-1) + passport_num_field.getText().substring(c));
				}
			}
		});

		first_name_field = new TextField(10);
		first_name_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (first_name_field.getText().length() > 24) {
					int c = first_name_field.getCaretPosition();
					first_name_field.setText(first_name_field.getText().substring(0, c-1) + first_name_field.getText().substring(c));
				}
			}
		});

		last_name_field = new TextField(10);
		last_name_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (last_name_field.getText().length() > 24) {
					int c = last_name_field.getCaretPosition();
					last_name_field.setText(last_name_field.getText().substring(0, c-1) + last_name_field.getText().substring(c));
				}
			}
		});

		country_field = new TextField(10);
		country_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (country_field.getText().length() > 24) {
					int c = country_field.getCaretPosition();
					country_field.setText(country_field.getText().substring(0, c-1) + country_field.getText().substring(c));
				}
			}
		});

		name_field = new TextField(10);
		name_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (name_field.getText().length() > 24) {
					int c = name_field.getCaretPosition();
					name_field.setText(name_field.getText().substring(0, c-1) + name_field.getText().substring(c));
				}
			}
		});

		hub_field = new TextField(10);
		hub_field.addTextListener(new TextListener(){
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				if (hub_field.getText().length() > 24) {
					int c = hub_field.getCaretPosition();
					hub_field.setText(hub_field.getText().substring(0, c-1) + hub_field.getText().substring(c));
				}
			}
		});

		// ComboBox
		year_box = new JComboBox<String>();
		for (int i = 1900; i < 2018; ++i) {
			year_box.addItem("" + i);
		}

		month_box = new JComboBox<String>();
		for (int i = 1; i < 13; ++i) {
			String temp = "" + i;
			if (i < 10) {
				temp = "0" + i;
			}
			month_box.addItem(temp);
		}

		day_box = new JComboBox<String>();
		for (int i = 1; i < 32; ++i) {
			String temp = "" + i;
			if (i < 10) {
				temp = "0" + i;
			}
			day_box.addItem(temp);
		}

		origin_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					origin_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		origin_box.setSelectedIndex(0);
		origin = origin_box.getSelectedItem().toString();

		destination_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'" + origin + "\'ORDER BY destination;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					destination_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		destination_box.setSelectedIndex(0);
		destination = destination_box.getSelectedItem().toString();
		


		// Listener
		year_box.addItemListener(new ItemListener(){
		
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (event.getStateChange()) {
					case ItemEvent.SELECTED:
						year = (String) event.getItem();
						if (Integer.parseInt(month) == 2) {
							int max = 0;
							day_box.removeAllItems();
							if (Integer.parseInt(year) % 400 == 0) {
								max = 29;
							}
							else if (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0) {
								max = 29;
							}
							else {
								max = 28;
							}
							for (int i = 1; i <= max; ++i) {
								String temp = "" + i;
								if (i < 10) {
									temp = "0" + i;
								}
								day_box.addItem(temp);
							}
							day_box.setSelectedItem("01");
							day = "01";
						}
						break;
				
					default:
						break;
				}
			}
		});

		month_box.addItemListener(new ItemListener(){
		
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (event.getStateChange()) {
					case ItemEvent.SELECTED:
						month = (String) event.getItem();
						int t = Integer.parseInt(month);
						day_box.removeAllItems();
						int max = 0;
						if (t == 2) {
							if (Integer.parseInt(year) % 400 == 0) {
								max = 29;
							}
							else if (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0) {
								max = 29;
							}
							else {
								max = 28;
							}
						}
						else if (t == 1 || t == 3 || t == 5 || t == 7 || t == 8 || t == 10 || t == 12) {
							max = 31;
						}
						else {
							max = 30;
						}
						for (int i = 1; i <= max; ++i) {
							String temp = "" + i;
							if (i < 10) {
								temp = "0" + i;
							}
							day_box.addItem(temp);
						}
						day_box.setSelectedItem("01");
						break;
				
					default:
						break;
				}
			}
		});

		day_box.addItemListener(new ItemListener(){
		
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (event.getStateChange()) {
					case ItemEvent.SELECTED:
						day = (String) event.getItem();
						break;
				
					default:
						break;
				}
			}
		});

		date_panel = new JPanel(new GridLayout(1, 3));
		date_panel.add(year_box);
		date_panel.add(month_box);
		date_panel.add(day_box);

		year_box.setSelectedItem("2017");
		month_box.setSelectedItem("01");
		day_box.setSelectedItem("01");

		origin_box.addItemListener(new ItemListener(){
		
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (event.getStateChange()) {
					case ItemEvent.SELECTED:
						origin = origin_box.getSelectedItem().toString();
						destination_box.removeAllItems();
						try {
							String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'" + origin + "\'ORDER BY destination;";
							List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
							for (List<String> row : result) {
								for (String content : row) {
									destination_box.addItem(content);
								}
							}
							destination_box.setSelectedIndex(0);
							destination = destination_box.getSelectedItem().toString();
						} catch (Exception e) {
							//TODO: handle exception
							System.err.println (e.getMessage());
						}
						break;
				
					default:
						break;
				}
			}
		});

		destination_box.addItemListener(new ItemListener(){
			
				@Override
				public void itemStateChanged(ItemEvent event) {
					switch (event.getStateChange()) {
						case ItemEvent.SELECTED:
							destination = destination_box.getSelectedItem().toString();
							break;
					
						default:
							break;
					}
				}
			});
	} 
	
	// 1. Add Passenger
	private void Choice_One () {
		tablePanel = new JPanel(new BorderLayout());
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("Add Passenger"));
		tablePanel.add(jp, BorderLayout.NORTH);
		JPanel controlPanel = new JPanel(new GridLayout(2,1));
		JButton submitButton = new JButton("Submit");
		controlPanel.add(submitButton);
		controlPanel.add(back_to_menu);
		tablePanel.add(controlPanel, BorderLayout.SOUTH);

		// Label Panel
		JPanel label_panel = new JPanel(new GridLayout(11,1));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please enter your Passport Number: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please enter your First Name: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please enter your Last Name: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please select your Date of Birth: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please enter your Country: "));
		label_panel.add(new JLabel(""));

		tablePanel.add(label_panel, BorderLayout.CENTER);

		// Input Panel
		JPanel input_panel = new JPanel(new GridLayout(11,1));
		input_panel.add(new JLabel(""));
		input_panel.add(passport_num_field);
		input_panel.add(new JLabel(""));
		input_panel.add(first_name_field);
		input_panel.add(new JLabel(""));
		input_panel.add(last_name_field);
		input_panel.add(new JLabel(""));
		input_panel.add(date_panel);
		input_panel.add(new JLabel(""));
		input_panel.add(country_field);
		input_panel.add(new JLabel(""));

		passport_num_field.setText("");
		first_name_field.setText("");
		last_name_field.setText("");
		country_field.setText("");

		passportNum = "";
		firstName = "";
		lastName = "";
		country = "";

		year_box.setSelectedItem("2017");
		month_box.setSelectedItem("01");
		day_box.setSelectedItem("01");

		year = "2017";
		month = "01";
		day = "01";

		tablePanel.add(input_panel, BorderLayout.EAST);

		status_label.setText("Please fill out your information.");

		Main_Frame_Update(mainPanel, tablePanel);

		submitButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent event) {
				String error_msg = "";
				passportNum = passport_num_field.getText().toUpperCase();
				firstName = first_name_field.getText();
				lastName = last_name_field.getText();
				country = country_field.getText();

				if (passportNum.length() == 0 || firstName.length() == 0 || lastName.length() == 0 || country.length() == 0) {
					error_msg += "Please fill out your information.\n";
				}
				if (passportNum.length() < 10 && passportNum.length() != 0) {
					error_msg += "Please enter your 10 digits Passport Number.\n";
				}
				if (firstName.length() + lastName.length() > 24) {
					error_msg += "Your name is too long.\n";
				}
				if (error_msg.length() > 0) {
					JOptionPane.showMessageDialog( null, error_msg, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				else {
					try {
						// Check passport number
						String query = "";
						query += "SELECT pID FROM Passenger WHERE passNum = \'";
						query += passportNum + "\';";
						int rowCount = my_esql.executeQuery(query);
						if (rowCount == 0) {
							// Get available pID
							query = "SELECT pID FROM Passenger;";
							List<List<String>> result =  my_esql.executeQueryAndReturnResult(query);
							int pID = 0;
							for (List<String> row : result) {
								for (String content : row) {
									if (pID != Integer.parseInt(content)) {
										break;
									}
									pID++;
								}
							}
							// Add passenger
							query = "";
							query = "INSERT INTO passenger (pid, passnum, fullname, bdate, country) VALUES (";
							query += pID + ", \'";
							query += passportNum + "\', \'";
							query += firstName.substring(0, 1).toUpperCase() + firstName.substring(1, firstName.length()).toLowerCase();
							query += " ";
							query += lastName.substring(0, 1).toUpperCase() + lastName.substring(1, lastName.length()).toLowerCase();
							query += "\', \'";
							query += year + "-" + month + "-" + day + "\', \'";
							query += country + "\');";
							my_esql.executeUpdate(query);
							System.out.printf("%s\n", query);

							passport_num_field.setText("");
							first_name_field.setText("");
							last_name_field.setText("");
							country_field.setText("");
					
							passportNum = "";
							firstName = "";
							lastName = "";
							country = "";
					
							year_box.setSelectedItem("2017");
							month_box.setSelectedItem("01");
							day_box.setSelectedItem("01");
					
							year = "2017";
							month = "01";
							day = "01";
							JOptionPane.showMessageDialog( null, "Adding Passenger is finished.", "Success", JOptionPane.PLAIN_MESSAGE);
							status_label.setText("Success");
						}
						else {
							JOptionPane.showMessageDialog( null, "This Passenger is already in list.", "ERROR", JOptionPane.ERROR_MESSAGE);
							passport_num_field.setText(null);
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
					}
				}
			}
		});
	}
	
	private void Choice_Two () {
		tablePanel = new JPanel(new BorderLayout());
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("Book Flight"));
		tablePanel.add(jp, BorderLayout.NORTH);
		JPanel controlPanel = new JPanel(new GridLayout(2,1));
		JButton submitButton = new JButton("Submit");
		controlPanel.add(submitButton);
		controlPanel.add(back_to_menu);
		tablePanel.add(controlPanel, BorderLayout.SOUTH);

		// Label Panel
		JPanel label_panel = new JPanel(new GridLayout(9,1));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please enter your Passport Number: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please select your Origin: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please select your Destination: "));
		label_panel.add(new JLabel(""));
		label_panel.add(new JLabel(" Please select your Departure Date: "));
		label_panel.add(new JLabel(""));

		tablePanel.add(label_panel, BorderLayout.CENTER);

		// Input Panel
		JPanel input_panel = new JPanel(new GridLayout(9,1));
		input_panel.add(new JLabel(""));
		input_panel.add(passport_num_field);
		input_panel.add(new JLabel(""));
		input_panel.add(origin_box);
		input_panel.add(new JLabel(""));
		input_panel.add(destination_box);
		input_panel.add(new JLabel(""));
		input_panel.add(date_panel);
		input_panel.add(new JLabel(""));

		passport_num_field.setText(null);


		tablePanel.add(input_panel, BorderLayout.EAST);
		
		status_label.setText("Please fill out your information.");

		Main_Frame_Update(mainPanel, tablePanel);

		submitButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				status_label.setText(passportNum + ", " + origin + ", " + destination + ", " + year + "-" + month + "-" + day);
				String error_msg = "";
				passportNum = passport_num_field.getText().toUpperCase();

				if (passportNum.length() == 0) {
					error_msg += "Please fill out your Passport Number.";
				}
				else if (passportNum.length() < 10) {
					error_msg += "Please enter your 10 digits Passport Number.";
				}
				if (error_msg.length() > 0) {
					JOptionPane.showMessageDialog( null, error_msg, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				else {
					try {
						// Check passport number
						String query = "";
						query += "SELECT pID FROM Passenger WHERE passNum = \'";
						query += passportNum + "\';";
						List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
						if (result.size() > 0) {
							// Get pID
							String pID = result.get(0).get(0);

							//Get bookRef
							String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
							String bookref = "";
							while (true) {
								Random random = new Random();
								for (int i = 0; i < 10; ++i) {
									bookref += letters.charAt(random.nextInt(26));
								}
								try {
									query = "SELECT bookRef FROM Booking WHERE bookRef = \'";
									query += bookref + "\';";
									int rows = my_esql.executeQuery(query);
									if (rows > 0) {
										continue;
									} else {
										break;
									}
								} catch (Exception e) {
									//TODO: handle exception
									System.err.println (e.getMessage());
									break;
								}
							}
							// Get Flight Number
							query = "SELECT DISTINCT flightNum FROM Flight WHERE ";
							query += "origin = \'" + origin + "\' and ";
							query += "destination = \'" + destination + "\';";
							result = my_esql.executeQueryAndReturnResult(query);
							String flightNum = result.get(0).get(0);

							// Book Flight
							query = "INSERT INTO Booking (bookRef, departure, flightNum, pID) VALUES (\'";
							query += bookref + "\', \'";
							query += year_box.getSelectedItem().toString() + "-" + month_box.getSelectedItem().toString();
							query += "-" + day_box.getSelectedItem().toString() + "\', \'";
							query += flightNum + "\', \'";
							query += pID + "\');";

							my_esql.executeUpdate(query);

							passport_num_field.setText("");
					
							passportNum = "";
					
							year_box.setSelectedItem("2017");
							month_box.setSelectedItem("01");
							day_box.setSelectedItem("01");
					
							year = "2017";
							month = "01";
							day = "01";
							JOptionPane.showMessageDialog( null, "Book Flight is finished.", "Success", JOptionPane.PLAIN_MESSAGE);
							status_label.setText("Success");
						}
						else {
							JOptionPane.showMessageDialog( null, "This Passenger does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
							passport_num_field.setText(null);
						}
					}catch(Exception e){
						System.err.println (e.getMessage());
					}
				}
			}
		});
	}
	
	private void Choice_Three () {
		tablePanel = new JPanel(new BorderLayout());
		JPanel controlPanel = new JPanel(new GridLayout(2, 1));
		JButton submitButton = new JButton("Submit");
		controlPanel.add(submitButton);
		controlPanel.add(back_to_menu);
		tablePanel.add(controlPanel, BorderLayout.SOUTH);

		JPanel subPanel_one = new JPanel(new GridLayout(5, 1));
		JPanel subPanel_two = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_three = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_four = new JPanel(new GridLayout(1, 2));

		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("Review Flight"));
		subPanel_one.add(jp);

		// For Passport Number, flightNum list will be update after click check
		subPanel_two.add(new JLabel(" Please enter your Passport Number, then click Check: "));
		JPanel ppNPanel = new JPanel(new GridLayout(1, 2));
		JButton checkButton = new JButton("Check");
		ppNPanel.add(passport_num_field);
		ppNPanel.add(checkButton);
		subPanel_two.add(ppNPanel);
		subPanel_one.add(subPanel_two);

		// For flight number
		subPanel_three.add(new JLabel(" Please select your Flight: "));
		JComboBox<String> flight_box = new JComboBox<String>();
		subPanel_three.add(flight_box);
		subPanel_one.add(subPanel_three);

		// For rate score
		subPanel_four.add(new JLabel(" Please select your the Score (0, 5): "));
		JComboBox<String> rate_box = new JComboBox<String>();
		for (int i = 1; i < 6; ++i) {
			rate_box.addItem("" + i);
		}
		rate_box.setSelectedIndex(0);
		subPanel_four.add(rate_box);
		subPanel_one.add(subPanel_four);

		// For comment
		subPanel_one.add(new JLabel(" Please make your comment: "));
		TextArea commentArea = new TextArea();
		//subPanel_one.add(commentArea);

		passport_num_field.setText(null);

		tablePanel.add(subPanel_one, BorderLayout.NORTH);
		tablePanel.add(commentArea, BorderLayout.CENTER);

		status_label.setText("Please fill out your information.");

		Main_Frame_Update(mainPanel, tablePanel);

		checkButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String error_msg = "";
				passportNum = passport_num_field.getText().toUpperCase();

				if (passportNum.length() == 0) {
					error_msg += "Please fill out your Passport Number.";
				}
				else if (passportNum.length() < 10) {
					error_msg += "Please enter your 10 digits Passport Number.";
				}
				if (error_msg.length() > 0) {
					JOptionPane.showMessageDialog( null, error_msg, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				else {
					try {
						// Get flight numbers
						String query = "";
						query += "SELECT B.flightNum FROM Booking B, Passenger P1 WHERE P1.pID = B.pID and P1.passNum = \'";
						query += passportNum + "\' ";
						query += "EXCEPT ";
						query += "SELECT R.flightNum FROM Ratings R, Passenger P2 WHERE P2.pID = R.pID and P2.passNum = \'";
						query += passportNum + "\' ORDER BY flightNum;";
						List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
						flight_box.removeAllItems();
						if (result.size() > 0) {
							for (List<String> row : result) {
								for (String content : row) {
									flight_box.addItem(content);
								}
							}
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
			}
		});

		submitButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String error_msg = "";
				passportNum = passport_num_field.getText().toUpperCase();

				if (passportNum.length() == 0 || flight_box.getSelectedItem().toString().length() == 0) {
					error_msg += "Please fill out your Passport Number.";
				}
				else if (passportNum.length() < 10) {
					error_msg += "Please enter your 10 digits Passport Number.";
				}
				if (error_msg.length() > 0) {
					JOptionPane.showMessageDialog( null, error_msg, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				else {
					try {
						// Check passport number
						String query = "SELECT pID FROM Passenger WHERE passNum = \'";
						query += passportNum + "\';";
						List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
						if (result.size() > 0) {
							// Get pID
							String pID = result.get(0).get(0);
							// Get available rID
							query = "SELECT rID FROM Ratings;";
							result =  my_esql.executeQueryAndReturnResult(query);
							int rID = 0;
							for (List<String> row : result) {
								for (String content : row) {
									if (rID != Integer.parseInt(content)) {
										break;
									}
									rID++;
								}
							}
							// Insert
							query = "INSERT INTO Ratings (rID, pID, flightNum, score, comment) VALUES (";
							query += rID + ", ";
							query += pID + ", \'";
							query += flight_box.getSelectedItem().toString() + "\', ";
							query += rate_box.getSelectedItem().toString() + ", \'";
							query += commentArea.getText() + "\');";
							my_esql.executeUpdate(query);

							passportNum = "";
							passport_num_field.setText(null);
							commentArea.setText(null);

							JOptionPane.showMessageDialog( null, "Comment is finished.", "Success", JOptionPane.PLAIN_MESSAGE);
							status_label.setText("Success");
						}
						else {
							JOptionPane.showMessageDialog( null, "This Passenger does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
							passport_num_field.setText(null);
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
			}
		});
	}
	
	private void Choice_Four () {
		tablePanel = new JPanel(new BorderLayout());

		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("Insert or Update Flight"));
		tablePanel.add(jp, BorderLayout.NORTH);

		JPanel controlPanel = new JPanel(new GridLayout(2,1));
		JButton submitButton = new JButton("Submit");
		controlPanel.add(submitButton);
		controlPanel.add(back_to_menu);
		tablePanel.add(controlPanel, BorderLayout.SOUTH);

		JPanel subPanel = new JPanel(new GridLayout(4, 1));
		JPanel subPanel_one = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_two = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_three = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_four = new JPanel(new GridLayout(1, 2));

		// For airline name
		subPanel_one.add(new JLabel(" Please enter the name of the airline, then click Check: "));
		JPanel ssPanel = new JPanel(new GridLayout(1, 2));
		JButton checkButton = new JButton("Check");
		ssPanel.add(name_field);
		ssPanel.add(checkButton);
		subPanel_one.add(ssPanel);
		subPanel.add(subPanel_one);
		name_field.setText(null);

		// For founded year
		subPanel_two.add(new JLabel(" Please enter the Year the airline was founded: "));
		JComboBox<String> founded_box = new JComboBox<String>();
		for (int i = 1900; i <= 2017; ++i) {
			founded_box.addItem("" + i);
		}
		founded_box.setSelectedItem("2017");
		subPanel_two.add(founded_box);
		subPanel.add(subPanel_two);

		// For country
		subPanel_three.add(new JLabel(" Please enter the Country which the airline operates: "));
		subPanel_three.add(country_field);
		subPanel.add(subPanel_three);
		country_field.setText(null);

		// For hub
		subPanel_four.add(new JLabel(" Please enter the Hub airport of the airline: "));
		subPanel_four.add(hub_field);
		subPanel.add(subPanel_four);
		hub_field.setText(null);

		tablePanel.add(subPanel, BorderLayout.CENTER);
		
		status_label.setText("Please fill out your information.");

		Main_Frame_Update(mainPanel, tablePanel);

		checkButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (name_field.getText().length() > 0) {
					try {
						String query = "SELECT founded, country, hub FROM Airline WHERE name = \'";
						query += name_field.getText() + "\';";
						List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
						if (result.size() > 0) {
							founded_box.setSelectedItem(result.get(0).get(0));
							country_field.setText(result.get(0).get(1));
							hub_field.setText(result.get(0).get(2));
						}
						else {
							JOptionPane.showMessageDialog( null, "This airline does not exist in the database.", "WARNING", JOptionPane.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
				}
				else {
					JOptionPane.showMessageDialog( null, "Please fill out the name of the airline.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		submitButton.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (name_field.getText().length() > 0 && country_field.getText().length() >0 && hub_field.getText().length() > 0) {
					try {
						String query = "SELECT airId FROM Airline WHERE name = \'";
						query += name_field.getText() + "\';";
						List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
						if (result.size() > 0) {
							int choice = JOptionPane.showConfirmDialog( null, "This airline is already in the database.\nDo you want to update it?", "WARNING", JOptionPane.WARNING_MESSAGE);
							if (choice == 0) {
								query = "UPDATE Airline ";
								query += "SET ";
								query += "founded = " + founded_box.getSelectedItem().toString() + ", ";
								query += "country = \'" + country_field.getText() + "\', ";
								query += "hub = \'" + hub_field.getText() + "\' ";
								query += "WHERE ";
								query += "name = \'" + name_field.getText() + "\' and ";
								query += "airId = " + result.get(0).get(0) + ";";
								my_esql.executeUpdate(query);
								JOptionPane.showMessageDialog( null, "Update is finished.", "Success", JOptionPane.PLAIN_MESSAGE);
							}
						}
						else {
							// Get available airId
							query = "SELECT airId FROM Airline ORDER BY airId;";
							result =  my_esql.executeQueryAndReturnResult(query);
							int airId = 0;
							for (List<String> row : result) {
								for (String content : row) {
									if (airId != Integer.parseInt(content)) {
										break;
									}
									airId++;
								}
							}
							query = "INSERT INTO Airline (airId, name, founded, country, hub) VALUES (";
							query += airId + ", \'";
							query += name_field.getText() + "\', ";
							query += founded_box.getSelectedItem().toString() + ", \'";
							query += country_field.getText() + "\', \'";
							query += hub_field.getText() + "\');";
							my_esql.executeUpdate(query);
							JOptionPane.showMessageDialog( null, "Insert is finished.", "Success", JOptionPane.PLAIN_MESSAGE);
						}
					} catch (Exception e) {
						//TODO: handle exception
						System.err.println (e.getMessage());
					}
					name_field.setText(null);
					founded_box.setSelectedIndex(founded_box.getItemCount() - 1);
					country_field.setText(null);
					hub_field.setText(null);
					status_label.setText("Success");
				}
				else {
					JOptionPane.showMessageDialog( null, "Please fill out the information.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private void Choice_Five () {
		Create_Index("CREATE INDEX origin_select ON Flight USING BTREE (origin);", "DROP INDEX origin_select;");
		Create_Index("CREATE INDEX destination_select ON Flight USING BTREE (destination);", "DROP INDEX destination_select;");
		JComboBox<String> origin_flight_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					origin_flight_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		origin_flight_box.setSelectedIndex(0);
		
		JComboBox<String> destination_flight_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
			query += origin_flight_box.getSelectedItem().toString() + "\' ORDER BY destination;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					destination_flight_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		destination_flight_box.setSelectedIndex(0);

		// Set table
		String[] header = {"Flights"};

		DefaultTableModel model = new DefaultTableModel(null, header);
		try {
			String query = "SELECT DISTINCT flightnum FROM Flight WHERE origin = \'";
			query += origin_flight_box.getSelectedItem().toString() + "\' and destination = \'";
			query += destination_flight_box.getSelectedItem().toString() + "\' ORDER BY flightnum;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				model.addRow(row.toArray());
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}

		JTable table = new JTable();
		table.setModel(model);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, dtcr);

		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		tablePanel = new JPanel(new BorderLayout());
		
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("List Flights from Origin to Destination"));
		tablePanel.add(jp, BorderLayout.NORTH);
		
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);

		JPanel subsubPanel = new JPanel(new GridLayout(2,2));
		JPanel subPanel_one = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_one.add(new JLabel("Origin City"));
		subsubPanel.add(subPanel_one);

		JPanel subPanel_two = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_two.add(new JLabel("Destination City"));
		subsubPanel.add(subPanel_two);
		
		JPanel subPanel_three = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_three.add(origin_flight_box);
		subsubPanel.add(subPanel_three);
		
		JPanel subPanel_four = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_four.add(destination_flight_box);
		subsubPanel.add(subPanel_four);

		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(subsubPanel, BorderLayout.NORTH);
		subPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		tablePanel.add(subPanel, BorderLayout.CENTER);

		status_label.setText("Please select the Origin City and Destination City.");
		
		Main_Frame_Update(mainPanel, tablePanel);

		// Listener
		origin_flight_box.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
					query += origin_flight_box.getSelectedItem().toString() + "\' ORDER BY destination;";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					destination_flight_box.removeAllItems();
					for (List<String> row : result) {
						for (String content : row) {
							destination_flight_box.addItem(content);
						}
					}
					destination_flight_box.setSelectedIndex(0);
					
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}
					
					// Start Timer
					long startTime = System.currentTimeMillis();

					query = "SELECT DISTINCT flightnum FROM Flight WHERE origin = \'";
					query += origin_flight_box.getSelectedItem().toString() + "\' and destination = \'";
					query += destination_flight_box.getSelectedItem().toString() + "\' ORDER BY flightnum;";
					result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
						
					// End Timer		
					long endTime = System.currentTimeMillis();

					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});

		destination_flight_box.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					String query = "SELECT DISTINCT flightnum FROM Flight WHERE origin = \'";
					query += origin_flight_box.getSelectedItem().toString() + "\' and destination = \'";
					query += destination_flight_box.getSelectedItem().toString() + "\' ORDER BY flightnum;";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});
	}
	
	private void Choice_Six () {
		Create_Index("CREATE INDEX destination_select ON Flight USING BTREE (destination);", "DROP INDEX destination_select;");
		
		tablePanel = new JPanel(new BorderLayout());

		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("List Most Popular Destination"));
		tablePanel.add(jp, BorderLayout.NORTH);
		
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);

		JPanel subsubPanel = new JPanel(new BorderLayout());
		JPanel subPanel_one = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_one.add(new JLabel("Please select the number of Popular Destinations you want to display :"));
		subsubPanel.add(subPanel_one, BorderLayout.CENTER);

		JComboBox<String> num_box = new JComboBox<String>();
		subsubPanel.add(num_box, BorderLayout.EAST);
		try {
			String query = "SELECT DISTINCT destination FROM Flight;";
			List<List<String>> result = executeQueryAndReturnResult(query);
			System.out.printf("%s\n", result.size());
			for (int i = 0; i <= result.size(); ++i) {
				num_box.addItem("" + i);
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		num_box.setSelectedIndex(0);

		// Set table
		String[] header = {"Destination City", "Number of Flights"};
		
		DefaultTableModel model = new DefaultTableModel(null, header);
		try {
			String query = "SELECT destination, Count (destination) ";
			query += "FROM Flight ";
			query += "GROUP BY destination ";
			query += "ORDER BY COUNT (destination) ";
			query += "desc limit ";
			query += num_box.getSelectedItem() + ";";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				model.addRow(row.toArray());
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}

		JTable table = new JTable();
		table.setModel(model);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, dtcr);
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(subsubPanel, BorderLayout.NORTH);
		subPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		tablePanel.add(subPanel, BorderLayout.CENTER);

		status_label.setText(" ");
		
		Main_Frame_Update(mainPanel, tablePanel);

		num_box.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					String query = "SELECT destination, Count (destination) ";
					query += "FROM Flight ";
					query += "GROUP BY destination ";
					query += "ORDER BY COUNT (destination) ";
					query += "desc limit ";
					query += num_box.getSelectedItem() + ";";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});
	}
	
	private void Choice_Seven () {
		tablePanel = new JPanel(new BorderLayout());
		
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("List Highest Rated Destination"));
		tablePanel.add(jp, BorderLayout.NORTH);
		
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);

		JPanel subsubPanel = new JPanel(new GridLayout(1, 2));
		JPanel subPanel_one = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_one.add(new JLabel("Please select the number of Highest Rated Destinations you want to display :"));
		subsubPanel.add(subPanel_one);

		JComboBox<String> num_box = new JComboBox<String>();
		subsubPanel.add(num_box);
		try {
			String query = "SELECT DISTINCT destination FROM Flight;";
			List<List<String>> result = executeQueryAndReturnResult(query);
			System.out.printf("%s\n", result.size());
			for (int i = 0; i <= result.size(); ++i) {
				num_box.addItem("" + i);
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		num_box.setSelectedIndex(0);

		// Set table
		String[] header = {"Airline Name", "Flight Number", "Origin", "Destination", "Plane Type", "Average Score"};
		
		DefaultTableModel model = new DefaultTableModel(null, header);
		try {
			String query = "SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, avg(R.score) ";
			query += "FROM Flight F, Ratings R,Airline A ";
			query += "WHERE F.flightNum = R.flightNum AND A.airId=F.airId ";
			query += "GROUP BY A.name, F.flightNum, F.origin, F.destination, F.plane ";
			query += "ORDER BY avg(R.score) ";
			query += "desc limit ";
			query += num_box.getSelectedItem() + ";";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				model.addRow(row.toArray());
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}

		JTable table = new JTable();
		table.setModel(model);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, dtcr);
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(subsubPanel, BorderLayout.NORTH);
		subPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		tablePanel.add(subPanel, BorderLayout.CENTER);

		status_label.setText(" ");
		
		Main_Frame_Update(mainPanel, tablePanel);

		num_box.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					String query = "SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, avg(R.score) ";
					query += "FROM Flight F, Ratings R,Airline A ";
					query += "WHERE F.flightNum = R.flightNum AND A.airId=F.airId ";
					query += "GROUP BY A.name, F.flightNum, F.origin, F.destination, F.plane ";
					query += "ORDER BY avg(R.score) ";
					query += "desc limit ";
					query += num_box.getSelectedItem() + ";";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						row.set(5, row.get(5).substring(0, 6));
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});
	}
	
	private void Choice_Eight () {
		Create_Index("CREATE INDEX origin_select ON Flight USING BTREE (origin);", "DROP INDEX origin_select;");
		Create_Index("CREATE INDEX destination_select ON Flight USING BTREE (destination);", "DROP INDEX destination_select;");
		JComboBox<String> origin_flight_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT origin FROM Flight ORDER BY origin;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					origin_flight_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		origin_flight_box.setSelectedIndex(0);
		
		JComboBox<String> destination_flight_box = new JComboBox<String>();
		try {
			String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
			query += origin_flight_box.getSelectedItem().toString() + "\' ORDER BY destination;";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					destination_flight_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		destination_flight_box.setSelectedIndex(0);

		tablePanel = new JPanel(new BorderLayout());
		
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JLabel("List Flights from Origin to Destination in order of Duration"));
		jp.setBorder(BorderFactory.createLineBorder(Color.black));
		tablePanel.add(jp, BorderLayout.NORTH);
		
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);

		JPanel subsubPanel = new JPanel(new GridLayout(2,2));
		JPanel subPanel_one = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_one.add(new JLabel("Origin City"));
		subsubPanel.add(subPanel_one);

		JPanel subPanel_two = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_two.add(new JLabel("Destination City"));
		subsubPanel.add(subPanel_two);
		
		JPanel subPanel_three = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_three.add(origin_flight_box);
		subsubPanel.add(subPanel_three);
		
		JPanel subPanel_four = new JPanel(new FlowLayout(FlowLayout.CENTER));
		subPanel_four.add(destination_flight_box);
		subsubPanel.add(subPanel_four);

		JPanel subsubsubPanel = new JPanel(new BorderLayout());
		JPanel subsubsubPanel_one = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subsubsubPanel_one.add(new JLabel("Please select the number of Highest Rated Destinations you want to display :"));
		subsubsubPanel.add(subsubsubPanel_one, BorderLayout.CENTER);

		JComboBox<String> num_box = new JComboBox<String>();
		subsubPanel.add(num_box);
		try {
			String query = "SELECT DISTINCT destination FROM Flight;";
			List<List<String>> result = executeQueryAndReturnResult(query);
			System.out.printf("%s\n", result.size());
			for (int i = 0; i <= result.size(); ++i) {
				num_box.addItem("" + i);
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		num_box.setSelectedIndex(0);
		subsubsubPanel.add(num_box, BorderLayout.EAST);

		// Set table
		String[] header = {"Airline Name", "Flight Number", "Plane Type", "Duration"};
		
		DefaultTableModel model = new DefaultTableModel(null, header);
		try {
			String query = "SELECT A.name, F.flightNum, F.plane, F.duration ";
			query += "FROM Flight F, Airline A ";
			query += "WHERE ";
			query += "F.airId = A.airId and ";
			query += "F.origin = \'"+ origin_flight_box.getSelectedItem().toString() + "\' and ";
			query += "F.destination = \'"+ destination_flight_box.getSelectedItem().toString() + "\' ";
			query += "ORDER BY F.duration ";
			query += "DESC LIMIT " + num_box.getSelectedItem().toString() + ";";
			List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				model.addRow(row.toArray());
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}

		JTable table = new JTable();
		table.setModel(model);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, dtcr);

		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);


		JPanel subsubsubsubPanel = new JPanel(new BorderLayout());
		subsubsubsubPanel.add(subsubsubPanel, BorderLayout.NORTH);
		subsubsubsubPanel.add(subsubPanel, BorderLayout.CENTER);
		
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(subsubsubsubPanel, BorderLayout.NORTH);
		subPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		tablePanel.add(subPanel, BorderLayout.CENTER);

		status_label.setText(" ");
		
		Main_Frame_Update(mainPanel, tablePanel);

		// Listener
		origin_flight_box.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = "SELECT DISTINCT destination FROM Flight WHERE origin = \'";
					query += origin_flight_box.getSelectedItem().toString() + "\' ORDER BY destination;";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					destination_flight_box.removeAllItems();
					for (List<String> row : result) {
						for (String content : row) {
							destination_flight_box.addItem(content);
						}
					}
					destination_flight_box.setSelectedIndex(0);
					
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					query = "SELECT A.name, F.flightNum, F.plane, F.duration ";
					query += "FROM Flight F, Airline A ";
					query += "WHERE ";
					query += "F.airId = A.airId and ";
					query += "F.origin = \'"+ origin_flight_box.getSelectedItem().toString() + "\' and ";
					query += "F.destination = \'"+ destination_flight_box.getSelectedItem().toString() + "\' ";
					query += "ORDER BY F.duration ";
					query += "DESC LIMIT " + num_box.getSelectedItem().toString() + ";";
					result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});

		destination_flight_box.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					String query = "SELECT A.name, F.flightNum, F.plane, F.duration ";
					query += "FROM Flight F, Airline A ";
					query += "WHERE ";
					query += "F.airId = A.airId and ";
					query += "F.origin = \'"+ origin_flight_box.getSelectedItem().toString() + "\' and ";
					query += "F.destination = \'"+ destination_flight_box.getSelectedItem().toString() + "\' ";
					query += "ORDER BY F.duration ";
					query += "DESC LIMIT " + num_box.getSelectedItem().toString() + ";";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});

		num_box.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					while (model.getRowCount() > 0) {
						model.removeRow(0);
					}

					// Start Timer
					long startTime = System.currentTimeMillis();

					String query = "SELECT A.name, F.flightNum, F.plane, F.duration ";
					query += "FROM Flight F, Airline A ";
					query += "WHERE ";
					query += "F.airId = A.airId and ";
					query += "F.origin = \'"+ origin_flight_box.getSelectedItem().toString() + "\' and ";
					query += "F.destination = \'"+ destination_flight_box.getSelectedItem().toString() + "\' ";
					query += "ORDER BY F.duration ";
					query += "DESC LIMIT " + num_box.getSelectedItem().toString() + ";";
					List<List<String>> result = my_esql.executeQueryAndReturnResult(query);
					for (List<String> row : result) {
						model.addRow(row.toArray());
					}
					// End Timer		
					long endTime = System.currentTimeMillis();
					
					status_label.setText("Runtime: " + (endTime - startTime) + " ms");
				} catch (Exception e) {
					//TODO: handle exception
					System.err.println (e.getMessage());
				}
			}
		});
	}
	
	// 9. Find Number of Available Seats on a given Flight
	private void Choice_Nine () {
		tablePanel = new JPanel(new BorderLayout());
		
		JPanel titlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		titlPanel.add(new JLabel("Find Number Of Available Seats For Flight"));
		titlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		tablePanel.add(titlPanel, BorderLayout.NORTH);
		
		JPanel rowPanel_one = new JPanel(new GridLayout(2, 1));
		rowPanel_one.add(new JLabel("Please select the flight number you want to check: "));
		rowPanel_one.add(new JLabel("Please select the Date of Departure: "));

		JPanel rowPanel_two = new JPanel(new GridLayout(2, 1));
		JComboBox<String> flight_number_box = new JComboBox<String>();
		flight_number_box.addItem("----");
		try {
			String query = "SELECT flightNum FROM flight ORDER BY flightNum;";
			List<List<String>> result = executeQueryAndReturnResult(query);
			for (List<String> row : result) {
				for (String content : row) {
					flight_number_box.addItem(content);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			System.err.println (e.getMessage());
		}
		flight_number_box.setSelectedIndex(0);
		rowPanel_two.add(flight_number_box);

		JComboBox<String> departure_box = new JComboBox<String>();
		departure_box.addItem("----------");
		departure_box.setSelectedIndex(0);
		rowPanel_two.add(departure_box);

		JPanel rowPanel = new JPanel(new BorderLayout());
		rowPanel.add(rowPanel_one, BorderLayout.CENTER);
		rowPanel.add(rowPanel_two, BorderLayout.EAST);
		rowPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		JPanel colPanel_one = new JPanel(new GridLayout(2, 1));
		colPanel_one.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_two = new JPanel(new GridLayout(2, 1));
		colPanel_two.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_three = new JPanel(new GridLayout(2, 1));
		colPanel_three.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_four = new JPanel(new GridLayout(2, 1));
		colPanel_four.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_five = new JPanel(new GridLayout(2, 1));
		colPanel_five.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_six = new JPanel(new GridLayout(2, 1));
		colPanel_six.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel colPanel_seven = new JPanel(new GridLayout(2, 1));
		colPanel_seven.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// Flight Number
		JLabel flight_number_label = new JLabel(" Flight Number ");
		flight_number_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_one.add(flight_number_label);
		JLabel flight_number = new JLabel("----");
		flight_number.setHorizontalAlignment(JLabel.CENTER);
		colPanel_one.add(flight_number);
		// Origin
		JLabel Origin_label = new JLabel(" Origin ");
		Origin_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_two.add(Origin_label);
		JLabel Origin = new JLabel("----");
		Origin.setHorizontalAlignment(JLabel.CENTER);
		colPanel_two.add(Origin);
		// Destination
		JLabel Destination_label = new JLabel(" Destination ");
		Destination_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_three.add(Destination_label);
		JLabel Destination = new JLabel("----");
		Destination.setHorizontalAlignment(JLabel.CENTER);
		colPanel_three.add(Destination);
		// Departure Date
		JLabel Departure_label = new JLabel(" Departure Date ");
		Departure_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_four.add(Departure_label);
		JLabel Departure = new JLabel("----");
		Departure.setHorizontalAlignment(JLabel.CENTER);
		colPanel_four.add(Departure);
		// Booked Seats
		JLabel BookedSeats_label = new JLabel(" Booked Seats ");
		BookedSeats_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_five.add(BookedSeats_label);
		JLabel BookedSeats = new JLabel("----");
		BookedSeats.setHorizontalAlignment(JLabel.CENTER);
		colPanel_five.add(BookedSeats);
		// Total Number of Seats
		JLabel TotalSeats_label = new JLabel(" Total Number of Seats ");
		TotalSeats_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_six.add(TotalSeats_label);
		JLabel TotalSeats = new JLabel("----");
		TotalSeats.setHorizontalAlignment(JLabel.CENTER);
		colPanel_six.add(TotalSeats);
		// Total Number of Available Seats
		JLabel AvailableSeats_label = new JLabel("Total Number of Available Seats");
		AvailableSeats_label.setHorizontalAlignment(JLabel.CENTER);
		colPanel_seven.add(AvailableSeats_label);
		JLabel AvailableSeats = new JLabel("----");
		AvailableSeats.setHorizontalAlignment(JLabel.CENTER);
		colPanel_seven.add(AvailableSeats);

		JPanel colPanel_a = new JPanel(new GridLayout(1, 5));
		colPanel_a.add(colPanel_one);
		colPanel_a.add(colPanel_two);
		colPanel_a.add(colPanel_three);
		colPanel_a.add(colPanel_four);
		colPanel_a.add(colPanel_five);
		JPanel colPanel_b = new JPanel(new GridLayout(1, 2));
		colPanel_b.add(colPanel_six);
		colPanel_b.add(colPanel_seven);
		JPanel colPanel = new JPanel(new GridLayout(2, 1));
		colPanel.add(colPanel_a);
		colPanel.add(colPanel_b);

		JPanel jp = new JPanel(new GridLayout(2, 1));
		jp.add(rowPanel);
		jp.add(colPanel);

		tablePanel.add(jp, BorderLayout.CENTER);
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);
		status_label.setText(" ");
		
		Main_Frame_Update(mainPanel, tablePanel);
		flight_number_box.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lock == 0) {
					lock = 1;
					departure_box.removeAllItems();;
					departure_box.addItem("----------");
					if (flight_number_box.getSelectedItem().toString() == "----") {
						flight_number.setText("----");
						Origin.setText("----");
						Destination.setText("----");
						Departure.setText("----");
						BookedSeats.setText("----");
						TotalSeats.setText("----");
						AvailableSeats.setText("----");
					}
					else {
						try {
							String query = "SELECT DISTINCT B.departure FROM Flight F, Booking B WHERE F.flightNum = B.flightNum ";
							query += "and F.flightNum = \'" + flight_number_box.getSelectedItem().toString() + "\';";
							List<List<String>> result = executeQueryAndReturnResult(query);
							status_label.setText("" + result.size());
							if (result.size() > 0) {
								for (List<String> row : result) {
									for (String content : row) {
										departure_box.addItem(content);
									}
								}
							}
							query = "SELECT origin, destination, seats FROM Flight WHERE ";
							query += "flightNum = \'" + flight_number_box.getSelectedItem().toString() + "\';";
							result = executeQueryAndReturnResult(query);
							if (result.size() > 0) {
								flight_number.setText(flight_number_box.getSelectedItem().toString());
								Origin.setText(result.get(0).get(0));
								Destination.setText(result.get(0).get(1));
								Departure.setText("----");
								BookedSeats.setText("----");
								TotalSeats.setText(result.get(0).get(2));
								AvailableSeats.setText(result.get(0).get(2));
							}
						} catch (Exception e) {
							//TODO: handle exception
							System.err.println (e.getMessage());
						}
					}
					departure_box.setSelectedIndex(0);
					lock = 0;
				}
			}
		});

		departure_box.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lock == 0) {
					lock = 1;
					if (flight_number_box.getSelectedItem().toString() == "----") {
						flight_number.setText("----");
						Origin.setText("----");
						Destination.setText("----");
						Departure.setText("----");
						BookedSeats.setText("----");
						TotalSeats.setText("----");
						AvailableSeats.setText("----");
					}
					else {
						if (departure_box.getSelectedItem().toString()  == "----------") {
							try {
								String query = "SELECT origin, destination, seats FROM Flight WHERE ";
								query += "flightNum = \'" + flight_number_box.getSelectedItem().toString() + "\';";
								List<List<String>> result = executeQueryAndReturnResult(query);
								if (result.size() > 0) {
									flight_number.setText(flight_number_box.getSelectedItem().toString());
									Origin.setText(result.get(0).get(0));
									Destination.setText(result.get(0).get(1));
									Departure.setText("----");
									BookedSeats.setText("----");
									TotalSeats.setText(result.get(0).get(2));
									AvailableSeats.setText(result.get(0).get(2));
								}
							} catch (Exception e) {
								//TODO: handle exception
								System.err.println (e.getMessage());
							}
						}
						else {
							try {
								// Start Timer
								long startTime = System.currentTimeMillis();

								String query = "SELECT F.flightNum, F.origin, F.destination, B.departure, B.c, F.seats, (F.seats - B.c) ";
								query += "FROM Flight F, ( ";
								query += "SELECT flightNum, departure, count(flightNum) AS c ";
								query += "FROM Booking ";
								query += "WHERE flightNum = \'" + flight_number_box.getSelectedItem().toString();
								query += "\' AND departure = \'" + departure_box.getSelectedItem().toString()+ "\' ";
								query += "GROUP BY flightNum, departure ";
								query += ") AS B ";
								query += "WHERE F.flightNum = B.flightNum;";
								List<List<String>> result = executeQueryAndReturnResult(query);
								if (result.size() > 0) {
									flight_number.setText(result.get(0).get(0));
									Origin.setText(result.get(0).get(1));
									Destination.setText(result.get(0).get(2));
									Departure.setText(result.get(0).get(3));
									BookedSeats.setText(result.get(0).get(4));
									TotalSeats.setText(result.get(0).get(5));
									AvailableSeats.setText(result.get(0).get(6));
								}
								// End Timer		
								long endTime = System.currentTimeMillis();
								
								status_label.setText("Runtime: " + (endTime - startTime) + " ms");
							} catch (Exception e) {
								//TODO: handle exception
								System.err.println (e.getMessage());
							}
						}
					}
					lock = 0;
				}
			}
		});
	}
	
	private void Choice_Ten () {
		System.out.printf("menu_10\n");
		system_end();
	}

	private void Info_Section() {
		tablePanel = new JPanel(new BorderLayout());

		JTextArea jta = new JTextArea();
		jta.setEditable(false);
		jta.setLineWrap(true);
		jta.setFont(new Font("Serif", 0, 18));
		String msg = "";
		//msg += " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa \n";
		msg += " ****************************************************\n";
		msg += "       UCR 2017 Fall CS 166 - Database Management System\n";
		msg += " ****************************************************\n";
		msg += " Instructor: Prof. Vassilis Tsotras\n";
		msg += " http://www.cs.ucr.edu/~tsotras/\n";
		msg += " TA: Vasilis Zois\tSection 22\n\n";
		msg += " This program is made by Leng Zhang and Nelly(Qiwen) Lyu.\n\n";
		msg += " Leng Zhang :          https://github.com/lengzhang\n\n";
		msg += " Nelly(Qiwen) Lyu : https://github.com/qlyu001\n";
		msg += " ****************************************************\n";
		msg += "     In this project, we will model and build an internet booking \n";
		msg += " system for airlines. We will use this system to track information \n";
		msg += " about different routes that they offer, passenger booking \n";
		msg += " information as wellas their reviews/ratings for a given flight.\n\n";
		msg += "     We not only finished the basic query, but also built up this\n";
		msg += " user friendly interface.\n";
        msg += "     Here, we built up nine different functions:\n";
		msg += "         1. we can insert a passenger into the system;\n";
        msg += "         2. we can book a flight;\n";
        msg += "         3. take in the customer review;\n";
        msg += "         4. it can insert or update flights;\n";
		msg += "         5. it can list the available flights between origin and\n";
		msg += "             destination function;\n";
        msg += "         6. it can list the the most popular destinations;\n";
        msg += "         7. it can list the highest reated routes;\n";
        msg += "         8. it can list the flight in order of the duration;\n";
        msg += "         9. it can finf the available seats for flight.\n";


		jta.setText(msg);
		jta.setCaretPosition(0);

		JScrollPane jsp = new JScrollPane(jta);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setPreferredSize(new Dimension(505, 400));

		tablePanel.add(jsp, BorderLayout.CENTER);
		tablePanel.add(back_to_menu, BorderLayout.SOUTH);

		status_label.setText("Information");
		
		Main_Frame_Update(mainPanel, tablePanel);
	}
}