package chap11;
import static javassist.gluonj.GluonJ.revise;
import javassist.gluonj.*;
import java.util.List;
import stone.Token;
import stone.StoneException;
import stone.ast.*;
import chap11.Symbols.Location;
import chap6.Environment;
import chap6.BasicEvaluator;
import chap7.ClosureEvaluator;
import sun.awt.Symbol;

//本次只支持函数和闭包，所以只依赖ClosureEvaluator修改器
@Require(ClosureEvaluator.class)
@Reviser public class EnvOptimizer {
    //重新定义环境，也就是给环境添加新的方法
    @Reviser public static interface EnvEx2 extends Environment {
        Symbols symbols();
        void put(int nest, int index, Object value);
        Object get(int nest, int index);
        void putNew(String name, Object value);
        Environment where(String name);
    }

    //定义新的环境接口
//    @Reviser
//    public static interface EnvEx2 extends Environment {
//        Symbols symbols();
//
//
//    }


    @Reviser public static abstract class ASTreeOptEx extends ASTree {
        //添加lookup方法
        //从syms中查找
        public void lookup(Symbols syms) {}
    }

    //为ASTree添加lookup方法
//    @Reviser
//    public static abstract class ASTreeOptEx extends ASTree {
//
//    }


    //ASTList的lookup方法
    @Reviser public static class ASTListEx extends ASTList {
        public ASTListEx(List<ASTree> c) { super(c); }
        public void lookup(Symbols syms) {
            //从根节点开始递归
            for (ASTree t: this)
                ((ASTreeOptEx)t).lookup(syms);
        }
    }

//    @Reviser
//    public static class ASTListEx extends ASTList {
//        //构造函数
//        public ASTListEx(List<ASTree> c) {
//            super(c);
//        }
//        public void lookup(Symbols syms) {
//            for (ASTree t : this) {
//                ((ASTreeOptEx)t).lookup(syms);
//            }
//        }
//    }

    /**
     * def fib (n) {
     *     if n < 2 {
     *         n
     *     } else {
     *         fib (n - 1) + fib (n - 2)
     *     }
     * }
     */

    //def和fun在调用自己的lookup方法之前，创建新的Symbols对象，表示新的环境，新的作用域
    //没看到新创建Symbols？在哪里创建的？
    @Reviser public static class DefStmntEx extends DefStmnt {
        protected int index, size;
        public DefStmntEx(List<ASTree> c) { super(c); }
        //查找变量的位置
        public void lookup(Symbols syms) {
            //从根节点开始递归
            //存储name对应的位置
            //name() 就是 def的name
            System.out.println("def lookup name() is " + name());
            index = syms.putNew(name());
            //返回新的标识符数量
            size = FunEx.lookup(syms, parameters(), body());
        }
        public Object eval(Environment env) {
            ((EnvEx2)env).put(0, index, new OptFunction(parameters(), body(),
                                                        env, size));
            return name();
        }
    }

//    @Reviser
//    public static class DefStmntEx extends DefStmnt {
//        protected int index;
//        protected int size;
//        //构造函数
//        public DefStmntEx(List<ASTree> c) {
//            super(c);
//        }
//        public void lookup(Symbols syms) {
//            index = syms.putNew(name());
//            //变量 参数 body
//            size = FunEx.lookup(syms, parameters(), body());
//        }
//        public Object eval(Environment env) {
//
//        }
//    }


    @Reviser public static class FunEx extends Fun {
        protected int size = -1;
        public FunEx(List<ASTree> c) { super(c); }
        public void lookup(Symbols syms) {
            size = lookup(syms, parameters(), body());
        }
        public Object eval(Environment env) {
            return new OptFunction(parameters(), body(), env, size);
        }
        public static int lookup(Symbols syms, ParameterList params,
                                 BlockStmnt body)
        {
            //从根节点开始递归
            //使用syms创建新的环境
            Symbols newSyms = new Symbols(syms);
            ((ParamsEx)params).lookup(newSyms);
            //使用revise方法，用于解决编译的问题
            ((ASTreeOptEx)revise(body)).lookup(newSyms);
            return newSyms.size();
        }

//        public static int lookup(Symbols syms, ParameterList params, BlockStmnt body) {
//            //创建新的环境
//            Symbols newSyms = new Symbols(syms);
//            //将参数放入新的环境
//            //将body中的变量放入新的环境
//        }
    }



    //params和name类的lookup方法
    @Reviser public static class ParamsEx extends ParameterList {
        protected int[] offsets = null;
        public ParamsEx(List<ASTree> c) { super(c); }
        public void lookup(Symbols syms) {
            int s = size();
            offsets = new int[s];
            for (int i = 0; i < s; i++){
                if (name(i).equals("n")) {
                    System.out.print("");
                }
                offsets[i] = syms.putNew(name(i));
            }

//            for (int i = 0; i < s; i++)
//                offsets[i] = syms.putNew(name(i));
        }
        public void eval(Environment env, int index, Object value) {
            ((EnvEx2)env).put(0, offsets[index], value);
        }

//        public void lookup(Symbols syms) {
//            int s = size();
//            offsets = new int[s];
//            for (int i = 0; i < s; i++) {
//                //依次存储参数
//                offsets[i] = syms.putNew(name(i));
//            }
//        }

    }

    //变量名以name表示，
    @Reviser public static class NameEx extends Name {
        protected static final int UNKNOWN = -1;
        //保存下标
        //lookup的时候保存下标，eval的时候直接使用
        protected int nest, index;
        public NameEx(Token t) { super(t); index = UNKNOWN; }
        //思路是什么？
        public void lookup(Symbols syms) {
            System.out.println("name() is " + name());
            Location loc = syms.get(name());
            if (loc == null)
                throw new StoneException("undefined name: " + name(), this);
            else {
                nest = loc.nest;
                index = loc.index;
            }
        }
        public void lookupForAssign(Symbols syms) {
            //返回标识符的存放位置
            Location loc = syms.put(name());
            nest = loc.nest;
            index = loc.index;
        }
        //思路是什么？
        public Object eval(Environment env) {
            //使用UNKNOWN
            if (index == UNKNOWN)
                return env.get(name());
            else
                return ((EnvEx2)env).get(nest, index);
        }
        public void evalForAssign(Environment env, Object value) {
            //UNKNOWN
            if (index == UNKNOWN)
                env.put(name(), value);
            else
                ((EnvEx2)env).put(nest, index, value);
        }
    }
    @Reviser public static class BinaryEx2 extends BasicEvaluator.BinaryEx {
        public BinaryEx2(List<ASTree> c) { super(c); }
        public void lookup(Symbols syms) {
            ASTree left = left();
            if ("=".equals(operator())) {
                if (left instanceof Name) {
                    //赋值操作时，调用name类的lookup方法
                    ((NameEx)left).lookupForAssign(syms);
                    ((ASTreeOptEx)right()).lookup(syms);
                    return;
                }
            }
            ((ASTreeOptEx)left).lookup(syms);
            ((ASTreeOptEx)right()).lookup(syms);
        }

//        public void lookup(Symbols syms) {
//            ASTree left = left();
//            //如果是赋值
//            if ("=".equals(operator())) {
//
//            }
//        }

        //计算赋值
        @Override
        protected Object computeAssign(Environment env, Object rvalue) {
            ASTree l = left();
            if (l instanceof Name) {
                ((NameEx)l).evalForAssign(env, rvalue);
                return rvalue;
            }
            else
                return super.computeAssign(env, rvalue);
        }
    }
}
