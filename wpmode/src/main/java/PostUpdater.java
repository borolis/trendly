import org.sqlite.core.DB;

import java.util.Date;
import java.util.LinkedList;

public class PostUpdater {

    private DBHelper dbHelper;
    private int timeInterval;
    public PostUpdater(int _timeInterval, DBHelper _dbHelper)
    {
        this.timeInterval = _timeInterval;
        this.dbHelper = _dbHelper;
    }

    public void doUpdate()
    {
        Date date = new Date();
        long lastUpdateTime = date.getTime();
        long currentUpdateTime = date.getTime();
        while (true)
        {
            date = new Date();
            currentUpdateTime = date.getTime();
            if(currentUpdateTime - lastUpdateTime > timeInterval)
            {

                updateAllPosts();
                date = new Date();
                currentUpdateTime = date.getTime();
                lastUpdateTime = currentUpdateTime;
            }
        }
    }

    private void updateAllPosts()
    {
        System.out.println("Starting update posts");
        ViewParser viewParser;
        LinkedList<PostDB> allPosts = dbHelper.getAllPosts();
        for (PostDB cur: allPosts) {
            viewParser = new ViewParser(cur.POST_URL);
                dbHelper.execUpdate(dbHelper.makeSQLupdateUpdatePost(viewParser.get_likes(), viewParser.get_reposts(), viewParser.get_views(), cur.POST_URL));
        }
        System.out.println("All posts updated!");
    }
}
