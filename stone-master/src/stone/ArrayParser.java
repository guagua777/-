package stone;
import static stone.Parser.rule;
import javassist.gluonj.Reviser;
import stone.ast.*;

/**
 * elements   :  expr { "," expr }
 * primary    :  ( "[" [ elements ] "]" | "(" expr ")" | number | identifier | string ) { postfix }
 * postifx    :  "(" [ args ] ")" | "[" expr "]"
 */
@Reviser public class ArrayParser extends FuncParser {
    //添加elements非终结符
    Parser elements = rule(ArrayLiteral.class)
                          .ast(expr).repeat(rule().sep(",").ast(expr));

    //Parser elements = rule(ArrayLiteral.class).ast(expr).repeat(rule().sep(",").ast(expr));

    public ArrayParser() {
        reserved.add("]");
        primary.insertChoice(rule().sep("[").maybe(elements).sep("]"));
        postfix.insertChoice(rule(ArrayRef.class).sep("[").ast(expr).sep("]"));
    }
}
