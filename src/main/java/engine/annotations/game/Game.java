package engine.annotations.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Game {

    String name();

    int width() default 800;
    int height() default 600;

    boolean resizable() default false;

}
