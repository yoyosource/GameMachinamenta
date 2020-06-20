package engine.annotations.screen;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Resources {
    Resource[] value();
}
