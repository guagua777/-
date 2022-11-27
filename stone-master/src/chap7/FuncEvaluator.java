package chap7;
import java.util.List;
import javassist.gluonj.*;
import stone.StoneException;
import stone.ast.*;
import chap6.BasicEvaluator;
import chap6.Environment;
import chap6.BasicEvaluator.ASTreeEx;
import chap6.BasicEvaluator.BlockEx;

// @Require 指定该修改器用到的其他修改器
// BasicEvaluator作为分组使用
@Require(BasicEvaluator.class)
@Reviser public class FuncEvaluator {

    //空间范围和时间范围
    //为Environment添加EnvEx中新增的方法
    @Reviser public static interface EnvEx extends Environment {
        void putNew(String name, Object value);
        Environment where(String name);
        void setOuter(Environment e);
    }


//    @Reviser
//    public static interface EnvEx extends Environment {
//        void putNew(String name, Object value);
//        Environment where(String name);
//        void setOuter(Environment e);
//    }


    //函数定义
    //添加eval方法
    @Reviser public static class DefStmntEx extends DefStmnt {
        public DefStmntEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            //将该环境，def的环境传递给Function
            ((EnvEx)env).putNew(name(), new Function(parameters(), body(), env));
            return name();
        }
    }

//    @Reviser
//    public static class DefStmntEx extends DefStmnt {
//        public DefStmntEx(List<ASTree> c) {
//            super(c);
//        }
//
//        public Object eval(Environment env) {
//            ((EnvEx)env).putNew(name(), new Function(parameters(), body(), env));
//            return name();
//        }
//    }


    //函数调用
    @Reviser public static class PrimaryEx extends PrimaryExpr {
        public PrimaryEx(List<ASTree> c) { super(c); }
        //返回调用方法
        public ASTree operand() { return child(0); }
        //返回值为Postfix，而不是Arguments
        public Postfix postfix(int nest) {
            return (Postfix)child(numChildren() - nest - 1);
        }
        public boolean hasPostfix(int nest) {
            return numChildren() - nest > 1;
        }
        public Object eval(Environment env) {
            return evalSubExpr(env, 0);
        }

        //改用循环的方式
//        public Object eval(Environment env) {
//            //Object op = ((ASTreeEx)operand()).eval(env);
//            Object result = ((ASTreeEx)operand()).eval(env);
//            int n = numChildren();
//            for (int i = 1; i < n; i++) {
//                //Object res = ((PostfixEx)postfix(i)).eval(env, op);
//                result = ((PostfixEx)postfix(i)).eval(env, result);
//            }
//            return result;
//        }

        //从外层数起的第几次函数调用
        //主要是为了支持op(a, b)(c)(e, f)这种调用
        //在类的实现中，类.new等也是通过该方法来eval的
        public Object evalSubExpr(Environment env, int nest) {
            if (hasPostfix(nest)) {
                //从后往前算的（重点重点重点）
                //最后获取操作符
                Object target = evalSubExpr(env, nest + 1);
                //postfix(nest)获取当前的实参列表
                return ((PostfixEx)postfix(nest)).eval(env, target);
            }
            else
                //获取操作符
                //在类的实现中，类.new通过类名获取类的定义信息，operand()为类名
                return ((ASTreeEx)operand()).eval(env);
        }


        //primary    :  ( "(" expr ")" | number | identifier | string ) { postfix }
//        public PrimaryEx(List<ASTree> c) {
//            super(c);
//        }
//        public ASTree operand() {
//            return child(0);
//        }
        //**********重点**********
        //获取第几个参数
        //children(0)是调用方法
        //children(1)是第一个参数，第一个参数nest为0
        //
//        public Postfix postfix(int nest) {
//            return (Postfix)child(numChildren() - nest - 1);
//        }
//        public boolean hasPostfix(int nest) {
//            return numChildren() - nest > 1;
//        }
//        public Object eval(Environment env) {
//            return evalSubExpr(env, 0);
//        }

//        public Object evalSubExpr(Environment env, int nest) {
//            if (hasPostfix(nest)) {
//                Object target = evalSubExpr(env, nest + 1);
//                PostfixEx argI = (PostfixEx)postfix(nest);
//                return argI.eval(env, target);
//            } else {
//                ASTreeEx op = (ASTreeEx)operand();
//                return op.eval(env);
//            }
//        }


    }
    @Reviser public static abstract class PostfixEx extends Postfix {
        public PostfixEx(List<ASTree> c) { super(c); }
        public abstract Object eval(Environment env, Object value);
    }


//    @Reviser
//    public static abstract class PostfixEx extends Postfix {
//        public PostfixEx(List<ASTree> c) {
//            super(c);
//        }
//        public abstract Object eval(Environment env, Object value);
//    }


    @Reviser public static class ArgumentsEx extends Arguments {
        public ArgumentsEx(List<ASTree> c) { super(c); }
        public Object eval(Environment callerEnv, Object value) {
            if (!(value instanceof Function))
                throw new StoneException("bad function", this);
            Function func = (Function)value;
            ParameterList params = func.parameters();
            if (size() != params.size())
                throw new StoneException("bad number of arguments", this);
            //创建新的环境
            //参考scheme中闭包
            //env的outer字段始终是，def的环境
            //func.makeEnv(callerEnv)，则为动态作用域
            Environment newEnv = func.makeEnv();
            int num = 0;
            for (ASTree a: this)
                ((ParamsEx)params).eval(newEnv, num++,
                                        ((ASTreeEx)a).eval(callerEnv));

            //动态作用域
            //((EnvEx)newEnv).setOuter(callerEnv);

            return ((BlockEx)func.body()).eval(newEnv);
        }


//        //参考scheme的解释器
//        public Object eval(Environment callerEnv, Object value) {
//            if (!(value instanceof Function)) {
//                throw new StoneException("bad fuction", this);
//            }
//            Function func = (Function) value;
//            ParameterList parameters = func.parameters;
//            if (size() != parameters.size()) {
//                throw new StoneException("bad number of arguments", this);
//            }
//            //创建新的环境
//            Environment newEnv = func.makeEnv();
//            int num = 0;
//            //获取每个实参的值，并将形参和实参的kv对存储新的环境中
//            for (ASTree e : this) {
//                Object v = ((ASTreeEx) e).eval(callerEnv);
//                //num++用于获取第i个形参
//                ((ParamsEx)parameters).eval(newEnv, num++, v);
//            }
//            //计算body的值
//            return ((BlockEx)func.body()).eval(newEnv);
//        }

        //这个地方书中错了，java只有引用类型，传递也是传递的引用
        //将int想象成引用类型


    }
    @Reviser public static class ParamsEx extends ParameterList {
        public ParamsEx(List<ASTree> c) { super(c); }
        public void eval(Environment env, int index, Object value) {
            ((EnvEx)env).putNew(name(index), value);
        }
    }
}
