package chap8;
import stone.ClosureParser;
import stone.ParseException;
import chap6.BasicInterpreter;
import chap7.NestedEnv;

public class NativeInterpreter extends BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new ClosureParser(),
            //首先调用Natives类的环境方法
            new Natives().environment(new NestedEnv()));
    }
}
