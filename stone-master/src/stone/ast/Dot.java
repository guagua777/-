package stone.ast;
import java.util.List;

/**
 * class创建和调用的节点表示
 */
public class Dot extends Postfix {
    public Dot(List<ASTree> c) { super(c); }
    public String name() { return ((ASTLeaf)child(0)).token().getText(); }
    public String toString() { return "." + name(); }

//    public Dot(List<ASTree> c) {
//        super(c);
//    }

//    public String name() {
//        return ((ASTLeaf)child(0)).token().getText();
//    }

}
