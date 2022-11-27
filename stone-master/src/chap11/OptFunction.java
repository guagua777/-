package chap11;
import stone.ast.BlockStmnt;
import stone.ast.ParameterList;
import chap6.Environment;
import chap7.Function;

//使用array来执行环境
public class OptFunction extends Function {
    //这个字段是干什么用的？
    protected int size;
    public OptFunction(ParameterList parameters, BlockStmnt body,
                       Environment env, int memorySize)
    {
        super(parameters, body, env);
        size = memorySize;
    }
    @Override public Environment makeEnv() {
        return new ArrayEnv(size, env);
    }

//    public OptFunction(ParameterList parameters, BlockStmnt body, Environment env, int memorySize) {
//        //参数 body 环境
//        super(parameters, body, env);
//        size = memorySize;
//    }

//    @Override
//    public Environment makeEnv() {
//        return new ArrayEnv(size, env);
//    }

}
