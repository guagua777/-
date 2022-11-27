package stone.ast;
import java.util.List;

/**
 * 用子节点个数为0，表示没有参数
 */
public class ParameterList extends ASTList {
    public ParameterList(List<ASTree> c) { super(c); }
    public String name(int i) { return ((ASTLeaf)child(i)).token().getText(); }
    public int size() { return numChildren(); }
}
