package engine;

import engine.annotations.game.*;
import engine.annotations.screen.*;
import engine.enums.screen.ScreenType;
import engine.system.game.GameEvent;
import engine.system.game.GameException;
import engine.system.game.GameExceptionEvent;
import engine.system.loops.GameLoop;
import engine.system.loops.Loop;
import engine.system.loops.RenderLoop;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;
import engine.utils.TaskQueue;
import org.reflections.Reflections;
import yapi.manager.log.LogManager;
import yapi.manager.log.Logging;
import yapi.manager.worker.Task;
import yapi.runtime.ThreadUtils;

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {

    JFrame jFrame = null;
    GameView gameView = new GameView();

    GameObject gameObject;
    List<ScreenObject> screenObjects;
    ScreenObject current = null;

    TaskQueue taskQueue = new TaskQueue();

    GameLoop gameLoop;
    RenderLoop renderLoop;

    public static GameEngine gameEngine;
    static Logging logging = new Logging("Game Engine");

    public static void main(String[] args) {
        LogManager.setAllowLogDefault();
        Set<Class<?>> gameClass = new Reflections("").getTypesAnnotatedWith(Game.class).stream().filter(GameEngine::validClass).collect(Collectors.toSet());
        logging.add("Game Object size=" + gameClass.size());
        if (gameClass.isEmpty()) {
            return;
        }
        List<Class<?>> classes = new ArrayList<>(gameClass);
        Object object;
        if (gameClass.size() == 1) {
            object = createObject(classes.get(0));
            logging.add("Game Object newInstance=" + object);
        } else {
            logging.add("Game Object checkArgs=" + Arrays.toString(args));
            if (args.length != 1) {
                return;
            }
            logging.add("Game Object getGameWithName=\"" + args[0] + "\"");
            classes = gameClass.stream().filter(c -> validClass(c, args[0])).collect(Collectors.toList());
            logging.add("Game Object size=" + classes.size());
            if (classes.isEmpty()) {
                return;
            }
            if (classes.size() == 1) {
                object = createObject(classes.get(0));
                logging.add("Game Object newInstance=" + object);
            } else {
                return;
            }
        }
        gameEngine = new GameEngine(object);
    }

    public GameEngine(Object gameObject) {
        this.gameObject = new GameObject(gameObject);
        logging.add("Game Engine (GameObject) " + this.gameObject);
        screenObjects = getScreens().parallelStream().map(GameEngine::createObject).filter(Objects::nonNull).map(ScreenObject::new).collect(Collectors.toList());
        List<ScreenObject> launchScreens = screenObjects.stream().filter(ScreenObject::isLaunchScreen).collect(Collectors.toList());
        if (launchScreens.size() != 1) {
            if (launchScreens.isEmpty()) throw new IllegalStateException("No LaunchScreen (@LaunchScresn) selected");
            throw new IllegalStateException("Too many LaunchScreen specified");
        }

        gameLoop = new GameLoop(1, "GameLoop");
        renderLoop = new RenderLoop(1, "RenderLoop");
        setCurrentScreen(launchScreens.get(0));
        logging.add("Game Engine (screenObjects) " + screenObjects);

        Runnable taskQueueRunnable = () -> {
            while (true) {
                System.out.println(taskQueue);
                if (taskQueue.isNotEmpty()) {
                    taskQueue.getTask().run();
                }
                ThreadUtils.sleep(1);
            }
        };
        Thread taskQueueThread = new Thread(taskQueueRunnable);
        taskQueueThread.setName("TaskQueueThread");
        taskQueueThread.setDaemon(true);
        taskQueueThread.start();

        createFrame();
    }

    private void setCurrentScreen(ScreenObject screenObject) {
        current = screenObject;
        gameLoop.setTarget(screenObject.getTPS());
        renderLoop.setTarget(screenObject.getUPS());
    }

    private void createFrame() {
        jFrame = new JFrame();
        jFrame.setContentPane(gameView);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setFocusTraversalKeysEnabled(false);

        jFrame.setSize(gameObject.getWidth(), gameObject.getHeight());
        jFrame.setResizable(gameObject.isResizable());
        jFrame.setName(gameObject.getName());
        jFrame.setTitle(gameObject.getName());

        jFrame.setLocationRelativeTo(null);
    }

    public void addTask(Task task, TaskQueue.TaskPriority taskPriority) {
        taskQueue.addTask(task, taskPriority);
    }

    public void tick(TickEvent event) {
        addTask(new Task(() -> {
            if (current != null) current.tick(event);
        }), TaskQueue.TaskPriority.HIGH);
    }

    public void render() {
        addTask(new Task(() -> {
            jFrame.repaint();
            if (current != null) current.render(new RenderEvent(gameView.graphics2D));
        }), TaskQueue.TaskPriority.HIGH);
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
    private boolean launchScreen = false;
    private Screen annotation;

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
        annotation = o.getClass().getDeclaredAnnotationsByType(Screen.class)[0];
        if (o.getClass().getDeclaredAnnotationsByType(LaunchScreen.class).length == 1) {
            launchScreen = true;
        }
        object = o;
        init();
    }

    public boolean isLaunchScreen() {
        return launchScreen;
    }

    private void init() {
        screenInit();
        renderInit();
        tickInit();
    }

    private void screenInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (UtilsObject.validMethod(method, ScreenInit.class, ScreenEvent.class)) {
                screenInitMethod = method;
            }
            if (UtilsObject.validMethod(method, ScreenClose.class, ScreenEvent.class)) {
                screenCloseMethod = method;
            }
        }
    }

    private void renderInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (UtilsObject.validMethod(method, RenderPre.class, RenderEvent.class)) {
                renderPreMethod = method;
            }
            if (UtilsObject.validMethod(method, Render.class, RenderEvent.class)) {
                renderMainMethod = method;
            }
            if (UtilsObject.validMethod(method, RenderPost.class, RenderEvent.class)) {
                renderPostMethod = method;
            }
        }
    }

    private void tickInit() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (UtilsObject.validMethod(method, TickPre.class, TickEvent.class)) {
                tickPreMethod = method;
            }
            if (UtilsObject.validMethod(method, Tick.class, TickEvent.class)) {
                tickMainMethod = method;
            }
            if (UtilsObject.validMethod(method, TickPost.class, TickEvent.class)) {
                tickPostMethod = method;
            }
        }
    }

    public int getTPS() {
        return annotation.ups();
    }

    public int getUPS() {
        return annotation.ups();
    }

    public int getFPS() {
        return annotation.fps();
    }

    public String getName() {
        return annotation.name();
    }

    public ScreenType getScreenType() {
        return annotation.type();
    }

    public void render(RenderEvent event) {
        try {
            UtilsObject.executeMethod(renderPreMethod, object, event);
            UtilsObject.executeMethod(renderMainMethod, object, event);
            UtilsObject.executeMethod(renderPostMethod, object, event);
        } catch (Exception exception) {
            GameEngine.logging.add("Game Engine (render) " + exception);
            GameEngine.gameEngine.gameObject.renderException(exception);
        }
    }

    public void tick(TickEvent event) {
        try {
            UtilsObject.executeMethod(tickPreMethod, object, event);
            UtilsObject.executeMethod(tickMainMethod, object, event);
            UtilsObject.executeMethod(tickPostMethod, object, event);
        } catch (Exception exception) {
            GameEngine.logging.add("Game Engine (tick) " + exception);
            GameEngine.gameEngine.gameObject.tickException(exception);
        }
    }

    public void init(ScreenEvent event) {
        try {
            UtilsObject.executeMethod(screenInitMethod, object, event);
        } catch (Exception exception) {
            GameEngine.logging.add("Game Engine (screenInit) " + exception);
            GameEngine.gameEngine.gameObject.screenInitException(exception);
        }
    }

    public void close(ScreenEvent event) {
        try {
            UtilsObject.executeMethod(screenCloseMethod, object, event);
        } catch (Exception exception) {
            GameEngine.logging.add("Game Engine (screenClose) " + exception);
            GameEngine.gameEngine.gameObject.screenCloseException(exception);
        }
    }

    @Override
    public String toString() {
        return "ScreenObject{" +
                "object=" + object +
                ", S(" + UtilsObject.getChar(screenInitMethod, 'I') + UtilsObject.getChar(screenCloseMethod, 'C') + ")" +
                ", R(" + UtilsObject.getChar(renderPreMethod, 'P') + UtilsObject.getChar(renderMainMethod, 'M') + UtilsObject.getChar(renderPostMethod, 'P') + ")" +
                ", T(" + UtilsObject.getChar(tickPreMethod, 'P') + UtilsObject.getChar(tickMainMethod, 'M') + UtilsObject.getChar(renderPostMethod, 'P') + ")" +
                '}';
    }

}

