package chap12;
import stone.ast.BlockStmnt;
import stone.ast.ParameterList;
import chap11.ArrayEnv;
import chap11.OptFunction;
import chap6.Environment;

//继承OptFunction
public class OptMethod extends OptFunction {
    OptStoneObject self;
    public OptMethod(ParameterList parameters, BlockStmnt body,
                     Environment env, int memorySize, OptStoneObject self)
    {
        super(parameters, body, env, memorySize);
        this.self = self;
    }
    @Override public Environment makeEnv() {
        ArrayEnv e = new ArrayEnv(size, env);
        //在数组的第一个元素（下标为0）保存this的值(self)
        //与ObjOptimizer中DotEx中的new情况一致
        e.put(0, 0, self);
        return e;
    }
}


//public class OptMethod extends OptFunction {
//    OptStoneObject self;
//
//    //参数 body env
//    //memorySize参数是干什么的
//    public OptMethod(ParameterList parameters, BlockStmnt body, Environment env,
//                     int memorySize, OptStoneObject self) {
//        super(parameters, body, env, memorySize);
//        this.self = self;
//    }
//
//    @Override
//    public Environment makeEnv() {
//        ArrayEnv e = new ArrayEnv(size, env);
//        //这句是干什么的？
//        e.put(0, 0, self);
//        return e;
//    }
//}
