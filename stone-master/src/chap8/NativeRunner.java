package chap8;
import javassist.gluonj.util.Loader;
import chap7.ClosureEvaluator;

public class NativeRunner {
    public static void main(String[] args) throws Throwable {
        //传入两个修改器
        Loader.run(NativeInterpreter.class, args, NativeEvaluator.class,
                   ClosureEvaluator.class);
    }
}
