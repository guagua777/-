package chap13;
import stone.ast.BlockStmnt;
import stone.ast.ParameterList;
import chap6.Environment;
import chap7.Function;

public class VmFunction extends Function {
    //表示虚拟机器语言前端的位置
    protected int entry;
    public VmFunction(ParameterList parameters, BlockStmnt body,
                      Environment env, int entry)
    {
        super(parameters, body, env);
        this.entry = entry;
    }
    public int entry() { return entry; }
}
