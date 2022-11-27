package chap8;
import java.util.List;
import stone.StoneException;
import stone.ast.ASTree;
import javassist.gluonj.*;
import chap6.Environment;
import chap6.BasicEvaluator.ASTreeEx;
import chap7.FuncEvaluator;

@Require(FuncEvaluator.class)
@Reviser public class NativeEvaluator {
    @Reviser public static class NativeArgEx extends FuncEvaluator.ArgumentsEx {
        public NativeArgEx(List<ASTree> c) { super(c); }
        @Override public Object eval(Environment callerEnv, Object value) {
            if (!(value instanceof NativeFunction))
                return super.eval(callerEnv, value);

            //如果不是native方法
            //callerEnv
//            if (!(value instanceof NativeFunction)) {
//                return super.eval(callerEnv, value);
//            }

            NativeFunction func = (NativeFunction)value;
            int nparams = func.numOfParameters();
            if (size() != nparams)
                throw new StoneException("bad number of arguments", this);
            Object[] args = new Object[nparams];
            int num = 0;
            //计算实参
            for (ASTree a: this) {
                ASTreeEx ae = (ASTreeEx)a;
                args[num++] = ae.eval(callerEnv);
            }
            //计算各个实参
            //为什么是this？因为是Arguments节点，只包含了实参
//            for (ASTree a : this) {
//                ASTreeEx ae = (ASTreeEx)a;
//                args[num++] = ae.eval(callerEnv)
//            }
            //调用native方法
            return func.invoke(args, this);

//            return func.invoke(args, this);
        }
    }

//    @Reviser
//    public static class NativeArgEx extends FuncEvaluator.ArgumentsEx {
//        public NativeArgEx(List<ASTree> c) {
//            super(c);
//        }
//
//        @Override
//        public Object eval(Environment callerEnv, Object value) {
//
//        }
//    }
}
