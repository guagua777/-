package chap12;
import stone.StoneException;
import chap11.Symbols;

//仅记录this的信息
public class SymbolThis extends Symbols {
    //新添加字段name
    public static final String NAME = "this";
    public SymbolThis(Symbols outer) {
        super(outer);
        //将this添加到 标识符与位置的关系表中
        add(NAME);
    }
    @Override public int putNew(String key) {
        throw new StoneException("fatal");
    }
    @Override public Location put(String key) {
        //因为变量只有this这一个变量，所以添加字段和方法的时候，直接添加到outer的关系表里面
        Location loc = outer.put(key); 
        if (loc.nest >= 0)
            loc.nest++;
        return loc;
    }

//    public static final String NAME = "this";
//    public SymbolThis(Symbols outer) {
//        super(outer);
//        add(NAME);
//    }
//
//    @Override
//    public int putNew(String key) {
//        throw
//    }
//
//    @Override
//    public Location put(String key) {
//        Location loc = outer.put(key);
//        if (loc.nest >= 0) {
//            loc.nest = loc.nest + 1;
//        }
//        return loc;
//    }

}
