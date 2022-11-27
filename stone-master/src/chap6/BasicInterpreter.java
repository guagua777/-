package chap6;
import stone.*;
import stone.ast.ASTree;
import stone.ast.NullStmnt;

public class BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new BasicParser(), new BasicEnv());
    }
    public static void run(BasicParser bp, Environment env)
        throws ParseException
    {
        Lexer lexer = new Lexer(new CodeDialog());
        while (lexer.peek(0) != Token.EOF) {
            ASTree t = bp.parse(lexer);
            if (!(t instanceof NullStmnt)) {
                //parser后得到的是ASTree结点
                //eval求值后才会得到具体的值（比如classinfo）
                Object r = ((BasicEvaluator.ASTreeEx)t).eval(env);
                System.out.println("=> " + r);
            }
        }
    }

//    public static void run(BasicParser bp, Environment env) throws ParseException {
//        Lexer lexer = new Lexer(new CodeDialog());
//        while (lexer.peek(0) != Token.EOF) {
//            ASTree t = bp.parse(lexer);
//            if (!(t instanceof NullStmnt)) {
//                Object r = ((BasicEvaluator.ASTreeEx) t).eval(env);
//                System.out.println("=> " + r);
//            }
//        }
//    }


}
