package stone.ast;
import chap9.ClassInfo;

import java.util.List;

/**
 * 定义class的抽象语法树
 * 节点
 */
public class ClassStmnt extends ASTList {
    public ClassStmnt(List<ASTree> c) { super(c); }

//    public ClassStmnt(List<ASTree> c) {
//        super(c);
//    }

//    public ClassInfo eval(Environment env) {
//
//    }


    public String name() { return ((ASTLeaf)child(0)).token().getText(); }

//    public String name() {
//        return ((ASTLeaf)child(0)).token().getText();
//    }
    public String superClass() {
        if (numChildren() < 3)
            return null;
        else
            return ((ASTLeaf)child(1)).token().getText();
    }



    public ClassBody body() { return (ClassBody)child(numChildren() - 1); }
    public String toString() {
        String parent = superClass();
        if (parent == null)
            parent = "*";
        return "(class " + name() + " " + parent + " " + body() + ")";
    }
}
