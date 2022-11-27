package stone.ast;
import java.util.List;

/**
 * 用于表示闭包
 */
public class Fun extends ASTList {
    public Fun(List<ASTree> c) { super(c); }
    public ParameterList parameters() { return (ParameterList)child(0); }
    //闭包的body
    public BlockStmnt body() { return (BlockStmnt)child(1); }
    public String toString() {
        return "(fun " + parameters() + " " + body() + ")";
    }

//    public Fun(List<ASTree> c) {
//        super(c);
//    }
//
//    //闭包的参数
//    public ParameterList parameters() {
//        return (ParameterList)child(0);
//    }

}
