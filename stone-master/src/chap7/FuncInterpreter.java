package chap7;
import stone.FuncParser;
import stone.ParseException;
import chap6.BasicInterpreter;

public class FuncInterpreter extends BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        //解释器的初始环境是一个NestedEnv
        run(new FuncParser(), new NestedEnv());
    }
}
