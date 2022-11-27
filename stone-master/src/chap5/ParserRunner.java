package chap5;
import stone.ast.ASTree;
import stone.*;

public class ParserRunner {
    public static void main(String[] args) throws ParseException {
        Lexer l = new Lexer(new CodeDialog());
        BasicParser bp = new BasicParser();
        while (l.peek(0) != Token.EOF) {
            ASTree ast = bp.parse(l);
//            String r = ast.toString();
//            System.out.println(r);
            System.out.println("=> " + ast.toString());
        }
    }
}
