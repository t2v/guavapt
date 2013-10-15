package jp.t2v.guavapt;

import java.util.Collections;
import java.util.List;

import lombok.Value;

@Guavapt
@Value
public class TestEntity {

    int id;
    String name;
    List<String> messages;
    boolean editRequired;

    public static final String type = "";

    public boolean hasName() {
        return !name.isEmpty();
    }
    
    public String upperName() {
        return name.toUpperCase();
    }
    
    public TestEntity withName(final String newName) {
        return new TestEntity(id, newName, messages, editRequired);
    }
    
    public static TestEntity create() {
        return new TestEntity(0, "", Collections.<String>emptyList(), true);
    }
    
}
