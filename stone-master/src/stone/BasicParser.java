package stone;
import static stone.Parser.rule;
import java.util.HashSet;
import stone.Parser.Operators;
import stone.ast.*;


/**
 * {} 至少0次
 * [] 0次或者是1次
 * | 或
 * () 一个完整的模式
 */

/**
 * 构成基本元素，最基本的表达式构成元素
 * primary    :  "(" expr ")" | number | identifier | string
 * 因子
 * factor     :  "-" primary | primary
 * 表达式：双目运算符，最基本的语言只支持双目运算符
 * //binary表达式的左右子节点，由factor创建
 * expr       :  factor { OP factor}
 * 代码块，statement之间使用;或者是EOL分割，statement可以出现0次
 * block      :  "{" [ statement ] { (";" | EOL) [ statement ]} "}"
 * simple     :  expr
 * statement  :  "if" expr block [ "else" block ]
 *               | "while" expr block
 *               | simple
 * 代码块block中可以省略;或EOL，而program中不可以省略，所以单独涉及了program
 * program    : [ statement ] (";" | EOL)
 */
public class BasicParser {
    HashSet<String> reserved = new HashSet<String>();
    Operators operators = new Operators();


    Parser expr0 = rule();

    //rule()方法，创建parser对象的工厂方法，factory方法
    //rule()，模式是空


    //构建primary表达式，所以外层的rule参数为PrimaryExpr
    //内层的or里面rule参数为空
//    Parser primary = rule(PrimaryExpr.class)
//            .or(rule().sep("(").ast(expr0).sep(")"),
//                    rule().number(NumberLiteral.class),
//                    rule().identifier(Name.class, reserved),
//                    rule().string(StringLiteral.class));
//    Parser factor = rule().or(
//            rule(NegativeExpr.class).sep("-").ast(primary),
//            primary);
//    Parser expr = expr0.expression(BinaryExpr.class, factor, operators);
//    Parser statement0 = rule();
//    Parser block = rule(BlockStmnt.class)
//            .sep("{").option(statement0)
//            .repeat(rule().sep(";", Token.EOL).option(statement0))
//            .sep("}");
//    Parser simple = rule(PrimaryExpr.class).ast(expr);


    //rule的参数有什么用？代表该rule构造的语法树的根节点的类型
    //节点类型
    //叶结点的rule也可以接收Class对象，作为参数，创建该类型的叶节点

    //sep主要是用于"(" ","等

    Parser primary = rule(PrimaryExpr.class)
        .or(rule().sep("(").ast(expr0).sep(")"),
            rule().number(NumberLiteral.class),
            rule().identifier(Name.class, reserved),
            rule().string(StringLiteral.class));
    Parser factor = rule().or(rule(NegativeExpr.class).sep("-").ast(primary),
                              primary);
    // 这个的定义为什么是这样？
    Parser expr = expr0.expression(BinaryExpr.class, factor, operators);

    Parser statement0 = rule();
    //这个地方使用statement0可以吗，后面的statement是否能够填充到statement0里面
    Parser block = rule(BlockStmnt.class)
        .sep("{").option(statement0)
        .repeat(rule().sep(";", Token.EOL).option(statement0))
        .sep("}");
    //rule中为什么传入PrimaryExpr
    Parser simple = rule(PrimaryExpr.class).ast(expr);

//     * statement  :  "if" expr block [ "else" block ]
//     *               | "while" expr block
//     *               | simple
//    Parser statement = statement0.or(
//            rule(IfStmnt.class).sep("if").ast(expr).ast(block).option(rule().sep("else").ast(block)),
//            rule(WhileStmnt.class).sep("while").ast(expr).ast(block),
//            simple);
    Parser statement = statement0.or(
            rule(IfStmnt.class).sep("if").ast(expr).ast(block)
                               .option(rule().sep("else").ast(block)),
            rule(WhileStmnt.class).sep("while").ast(expr).ast(block),
            simple);

    //program    : [ statement ] (";" | EOL)
//    Parser program = rule().or(statement, rule(NullStmnt.class))
//            .sep(";", Token.EOL);

    //program通过NullStmnt来实现的原因是，
    //不必要的结点;或EOL将被省略，如果是个空语句，那就没有结点了，所以使用NullStmnt来表示这种情况
    Parser program = rule().or(statement, rule(NullStmnt.class))
                           .sep(";", Token.EOL);

    public BasicParser() {
        //添加保留符号，为什么{不添加进去
        reserved.add(";");
        //根据语法规则，"{" 不需要添加到保留字中，因为"{"不会被识别为标识符
        reserved.add("}");
        reserved.add(Token.EOL);

        //添加带优先级和左右结合的操作符
        operators.add("=", 1, Operators.RIGHT);
        operators.add("==", 2, Operators.LEFT);
        operators.add(">", 2, Operators.LEFT);
        operators.add("<", 2, Operators.LEFT);
        operators.add("+", 3, Operators.LEFT);
        operators.add("-", 3, Operators.LEFT);
        operators.add("*", 4, Operators.LEFT);
        operators.add("/", 4, Operators.LEFT);
        operators.add("%", 4, Operators.LEFT);
    }
    //获取一行程序中包含的单词
    //以行为单位
    public ASTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}
