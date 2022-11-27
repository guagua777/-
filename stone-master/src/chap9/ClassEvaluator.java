package chap9;
import java.util.List;
import stone.StoneException;
import javassist.gluonj.*;
import stone.ast.*;
import chap6.Environment;
import chap6.BasicEvaluator.ASTreeEx;
import chap6.BasicEvaluator;
import chap7.FuncEvaluator;
import chap7.NestedEnv;
import chap7.FuncEvaluator.EnvEx;
import chap7.FuncEvaluator.PrimaryEx;
import chap9.StoneObject.AccessException;

@Require(FuncEvaluator.class)
@Reviser public class ClassEvaluator {
    @Reviser public static class ClassStmntEx extends ClassStmnt {
        public ClassStmntEx(List<ASTree> c) { super(c); }
        //class的eval得到的是class的信息
        public Object eval(Environment env) {
            ClassInfo ci = new ClassInfo(this, env);
            ((EnvEx)env).put(name(), ci);
            return name();
        }
    }

    /**
     * 为class添加eval方法
     */
//    @Reviser
//    public static class ClassStmntEx extends ClassStmnt {
//        public ClassStmntEx(List<ASTree> c) {
//            super(c);
//        }
//
//        public Object eval(Environment env) {
//            ClassInfo ci = new ClassInfo(this, env);
//            ((EnvEx)env).put(name(), ci);
//            return name();
//        }
//    }

    @Reviser public static class ClassBodyEx extends ClassBody {
        public ClassBodyEx(List<ASTree> c) { super(c); }
        //计算classbody的值，
        public Object eval(Environment env) {
            for (ASTree t: this)
                ((ASTreeEx)t).eval(env);
            return null;
        }
    }
    @Reviser public static class DotEx extends Dot {
        public DotEx(List<ASTree> c) { super(c); }
        //1.创建对象
        //2.调用对象的方法
        //3.为对象的字段赋值
        //由primaryEx的eval方法直接调用，具体可参见 class的bnf语法规则，在primary的postfix中包含.操作
        //value为.左侧的计算结果
        public Object eval(Environment env, Object value) {
            String member = name();
            if (value instanceof ClassInfo) {
                //创建对象
                if ("new".equals(member)) {
                    //这个classInfo是在哪里构造的？
                    //应该是语法解析到class的时候创建的
                    //猜测应该是parser的
                    //parser得到的是结点，eval的时候才会得到ClassInfo这个值
                    //classinfo是个值
                    ClassInfo ci = (ClassInfo)value;
                    //首先创建新的环境
                    NestedEnv e = new NestedEnv(ci.environment());
                    //stoneObject的环境是e，e的outer是classinfo的环境
                    StoneObject so = new StoneObject(e);
                    e.putNew("this", so);
                    //使用这个环境初始化body
                    initObject(ci, e);
                    return so;
                }
            }
            else if (value instanceof StoneObject) {
                try {
                    //获取对象中的值
                    return ((StoneObject)value).read(member);
                } catch (AccessException e) {}
            }
            throw new StoneException("bad member access: " + member, this);
        }

        //value是从哪里来的？
//        public Object eval(Environment env, Object value) {
//            String member = name();
//            if (value instanceof ClassInfo) {
//                if ("new".equals(member)) {
//                    ClassInfo ci = (ClassInfo) value;
//                    //创建新的环境
//                    //为什么要创建新的环境？
//                    NestedEnv e = new NestedEnv(ci.environment());
//                    StoneObject so = new StoneObject(e);
//                    e.putNew("this", so);
//                    initObject(ci, e);
//                    return so;
//                }
//            } else if (value instanceof StoneObject) {
//
//            }
//            throw new StoneException()
//        }


        //实现继承功能
        protected void initObject(ClassInfo ci, Environment env) {
            if (ci.superClass() != null)
                initObject(ci.superClass(), env);
            ((ClassBodyEx)ci.body()).eval(env);
        }

//        protected void initObject(ClassInfo ci, Environment env) {
//            if (ci.superClass != null) {
//                initObject(ci.superClass, env);
//            }
//            ((ClassBodyEx)ci.body()).eval(env)
//        }
    }
    @Reviser public static class AssignEx extends BasicEvaluator.BinaryEx {
        public AssignEx(List<ASTree> c) { super(c); }
        //哪里会调用到这里？

        @Override
        protected Object computeAssign(Environment env, Object rvalue) {
            ASTree le = left();
            //如果赋值运算左侧是一个字段
            //PRIMARY为：查看primary的bnf表达式
            //
            if (le instanceof PrimaryExpr) {
                //看看primaryExpr表达式是什么
                //( "[" [ elements ] "]" | "(" expr ")" | number | identifier | string ) { postfix }
                //postfix    :  "." identifier | "(" [ args ] ")"
                //其中postfix包括.identifier
                PrimaryEx p = (PrimaryEx)le;
                if (p.hasPostfix(0) && p.postfix(0) instanceof Dot) {
                    //需要计算p.get().next.x = 3，直到找到最终的x
                    //搞清楚evalSubExpr要做的事情
                        Object t = ((PrimaryEx)le).evalSubExpr(env, 1);
                    if (t instanceof StoneObject)
                        return setField((StoneObject)t, (Dot)p.postfix(0),
                                        rvalue);
                }
            }
            return super.computeAssign(env, rvalue);
        }

//        protected Object computeAssign(Environment env, Object rvalue) {
//            ASTree le = left();
//            if(le instanceof PrimaryExpr) {
//                PrimaryEx p = (PrimaryEx) le;
//                if (p.hasPostfix(0) && p.postfix(0) instanceof Dot) {
//                    Object t = ((PrimaryEx)le).evalSubExpr(env, 1);
//                    if (t instanceof StoneObject) {
//                        return setField((StoneObject)t, (Dot)p.postfix(0), rvalue);
//                    }
//                }
//            }
//            return super.computeAssign(env, rvalue);
//        }


        protected Object setField(StoneObject obj, Dot expr, Object rvalue) {
            String name = expr.name();
            try {
                obj.write(name, rvalue);
                return rvalue;
            } catch (AccessException e) {
                throw new StoneException("bad member access " + location()
                                         + ": " + name);
            }
        }
    }
}
