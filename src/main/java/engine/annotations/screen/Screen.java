package engine.annotations.screen;

import engine.enums.screen.ScreenType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Screen {

    String name();

    ScreenType type() default ScreenType.MenuScreen;

    int fps() default 60;
    int ups() default 60;
}
