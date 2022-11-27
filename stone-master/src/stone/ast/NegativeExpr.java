package stone.ast;
import chap6.BasicEvaluator;
import chap6.Environment;
import stone.StoneException;

import java.util.List;

public class NegativeExpr extends ASTList {
    public NegativeExpr(List<ASTree> c) { super(c); }
    //operand操作数
    //获取操作数
    public ASTree operand() { return child(0); }
    public String toString() {
        return "-" + operand();
    }

//    public Object eval(Environment env) {
//        //类型转换
//        //operand操作数
//        Object v = operand().eval(env);
//        if (v instanceof Integer)
//            return new Integer(-((Integer)v).intValue());
//        else
//            throw new StoneException("bad type for -", this);
//    }

}
