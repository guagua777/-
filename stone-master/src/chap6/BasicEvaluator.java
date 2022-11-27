package chap6;
import javassist.gluonj.*;
import stone.Token;
import stone.StoneException;
import stone.ast.*;
import java.util.List;

//分组
@Reviser public class BasicEvaluator {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    //嵌套子类必须是static类型
    //所有的类共用父类ASTree
    //为该抽象类添加抽象方法
    @Reviser public static abstract class ASTreeEx extends ASTree {
        public abstract Object eval(Environment env);
    }

//    @Reviser public static abstract class ASTreeEx extends ASTree {
//        public abstract Object eval(Environment env);
//    }

    @Reviser public static class ASTListEx extends ASTList {
        //覆盖父类的构造函数
        public ASTListEx(List<ASTree> c) { super(c); }
        //定义该类的eval方法
        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }
    @Reviser public static class ASTLeafEx extends ASTLeaf {
        public ASTLeafEx(Token t) { super(t); }
        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }

    //数字的eval
    @Reviser public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) { super(t); }
        //返回对应的值
        public Object eval(Environment e) { return value(); }

//        public Object eval(Environment e) {
//            return value();
//        }
    }
    @Reviser public static class StringEx extends StringLiteral {
        public StringEx(Token t) { super(t); }
        public Object eval(Environment e) { return value(); }
    }
    @Reviser public static class NameEx extends Name {
        public NameEx(Token t) { super(t); }
        //从环境中查找对应的值
        public Object eval(Environment env) {
            Object value = env.get(name());
            if (value == null)
                throw new StoneException("undefined name: " + name(), this);
            else
                return value;
        }
    }
    @Reviser public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            //类型转换
            //operand操作数
            Object v = ((ASTreeEx)operand()).eval(env);
            if (v instanceof Integer)
                return new Integer(-((Integer)v).intValue());
            else
                throw new StoneException("bad type for -", this);
        }

//        public Object eval(Environment env) {
//            Object v = ((ASTreeEx) operand()).eval(env);
//            if (v instanceof Integer) {
//                return new Integer(-((Integer)v).intValue());
//            } else {
//                throw new StoneException("bad type for -", this);
//            }
//        }

    }
    @Reviser public static class BinaryEx extends BinaryExpr {
        public BinaryEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            String op = operator();
            //遇到=时，特殊处理
            if ("=".equals(op)) {
                Object right = ((ASTreeEx)right()).eval(env);
                return computeAssign(env, right);
            }
            else {
                Object left = ((ASTreeEx)left()).eval(env);
                Object right = ((ASTreeEx)right()).eval(env);
                return computeOp(left, op, right);
            }
        }

//        public Object eval(Environment env) {
//            String operator = operator();
//            if ("=".equals(operator)) {
//                Object right = ((ASTreeEx) right()).eval(env);
//                return computeAssign(env, right);
//            } else {
//                Object left = ((ASTreeEx)left()).eval(env);
//                Object right = ((ASTreeEx)right()).eval(env);
//                return computeOp(left, op, right);
//            }
//        }

        protected Object computeAssign(Environment env, Object rvalue) {
            ASTree l = left();
            if (l instanceof Name) {
                env.put(((Name)l).name(), rvalue);
                return rvalue;
            }
            else
                throw new StoneException("bad assignment", this);
        }

//        protected Object computeAssign(Environment env, Object rvalue) {
//            ASTree l = left();
//            if (l instanceof Name) {
//                env.put(((Name)l).name(), rvalue);
//                return rvalue;
//            } else {
//                throw new StoneException("bad assignment", this);
//            }
//        }

        protected Object computeOp(Object left, String op, Object right) {
            if (left instanceof Integer && right instanceof Integer) {
                return computeNumber((Integer)left, op, (Integer)right);
            }
            else
                if (op.equals("+"))
                    return String.valueOf(left) + String.valueOf(right);
                else if (op.equals("==")) {
                    if (left == null)
                        return right == null ? TRUE : FALSE;
                    else
                        return left.equals(right) ? TRUE : FALSE;
                }
                else
                    throw new StoneException("bad type", this);
        }
        protected Object computeNumber(Integer left, String op, Integer right) {
            int a = left.intValue(); 
            int b = right.intValue();
            if (op.equals("+"))
                return a + b;
            else if (op.equals("-"))
                return a - b;
            else if (op.equals("*"))
                return a * b;
            else if (op.equals("/"))
                return a / b;
            else if (op.equals("%"))
                return a % b;
            else if (op.equals("=="))
                return a == b ? TRUE : FALSE;
            else if (op.equals(">"))
                return a > b ? TRUE : FALSE;
            else if (op.equals("<"))
                return a < b ? TRUE : FALSE;
            else
                throw new StoneException("bad operator", this);
        }
    }
    @Reviser public static class BlockEx extends BlockStmnt {
        public BlockEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            Object result = 0;
            for (ASTree t: this) {
                if (!(t instanceof NullStmnt))
                    result = ((ASTreeEx)t).eval(env);
            }
            return result;
        }

//        public Object eval(Environment env) {
//            Object result = 0;
//            for (ASTree t : this) {
//                if (!(t instanceof NullStmnt)) {
//                    result = ((ASTreeEx) t).eval(env);
//                }
//            }
//            return result;
//        }

    }
    @Reviser public static class IfEx extends IfStmnt {
        public IfEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            Object c = ((ASTreeEx)condition()).eval(env);
            if (c instanceof Integer && ((Integer)c).intValue() != FALSE)
                return ((ASTreeEx)thenBlock()).eval(env);
            else {
                ASTree b = elseBlock();
                if (b == null)
                    return 0;
                else
                    return ((ASTreeEx)b).eval(env);
            }
        }

        //if while block的eval
//        public Object eval(Environment env) {
//            Object c = ((ASTreeEx) condition()).eval(env);
//            if (c instanceof Integer && ((Integer)c).intValue() != FALSE) {
//                return ((ASTreeEx)thenBlock()).eval(env);
//            } else {
//                ASTree elseB = elseBlock();
//                if (elseB == null) {
//                    return 0;
//                } else {
//                    return ((ASTreeEx)elseB).eval(env);
//                }
//            }
//        }

    }
    @Reviser public static class WhileEx extends WhileStmnt {
        public WhileEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            Object result = 0;
            for (;;) {
                Object c = ((ASTreeEx)condition()).eval(env);
                if (c instanceof Integer && ((Integer)c).intValue() == FALSE)
                    return result;
                else
                    result = ((ASTreeEx)body()).eval(env);
            }
        }

//        public Object eval(Environment env) {
//
//        }

    }
}
