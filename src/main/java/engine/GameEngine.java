package engine;

import engine.annotations.game.Game;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameEngine {

    public static void main(String[] args) {
        Reflections reflections = new Reflections("");
        Set<Class<?>> gameClass = reflections.getTypesAnnotatedWith(Game.class);
        gameClass = gameClass.stream().filter(GameEngine::validClass).collect(Collectors.toSet());
        if (gameClass.isEmpty()) {
            return;
        }
        List<Class<?>> classes = new ArrayList<>(gameClass);
        Object object;
        if (gameClass.size() == 1) {
            object = getGameEngine(classes.get(0));
        } else {
            if (args.length != 1) {
                return;
            }
            classes = gameClass.stream().filter(c -> validClass(c, args[0])).collect(Collectors.toList());
            if (classes.isEmpty()) {
                return;
            }
            if (classes.size() == 1) {
                object = getGameEngine(classes.get(0));
            } else {
                return;
            }
        }
        System.out.println(object);
        System.out.println(gameClass);
    }

    private static boolean validClass(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean validClass(Class<?> clazz, String gameName) {
        try {
            return clazz.getAnnotationsByType(Game.class)[0].name().equals(gameName);
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getGameEngine(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

}
