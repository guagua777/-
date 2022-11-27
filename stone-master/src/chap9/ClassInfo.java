package chap9;
import stone.StoneException;
import stone.ast.ClassBody;
import stone.ast.ClassStmnt;
import chap6.Environment;

/**
 * 先解析后计算
 * parser ---> eval
 * ClassStmnt与ClassBody代表class的抽象语法树
 * ClassParser记录class的语法规则，解析class字符串后，生成ClassStmnt与ClassBody
 *
 * ClassInfo与StoneObject是计算(eval)时候使用的
 * ClassInfo代表计算时候的信息
 * StoneObject代表计算时候，类的对象的信息
 */
public class ClassInfo {
    protected ClassStmnt definition;
    protected Environment environment;
    protected ClassInfo superClass;

//    protected ClassStmnt definition;
//    protected Environment environment;
//    protected ClassInfo superClass;


    public ClassInfo(ClassStmnt cs, Environment env) {
        definition = cs;
        environment = env;
        Object obj = env.get(cs.superClass());
        if (obj == null)
            superClass = null;
        else if (obj instanceof ClassInfo)
            superClass = (ClassInfo)obj;
        else
            throw new StoneException("unknown super class: " + cs.superClass(),
                                     cs);
    }

//    public ClassInfo(ClassStmnt cs, Environment env) {
//        definition = cs;
//        environment = env;
//        //从语法分析中，获取父类
//        //想清楚父类是从哪里来的？从字符串的代码中extends关键字来的
//        Object obj = env.get(cs.superClass());
//        if (obj == null) {
//            superClass = null;
//        } else if (obj instanceof ClassInfo) {
//            superClass = (ClassInfo)obj;
//        } else {
//            throw new StoneException("", cs);
//        }
//    }


    public String name() { return definition.name(); }
    public ClassInfo superClass() { return superClass; }
    public ClassBody body() { return definition.body(); }
    public Environment environment() { return environment; }
    @Override public String toString() { return "<class " + name() + ">"; }
}
