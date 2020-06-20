package engine.annotations.screen;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(value = Resources.class)
public @interface Resource {

    String source();
    String name();

}
