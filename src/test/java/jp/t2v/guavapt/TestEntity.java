package jp.t2v.guavapt;

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

}
