import com.vk.api.sdk.streaming.objects.StreamingRule;
import org.json.simple.JSONObject;
import org.sqlite.core.DB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class APIRequest {
    private boolean isValid = false;
    String type;
    DBHelper dbHelper;
    String keyword;
    Long userId;
    Long projectId;
    VKStreamingAPIHandler vkStreamingAPIHandler;

    private boolean checkNewStreamingValues(Long _userId, Long _projectId, String _keyword)
    {
        if(_keyword == null) return false;
        if(_userId == null) return false;
        if(_projectId == null) return false;
        return true;
    }

    private boolean checkDeleteStreamingValues(Long _userId, Long _projectId)
    {
        if(_userId == null) {
            return false;
        }
        if(_projectId == null) {
            return false;
        }
        return true;
    }


    private void newStreamingRequest(JSONObject json)
    {

        try {
            userId = Long.parseLong(json.get("userId").toString());
            projectId = Long.parseLong(json.get("projectId").toString());
            keyword = json.get("keyword").toString();
        }
        catch (Exception e) {
            return;
        }

        if(!checkNewStreamingValues(userId, projectId, keyword)) {
            System.out.println("BAAAD new streaming request");
            System.out.println("zapros");
            System.out.println("type" + "[" + type + "]");
            System.out.println("userId" + "[" + userId + "]");
            System.out.println("projectId" + "[" + projectId + "]");
            System.out.println("keyword" + "[" + keyword + "]");
            return;
        }

        System.out.println("adding");
        Date date = new Date();
        long time = date.getTime();
        LinkedList<ProjectDB> projectsDB = dbHelper.getAllProjects();

        /*
        Long idOfUserProject = 1L;

        for (ProjectDB cur : projectsDB) {
            if (cur.USER_ID == userId)
            {
                idOfUserProject++;
            }
        }

        */
        vkStreamingAPIHandler.addRule(userId.toString() + "#" + projectId, keyword);
        LinkedList<String> tagsList = new LinkedList<>();
        LinkedList<String> idList = new LinkedList<>();

        tagsList.add(userId.toString() + "#" + projectId.toString());
        ProjectDB project = new ProjectDB(userId, time, idList, tagsList);
        dbHelper.execUpdate(dbHelper.makeSQLInsertNewProject(project));

    }



    private void deleteStreamingRequest(JSONObject json) {
        try {
            userId = Long.parseLong(json.get("userId").toString());
            projectId = Long.parseLong(json.get("projectId").toString());
        }
        catch (Exception e) {
            return;
        }
        if (!checkDeleteStreamingValues(userId, projectId)) {
            System.out.println("BAAAD new delete request");
            System.out.println("zapros");
            System.out.println("type" + "[" + type + "]");
            System.out.println("userId" + "[" + userId + "]");
            System.out.println("projectId" + "[" + projectId + "]");
            return;
        }
        System.out.println("delete");

        String tag = userId.toString() + "#" + projectId.toString();
        try {
            vkStreamingAPIHandler.deleteRule(tag);
            dbHelper.execUpdate(dbHelper.makeSQLDeleteProjectByTags(tag));
            dbHelper.execUpdate(dbHelper.makeSQLDeletePostsByTags(tag));
        }
        catch (Exception e) {
            System.out.println("can't delete this tag");
        }
    }



    private void clearStreamingRequest(JSONObject json) {
        System.out.println("deleting all rules");
        ArrayList<StreamingRule> rules = vkStreamingAPIHandler.getRules();
        for (StreamingRule cur: rules) {
                try {
                vkStreamingAPIHandler.deleteRule(cur.getTag());
                dbHelper.execUpdate(dbHelper.makeSQLDeleteProjectByTags(cur.getTag()));
                dbHelper.execUpdate(dbHelper.makeSQLDeletePostsByTags(cur.getTag()));
            }
            catch (Exception e) {
                System.out.println("can't delete this tag");
            }
        }
    }

    private void getLikesRequest(JSONObject json)
    {

    }

    private void getRepostsRequest(JSONObject json)
    {

    }

    private void getViewsRequest(JSONObject json)
    {

    }

    private boolean typeIsValid(String type)
    {

        switch (type)
        {
            case "new_streaming": break;
            case "get_likes": break;
            case "get_reposts": break;
            case "get_views": break;
            case "clear_streaming": break;
            case "delete_streaming": break;
            default: return false;
        }
        return true;
    }

    public APIRequest(JSONObject json, VKStreamingAPIHandler _vkStreamingAPIHandler, DBHelper _dbHelper)
    {
        dbHelper = _dbHelper;
        vkStreamingAPIHandler = _vkStreamingAPIHandler;
        type = json.get("type").toString();
        if(typeIsValid(type)) {
            isValid = true;
        }
        else {
            return;
        }
        switch (type) {
            case "new_streaming":
            {
                newStreamingRequest(json);
                break;
            }

            case "delete_streaming":
            {
                deleteStreamingRequest(json);
                break;
            }
            case "clear_streaming":
            {
                clearStreamingRequest(json);
                break;
            }
            case "get_likes":
            {
                getLikesRequest(json);
                ///TODO just get likes
                break;
            }

            case "get_reposts":
            {
                getRepostsRequest(json);
                ///TODO just get reposts
                break;
            }
            case "get_views":
            {
                getViewsRequest(json);
                ///TODO just get views
                break;
            }
            default: return;
        }

        /*
            "type":"new_streaming",
            "keyword":"LALKA",
            "userId":"1112352313",
            "projectId":"234235235",
        */

    }

    public boolean isValid() {
        return isValid;
    }
}
