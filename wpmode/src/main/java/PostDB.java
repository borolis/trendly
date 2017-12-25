import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

public class PostDB {
    Long ID_IN_DB;
    Long POST_ID;
    Long POST_OWNER_ID;
    Long POST_LIKE;
    Long POST_REPOST;
    Long POST_VIEW;
    List<String> POST_TAGS;
    Long POST_TIME;

    String POST_URL;
    String POST_TEXT;

    public PostDB(Long _POST_ID,
                  Long _POST_OWNER_ID,
                  Long _POST_LIKE,
                  Long _POST_REPOST,
                  Long _POST_VIEW,
                  List<String> _POST_TAGS,
                  Long _POST_TIME,
                  String _POST_URL,
                  String _POST_TEXT)
    {
        ID_IN_DB = -1L;
        POST_ID = _POST_ID;
        POST_OWNER_ID = _POST_OWNER_ID;
        POST_LIKE = _POST_LIKE;
        POST_REPOST = _POST_REPOST;
        POST_VIEW = _POST_VIEW;
        POST_TAGS = _POST_TAGS;
        POST_TIME = _POST_TIME;
        POST_URL = _POST_URL;
        POST_TEXT = _POST_TEXT;
    }

    public String getPOST_TAGS() {
        return listStringToText(POST_TAGS);
    }

    public PostDB(Long _POST_ID,
                  Long _POST_OWNER_ID,
                  Long _POST_LIKE,
                  Long _POST_REPOST,
                  Long _POST_VIEW,
                  List<String> _POST_TAGS,
                  Long _POST_TIME,
                  String _POST_URL,
                  String _POST_TEXT,
                  Long _ID_IN_DB)
    {
        ID_IN_DB = _ID_IN_DB;
        POST_ID = _POST_ID;
        POST_OWNER_ID = _POST_OWNER_ID;
        POST_LIKE = _POST_LIKE;
        POST_REPOST = _POST_REPOST;
        POST_VIEW = _POST_VIEW;
        POST_TAGS = _POST_TAGS;
        POST_TIME = _POST_TIME;
        POST_URL = _POST_URL;
        POST_TEXT = _POST_TEXT;
    }

    public PostDB()
    {
    }
    
    public String listStringToText(List<String> lst)
    {
        String result = "";
        String splitter = "XXX" + ":" + "XXX";
        if(lst.size() == 0) {
            return result;
        }
        for (String res:lst) {
            result += (res + splitter);
        }

        result = result.substring(0, result.length() - splitter.length());
        return result;
    }

    public LinkedList<String> textToListString(String text)
    {
        LinkedList<String> result = new LinkedList<>();
        if(text.length() == 0) {
            return result;
        }
        String splitter = "XXX" + ":" + "XXX";

        String[] arr = text.split(splitter);
        for (String cur:arr) {
            result.add(cur);
        }
        return result;
    }

    
}
