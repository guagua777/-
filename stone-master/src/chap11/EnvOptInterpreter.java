package chap11;
import chap6.BasicEvaluator;
import chap6.Environment;
import chap8.Natives;
import stone.BasicParser;
import stone.ClosureParser;
import stone.CodeDialog;
import stone.Lexer;
import stone.ParseException;
import stone.Token;
import stone.ast.ASTree;
import stone.ast.NullStmnt;

public class EnvOptInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new ClosureParser(),
            //创建了一个ResizableArrayEnv，用于记录全局变量
            new Natives().environment(new ResizableArrayEnv()));
    }

//    public static void main(String[] args) {
//        //全局环境
//        run(new ClosureParser(), new Natives().environment(new ResizableArrayEnv()));
//    }


    public static void run(BasicParser bp, Environment env)
        throws ParseException
    {
        Lexer lexer = new Lexer(new CodeDialog());
        while (lexer.peek(0) != Token.EOF) {
            ASTree t = bp.parse(lexer);
            if (!(t instanceof NullStmnt)) {
                //首先调用lookup方法，eval方法在lookup方法之后
                ((EnvOptimizer.ASTreeOptEx)t).lookup(
                        ((EnvOptimizer.EnvEx2)env).symbols());
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
//                //先找到所有的变量
//                ((EnvOptimizer.ASTreeOptEx)t).lookup(((EnvOptimizer.EnvEx2)env).symbols());
//                Object r = ((BasicEvaluator.ASTreeEx)t).eval(env);
//                System.out.println("=> " + r);
//            }
//        }
//    }

}
