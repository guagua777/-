package chap11;

import java.util.HashMap;

//符号类
//变量名
//现在需要保存下标
//该对象是一张hash表

/**
 * 作用：记录变量名的存储位置
 * 为什么需要table字段？
 *
 * 记录标识符和下标的对应关系
 */
public class Symbols {
    //存储key和下标的对应关系
    //这个是干什么的
    protected HashMap<String,Integer> table;

    //这两个字段分别是做什么用的
    protected Symbols outer;

    public Symbols() { this(null); }
    public Symbols(Symbols outer) {
        this.outer = outer;
        this.table = new HashMap<String,Integer>();
    }

//    protected Symbols outer;
//    protected HashMap<String, Integer> table;
//    public Symbols() {
//        this(null);
//    }
//    public Symbols(Symbols outer) {
//        this.outer = outer;
//        this.table = new HashMap<>();
//    }

    //内部类location
    public static class Location {
        //记录在哪一层，哪一个下标
        public int nest, index;
        public Location(int nest, int index) {
            this.nest = nest;
            this.index = index;
        }
    }


    public int size() { return table.size(); }
    public void append(Symbols s) { table.putAll(s.table); }

//    public void append(Symbols s) {
//        table.putAll(s.table);
//    }

    public Integer find(String key) { return table.get(key); }
    public Location get(String key) { return get(key, 0); }


    //查找标识符的存放位置
    public Location get(String key, int nest) {
        Integer index = table.get(key);
        if (index == null)
            if (outer == null)
                return null;
            else
                return outer.get(key, nest + 1);
        else
            return new Location(nest, index.intValue());
    }

//    public Integer find(String key) {
//        return table.get(key);
//    }
//    public Location get(String key) {
//        return get(key, 0);
//    }
//    public Location get(String key, int nest) {
//        Integer index = table.get(key);
//        //当前层级下没有
//        if (index == null) {
//            if (outer == null) {
//                return null;
//            } else {
//                return outer.get(key, nest + 1);
//            }
//        } else {
//            return new Location(nest, index.intValue());
//        }
//    }


    public int putNew(String key) {
        Integer i = find(key);
        if (i == null)
            return add(key);
        else
            return i;
    }

//    public int putNew(String key) {
//        Integer i = find(key);
//        if (i == null) {
//            return add(key);
//        } else {
//            return i;
//        }
//    }

    //存放key
    public Location put(String key) {
        Location loc = get(key, 0);
        if (loc == null)
            return new Location(0, add(key));
        else
            return loc;
    }

//    public Location put(String key) {
//        Location loc = get(key, 0);
//        if (loc == null) {
//            //存放新的标识符和索引的对应关系
//            return new Location(0, add(key));
//        } else {
//            return loc;
//        }
//    }

    //存放标识符，获取标识符对应的下标
    protected int add(String key) {
        int i = table.size();
        table.put(key, i);
        return i;
    }

//    protected int add(String key) {
//        int i = table.size();
//        table.put(key, i);
//        return i;
//    }




}
