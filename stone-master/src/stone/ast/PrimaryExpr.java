package stone.ast;
import java.util.List;

public class PrimaryExpr extends ASTList {
    public PrimaryExpr(List<ASTree> c) { super(c); }
    //parser库中，会调用create方法，创建该节点对象，而不是使用默认的构造方法创建对象
    public static ASTree create(List<ASTree> c) {
        //如果子节点只有一个，直接返回子节点，不创建当前结点
        return c.size() == 1 ? c.get(0) : new PrimaryExpr(c);
    }
}
