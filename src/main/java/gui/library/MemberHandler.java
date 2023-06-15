package gui.library;

import java.sql.*;

public class MemberHandler {
    private static final String DB_url = "jdbc:derby:database/forum;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;
    public static MemberHandler handler;
    public static String tableName = "Member";

    //Constructor
    public MemberHandler(){
        createConnection();
        createTable();
    }

    /**
     * Get the single instance of the database handler
     * @return database handler
     */

    public static MemberHandler getHandler(){
        if(handler == null){
            handler = new MemberHandler();
            return handler;
        }else{
            return handler;
        }
    }

    /**
     * Create Table with different columns in the database
     */
    private void createTable() {
        // Name of the folder stored from user input
        String TABLE_NAME = tableName;
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dmn = conn.getMetaData();
            ResultSet tables = dmn.getTables(null,null,TABLE_NAME,null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME+ " exists");
            }
            else{
                String statement = "CREATE TABLE " + TABLE_NAME + " ("
                        + "firstName varchar(200), \n"
                        + "lastName varchar(200), \n"
                        + "pinNumber varchar(200), \n"
                        + "userId varchar(200), \n"
                        + "Title varchar(200), \n"
                        + "Author varchar(200), \n"
                        + "Genre varchar(200), \n"
                        + "Quantity varchar(200), \n"
                        + "Id varchar(200))";
                //System.out.println(statement);
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create Connection with the database
     */
    private void createConnection() {
        try{
            conn = DriverManager.getConnection(DB_url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the action included in the string
     * @param qu action as a string
     * @return True if the action is executed or False if it is not executed
     */
    public boolean execAction(String qu) {
        try{
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Did not enter data");

        }
        return false;
    }

    /**
     * Create an updatable ResultSet object from the query
     * @param query String, ex. "SELECT * FROM " + database table name
     * @return The resultSet or null if the database does not exist
     */
    public ResultSet execQuery(String query) {
        ResultSet resultSet;
        try{
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("There is no such table.");
            e.printStackTrace();
            return null;
        }
        return resultSet;
    }
}
