package jp.t2v.guavapt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface Guavapt {

    boolean functions() default true;

    boolean predicates() default true;

}
