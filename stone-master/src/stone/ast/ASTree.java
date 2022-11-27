package stone.ast;
import java.util.Iterator;

public abstract class ASTree implements Iterable<ASTree> {
    //返回第i个子节点
    public abstract ASTree child(int i);
    //返回子节点的个数，如果没有子节点，则返回0
    public abstract int numChildren();
    //返回一个用于遍历子节点的iterator
    //用于依次遍历所有的子节点
    public abstract Iterator<ASTree> children();
    public abstract String location();
    //用于类型转换，将ASTree类型转换为Iterator类型
    public Iterator<ASTree> iterator() { return children(); }
}
