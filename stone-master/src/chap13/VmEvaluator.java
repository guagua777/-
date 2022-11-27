package chap13;
import java.util.List;
import stone.StoneException;
import stone.Token;
import chap11.EnvOptimizer;
import chap6.Environment;
import chap6.BasicEvaluator.ASTreeEx;
import chap7.FuncEvaluator;
import javassist.gluonj.*;
import static chap13.Opcode.*;
import static javassist.gluonj.GluonJ.revise;
import stone.ast.*;

//依赖EnvOptimizer
@Require(EnvOptimizer.class)
@Reviser public class VmEvaluator {
    @Reviser public static interface EnvEx3 extends EnvOptimizer.EnvEx2 {
        //添加两个方法
        StoneVM stoneVM();
        //code方法是做什么用的
        Code code();
    }
    @Reviser public static abstract class ASTreeVmEx extends ASTree {
        //给抽象语法树结点，添加compile方法
        public void compile(Code c) {}
    }

    //ASTree分为astlist和astleaf两种
    @Reviser public static class ASTListEx extends ASTList {
        public ASTListEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            for (ASTree t: this)
                ((ASTreeVmEx)t).compile(c);
        }
    }

    /**
     * def fib (n) {
     *     a = a + 1
     *     if n < 2 {
     *         n
     *     } else {
     *         fib (n - 1) + fib (n - 2)
     *     }
     * }
     */
    //执行编译操作
    //函数编译后，将有arguments的eval方法执行
    @Reviser public static class DefStmntVmEx extends EnvOptimizer.DefStmntEx {
        public DefStmntVmEx(List<ASTree> c) { super(c); }
        //添加编译complile方法
        //修改eval方法
        //def
        @Override public Object eval(Environment env) {
            String funcName = name();
            EnvEx3 vmenv = (EnvEx3)env;
            //这个是什么意思
            Code code = vmenv.code();
            //获取code的位置
            int entry = code.position();
            compile(code);
            //环境中放入，方法名和对应的值
            ((EnvEx3)env).putNew(funcName, new VmFunction(parameters(), body(),
                                                          env, entry));
            return funcName;
        }

        //往code中添加指令
        public void compile(Code c) {
            c.nextReg = 0;
            c.frameSize = size + StoneVM.SAVE_AREA_SIZE;
            //为什么def是添加save指令？
            //为什么要添加save move restore return指令
            c.add(SAVE);
            c.add(encodeOffset(size));
            ((ASTreeVmEx)revise(body())).compile(c);
            c.add(MOVE);
            c.add(encodeRegister(c.nextReg - 1));
            c.add(encodeOffset(0));
            c.add(RESTORE);
            c.add(encodeOffset(size));
            c.add(RETURN);
        }
    }
    @Reviser public static class ParamsEx2 extends EnvOptimizer.ParamsEx {
        public ParamsEx2(List<ASTree> c) { super(c); }
        //？？？
        @Override public void eval(Environment env, int index, Object value) {
            StoneVM vm = ((EnvEx3)env).stoneVM();
            vm.stack()[offsets[index]] = value;
        }
    }
    @Reviser public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) { super(t); }
        public void compile(Code c) {
            int v = value();
            //添加BCONST指令
            if (Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE) {
                c.add(BCONST);
                c.add((byte)v);
            }
            else {
                c.add(ICONST);
                c.add(v);
            }
            c.add(encodeRegister(c.nextReg++));
        }
    }
    @Reviser public static class StringEx extends StringLiteral {
        public StringEx(Token t) { super(t); }
        public void compile(Code c) {
            int i = c.record(value());
            c.add(SCONST);
            c.add(encodeShortOffset(i));
            c.add(encodeRegister(c.nextReg++));
        }
    }
    @Reviser public static class NameEx2 extends EnvOptimizer.NameEx {
        public NameEx2(Token t) { super(t); }
        public void compile(Code c) {
            if (nest > 0) {
                c.add(GMOVE);
                c.add(encodeShortOffset(index));
                c.add(encodeRegister(c.nextReg++));
            }
            else {
                c.add(MOVE);
                c.add(encodeOffset(index));
                c.add(encodeRegister(c.nextReg++));
            }
        }
        public void compileAssign(Code c) {
            if (nest > 0) {
                c.add(GMOVE);
                c.add(encodeRegister(c.nextReg - 1));
                c.add(encodeShortOffset(index));
            }
            else {
                c.add(MOVE);
                c.add(encodeRegister(c.nextReg - 1));
                c.add(encodeOffset(index));
            }
        }
    }
    @Reviser public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            ((ASTreeVmEx)operand()).compile(c);
            c.add(NEG);
            c.add(encodeRegister(c.nextReg - 1));   
        }
    }
    @Reviser public static class BinaryEx extends BinaryExpr {
        public BinaryEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            String op = operator();
            if (op.equals("=")) {
                ASTree l = left();
                if (l instanceof Name) {
                    ((ASTreeVmEx)right()).compile(c);
                    ((NameEx2)l).compileAssign(c);
                }
                else
                    throw new StoneException("bad assignment", this);
            }
            else {
                ((ASTreeVmEx)left()).compile(c);
                ((ASTreeVmEx)right()).compile(c);
                c.add(getOpcode(op));
                c.add(encodeRegister(c.nextReg - 2));
                c.add(encodeRegister(c.nextReg - 1));
                c.nextReg--;
            }
        }

        //转换成对应的指令
        protected byte getOpcode(String op) {
            if (op.equals("+"))
                return ADD;
            else if (op.equals("-"))
                return SUB;
            else if (op.equals("*"))
                return MUL;
            else if (op.equals("/"))
                return DIV;
            else if (op.equals("%"))
                return REM;
            else if (op.equals("=="))
                return EQUAL;
            else if (op.equals(">"))
                return MORE;
            else if (op.equals("<"))
                return LESS;
            else
                throw new StoneException("bad operator", this);
        }
    }
    @Reviser public static class PrimaryVmEx extends FuncEvaluator.PrimaryEx {
        public PrimaryVmEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            compileSubExpr(c, 0);
        }
        public void compileSubExpr(Code c, int nest) {
            if (hasPostfix(nest)) {
                compileSubExpr(c, nest + 1);
                ((ASTreeVmEx)revise(postfix(nest))).compile(c);
            }
            //操作符
            else
                ((ASTreeVmEx)operand()).compile(c);
        }
    }
    @Reviser public static class ArgumentsEx extends Arguments {
        public ArgumentsEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            //调用方栈帧的大小？
            int newOffset = c.frameSize;
            int numOfArgs = 0;
            //将实参保存到栈帧中
            for (ASTree a: this) {
                ((ASTreeVmEx)a).compile(c);
                //复制到栈中？？
                c.add(MOVE);
                c.add(encodeRegister(--c.nextReg));
                c.add(encodeOffset(newOffset++));
                numOfArgs++;
            }
            //call指令调用新的函数
            c.add(CALL);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeOffset(numOfArgs));
            c.add(MOVE);
            c.add(encodeOffset(c.frameSize));
            c.add(encodeRegister(c.nextReg++));
        }
        //虚拟机器语言的执行开始的地方
        public Object eval(Environment env, Object value) {
            if (!(value instanceof VmFunction))
                throw new StoneException("bad function", this);
            VmFunction func = (VmFunction)value;
            ParameterList params = func.parameters();
            if (size() != params.size())
                throw new StoneException("bad number of arguments", this);
            int num = 0;
            //参数
            for (ASTree a: this)
                ((ParamsEx2)params).eval(env, num++, ((ASTreeEx)a).eval(env)); 
            StoneVM svm = ((EnvEx3)env).stoneVM();
            svm.run(func.entry());
            //把从栈区取的返回值作为自身的返回值
            return svm.stack()[0];
        }
    }
    @Reviser public static class BlockEx extends BlockStmnt {
        public BlockEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            if (this.numChildren() > 0) {
                int initReg = c.nextReg;
                for (ASTree a: this) {
                    //还原为之前的数量
                    c.nextReg = initReg;
                    ((ASTreeVmEx)a).compile(c);
                }
            }
            else {
                //空代码块转换为机器语言
                c.add(BCONST);
                c.add((byte)0);
                c.add(encodeRegister(c.nextReg++));
            }
        }
    }
    @Reviser public static class IfEx extends IfStmnt {
        public IfEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            //if语句的条件表达式，编译
            //运行的时候，参考
            ((ASTreeVmEx)condition()).compile(c);
            int pos = c.position();
            //在条件表达式后面，插入ifzero指令
            c.add(IFZERO);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeShortOffset(0));
            int oldReg = c.nextReg;
            ((ASTreeVmEx)thenBlock()).compile(c);
            int pos2 = c.position();
            //插入got指令
            c.add(GOTO);
            c.add(encodeShortOffset(0));
            c.set(encodeShortOffset(c.position() - pos), pos + 2);
            ASTree b = elseBlock();
            c.nextReg = oldReg;
            //else的编译
            if (b != null)
                ((ASTreeVmEx)b).compile(c);
            else {
                c.add(BCONST);
                c.add((byte)0);
                c.add(encodeRegister(c.nextReg++));
            }
            c.set(encodeShortOffset(c.position() - pos2), pos2 + 1);
        }
    }
    @Reviser public static class WhileEx extends WhileStmnt {
        public WhileEx(List<ASTree> c) { super(c); }
        public void compile(Code c) {
            int oldReg = c.nextReg;
            c.add(BCONST);
            c.add((byte)0);
            c.add(encodeRegister(c.nextReg++));
            int pos = c.position();
            ((ASTreeVmEx)condition()).compile(c);
            int pos2 = c.position();
            c.add(IFZERO);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeShortOffset(0));
            c.nextReg = oldReg;
            ((ASTreeVmEx)body()).compile(c);
            int pos3= c.position();
            c.add(GOTO);
            c.add(encodeShortOffset(pos - pos3));
            c.set(encodeShortOffset(c.position() - pos2), pos2 + 2);
        }
    }
}
