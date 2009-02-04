package util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateDB {

    public static void main(String[] args) {
        System.out.println("Creating database ...");
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            //initialize driver and register it with DriverManager
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            
            //connect and create the db if not present
            connection = DriverManager.getConnection(
                    "jdbc:derby:target/store_db;create=true",
                    "",
                    "");
            
            
            try {
                preparedStatement = connection.prepareStatement("DROP TABLE CATALOG");
                preparedStatement.execute();
            }catch(Exception e) {
                //ignore to avoid erros when db is being created from scratch
            }
            
            
            preparedStatement = connection.prepareStatement("CREATE TABLE CATALOG("
                                                            + "id             NUMERIC(5 , 0) NOT NULL,"
                                                            + "product_name   VARCHAR(30),"
                                                            + "currency_code  CHAR(3),"
                                                            + "price          REAL,"
                                                            + "primary key (id)"
                                                            + ")");
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("INSERT INTO CATALOG VALUES(0,'Apple', 'USD', 2.99)");
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("INSERT INTO CATALOG VALUES(1,'Orange', 'USD', 3.55)");
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("INSERT INTO CATALOG VALUES(2,'Pear', 'USD', 1.55)");
            preparedStatement.execute();

            System.out.println("Done !");
            
        } catch (SQLException ex) {         
            ex.printStackTrace();
        }catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
