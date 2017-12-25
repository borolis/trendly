import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {
        final DBHelper dbHelper = new DBHelper();
        DBSpam dbSpam = new DBSpam();

        SpamFilter spamFilter = new SpamFilter(0.1, 100, dbSpam);

        TransportClient transportClient = null;
        VkApiClient vk = null;
        try{
            transportClient = HttpTransportClient.getInstance();
            vk = new VkApiClient(transportClient);
        } catch (Exception e){
            System.out.println(e.toString());
        }

        VKStreamingAPIHandler vkStreamingAPIHandler = new VKStreamingAPIHandler(vk, transportClient, dbHelper, spamFilter);  //эт моё

        APIServlet apiServlet = new APIServlet(vkStreamingAPIHandler, dbHelper);              //тож моё
        Server server = new Server  (8080);

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.addServlet(new ServletHolder(apiServlet), "/api");
        server.setHandler(servletHandler);


        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int postUpdateInterval = 60000;
                PostUpdater postUpdater = new PostUpdater(postUpdateInterval , dbHelper);
                postUpdater.doUpdate();
            }
        });

        myThread.start();

        //System.out.println(dbHelper.makeSQLupdateUpdatePost(222, 333,444,"https://vk.com/wall-157357316_448"));

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
