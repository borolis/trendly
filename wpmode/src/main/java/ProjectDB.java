import java.util.LinkedList;
import java.util.List;

public class ProjectDB {
    Long ID_IN_DB;
    Long USER_ID;
    List<String> POSTS_TAGS;
    List<String> POSTS_ID;
    Long TIME;

    public ProjectDB(Long _USER_ID,
                     Long _TIME,
                     List<String> _POSTS_ID,
                     List<String> _POSTS_TAGS
                    )
    {
        ID_IN_DB = -1L;
        USER_ID = _USER_ID;
        TIME = _TIME;
        POSTS_ID = _POSTS_ID;
        POSTS_TAGS = _POSTS_TAGS;
    }

    public ProjectDB(Long _USER_ID,
                     Long _TIME,
                     List<String> _POSTS_ID,
                     List<String> _POSTS_TAGS,
                     Long _ID_IN_DB)
    {
        ID_IN_DB = _ID_IN_DB;
        USER_ID = _USER_ID;
        TIME = _TIME;
        POSTS_ID = _POSTS_ID;
        POSTS_TAGS = _POSTS_TAGS;
    }

    public ProjectDB()
    {
    }

    public String getPOSTS_TAGS() {
        return listStringToText(POSTS_TAGS);
    }

    public String listStringToText(List<String> lst)
    {
        String result = "";

        if(lst.size() == 0) {
            return result;
        }

        String splitter = "XXX" + ":" + "XXX";

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
