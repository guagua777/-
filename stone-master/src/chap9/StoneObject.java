package chap9;
import chap6.Environment;
import chap7.FuncEvaluator.EnvEx;
//类的对象信息
//想清楚都包含哪些元素

/**
 * 1. 环境
 * StoneObject主要用于保存字段的值
 * 环境主要用于保存变量的值
 *
 * 将对象视作一种环境
 */
public class StoneObject {
    public static class AccessException extends Exception {}
    //该环境的外层环境，用于执行class语句，对类进行定义
    protected Environment env;
    public StoneObject(Environment e) { env = e; }
    @Override public String toString() { return "<object:" + hashCode() + ">"; }
    public Object read(String member) throws AccessException {
        //为什么是这样？
        Environment env = getEnv(member);
        Object o = env.get(member);
        return o;
//        return getEnv(member).get(member);
    }
    public void write(String member, Object value) throws AccessException {
        EnvEx e = (EnvEx)getEnv(member);
        e.putNew(member, value);
//        ((EnvEx)getEnv(member)).putNew(member, value);
    }
    protected Environment getEnv(String member) throws AccessException {
        Environment e = ((EnvEx)env).where(member);
        //为什么需要e == env?
        //因为不能是外部的环境，outer的环境，outer的环境是class的环境
        if (e != null && e == env)
            return e;
        else
            throw new AccessException();
    }

//    protected Environment env;
//    public StoneObject(Environment e) {
//        env = e;
//    }

//    public static class AccessException extends Exception {
//
//    }


}
