import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sqlite.core.DB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class APIServlet extends HttpServlet {
    DBHelper dbHelper;
    VKStreamingAPIHandler vkStreamingAPIHandler;

    public APIServlet(VKStreamingAPIHandler _vkStreamingAPIHandler, DBHelper _dbHelper) {
        this.vkStreamingAPIHandler = _vkStreamingAPIHandler;
        this.dbHelper = _dbHelper;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Somewhere here is our API, use POST");
        response.setContentType("text/html;charset=windows-1251");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }


        JSONObject jsonObject = (JSONObject) JSONValue.parse(jb.toString());
        APIRequest apiRequest = new APIRequest(jsonObject, vkStreamingAPIHandler, dbHelper );

        if(apiRequest.isValid())
        {
              vkStreamingAPIHandler.printRules(vkStreamingAPIHandler.getRules());
              response.setStatus(HttpServletResponse.SC_OK);
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        ///TODO виепат теймура

    }

}

