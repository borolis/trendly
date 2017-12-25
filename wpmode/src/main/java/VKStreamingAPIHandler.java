import com.vk.api.sdk.actions.Streaming;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.streaming.responses.GetServerUrlResponse;
import com.vk.api.sdk.streaming.clients.StreamingEventHandler;
import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import com.vk.api.sdk.streaming.exceptions.StreamingApiException;
import com.vk.api.sdk.streaming.exceptions.StreamingClientException;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.vk.api.sdk.streaming.objects.StreamingRule;
import com.vk.api.sdk.streaming.objects.responses.StreamingGetRulesResponse;
import com.vk.api.sdk.streaming.objects.responses.StreamingResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VKStreamingAPIHandler {
    private SpamFilter spamFilter;
    private DBHelper dbHelper;
    private Integer CLIENT_ID = 6206359; //app full streamingApi
    private String CLIENT_SECRET = "Agw6qxbUC5zV6PqrYJ7A"; //app full streamingApi
    private StreamingActor actor;
    private VkStreamingApiClient streamingClient;

    public void addRule(String tag, String value) {
        try {
            System.out.println("Adding rule to Streaming");
            System.out.println("tag:[" + tag + "] value: [" + value + "]");
            StreamingResponse response = streamingClient.rules().add(actor, tag, value).execute();
            System.out.println("Response: [" + response.toString()+ "]");
            System.out.println("______________");
            System.out.println();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteRule(String tag) {
        try {

            printRules(getRules());

            System.out.println();
            System.out.println("Deleting rule from Streaming");
            System.out.println("tag:[" + tag + "]");

            StreamingResponse response = streamingClient.rules().delete(actor, tag).execute();
            System.out.println("Response: [" + response.toString()+ "]");
            System.out.println("______________");
            System.out.println();
        }
        catch (Exception e) {
            System.out.println("Debug: cannot delete this tag");
        }
    }

    public ArrayList<StreamingRule> getRules() {
        ArrayList<StreamingRule> rules = null;
        try {
            StreamingGetRulesResponse response = streamingClient.rules().get(actor).execute();
            rules = (ArrayList<StreamingRule>) response.getRules();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        if(rules == null) {
            return new ArrayList<StreamingRule>();
        }
        return rules;
    }

    public void printRules(List<StreamingRule> rules)
    {
        System.out.println("ALL RULES IN STREAMING");
        for (StreamingRule cur: rules) {
            System.out.println("tag:[" + cur.getTag() + "] value: [" + cur.getValue() + "]");
        }
        System.out.println("______________");
        System.out.println();
    }

    boolean needToAdd(List<String> a, List<String> b)
    {
        for (String s1: a) {
            for (String s2: b) {
                if(s1.equals(s2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public VKStreamingAPIHandler(VkApiClient vk, TransportClient transportClient, DBHelper _dbHelper, final SpamFilter spamFilter)
    {
        this.spamFilter = spamFilter;
        this.dbHelper = _dbHelper;
        /*
            Получение токена для работы streaming api
         */
        Integer CLIENT_ID = 6206359; //app full streamingapi
        final String CLIENT_SECRET = "Agw6qxbUC5zV6PqrYJ7A"; //app full streamingapi
        ServiceClientCredentialsFlowResponse authResponse = null;
        try {
            authResponse = vk.oauth().serviceClientCredentialsFlow(CLIENT_ID, CLIENT_SECRET).execute();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        String serviceKey = authResponse.getAccessToken();  //токен

        ServiceActor application = new ServiceActor(CLIENT_ID, serviceKey);
        VkApiClient vkClient = new VkApiClient(transportClient);
        streamingClient = new VkStreamingApiClient(transportClient);
        try {
            GetServerUrlResponse getServerUrlResponse = vkClient.streaming().getServerUrl(application).execute();
            actor = new StreamingActor(getServerUrlResponse.getEndpoint(), getServerUrlResponse.getKey());

            StreamingGetRulesResponse response = streamingClient.rules().get(actor).execute();
            System.out.println(response.getRules());


            streamingClient.stream().get(actor, new StreamingEventHandler() {
                @Override
                public void handle(StreamingCallbackMessage message) {
                    StreamingResponseHandler event = new StreamingResponseHandler(message);
                    ViewParser viewParser = new ViewParser(event.getEventUrl());
                    PostDB post = new PostDB(   Long.parseLong(event.getPostId()),
                                                Long.parseLong(event.getPostOwnerId()),
                                                Long.parseLong(viewParser.get_likes()),
                                                Long.parseLong(viewParser.get_reposts()),
                                                Long.parseLong(viewParser.get_views()),
                                                event.getTags(),
                                                Long.parseLong(event.getCreationTime()),
                                                event.getEventUrl(),
                                                event.getText()
                                            );


                    if(spamFilter.checkSpam(post.POST_TEXT.split(" ")) || message.getEvent().getEventType().getValue().equals("comment")) {
                        System.out.println("SPAM OR COMMENT DETECTED!!!");
                    }
                    else {
                        dbHelper.execUpdate(dbHelper.makeSQLInsertNewPost(post));
                        String lastInsertId = dbHelper.getLastInsertId();
                        post.ID_IN_DB = Long.parseLong(lastInsertId);

                        LinkedList<ProjectDB> projectsDB = dbHelper.getAllProjects();
                        /// 1#1, 1#2   post



                        /// 1#1
                        /// 1#2
                        /// 1#1, 1#2


                        for (ProjectDB cur: projectsDB) {
                            if(needToAdd(post.POST_TAGS, cur.POSTS_TAGS))
                            {
                                ///ADD ID TO THIS PROJECT
                                cur.POSTS_ID.add(post.ID_IN_DB.toString());
                                dbHelper.execUpdate(dbHelper.makeSQLUpdateProject(cur.listStringToText(cur.POSTS_ID),
                                        cur.ID_IN_DB)
                                );
                            }
                        }

                        //ProjectDB projectDB = dbHelper.getProjectByTags(post.textToListString(post.getPOST_TAGS()));
                    }

                    System.out.println("!!!! NEW STREAMING MESSAGE !!!!");
                    System.out.println("URL:" + event.getEventUrl());
                    System.out.println("TEXT:" + event.getText());
                    System.out.println("POST_ID:" + event.getPostId());
                    System.out.println("POST_OWNER:" + event.getPostOwnerId());
                    System.out.println("TIME:" + event.getCreationTime());
                    System.out.println("Просмотры: " + viewParser.get_views());
                    System.out.println("Лайки: " + viewParser.get_likes());
                    System.out.println("Репосты: " + viewParser.get_reposts());
                    System.out.println("Тэги: " + event.getTags());

                }
            }).execute();

            //deleteRule("1", streamingClient, actor);
            //addRule("2","Xiaomi", streamingClient, actor);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
