package chap7;
import stone.ast.BlockStmnt;
import stone.ast.ParameterList;
import chap6.Environment;

/**
 * FUNCTION由三部分组成
 * 1. 参数
 * 2. body
 * 3. 环境（这个是重点）
 */
public class Function {
    protected ParameterList parameters;
    protected BlockStmnt body;
    protected Environment env;
    public Function(ParameterList parameters, BlockStmnt body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }
    public ParameterList parameters() { return parameters; }
    public BlockStmnt body() { return body; }
    public Environment makeEnv() { return new NestedEnv(env); }
    @Override public String toString() { return "<fun:" + hashCode() + ">"; }

//    protected ParameterList parameters;
//    protected BlockStmnt body;
//    protected Environment env;

//    public Environment makeEnv() {
//        return new NestedEnv(env);
//    }



}
