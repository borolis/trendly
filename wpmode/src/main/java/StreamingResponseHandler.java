import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;

import java.util.LinkedList;

public class StreamingResponseHandler {
    private String eventUrl = "";
    private String text = "";
    private String creationTime = "";
    private String postId = "";
    private String postOwnerId = "";
    private LinkedList<String> tags;

    public StreamingResponseHandler(String _eventUrl, String _text, String _postId, String _postOwnerId, String _creationTime)
    {
        eventUrl = _eventUrl;
        text = _text;
        creationTime = _creationTime;
        postId = _postId;
        postOwnerId = _postOwnerId;
    }

    public StreamingResponseHandler(StreamingCallbackMessage message)
    {
        eventUrl = message.getEvent().getEventUrl();
        text = message.getEvent().getText();
        creationTime = message.getEvent().getCreationTime().toString();
        postId = message.getEvent().getEventId().getPostId().toString();
        postOwnerId = message.getEvent().getEventId().getPostOwnerId().toString();
        tags = new LinkedList<>();
        tags.addAll(message.getEvent().getTags());
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public String getText() {
        return text;
    }

    public String getPostId() { return postId; }

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public LinkedList<String> getTags() {
        return tags;
    }
}
