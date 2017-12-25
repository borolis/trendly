//import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewParser {
    //private DOMParser domParser = null;
    private String wallUrl = "";

    private String _likes = "";
    private String _reposts = "";
    private String _views = "";

    public ViewParser(String _wallUrl)
    {
        //domParser = new DOMParser();
        int start = _wallUrl.indexOf("wall");
        int end = _wallUrl.indexOf("?");
        if(start != -1) {
            if(end != -1) {
                wallUrl = _wallUrl.substring(start, end);
            }
            else {
                wallUrl = _wallUrl.substring(start, _wallUrl.length());
            }
            parse();
        }
    }

    private void parse()
    {
        _likes = getLikes();
        _reposts = getReposts();
        _views = getViews();
    }

    private String getViews()
    {
        String all = "";
        String splitter = "<!>";
        try {
            all = vkPhpQuery(wallUrl, "views");
            String[] arr = all.split(splitter);
            int index = arr[arr.length-1].indexOf(" просм");
            String result = arr[arr.length-1].substring(0, index);
            return result;
        }
        catch (Exception e) {
            System.out.println("access denied");
        }
        return "NaN";
    }

    private String getLikes()
    {
        String result = "";
        String all = "";
        String splitter = "<!>";
        try {
            all = vkPhpQuery(wallUrl, "likes");
            String[] arr = all.split(splitter);
            ///TODO parse likes

            ///TODO find <input***/>
            int indx1 = -1;
            int indx2 = -1;
            result = arr[5];
            indx1 = result.indexOf("<input");
            result = result.substring(indx1, result.length());
            indx2 = result.indexOf("/>");
            result = result.substring(0, indx2);

            ///TODO find value
            indx1 = -1;
            indx2 = -1;
            indx1 = result.indexOf("value=\"");
            result = result.substring(indx1, result.length());
            //System.out.println(result);

            String[] as = result.split("\"");

            return as[1];
        }
        catch (Exception e) {
            System.out.println("access denied");
        }
        return "NaN";
    }

    private String getReposts()
    {
        String result = "";
        String all = "";
        String splitter = "<!>";
        try {
            all = vkPhpQuery(wallUrl, "reposts");
            String[] arr = all.split(splitter);
            ///TODO parse reposts
            ///TODO find <input***/>
            int indx1 = -1;
            int indx2 = -1;
            result = arr[5];
            indx1 = result.indexOf("<input");
            result = result.substring(indx1, result.length());
            indx2 = result.indexOf("/>");
            result = result.substring(0, indx2);

            ///TODO find value
            indx1 = -1;
            indx2 = -1;
            indx1 = result.indexOf("value=\"");
            result = result.substring(indx1, result.length());
            //System.out.println(result);

            String[] as = result.split("\"");

            return as[1];
        }

        catch (Exception e) {
            System.out.println("access denied");
        }
        return "NaN";
    }

    private String vkPhpQuery(String wallUrl, String mode) throws UnsupportedEncodingException {
        String baseURL = "https://vk.com/like.php";
        StringBuilder builder = new StringBuilder();
        builder.append("act=a_get_stats");
        builder.append("&");
        builder.append("al=1");
        builder.append("&");
        builder.append("object=" + wallUrl);
        builder.append("&");
        switch (mode)
        {
            case "likes":
            {
                builder.append("has_share=1");
                break;
            }
            case "views":
            {
                builder.append("views=1");
                break;
            }
            case "reposts":
            {
                builder.append("published=1");
                break;
            }

        }



        String params = builder.toString();
        byte[] data = null;
        InputStream is = null;

        try {
            URL url = new URL(baseURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
            OutputStream os = conn.getOutputStream();
            data = params.getBytes("WINDOWS-1251");
            os.write(data);
            data = null;

            conn.connect();
            int responseCode= conn.getResponseCode();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = conn.getInputStream();

            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            data = baos.toByteArray();
        } catch (Exception e) {
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception ex) {}
        }

        return new String(data, "WINDOWS-1251");
    }

    public String get_likes() {
        return _likes;
    }

    public String get_reposts() {
        return _reposts;
    }

    public String get_views() {
        return _views;
    }

    public String getWallUrl() {
        return wallUrl;
    }
}
