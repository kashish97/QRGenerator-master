package androidmads.example.Utils;

public class Upload {
    public String name;
    public String type;
    public String description;

    public String url;


    public Upload( ) {

    }

    public Upload(String name, String type, String description, String url) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}