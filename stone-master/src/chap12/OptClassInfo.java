package chap12;
import java.io.File;
import java.util.ArrayList;
import stone.ast.ClassStmnt;
import stone.ast.DefStmnt;
import chap11.Symbols;
import chap12.ObjOptimizer.DefStmntEx2;
import chap6.Environment;
import chap9.ClassInfo;

//保存位置
public class OptClassInfo extends ClassInfo {
    //只看构造函数和字段就可以知道，这个类主要是做什么的
    protected Symbols methods, fields;
    protected DefStmnt[] methodDefs;
    public OptClassInfo(ClassStmnt cs, Environment env, Symbols methods,
                        Symbols fields)
    {
        super(cs, env);
        this.methods = methods;
        this.fields = fields;
        this.methodDefs = null;
    }

//    //方法名和位置的对应关系
//    protected Symbols methods;
//    //字段和位置
//    protected Symbols fields;
//    //方法列表
//    protected DefStmnt[] methodDefs;
//    public OptClassInfo(ClassStmnt cs, Environment env, Symbols methods, Symbols fields) {
//        super(cs, env);
//        this.methods = methods;
//        this.fields = fields;
//        this.methodDefs = null;
//    }


    public int size() { return fields.size(); }
    @Override public OptClassInfo superClass() {
        return (OptClassInfo)superClass;
    }
    public void copyTo(Symbols f, Symbols m, ArrayList<DefStmnt> mlist) {
        f.append(fields);
        m.append(methods);
        for (DefStmnt def: methodDefs)
            mlist.add(def);
    }

//    public void copyTo(Symbols f, Symbols m, ArrayList<DefStmnt> mlist) {
//        f.append(fields);
//        m.append(methods);
//        for ()
//    }

//    public int size() {
//        return fields.size();
//    }
//
//    //OptClassInfo本质上是classInfo
//    @Override
//    public OptClassInfo superClass() {
//
//    }

    public Integer fieldIndex(String name) { return fields.find(name); }
    public Integer methodIndex(String name) { return methods.find(name); }
    public Object method(OptStoneObject self, int index) {
        DefStmnt def = methodDefs[index];
        return new OptMethod(def.parameters(), def.body(), environment(),
                             ((DefStmntEx2)def).locals(), self);
    }
    public void setMethods(ArrayList<DefStmnt> methods) {
        methodDefs = methods.toArray(new DefStmnt[methods.size()]);
    }

//    //获取字段的索引
//    public Integer fieldIndex(String name) {
//        return fields.find(name);
//    }
//    public Integer methodIndex(String name) {
//        return methods.find(name);
//    }
//    //self为stone对象
//    public Object method(OptStoneObject self, int index) {
//        DefStmnt def = methodDefs[index];
//        return new OptMethod(def.parameters(), def.body(), environment(), ((DefStmntEx2)def).locals(), self);
//    }

}
