package stone;
import static stone.Parser.rule;
import stone.ast.Fun;


/**
 * primary : " fun " param_list block | 原来的primary
 * primary :  ( "(" expr ")" | number | identifier | string ) { postfix }
 */
public class ClosureParser extends FuncParser {
    public ClosureParser() {
        primary.insertChoice(rule(Fun.class).sep("fun").ast(paramList).ast(block));
    }
}
