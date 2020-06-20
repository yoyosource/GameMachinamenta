package engine.annotations.screen;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(value = ResourceLoaders.class)
public @interface ResourceLoader {

    String name();

}
