package pojos;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

public class TestDocument {
    @BsonId
    @BsonProperty("_id")
    private UUID id = UUID.randomUUID();
    @BsonProperty("name")
    private String name;

    public TestDocument(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public TestDocument() {
        // default constructor
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
