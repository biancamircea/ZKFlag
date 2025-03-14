package ro.mta.toggleserverapi.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException{
    private final String link;

    public ResourceAlreadyExistsException(String message, String link) {
        super(message);
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
