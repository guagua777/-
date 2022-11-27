package stone.ast;

import java.util.List;

//def用于定义方法
public class DefStmnt extends ASTList {
    public DefStmnt(List<ASTree> c) { super(c); }
    public String name() { return ((ASTLeaf)child(0)).token().getText(); }
    public ParameterList parameters() { return (ParameterList)child(1); }

//    //因为是语法树，所以使用child(1)就能获取所有的参数
//    //后者根据def的dnf构造，也可以看出来
//    public ParameterList parameters() {
//
//    }


    public BlockStmnt body() { return (BlockStmnt)child(2); } 
    public String toString() {
        return "(def " + name() + " " + parameters() + " " + body() + ")";
    }
}
