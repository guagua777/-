package stone;
import static stone.Parser.rule;
import stone.ast.ClassBody;
import stone.ast.ClassStmnt;
import stone.ast.Dot;
import stone.ast.Postfix;

/**
 * member     :  def | simple
 * class_body :  "{" [ member ] {(";" | EOL) [ member ]} "}"
 * defclass   :  "class" identifier [ "extends" identifier ] class_body
 * postfix    :  "." identifier | "(" [ args ] ")"
 * program    :  [ defcalss | def | statement ] (";" | EOL)
 */
public class ClassParser extends ClosureParser {
    Parser member = rule().or(def, simple);

//    Parser member = rule().or(def, simple);


    Parser class_body = rule(ClassBody.class).sep("{").option(member)
                            .repeat(rule().sep(";", Token.EOL).option(member))
                            .sep("}");

//    Parser class_body = rule(ClassBody.class).sep("{")
//            .option(member).repeat(rule().sep(";", Token.EOL).option(member))
//            .sep("}");

    Parser defclass = rule(ClassStmnt.class).sep("class").identifier(reserved)
                          .option(rule().sep("extends").identifier(reserved))
                          .ast(class_body);

//    Parser defclass = rule(ClassStmnt.class).sep("class").identifier(reserved)
//            .option(rule().sep("extends").identifier(reserved)).ast(class_body);

    //postfix    :  "." identifier | "(" [ args ] ")"
    //program    :  [ defcalss | def | statement ] (";" | EOL)
    public ClassParser() {
        postfix.insertChoice(rule(Dot.class).sep(".").identifier(reserved));
        program.insertChoice(defclass);

//        postfix.insertChoice(rule(Dot.class).sep(".").identifier(reserved));
//        program.insertChoice(defclass);

    }

}
