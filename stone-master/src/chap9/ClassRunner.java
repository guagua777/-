package chap9;
import javassist.gluonj.util.Loader;
import chap7.ClosureEvaluator;
import chap8.NativeEvaluator;

public class ClassRunner {
    public static void main(String[] args) throws Throwable {
        //三个扩展器
        //class native closure
        Loader.run(ClassInterpreter.class, args, ClassEvaluator.class,
                   NativeEvaluator.class, ClosureEvaluator.class);
    }
}
