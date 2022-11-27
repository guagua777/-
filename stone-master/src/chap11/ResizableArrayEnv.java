package chap11;
import java.util.Arrays;
import chap6.Environment;
import chap11.EnvOptimizer.EnvEx2;

//记录全局变量
public class ResizableArrayEnv extends ArrayEnv {
    //hash表
    //存储全局的变量名
    protected Symbols names;
    public ResizableArrayEnv() {
        super(10, null);
        names = new Symbols();
    }
    @Override public Symbols symbols() { return names; }
    //重写get put 等
    @Override public Object get(String name) {
        Integer i = names.find(name);
        if (i == null)
            if (outer == null)
                return null;
            else
                return outer.get(name);
        else
            return values[i];
    }
    @Override public void put(String name, Object value) {
        Environment e = where(name);
        if (e == null)
            e = this;
        ((EnvEx2)e).putNew(name, value);
    }

//    @Override
//    public void put(String name, Object value) {
//        Environment e = where(name);
//        if (e == null) {
//            //重点
//            e = this;
//        }
//        ((EnvEx2)e).putNew(name, value);
//    }

    @Override public void putNew(String name, Object value) {
        assign(names.putNew(name), value);
    }
    @Override public Environment where(String name) {
        if (names.find(name) != null)
            return this;
        else if (outer == null)
            return null;
        else
            return ((EnvEx2)outer).where(name);
    }

//    @Override
//    public Environment where(String name) {
//        if (names.find(name) != null) {
//            //从this中获取
//            return this;
//        } else if (outer == null) {
//            return null;
//        } else {
//            return ((EnvEx2)outer).where(name);
//        }
//    }

    @Override public void put(int nest, int index, Object value) {
        if (nest == 0)
            assign(index, value);
        else
            super.put(nest, index, value);
    }
    protected void assign(int index, Object value) {
        if (index >= values.length) {
            int newLen = values.length * 2;
            if (index >= newLen)
                newLen = index + 1;
            values = Arrays.copyOf(values, newLen);
        }
        values[index] = value;
    }

//    protected void assign(int index, Object value) {
//        if (index >= values.length) {
//            //扩容
//            int newLen = values.length * 2;
//            if (index >= newLen) {
//                //这一步是什么意思？
//                newLen = index + 1;
//            }
//            values = Arrays.copyOf(values, newLen);
//        }
//        values[index] = value;
//    }
}
