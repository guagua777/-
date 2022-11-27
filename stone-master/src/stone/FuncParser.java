package stone;
import static stone.Parser.rule;

import com.sun.javafx.css.Rule;
import stone.ast.ParameterList;
import stone.ast.Arguments;
import stone.ast.DefStmnt;


/**
 * primary    :  "(" expr ")" | number | identifier | string
 * factor     :  "-" primary | primary
 * expr       :  factor { OP factor}
 * block      :  "{" [ statement ] { (";" | EOL) [ statement ]} "}"
 * simple     :  expr
 * statement  :  "if" expr block [ "else" block ]
 *               | "while" expr block
 *               | simple
 * program    : [ statement ] (";" | EOL)
 */

/**
 * param      :  identifier
 * //形参
 * params     :  param { "," param }
 * param_list :  "(" [ params ] ")
 * def        :  "def" identifier  param_list block
 * //实参
 * args       :  expr { "," expr }
 * //后缀，词尾
 * postfix    :  "(" [ args ] ")"
 * //基本元素，添加对函数调用的支持
 * primary    :  ( "(" expr ")" | number | identifier | string ) { postfix }
 * //支持函数调用
 * simple     :  expr [ args ]
 * program    :  [ def | statement ] (";" | EOL)
 */

//继承basicParser
public class FuncParser extends BasicParser {
    Parser param = rule().identifier(reserved);
    Parser params = rule(ParameterList.class)
                        .ast(param).repeat(rule().sep(",").ast(param));
    //使用了maybe
    //添加可省略的非终结符
    //如果省略，会保留根节点
    Parser paramList = rule().sep("(").maybe(params).sep(")");
    Parser def = rule(DefStmnt.class)
                     .sep("def").identifier(reserved).ast(paramList).ast(block);
    Parser args = rule(Arguments.class)
                      .ast(expr).repeat(rule().sep(",").ast(expr));
    // maybe 方法，向模式中添加可省略的非终结符
    Parser postfix = rule().sep("(").maybe(args).sep(")");


//    Parser param = rule().identifier(reserved);
//    Parser params = rule(ParameterList.class).ast(param).repeat(rule().sep(",").ast(param));
//    Parser paramList = rule().sep("(").maybe(params).sep(")");
//    Parser def = rule(DefStmnt.class).sep("def").identifier(reserved).ast(paramList).ast(block);
//    Parser args = rule(Arguments.class).ast(expr).repeat(rule().sep(",").ast(expr));
//    Parser postfix = rule().sep("(").maybe(args).sep(")");



    public FuncParser() {
        //添加)
        reserved.add(")");
        primary.repeat(postfix);
        simple.option(args);
        program.insertChoice(def);
    }
}
