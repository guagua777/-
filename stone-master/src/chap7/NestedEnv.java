package chap7;
import java.util.HashMap;
import chap6.Environment;
import chap7.FuncEvaluator.EnvEx;


//空间范围和时间范围
public class NestedEnv implements Environment {
    protected HashMap<String,Object> values;
    protected Environment outer;
    public NestedEnv() { this(null); }
    public NestedEnv(Environment e) {
        values = new HashMap<String,Object>();
        outer = e;
    }
    public void setOuter(Environment e) { outer = e; }
    public Object get(String name) {
        Object v = values.get(name);
        if (v == null && outer != null)
            return outer.get(name);
        else
            return v;
    }

//    protected HashMap<String, Object> values;
//    //外部的环境
//    protected Environment outer;
//    public NestedEnv() {
//        this(null);
//    }
//    //传入外部的环境
//    public NestedEnv(Environment e) {
//        values = new HashMap<>();
//        outer = e;
//    }
//    public void setOuter(Environment e) {
//        outer = e;
//    }
//    public Object get(String name) {
//        Object v = values.get(name);
//        if (v == null && outer != null) {
//            return outer.get(name);
//        } else {
//            return v;
//        }
//    }


    public void putNew(String name, Object value) { values.put(name, value); }

//    public void putNew(String name, Object value) {
//        values.put(name, value);
//    }

    public void put(String name, Object value) {
        Environment e = where(name);
        if (e == null)
            e = this;
        ((EnvEx)e).putNew(name, value);
    }

//
//    public void put(String name, Object value) {
//
//    }


    public Environment where(String name) {
        if (values.get(name) != null)
            return this;
        else if (outer == null)
            return null;
        else
            return ((EnvEx)outer).where(name);
    }

//    public Environment where(String name) {
//        if (values.get(name) != null) {
//            return this;
//        } else if (outer == null) {
//            return null;
//        } else {
//            //报错
//            //Environment中没有where方法
//            //return outer.where()
//            return ((EnvEx)outer).where(name);
//        }
//    }
}
