package chap8;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import stone.StoneException;
import chap6.Environment;

public class Natives {

    //返回一个含有原生方法的环境
    public Environment environment(Environment env) {
        appendNatives(env);
        return env;
    }

//    public Environment environment(Environment env) {
//        appendNatives(env);
//        return env;
//    }


    protected void appendNatives(Environment env) {
        //第三个参数为什么是Natives.class
        //Natives.class含有同名的原生函数
        append(env, "print", Natives.class, "print", Object.class);
        append(env, "read", Natives.class, "read");
        append(env, "length", Natives.class, "length", String.class);
        append(env, "toInt", Natives.class, "toInt", Object.class);
        append(env, "currentTime", Natives.class, "currentTime");
    }
    protected void append(Environment env, String name, Class<?> clazz,
                          String methodName, Class<?> ... params) {
        Method m;
        try {
            m = clazz.getMethod(methodName, params);
        } catch (Exception e) {
            throw new StoneException("cannot find a native function: "
                                     + methodName);
        }
        //创建native方法，并放入环境中
        env.put(name, new NativeFunction(methodName, m));
    }

//    public void append(Environment env, String name, Class<?> clazz, String methodName, Class<?>... params) {
//        Method m;
//        try {
//            //从Natives类中获取方法
//            m = clazz.getMethod(methodName, params);
//        } catch (Exception e) {
//            throw new StoneException("" + methodName);
//        }
//        env.put(name, new NativeFunction(methodName, m));
//    }

    // native methods
    public static int print(Object obj) {
        System.out.println(obj.toString());
        return 0;
    }
    public static String read() {
        return JOptionPane.showInputDialog(null);
    }
    public static int length(String s) { return s.length(); }
    public static int toInt(Object value) {
        if (value instanceof String)
            return Integer.parseInt((String)value);
        else if (value instanceof Integer)
            return ((Integer)value).intValue();
        else
            throw new NumberFormatException(value.toString());
    }
    private static long startTime = System.currentTimeMillis();
    public static int currentTime() {
        return (int)(System.currentTimeMillis() - startTime);
    }


    //native方法
//    public static int toInt(Object value) {
//        if (value instanceof String) {
//            return Integer.parseInt((String) value);
//        } else if (value instanceof Integer) {
//            return ((Integer)value).intValue();
//        } else {
//            throw new NumberFormatException(value.toString());
//        }
//    }
//    private static long startTime = System.currentTimeMillis();
//    public static int currentTime() {
//        return (int) (System.currentTimeMillis() - startTime);
//    }
//    public static int print(Object obj) {
//        System.out.println(obj.toString());
//        return 0;
//    }
//    public static String read() {
//        return JOptionPane.showInputDialog(null);
//    }
//    public static int length(String s) {
//        return s.length();
//    }
}
