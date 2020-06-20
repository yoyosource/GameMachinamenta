package engine;

import engine.annotations.game.Game;
import engine.annotations.screen.*;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameEngine {

    public static void main(String[] args) {
        Set<Class<?>> gameClass = new Reflections("").getTypesAnnotatedWith(Game.class).stream().filter(GameEngine::validClass).collect(Collectors.toSet());
        if (gameClass.isEmpty()) {
            return;
        }
        List<Class<?>> classes = new ArrayList<>(gameClass);
        Object object;
        if (gameClass.size() == 1) {
            object = createObject(classes.get(0));
        } else {
            if (args.length != 1) {
                return;
            }
            classes = gameClass.stream().filter(c -> validClass(c, args[0])).collect(Collectors.toList());
            if (classes.isEmpty()) {
                return;
            }
            if (classes.size() == 1) {
                object = createObject(classes.get(0));
            } else {
                return;
            }
        }
        System.out.println(object);
        System.out.println(gameClass);
        System.out.println(getScreens());
    }

    private static List<Class<?>> getScreens() {
        return new Reflections("").getTypesAnnotatedWith(Screen.class).stream().filter(GameEngine::validClass).collect(Collectors.toList());
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

    public static Object createObject(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

}

class ScreenObject {

    private Object object = null;

    private Method screenInitMethod = null;
    private Method screenCloseMethod = null;

    private Method renderPreMethod = null;
    private Method renderMainMethod = null;
    private Method renderPostMethod = null;

    private Method tickPreMethod = null;
    private Method tickMainMethod = null;
    private Method tickPostMethod = null;

    public ScreenObject(Object o) {
        if (o.getClass().getDeclaredAnnotationsByType(Screen.class).length != 1) {
            return;
        }
        object = o;
        init();
    }

    private void init() {
        screenInit();
        renderInit();
        tickInit();
    }

    private <T extends Annotation> boolean validMethod(Method method, Class<T> annotationClass, Class<?> clazz) {
        if (method.getDeclaredAnnotationsByType(annotationClass).length != 1) return false;
        if (method.getParameterCount() != 1) return false;
        return hasCorrectParameter(method, clazz);
    }

    private boolean hasCorrectParameter(Method method, Class<?> clazz) {
        return method.getParameterTypes()[0].getTypeName().equals(clazz.getTypeName());
    }

    private void screenInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (validMethod(method, ScreenInit.class, ScreenEvent.class)) {
                screenInitMethod = method;
            }
            if (validMethod(method, ScreenClose.class, ScreenEvent.class)) {
                screenInitMethod = method;
            }
        }
    }

    private void renderInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (validMethod(method, RenderPre.class, RenderEvent.class)) {
                renderPreMethod = method;
            }
            if (validMethod(method, Render.class, RenderEvent.class)) {
                renderMainMethod = method;
            }
            if (validMethod(method, RenderPost.class, RenderEvent.class)) {
                renderPostMethod = method;
            }
        }
    }

    private void tickInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (validMethod(method, TickPre.class, TickEvent.class)) {
                tickPreMethod = method;
            }
            if (validMethod(method, Tick.class, TickEvent.class)) {
                tickMainMethod = method;
            }
            if (validMethod(method, TickPost.class, TickEvent.class)) {
                tickPostMethod = method;
            }
        }
    }

}
