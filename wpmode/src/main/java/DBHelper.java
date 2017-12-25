

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;


public class DBHelper {
    private static final String url = "jdbc:sqlite:../backend/data.db";
    private static final String user = "";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public DBHelper() {

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
                System.out.println("!!!!NOT CONNECTED!!!!");
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

            System.out.println("SQL Updated:" + count + " rows!");

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    public LinkedList<PostDB> getAllPosts() {
        DBConnect();

        String query = makeSQLqueryGetAllPosts();

        LinkedList<PostDB> allPosts = new LinkedList<>();
        PostDB clear = new PostDB();

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                allPosts.add(new PostDB(
                        rs.getLong("post_id"),
                        rs.getLong("post_owner_id"),
                        rs.getLong("post_like"),
                        rs.getLong("post_repost"),
                        rs.getLong("post_view"),
                        clear.textToListString(rs.getString("post_tags")),
                        rs.getLong("post_time"),
                        rs.getString("post_url"),
                        rs.getString("post_text"),
                        rs.getLong("id")
                ));
            }
            return allPosts;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            //sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public int getWorker(String query) {
        DBConnect();
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery(query);
            int result = 0;
            while (rs.next()) {
               /* result = new int(
                        rs.getString("login"),
                        rs.getString("fullName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("position"),
                        rs.getString("phoneNumber"),
                        rs.getString("country"),
                        rs.getString("photo"),
                        rs.getString("workersCategory"),
                        rs.getString("workersLoad"),
                        rs.getString("workersFeedback")
                );
                */
            }
            return result;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return 0;
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

    public String makeSQLInsertNewPost(PostDB post) {

        //myDB.execUpdate(makeSQLInsertNewPost);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        stringBuilder.append("Posts ");
        stringBuilder.append("(post_url, post_text, post_id, post_owner_id, post_like, post_repost, post_view, post_tags, post_time)");
        stringBuilder.append("values");
        stringBuilder.append("(");
        stringBuilder.append("'" + post.POST_URL + "',");
        stringBuilder.append("'" + post.POST_TEXT + "',");
        stringBuilder.append("'" + post.POST_ID + "',");
        stringBuilder.append("'" + post.POST_OWNER_ID + "',");
        stringBuilder.append("'" + post.POST_LIKE + "',");
        stringBuilder.append("'" + post.POST_REPOST + "',");
        stringBuilder.append("'" + post.POST_VIEW + "',");
        stringBuilder.append("'" + post.getPOST_TAGS() + "',");
        stringBuilder.append("'" + post.POST_TIME + "'");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }


    public String makeSQLInsertNewProject(ProjectDB project) {

        //myDB.execUpdate(makeSQLInsertNewPost);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        stringBuilder.append("Projects ");
        stringBuilder.append("(user_id, time, posts_id, posts_tags)");
        stringBuilder.append("values");
        stringBuilder.append("(");
        stringBuilder.append("'" + project.USER_ID + "',");
        stringBuilder.append("'" + project.TIME + "',");
        stringBuilder.append("'" + project.listStringToText(project.POSTS_ID) + "',");
        stringBuilder.append("'" + project.listStringToText(project.POSTS_TAGS) + "'");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    //DELETE FROM Projects where `posts_tags` = "#"

    public String makeSQLDeleteProjectByTags(String posts_tags) {

        //myDB.execUpdate(makeSQLDeleteProjectByTags);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM Projects where `posts_tags` = ");
        stringBuilder.append("'");
        stringBuilder.append(posts_tags);
        stringBuilder.append("'");
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String makeSQLDeletePostsByTags(String posts_tags) {

        //myDB.execUpdate(makeSQLDeleteProjectByTags);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM Posts where `post_tags` = ");
        stringBuilder.append("'");
        stringBuilder.append(posts_tags);
        stringBuilder.append("'");
        stringBuilder.append(";");
        return stringBuilder.toString();
    }


    public String makeSQLUpdateProject(String posts_id, Long _id) {
        //myDB.execUpdate("SELECT * FROM borolis.borolis WHERE login='TestUser'");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE Projects ");
        stringBuilder.append("SET ");

        stringBuilder.append("`posts_id`=");
        stringBuilder.append("'");
        stringBuilder.append(posts_id);
        stringBuilder.append("'");

        stringBuilder.append(" WHERE `id`=");
        stringBuilder.append("'");
        stringBuilder.append(_id);
        stringBuilder.append("'");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public LinkedList<ProjectDB> getAllProjects() {
        DBConnect();

        String query = makeSQLqueryGetAllProjects();

        LinkedList<ProjectDB> allProjects = new LinkedList<>();
        ProjectDB clear = new ProjectDB();

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                allProjects.add(new ProjectDB(
                        rs.getLong("user_id"),
                        rs.getLong("time"),
                        clear.textToListString(rs.getString("posts_id")),
                        clear.textToListString(rs.getString("posts_tags")),
                        rs.getLong("id")
                                                ));
            }
            return allProjects;

        } catch (Exception sqlEx) {
            System.out.println(sqlEx.toString());
            //sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }






    //

    public ProjectDB getProjectByTags(LinkedList<String> tags) {
        DBConnect();

        ProjectDB result = new ProjectDB();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM Projects WHERE `posts_tags`='");
        stringBuilder.append(result.listStringToText(tags));
        stringBuilder.append("';");
        String query = stringBuilder.toString();

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            rs.next();
                result = new ProjectDB(
                            rs.getLong("user_id"),
                            rs.getLong("time"),
                            result.textToListString(rs.getString("posts_id")),
                            result.textToListString(rs.getString("posts_tags")),
                            rs.getLong("id")
                            );

            return result;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            //sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public String getLastInsertId() {
        DBConnect();
        String query = "SELECT MAX(id) FROM Posts";
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery(query);
            String result = "";
            while (rs.next()) {
                result = rs.getString("MAX(id)");
            }
            return result;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return "-1";
    }


    public String makeSQLInsertAuth(String login, String session_id) {

        //myDB.execUpdate("INSERT INTO borolis (login, password, email, session_id) values('TestUser', '123456', 'boroliska@gmail.com', 'id001')");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        stringBuilder.append("sessionidtologin ");
        stringBuilder.append("(login, session_id)");
        stringBuilder.append("values");
        stringBuilder.append("(");
        stringBuilder.append("'" + login + "',");
        stringBuilder.append("'" + session_id + "'");
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    public String makeSQLDeleteAuth(String session_id) {
        System.out.println(session_id);
        //myDB.execUpdate("DELETE FROM `borolis`.`sessionidtologin` WHERE `session_id`='sessionId';");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM `borolis`.`sessionidtologin` WHERE `session_id`='");
        stringBuilder.append(session_id);
        stringBuilder.append("';");
        return stringBuilder.toString();
    }


    public String makeSQLqueryGetAccByLogin(String login) {
        //myDB.execUpdate("SELECT * FROM borolis.borolis WHERE login='TestUser'");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM borolis.borolis WHERE login='");
        stringBuilder.append(login);
        stringBuilder.append("';");

        return stringBuilder.toString();
    }


    public String makeSQLqueryGetWorkerByLogin(String login) {
        //myDB.execUpdate("SELECT * FROM borolis.borolis WHERE login='TestUser'");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM borolis.workers WHERE login='");
        stringBuilder.append(login);
        stringBuilder.append("';");

        return stringBuilder.toString();
    }
/*

    public Account getAccBySession(String session_id) {
        DBConnect();
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery(makeSQLqueryGetLoginBySession(session_id));

            String login = null;
            while (rs.next()) {
                login = rs.getString("login");
            }
            if (login == null) {
                return null;
            }
            Account result = getAccount(makeSQLqueryGetAccByLogin(login));
            return result;

        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.toString());
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }
*/


    public String makeSQLqueryGetLoginBySession(String session_id) {
        //myDB.execUpdate("SELECT * FROM borolis.borolis WHERE login='TestUser'");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT login FROM borolis.sessionidtologin WHERE session_id='");
        stringBuilder.append(session_id);
        stringBuilder.append("';");

        return stringBuilder.toString();
    }


    public String makeSQLqueryGetAllPosts() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM Posts");

        return stringBuilder.toString();
    }
    public String makeSQLqueryGetAllProjects() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM Projects");

        return stringBuilder.toString();
    }



    public String makeSQLupdateUpdatePost(String postLike, String postRepost, String postView, String postUrl) {
        //myDB.execUpdate("SELECT * FROM borolis.borolis WHERE login='TestUser'");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE Posts ");
        stringBuilder.append("SET ");
        stringBuilder.append("`post_like`=");
        stringBuilder.append("'");
        stringBuilder.append(postLike);
        stringBuilder.append("'");

        stringBuilder.append(", ");

        stringBuilder.append("`post_repost`=");
        stringBuilder.append("'");
        stringBuilder.append(postRepost);
        stringBuilder.append("'");

        stringBuilder.append(", ");

        stringBuilder.append("`post_view`=");
        stringBuilder.append("'");
        stringBuilder.append(postView);
        stringBuilder.append("'");

        stringBuilder.append(" WHERE `post_url`='");
        stringBuilder.append(postUrl);
        stringBuilder.append("';");

        return stringBuilder.toString();
    }


    /*
    public String makeSQLupdateUpdateWorker(Worker worker) {

        //UPDATE `borolis`.`workers` SET `position` = 'Teamlead2', `fullName` = 'boriska' WHERE `workers`.`login` = 'borolis';

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE `borolis`.`workers` SET ");

//UPDATE `workers` SET `_id`=[value-1],`login`=[value-2],`fullname`=[value-3],`email`=[value-4],
// `password`=[value-5],`position`=[value-6],`phonenumber`=[value-7],`country`=[value-8],`photo`=[value-9],
// `workersCategory`=[value-10],`workersload`=[value-11],`workersfeedback`=[value-12] WHERE 1

        stringBuilder.append("`login`=" + "'" + worker.getLogin() + "', ");

        stringBuilder.append("`fullName`=" + "'" + worker.getFullName() + "', ");

        stringBuilder.append("`email`=" + "'" + worker.getEmail() + "', ");

        stringBuilder.append("`password`=" + "'" + worker.getPassword() + "', ");

        stringBuilder.append("`position`=" + "'" + worker.getPosition() + "', ");

        stringBuilder.append("`phoneNumber`=" + "'" + worker.getPhoneNumber() + "', ");

        stringBuilder.append("`country`=" + "'" + worker.getCountry() + "', ");

        stringBuilder.append("`photo`=" + "'" + worker.getPhoto() + "', ");

        stringBuilder.append("`workersCategory`=" + "'" + worker.getWorkersCategory() + "', ");

        stringBuilder.append("`workersLoad`=" + "'" + worker.getWorkersLoad() + "', ");

        stringBuilder.append("`workersFeedback`=" + "'" + worker.getWorkersFeedback() + "' ");

        stringBuilder.append(" WHERE `workers`.`login`='");
        stringBuilder.append(worker.getLogin());
        stringBuilder.append("';");
        System.out.println("|" + stringBuilder.toString() + "|");
        return stringBuilder.toString();
    }
*/

    //UPDATE `borolis`.`borolis` SET `session_id`='id9399' WHERE `_id`='2';


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