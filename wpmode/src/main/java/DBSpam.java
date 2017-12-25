import java.sql.*;
import java.util.LinkedList;


public class DBSpam {
    private static final String url = "jdbc:sqlite:../backend/spam.db";
    private static final String user = "";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public DBSpam() {

        String query = "show tables";

    }

    private void DBConnect() {
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url);

        } catch (SQLException e) {
            try {
                con.close();
            } catch (SQLException e1) {
                System.out.println("!!!!NOT CONNECTED: SPAM!!!!");
            }
        }
    }

    public void execUpdate(String query) {
        DBConnect();
        try {
            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            int count = stmt.executeUpdate(query);
            System.out.println("DB SPAM: SQL Updated:" + count + " rows!");

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    public LinkedList<String> getAllSpam() {
        DBConnect();

        String query = makeSQLqueryGetAllSpam();

        LinkedList<String> allSpam = new LinkedList<>();

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                allSpam.add( rs.getString("spam_text"));
            }
            return allSpam;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            //sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }


    public ResultSet execQuery(String query) {
        DBConnect();
        try {
            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query


            //result это указатель на первую строку с выборки
            //чтобы вывести данные мы будем использовать
            //метод next() , с помощью которого переходим к следующему элементу
            System.out.println("Выводим statement");

            rs = stmt.executeQuery(query);
            return rs; //не факт что сработает

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            System.out.println(11);
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public String makeSQLInsertSpam(String spam) {

        //myDB.execUpdate(makeSQLInsertNewPost);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        stringBuilder.append("Spam ");
        stringBuilder.append("(spam_text)");
        stringBuilder.append("values");
        stringBuilder.append("(");
        stringBuilder.append("'" + spam + "'");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }



    public String makeSQLqueryGetAllSpam() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM Spam");

        return stringBuilder.toString();
    }


    public void closeConnection() {
        try {
            con.close();
        } catch (Exception e) {
        }
        try {
            stmt.close();
        } catch (Exception e) {
        }
        try {
            rs.close();
        } catch (Exception e) {
        }
    }
}