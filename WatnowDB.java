//STEP 1. Import required packages
import java.sql.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
//TODO, VA ENTRE COMILLAS LOS VALORES DE EL INSERT EN SQL???

public class WatnowDB {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = ESCRIBIRURL;

   //  Database credentials
   Connection conn;
   Statement stmt;
   LatitudeAndLongitudeWithPincode coordinateManager;
   
   public void initDatabaseConnection(String user,String pass){
      try{
         Class.forName("com.mysql.jdbc.Driver");
         System.out.println("Connecting to database...");
         conn = DriverManager.getConnection(DB_URL,user,pass);
               }catch(Exception ex){
         ex.printStackTrace();
         SOP("Ha ocurrido un error intentando conectar a la BBDD.");
         System.exit(-1);
      }

   }
   
   public void closeDatabaseConnection(){
   try{conn.close();}catch(Exception e){ e.printStackTrace();}
   }
   
   public String getFileName(){
   Scanner scanner = new Scanner(System.in);
   System.out.println("Por favor introduce el nombre del archivo:");
   String file = scanner.next();
   return file;
   }
   
   public void startProcess(String filename){
   BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ";";
   try {

		br = new BufferedReader(new FileReader(filename));
		br.readLine();
		while ((line = br.readLine()) != null) {
         String[] actualLine = line.split(cvsSplitBy);
         insertarNuevoLocal(actualLine);
         
      }}catch(Exception e){
      e.printStackTrace();
      }

   }
   
   
   public String getCityId(String city){
      String cityId=null;
      try{
	   String sql = "SELECT iCityId FROM city WHERE vCityName='" + city + "';";
      stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);
       rs.next();
		cityId = rs.getString("iCityId");
		rs.close();
      }catch(Exception e){e.printStackTrace();}
		if(cityId==null){
      pak("Error intentando recuperar el cityId");
      System.exit(-1);
      }
      return cityId;
   }
   
   public int getVenueRowId(String name, String address){
	   int iVenueId = -1;
      try{
      String sql = "SELECT iVenueId FROM venue WHERE vName='" + name + "' AND vAddress='" + address + "'";
	    
       ResultSet rs = stmt.executeQuery(sql);
       rs.next();
		iVenueId = rs.getInt("iVenueId");
		rs.close();
      }catch(Exception e){e.printStackTrace();}
      if(iVenueId==-1){
      pak("Error intentando recuperar el iVenueId");
      System.exit(-1);
      }
		return iVenueId;
	   
   }
   
   public String[] getCoordinates(String address){
      String[] coordinates = null;
      try{
      coordinates = coordinateManager.getLatLongPositions(address);
      }catch(Exception e){
      e.printStackTrace();
      }
      if(coordinates.equals(null)){
      pak("Error intentando conseguir las coordenadas.");
      System.exit(-1);
      }
      return coordinates;
   }
   
   public VenueBean crearVenueBean(String[] linea){
	   String establishment = linea[0];
	   String address = linea[1];
	   String postcode = linea[2];
	   String city = linea[3];
	   String iCityId = getCityId(city);
	   int cityId = Integer.parseInt(iCityId);
	   String country = linea[4];
	   String venueType = linea[5];
	   String priceString = linea[6];
	   int price = Integer.parseInt(priceString);
	   String phoneNumber = linea[7];
	   String[] coordinates = getCoordinates(address + "," + city + "," + postcode + "," + country);
	   BigDecimal latitude = new BigDecimal(coordinates[0]);
	   BigDecimal longitude = new BigDecimal(coordinates[1]);
	   String vCountry = "1"; //TODO ESTE SIEMPRE ES UNO
	   java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	    
	    VenueBean venue = new VenueBean(establishment, address, postcode, city, cityId, country, venueType,latitude, longitude, phoneNumber, price,sqlDate, sqlDate );
	    return venue;
   }
   
   public VenueTimetableBean crearVenueTimetable(String[] linea){
	   String mondayOpen = linea[8];
	   String mondayClose = linea[9];
	   String tuesdayOpen = linea[10];
	   String tuesdayClose = linea[11];
	   String wednesdayOpen = linea[12];
	   String wednesdayClose = linea[13];
	   String thursdayOpen = linea[14];
	   String thursdayClose = linea[15];
	   String fridayOpen = linea[16];
	   String fridayClose = linea[17];
	   String saturdayOpen = linea[18];
	   String saturdayClose = linea[19];
	   String sundayOpen = linea[20];
	   String sundayClose = linea[21];
	   VenueTimetableBean timetable = new VenueTimetableBean(mondayOpen, mondayClose, tuesdayOpen, tuesdayClose, wednesdayOpen, wednesdayClose, thursdayOpen, thursdayClose, fridayOpen, fridayClose, saturdayOpen, saturdayClose, sundayOpen, sundayClose);
	   return timetable;
   }
   
   public void insertVenue(VenueBean venue, VenueTimetableBean timetable){


	   String sql = "INSERT INTO venue( vName, vAddress, vPostalCode, iCityId, vCountry, iLatitude, iLongitude, vPhonenumber, vPriceRange, tDescription, iLikes, iDislikes, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'"+venue.establishment+"\',\'"+venue.address+"\',\'"+venue.postalCode+"\',\'"+venue.iCityId+"\',\'"+venue.country+"\',\'"+venue.latitude+"\',\'"+venue.longitude+"\',\'"+venue.phonenumber+"\',\'"+venue.priceRange+"\',\'"+venue.tDescription+"\',\'"+venue.likes+"\',\'"+venue.dislikes+"\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')";
	   String timetablesql ="";
	   String venuetypesql ="";
	   try{
	   stmt = conn.createStatement();
	   
		   SOP("Escribiendo en la tabla venue...");
		   stmt.executeUpdate(sql);
	   }catch(SQLException ex){
		   SOP("Error al intentar escribir en la tabla venue.");
		   ex.printStackTrace();
		   System.exit(-1);
	   }
	   try{
		   SOP("Escribiendo en la tabla venuetime...");
		   int venueRowId =getVenueRowId(venue.establishment, venue.address);
		   insertTimetable(stmt, timetable, venueRowId, venue);
	   }catch(SQLException ex){
		   ex.printStackTrace();
		   SOP("Error al intentar escribir en la tabla venuetime, procediendo a borrar el registro creado en la tabla venue.");
		   borrarRegistroVenue(venue.establishment, venue.address);
		   System.exit(-1);
	   }
	   
	   try{
		   SOP("Escribiendo en la tabla venueType...");
		   int venueRowId =getVenueRowId(venue.establishment, venue.address);
		   String sqlVenueType = "INSERT INTO venuetype (iVenueId,eType, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'" + venue.venueType + "\',\'"+venue.dInsertedDateTime +"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')";
		   stmt.executeUpdate(sqlVenueType);
	   }catch(SQLException ex){
		   ex.printStackTrace();
		   SOP("Error al intentar escribir en la tabla venuetime, procediendo a borrar el registro creado en las tabla venue y venuetime.");
		   borrarRegistrosVenueyTimetable(venue.establishment, venue.address);
		   System.exit(-1);
	   }
	   SOP("Insertado correctamente el local: " + venue.establishment);
	   try{stmt.close();}catch(SQLException e){e.printStackTrace();}
   }
   
   public void borrarRegistroVenue(String establishment, String address){
	   int venueRowId = -1;
	   try{
		   venueRowId =getVenueRowId(establishment, address);
		   stmt = conn.createStatement();
		   String erasevenue = "DELETE * FROM venue WHERE iVenueId= \'" + Integer.toString(venueRowId) + "\'" ;
		   stmt.executeUpdate(erasevenue);
	   }catch(SQLException ex){
		   ex.printStackTrace();
		   pak("ERROR AL INTENTAR BORRAR EL REGISTRO " + establishment + ". BORRARLO MANUALMENTE. ROWID = " + venueRowId);
	   }
   }
   
   public void borrarRegistrosVenueyTimetable(String establishment, String address){
	   int venueRowId = -1;
	   try{
		   venueRowId =getVenueRowId(establishment, address);
		   stmt = conn.createStatement();
		   String erasevenue = "DELETE * FROM venue WHERE iVenueId= \'" + Integer.toString(venueRowId) + "\'" ;
		   String erasetimetable = "DELETE * FROM venuetime WHERE iVenueId= \'" + Integer.toString(venueRowId) + "\'" ;
		   stmt.executeUpdate(erasevenue);
		   stmt.executeUpdate(erasetimetable);
	   }catch(SQLException ex){
		   ex.printStackTrace();
		   pak("ERROR AL INTENTAR BORRAR EL REGISTRO " + establishment + ". BORRARLO MANUALMENTE DE LAS TABLAS venue Y venuetime. ROWID = " + venueRowId);
	   }
   }
   public void insertTimetable(Statement stmt, VenueTimetableBean timetable, int venueRowId, VenueBean venue) throws SQLException{
	  String monday=null;
	  String tuesday=null;
	  String wednesday=null;
	  String thursday=null;
	  String friday=null;
	  String saturday=null;
	  String sunday=null;
	//Monday
	  
	  if(timetable.monO.equalsIgnoreCase("closed")||timetable.monC.equalsIgnoreCase("closed")){
		   //monday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Monday\',\'"+timetable.monO +"\',\'"+timetable.monC+"\',"+ "\'Monday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 

	  }else{
		  if((Integer.parseInt(timetable.monO.split(":")[0]))>Integer.parseInt(timetable.monC.split(":")[1])){
		   monday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Monday\',\'"+timetable.monO +"\',\'"+timetable.monC+"\',"+ "\'Tuesday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
		  }else{
		   monday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Monday\',\'"+timetable.monO +"\',\'"+timetable.monC+"\',"+ "\'Monday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
		  }
	  }
	  
	  
	  
	  //Tuesday
	  if(timetable.tusO.equalsIgnoreCase("closed")||timetable.tusC.equalsIgnoreCase("closed")){
		 //  tuesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Tuesday\',\'"+timetable.tusO +"\',\'"+timetable.tusC+"\',"+ "\'Tuesday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{
	  
	   if((Integer.parseInt(timetable.tusO.split(":")[0]))>Integer.parseInt(timetable.tusC.split(":")[1])){
		   tuesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Tuesday\',\'"+timetable.tusO +"\',\'"+timetable.tusC+"\',"+ "\'Wednesday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   tuesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Tuesday\',\'"+timetable.tusO +"\',\'"+timetable.tusC+"\',"+ "\'Tuesday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	  
	  
	  //Wednesday
	  if(timetable.wesO.equalsIgnoreCase("closed")||timetable.wesC.equalsIgnoreCase("closed")){
		 //  wednesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Wednesday\',\'"+timetable.wesO +"\',\'"+timetable.wesO+"\',"+ "\'Wednesday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{
	   if((Integer.parseInt(timetable.wesO.split(":")[0]))>Integer.parseInt(timetable.wesC.split(":")[1])){
		   wednesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Wednesday\',\'"+timetable.wesO +"\',\'"+timetable.wesO+"\',"+ "\'Thursday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   wednesday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Wednesday\',\'"+timetable.wesO +"\',\'"+timetable.wesO+"\',"+ "\'Wednesday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	  
	  
	  //Thursday
	  if(timetable.thuO.equalsIgnoreCase("closed")||timetable.thuC.equalsIgnoreCase("closed")){
		   //thursday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Thursday\',\'"+timetable.thuO +"\',\'"+timetable.thuC+"\',"+ "\'Thursday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{	  
	   if((Integer.parseInt(timetable.thuO.split(":")[0]))>Integer.parseInt(timetable.thuC.split(":")[1])){
		   thursday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Thursday\',\'"+timetable.thuO +"\',\'"+timetable.thuC+"\',"+ "\'Friday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   thursday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Thursday\',\'"+timetable.thuO +"\',\'"+timetable.thuC+"\',"+ "\'Thursday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	 
	  
	  //Friday
	  if(timetable.friO.equalsIgnoreCase("closed")||timetable.friC.equalsIgnoreCase("closed")){
		   //friday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Friday\',\'"+timetable.friO +"\',\'"+timetable.friC+"\',"+ "\'Friday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{
	  
	   if((Integer.parseInt(timetable.friO.split(":")[0]))>Integer.parseInt(timetable.friC.split(":")[1])){
		   friday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Friday\',\'"+timetable.friO +"\',\'"+timetable.friC+"\',"+ "\'Saturday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   friday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Friday\',\'"+timetable.friO +"\',\'"+timetable.friC+"\',"+ "\'Friday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	  
	  
	  
	  //Saturday
	  if(timetable.satO.equalsIgnoreCase("closed")||timetable.satC.equalsIgnoreCase("closed")){
		   //saturday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Saturday\',\'"+timetable.satO +"\',\'"+timetable.satC+"\',"+ "\'Saturday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{
	  
	   if((Integer.parseInt(timetable.satO.split(":")[0]))>Integer.parseInt(timetable.satC.split(":")[1])){
		   saturday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Saturday\',\'"+timetable.satO +"\',\'"+timetable.satC+"\',"+ "\'Sunday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   saturday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Saturday\',\'"+timetable.satO +"\',\'"+timetable.satC+"\',"+ "\'Saturday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	  
	  
	  
	  //Sunday
	  if(timetable.sunO.equalsIgnoreCase("closed")||timetable.sunC.equalsIgnoreCase("closed")){
		   //sunday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Sunday\',\'"+timetable.sunO +"\',\'"+timetable.sunC+"\',"+ "\'Sunday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	  }else{
		  if((Integer.parseInt(timetable.sunO.split(":")[0]))>Integer.parseInt(timetable.sunC.split(":")[1])){
		   sunday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Sunday\',\'"+timetable.sunO +"\',\'"+timetable.sunC+"\',"+ "\'Monday',\'0\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }else{
		   sunday = "INSERT INTO venuetime (iVenueId, eDay, tStartTime, tEndTime, eEndDay, iSameDay, dInsertedDateTime, dUpdatedDateTime, iUpdatedBy ) VALUES (\'" + venueRowId + "\',\'Sunday\',\'"+timetable.sunO +"\',\'"+timetable.sunC+"\',"+ "\'Sunday',\'1\',\'"+venue.dInsertedDateTime+"\',\'"+venue.dUpdatedDateTime+"\',\'"+venue.updatedBy+"\')"; 
	   }}
	 if(monday!=null){
	  stmt.executeUpdate(monday);
	  }
	 if(tuesday!=null){
		  stmt.executeUpdate(tuesday);
		  }
	 if(wednesday!=null){
		  stmt.executeUpdate(wednesday);
		  }
	 if(thursday!=null){
		  stmt.executeUpdate(thursday);
		  }
	 if(friday!=null){
		  stmt.executeUpdate(friday);
		  }
	 if(saturday!=null){
		  stmt.executeUpdate(saturday);
		  }
	 if(sunday!=null){
		  stmt.executeUpdate(sunday);
		  }	   
   }
   
   public void insertarNuevoLocal(String[] linea){
	if(linea.length==0){
		SOP("Reached the end of the file (or empty line has been encountered).");
		return;
	}
   VenueBean venueBean = crearVenueBean(linea);
   VenueTimetableBean timetable = crearVenueTimetable(linea);
   insertVenue(venueBean, timetable);
   }
   
   public void SOP(String message){
	   System.out.println(message);
	   return;
	   
   }
   
    private void pak(String message)
 { 
        System.out.println(message);
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
 }
   
   public void consultaLocales(Scanner scanner) throws Exception{
	   SOP("Escribe el nombre del local que quieres consultar o dejalo en blanco para consultar la tabla entera:");
	   scanner.nextLine();
	   String nameOfVenue = scanner.nextLine();
	   String sqlVenueQuery;
	   if(!nameOfVenue.equals("")){
		   sqlVenueQuery = "SELECT * FROM venue WHERE vName=\'" + nameOfVenue + "\'";
	   }else{
		   sqlVenueQuery = "SELECT * FROM venue ";
	   }
      stmt = conn.createStatement();
	   ResultSet rs = stmt.executeQuery(sqlVenueQuery);
	   SOP("Consulta realizada correctamente.¿Quieres escribirlo en un archivo log.txt? (Y/N) -  Si no eliges Y el contenido será impreso en pantalla.");
	   String opcion = scanner.nextLine();
	   if(opcion.equals("Y")){
		   FileWriter fw = new FileWriter("logConsultaLocal_"+nameOfVenue+".doc");
		   BufferedWriter writer = new BufferedWriter(fw);
		   while(rs.next()){
			   writer.append("----------------------------------------");
			   writer.newLine();
			   writer.append("iVenueId: " + rs.getString("iVenueId"));
			   writer.newLine();
			   writer.append("vName: " + rs.getString("vName"));
			   writer.newLine();
			   writer.append("vAddress: " + rs.getString("vAddress"));
			   writer.newLine();
			   writer.append("vPostalCode: " + rs.getString("vPostalCode"));
			   writer.newLine();
			   writer.append("iCityId: " + rs.getString("iCityId"));
			   writer.newLine();
			   writer.append("vCountry: " + rs.getString("vCountry"));
			   writer.newLine();
			   writer.append("iLatitude: " + rs.getString("iLatitude"));
			   writer.newLine();
			   writer.append("iLongitude: " + rs.getString("iLongitude"));
			   writer.newLine();
			   writer.append("vPhonenumber: " + rs.getString("vPhonenumber"));
			   writer.newLine();
			   writer.append("vPriceRange: " + rs.getString("vPriceRange"));
			   writer.newLine();
			   writer.append("----------------------------------------");
			   writer.newLine();
			   writer.flush();
				
		   }
		   
		      writer.append("-------------------END OF RESULTS-------------------");
		      writer.close();
		      SOP("Un archivo ha sido generado en la carpeta del programa.");
		      pak("");
	   }else{
		   while(rs.next()){
		   SOP("----------------------------------------");
			   SOP("iVenueId: " + rs.getString("iVenueId"));
			   SOP("vName: " + rs.getString("vName"));
			   SOP("vAddress: " + rs.getString("vAddress")+ "\n");
			   SOP("vPostalCode: " + rs.getString("vPostalCode"));
			   SOP("iCityId: " + rs.getInt("iCityId"));
			   SOP("vCountry: " + rs.getString("vCountry"));
			   SOP("iLatitude: " + rs.getString("iLatitude"));
			   SOP("iLongitude: " + rs.getString("iLongitude"));
			   SOP("vPhonenumber: " + rs.getString("vPhonenumber"));
			   SOP("vPriceRange: " + rs.getString("vPriceRange"));
			   SOP("----------------------------------------");
			   
		   }

		      SOP("-------------------END OF RESULTS-----------------------");
	   }
      rs.close();
      
   
	   return;
	  
	   
   }
   
   public void consultaHorariosyTipo(Scanner scanner) throws Exception{
      String sqlTimetableQuery;
      String sqlTypeQuery;
	    SOP("Escribe el rowId del local del que quieres consultar los horarios, si no lo tienes consultalo con la opcion de consultar locales:");
	   int venueId = scanner.nextInt();
	   sqlTimetableQuery = "SELECT * FROM venuetime WHERE iVenueId='" + venueId + "'";
      stmt = conn.createStatement();
	   ResultSet rs = stmt.executeQuery(sqlTimetableQuery);
	   while(rs.next()){
		   SOP("----------------------------------------");
			   SOP("iVenueId: " + rs.getString("iVenueId"));
			   SOP("eDay: " + rs.getString("eDay"));
			   SOP("tStartTime: " + rs.getString("tStartTime")+ "\n");
			   SOP("tEndTime: " + rs.getString("tEndTime"));
			   SOP("eEndDay: " + rs.getString("eEndDay"));
			   SOP("iSameDay: " + rs.getString("iSameDay"));
			   SOP("----------------------------------------");
			   
		   }
		   rs.close();
       
	    sqlTypeQuery = "SELECT * FROM venuetype WHERE iVenueId='" + venueId + "'";
      
		rs = stmt.executeQuery(sqlTypeQuery);
		while(rs.next()){
		   SOP("----------------------------------------");
			   SOP("iVenueId: " + rs.getString("iVenueId"));
			   SOP("eType: " + rs.getString("eType"));
			   SOP("----------------------------------------");
			   
		   }
         rs.close();
         
	   
   }
   public String[] introducirLocalManualmente(Scanner scanner){
	   String[] linea = new String[22];
	   SOP("Por favor introduce el establecimiento: ");
	   scanner.nextLine();
	   linea[0] =  scanner.nextLine();
	   SOP("Por favor introduce la direccion: ");
	   scanner.nextLine();
	   linea[1] = scanner.nextLine();
	   SOP("Por favor introduce el codigo postal: ");
	   scanner.nextLine();
	   linea[2] = scanner.nextLine();
	   SOP("Por favor introduce la ciudad: ");
	   scanner.nextLine();
	   linea[3] = scanner.nextLine();
	   SOP("Por favor introduce el pais: ");
	   scanner.nextLine();
	   linea[4] = scanner.nextLine();
	   SOP("Por favor introduce el tipo de local: ");
	   scanner.nextLine();
	   linea[5] = scanner.nextLine();
	   SOP("Por favor introduce el precio (numerico: 1, 2 o 3): ");
	   scanner.nextLine();
	   linea[6] = scanner.nextLine();
	   SOP("Por favor introduce numero de telefono: ");
	   scanner.nextLine();
	   linea[7] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del lunes: ");
	   scanner.nextLine();
	   linea[8] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del lunes: ");
	   scanner.nextLine();
	   linea[9] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del martes: ");
	   scanner.nextLine();
	   linea[10] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del martes: ");
	   scanner.nextLine();
	   linea[11] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del miercoles: ");
	   scanner.nextLine();
	   linea[12] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del miercoles: ");
	   scanner.nextLine();
	   linea[13] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del jueves: ");
	   scanner.nextLine();
	   linea[14] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del jueves: ");
	   scanner.nextLine();
	   linea[15] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del viernes: ");
	   scanner.nextLine();
	   linea[16] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del viernes: ");
	   scanner.nextLine();
	   linea[17] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del sabado: ");
	   scanner.nextLine();
	   linea[18] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del sabado: ");
	   scanner.nextLine();
	   linea[19] = scanner.nextLine();
	   SOP("Por favor introduce la hora de apertura del domingo: ");
	   scanner.nextLine();
	   linea[20] = scanner.nextLine();
	   SOP("Por favor introduce la hora de cierre del domingo: ");
	   scanner.nextLine();
	   linea[21] = scanner.nextLine();	   
	   return linea;
   }
   public void consultarCiudades() throws SQLException{
	   String sql = "SELECT * from city";
	   
	   stmt = conn.createStatement();
	   ResultSet rs = stmt.executeQuery(sql);
	   SOP("----LISTA DE LAS CIUDADES---");
	   SOP("----------------------------");
	   while(rs.next()){
		   SOP("*ID: " + rs.getString("iCityId")+ "  Ciudad: " + rs.getString("vCityName"));
		   SOP("----------------------------");
		   
	   }
	   SOP("--------FIN DE LISTA--------");
	   SOP("----------------------------");
	   
   }
   public void consulta(String tabla, String campodebusqueda, String valordebusqueda, String campoderespuesta) throws SQLException{
	   String sql = "SELECT * from " + tabla + " WHERE " +  campodebusqueda + "=\'" + valordebusqueda + "\'";
	   
	   stmt = conn.createStatement();
	   ResultSet rs = stmt.executeQuery(sql);
	   SOP("----LISTA DE RESPUESTA---");
	   SOP("----------------------------");
	   while(rs.next()){
		   SOP("*Campo de busqueda: " + rs.getString(campodebusqueda)+ "  Campo de respuesta: " + rs.getString(campoderespuesta));
		   SOP("----------------------------");
		   
	   }
	   SOP("--------FIN DE RESPUESTA--------");
	   SOP("----------------------------");
	   
   }
   
   public void borrar(String tabla,String columna, String id) throws SQLException{
	   if(stmt==null){
		   stmt = conn.createStatement();
	   }
	   String sql = "DELETE FROM " + tabla + " WHERE " + columna + "=\'" + id + "\'";
	   stmt.executeUpdate(sql);
   }
   
   public void run() {
   conn = null;
   stmt = null;
   Scanner scanner = new Scanner(System.in);
   coordinateManager = new LatitudeAndLongitudeWithPincode();
   while(true){
	   SOP(" ____________________________________________________________ ");
	   SOP("| Bienvenido a Watnow DB Tools v1.2 - Creado por GDJG        |");
	   SOP("|____________________________________________________________|");
	   SOP("|                                                            |");
	   SOP("|Selecciona la opcion deseada                                |");
	   SOP("| 1 - Conexion a la BBDD                                     |");
	   SOP("| 2 - Actualizacion de locales en la BBDD                    |");
	   SOP("| 3 - Comprobacion de que existe local(es) en la BBDD        |");
	   SOP("| 4 - Consultar horarios y tipo de un local                  |");
	   SOP("| 5 - Consultar ciudades                                     |");
	   SOP("| 6 - Conseguir coordenadas de una direccion                 |");
	   SOP("| 7 - Introduccion manual de un local          *             |");
	   SOP("| 8 - Consulta de valor en la base de datos    *             |");
	   SOP("| 9 - Borrar datos de la base de datos         *             |");
	   SOP("|10 - Cerrar conexion y salir del programa                   |");
	   SOP("|____________________________________________________________|");
	   SOP("|           Por favor introduce el numero deseado:           |");
	   SOP("|____________________________________________________________|");
	   int opcion = scanner.nextInt();
	   switch(opcion){
		   case 1:
				if(conn==null){
					/* TODO DESCOMENTAR ESTO
					SOP("Introduce usuario:");
					
					String user = scanner.next();
					SOP("Introduce password:");
					String password = scanner.next();*/
					initDatabaseConnection(user,password);//QUITAR LAS CONTRASE�AS DE AQUI
               SOP("Conexion correcta a la base de datos!");
               pak("__________________________________________");
				}else{
					pak("La conexion BBDD ya esta online.");
				}
				break;
			case 2:
			if(conn!=null){
			String filename = getFileName();
			startProcess(filename);
			}else{pak("ERROR: Por favor inicia la conexion a la BBDD."); }
				break;
			case 3: 
			if(conn!=null){
         try{
				consultaLocales(scanner);
            }catch(Exception e){
            pak("ERROR: Error intentando consultar los locales");
            e.printStackTrace();
            break;
            }
			}else{SOP("Por favor inicia la conexion a la BBDD.");}
				break;
			case 4: 
			if(conn!=null){
         try{
				consultaHorariosyTipo(scanner);}catch(Exception e){
            pak("ERROR: Error intentando consultar los locales");
            
            break;
            }
			}else{SOP("ERROR: Por favor inicia la conexion a la BBDD.");}
				break;
			case 5:
				if(conn!=null){
			         try{
							consultarCiudades();}catch(SQLException e){
			            pak("ERROR: Error intentando consultar los locales");
			            e.printStackTrace();
			            break;
			            }
						}else{SOP("ERROR: Por favor inicia la conexion a la BBDD.");}
				break;
			
			case 6:
				SOP("Por favor introduce la direccion de la que quieras conseguir las coordenadas:");
				scanner.nextLine();
				String direccion = scanner.nextLine();
				String [] coordenadas = getCoordinates(direccion);
				SOP("Direccion: " + direccion);
				SOP("Latitud: " + coordenadas[0]);
				SOP("Longitud: " + coordenadas[1]);
				pak("");
				break;
			case 7:
				SOP("Introduccion manual de local:");
				String[] nuevoLocal =  introducirLocalManualmente(scanner);
				insertarNuevoLocal(nuevoLocal);
				break;
			case 8:
				if(conn!=null){
			         try{
			        	 SOP("Tabla:");
			        	 String tabla = scanner.next();
			        	 SOP("Columna de busqueda:");
			        	 String campodebusqueda = scanner.next();
			        	 SOP("Valor de busqueda:");
			        	 String valordebusqueda = scanner.next();
			        	 SOP("Columna de respuesta:");
			        	 String campoderespuesta = scanner.next();
			        	 
			        	 consulta(tabla,campodebusqueda, valordebusqueda,campoderespuesta);
			        	 }catch(SQLException e){
			            pak("ERROR: Error intentando consultar.");
			            e.printStackTrace();
			            break;
			            }
						}else{SOP("ERROR: Por favor inicia la conexion a la BBDD.");}
				break;
			case 9:
				if(conn!=null){
			         try{
			        	 SOP("Tabla:");
			        	 String tabla = scanner.next();
			        	 SOP("Columna:");
			        	 String columna = scanner.next();
			        	 SOP("Valor de columna:");
			        	 String id = scanner.next();
			        	 borrar(tabla,columna,id);
			        	 pak("Los registros han sido borrados correctamente.");
			        	 }catch(SQLException e){
			            pak("ERROR: Error intentando borrar.");
			            e.printStackTrace();
			            break;
			            }
						}else{SOP("ERROR: Por favor inicia la conexion a la BBDD.");}
				break;
			case 10:
				if(conn!=null){
				closeDatabaseConnection();
            SOP("Conexion a la base de datos finalizada correctamente.");
            pak("Saliendo del programa.");
            System.exit(0);
				}
				break;
			default:
				pak("Por favor selecciona una opcion del menu.");
				break;
	   }
	   
   }
     
   }
   public static void main(String[] args){
   WatnowDB startProgram = new WatnowDB();
   startProgram.run();
   }   
}