package stone.ast;
import java.util.List;

//继承postfix
//可参考bnf范式
public class ArrayRef extends Postfix {
    public ArrayRef(List<ASTree> c) { super(c); }
    public ASTree index() { return child(0); }
    public String toString() { return "[" + index() + "]"; }
}
