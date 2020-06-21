package engine.annotations.game;

import engine.enums.game.LanguageType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(value = Languages.class)
public @interface Language {

    String languageName();
    String languageShort();
    String source();
    LanguageType type();

}
