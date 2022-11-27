package chap7;
import java.util.List;
import javassist.gluonj.*;
import stone.ast.ASTree;
import stone.ast.Fun;
import chap6.Environment;

//需要FuncEvaluator的修改器
@Require(FuncEvaluator.class)
@Reviser public class ClosureEvaluator {
    @Reviser public static class FunEx extends Fun {
        public FunEx(List<ASTree> c) { super(c); }

        //重点关注env
        //重点
        //返回一个函数（函数中有环境）
        public Object eval(Environment env) {
            //构建一个function来表示闭包
            //直接返回这个Function，就可以将这个Function赋值给变量，或者传递给函数参数了
            return new Function(parameters(), body(), env);
            // def中为：将新创建的Function与对应的函数名，放到env里面,
            //返回函数名
            //((EnvEx)env).putNew(name(), new Function(parameters(), body(), env));
            //return name();
        }

    }

    /**
     * def counter(c) {
     *     fun () {
     *         c = c + 1
     *     }
     * }  返回值为counter
     *
     * c1 = counter(0) 返回值为<fun:873610597>
     * c2 = counter(0) 返回值为<fun:1497845528>
     * c1() 返回值为1
     * c1() 返回值为2
     * c2() 返回值为1
     * => counter
     * => <fun:873610597>
     * => <fun:1497845528>
     * => 1
     * => 2
     * => 1
     */

}