class GameObject {

    private Object object = null;

    private Method startUpMethod = null;

    private Method screenInitExceptionMethod = null;
    private Method screenCloseExceptionMetod = null;
    private Method renderExceptionMethod = null;
    private Method tickExceptionMethod = null;

    public GameObject(Object o) {
        if (o.getClass().getDeclaredAnnotationsByType(Game.class).length != 1) {
            return;
        }
        object = o;
        init();
    }

    private void init() {
        initMethod();
        exceptionMethod();
    }

    private void initMethod() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (UtilsObject.validMethod(method, GameInit.class, GameEvent.class)) {
                startUpMethod = method;
            }
        }
    }

    private void exceptionMethod() {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (UtilsObject.validMethod(method, GameScreenInitException.class, GameExceptionEvent.class)) {
                screenInitExceptionMethod = method;
            }
            if (UtilsObject.validMethod(method, GameScreenCloseException.class, GameExceptionEvent.class)) {
                screenCloseExceptionMetod = method;
            }
            if (UtilsObject.validMethod(method, GameRenderException.class, GameExceptionEvent.class)) {
                renderExceptionMethod = method;
            }
            if (UtilsObject.validMethod(method, GameTickException.class, GameExceptionEvent.class)) {
                tickExceptionMethod = method;
            }
        }
    }

    public int getWidth() {
        return object.getClass().getAnnotationsByType(Game.class)[0].width();
    }

    public int getHeight() {
        return object.getClass().getAnnotationsByType(Game.class)[0].height();
    }

    public String getName() {
        return object.getClass().getAnnotationsByType(Game.class)[0].name();
    }

    public boolean isResizable() {
        return object.getClass().getAnnotationsByType(Game.class)[0].resizable();
    }

    public void executeInit(GameEvent event) {
        UtilsObject.executeMethod(startUpMethod, object, event);
    }

    public void screenInitException(Exception exception) {
        if (screenInitExceptionMethod == null) throw new GameException(exception.getMessage(), exception.getCause());
        UtilsObject.executeMethod(screenInitExceptionMethod, object, new GameExceptionEvent(exception));
    }

    public void screenCloseException(Exception exception) {
        if (screenCloseExceptionMetod == null) throw new GameException(exception.getMessage(), exception.getCause());
        UtilsObject.executeMethod(screenCloseExceptionMetod, object, new GameExceptionEvent(exception));
    }

    public void renderException(Exception exception) {
        if (renderExceptionMethod == null) throw new GameException(exception.getMessage(), exception.getCause());
        UtilsObject.executeMethod(renderExceptionMethod, object, new GameExceptionEvent(exception));
    }

    public void tickException(Exception exception) {
        if (tickExceptionMethod == null) throw new GameException(exception.getMessage(), exception.getCause());
        UtilsObject.executeMethod(tickExceptionMethod, object, new GameExceptionEvent(exception));
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "object=" + object +
                ", M(" + UtilsObject.getChar(startUpMethod, 'I') + ")" +
                ", E(" + UtilsObject.getChar(screenInitExceptionMethod, 'I') + UtilsObject.getChar(screenCloseExceptionMetod, 'C') + UtilsObject.getChar(renderExceptionMethod, 'R') + UtilsObject.getChar(tickExceptionMethod, 'T') +
                '}';
    }
}

class UtilsObject {

    static <T extends Annotation> boolean validMethod(Method method, Class<T> annotationClass, Class<?> clazz) {
        if (method.getDeclaredAnnotationsByType(annotationClass).length != 1) return false;
        if (method.getParameterCount() != 1) return false;
        return hasCorrectParameter(method, clazz);
    }

    private static boolean hasCorrectParameter(Method method, Class<?> clazz) {
        return method.getParameterTypes()[0].getTypeName().equals(clazz.getTypeName());
    }

    static void executeMethod(Method method, Object object, Object... objects) {
        if (method == null) return;
        try {
            method.setAccessible(true);
            method.invoke(object, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {

        }
    }

    static char getChar(Method method, char c) {
        return method == null ? ' ' : c;
    }

}
