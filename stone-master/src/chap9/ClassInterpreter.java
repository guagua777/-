package chap9;
import stone.ClassParser;
import stone.ParseException;
import chap6.BasicInterpreter;
import chap7.NestedEnv;
import chap8.Natives;

public class ClassInterpreter extends BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        //传入的环境是含有原生方法的环境
        run(new ClassParser(), new Natives().environment(new NestedEnv())); 
    }
}
