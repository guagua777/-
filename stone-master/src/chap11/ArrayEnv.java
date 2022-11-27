package chap11;
import stone.StoneException;
import chap11.EnvOptimizer.EnvEx2;
import chap6.Environment;

//直接存储的是值，没有key
//key为下标
//使用数组来定义环境
//适用于函数的参数与局部变量
public class ArrayEnv implements Environment {
    protected Object[] values;
    protected Environment outer;
    public ArrayEnv(int size, Environment out) {
        values = new Object[size];
        outer = out;
    }
    public Symbols symbols() { throw new StoneException("no symbols"); }


    //重写get和put
    public Object get(int nest, int index) {
        if (nest == 0)
            return values[index];
        else if (outer == null)
            return null;
        else
            return ((EnvEx2)outer).get(nest - 1, index);
    }
    public void put(int nest, int index, Object value) {
        if (nest == 0)
            values[index] = value;
        else if (outer == null)
            throw new StoneException("no outer environment");
        else
            ((EnvEx2)outer).put(nest - 1, index, value);
    }


//    public void put(int nest, int index, Object value) {
//        if (nest == 0) {
//            values[index] = value;
//        } else if (outer == null) {
//            throw new StoneException("");
//        } else {
//            ((EnvEx2)outer).put();
//        }
//    }

//    protected Object[] values;
//    protected Environment outer;
//    public ArrayEnv(int size, Environment out) {
//        values = new Object[size];
//        outer = out;
//    }
//    public Symbols symbols(){
//        throw new StoneException("no symbols");
//    }
//    public Object get(int nest, int index) {
//        if (nest == 0) {
//            return values[index];
//        } else if (outer == null) {
//            return null;
//        } else {
//            //递归获取
//            return ((EnvEx2)outer).get(nest - 1, index);
//        }
//    }
    



    //原来的get put putNew where全部失效
    public Object get(String name) { error(name); return null; }
    public void put(String name, Object value) { error(name); }
    public void putNew(String name, Object value) { error(name); }
    public Environment where(String name) { error(name); return null; }
    public void setOuter(Environment e) { outer = e; }
    private void error(String name) {
        throw new StoneException("cannot access by name: " + name);
    }
}
