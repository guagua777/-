package chap12;
import java.util.ArrayList;
import java.util.List;
import static javassist.gluonj.GluonJ.revise;
import javassist.gluonj.*;
import stone.*;
import stone.ast.*;
import chap6.Environment;
import chap6.BasicEvaluator;
import chap6.BasicEvaluator.ASTreeEx;
import chap7.FuncEvaluator.PrimaryEx;
import chap11.ArrayEnv;
import chap11.EnvOptimizer;
import chap11.Symbols;
import chap11.EnvOptimizer.ASTreeOptEx;
import chap11.EnvOptimizer.EnvEx2;
import chap11.EnvOptimizer.ParamsEx;
import chap12.OptStoneObject.AccessException;

@Require(EnvOptimizer.class)
@Reviser public class ObjOptimizer {

    //classStmnt
    @Reviser public static class ClassStmntEx extends ClassStmnt {
        public ClassStmntEx(List<ASTree> c) { super(c); }
        //添加空的lookup方法
        public void lookup(Symbols syms) {}
        public Object eval(Environment env) {
            //((EnvEx2)env).symbols()为全局的标识符与位置的对应关系表
            //methodNames为方法的关系表
            Symbols methodNames = new MemberSymbols(((EnvEx2)env).symbols(),
                                                    MemberSymbols.METHOD);
            //字段的关系表
            Symbols fieldNames = new MemberSymbols(methodNames,
                                                   MemberSymbols.FIELD);
            //创建一个OptClassInfo添加到环境中，保存当前定义类的信息
            OptClassInfo ci = new OptClassInfo(this, env, methodNames,
                                               fieldNames);
            //放入类名和对应的类的信息
            ((EnvEx2)env).put(name(), ci);
            ArrayList<DefStmnt> methods = new ArrayList<DefStmnt>();
            //将父类的字段和方法复制到本类
            if (ci.superClass() != null)
                ci.superClass().copyTo(fieldNames, methodNames, methods);
            //创建symbolThis，并将field的symbols设置为SymbolThis的outer
            Symbols newSyms = new SymbolThis(fieldNames);
            //对body执行lookup方法
            ((ClassBodyEx)body()).lookup(newSyms, methodNames, fieldNames,
                                         methods);
            ci.setMethods(methods);
            return name();
        }

//        public Object eval(Environment env) {
//
//        }


    }
    @Reviser public static class ClassBodyEx extends ClassBody {
        public ClassBodyEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            for (ASTree t: this)
                if (!(t instanceof DefStmnt))
                    ((ASTreeEx)t).eval(env);
            return null;
        }
        public void lookup(Symbols syms, Symbols methodNames,
                           Symbols fieldNames, ArrayList<DefStmnt> methods)
        {
            for (ASTree t: this) {
                //对def做特殊处理
                if (t instanceof DefStmnt) {
                    DefStmnt def = (DefStmnt)t;
                    int oldSize = methodNames.size();
                    //检查是否已经存在
                    int i = methodNames.putNew(def.name());
                    if (i >= oldSize)
                        methods.add(def);
                    else
                        methods.set(i, def);
                    //这句是干什么的？
                    ((DefStmntEx2)def).lookupAsMethod(fieldNames);
                }
                else
                    ((ASTreeOptEx)t).lookup(syms);
            }
        }

//        public void lookup(Symbols syms, Symbols methodNames, Symbols fieldNames, ArrayList<DefStmnt> methods) {
//            for (ASTree t : this) {
//                if (t instanceof DefStmnt) {
//                    DefStmnt def = (DefStmnt)t;
//                    int oldSize = methodNames.size();
//                    int i = methodNames.putNew(def.name());
//                    if (i >= oldSize) {
//                        methods.add(def);
//                    } else {
//                        methods.set(i, def);
//                    }
//                } else {
//                    //这句是什么意思？
//                    ((ASTreeOptEx)t).lookup(syms);
//                }
//            }
//        }
    }
    @Reviser public static class DefStmntEx2 extends EnvOptimizer.DefStmntEx {
        public DefStmntEx2(List<ASTree> c) { super(c); }
        public int locals() { return size; }
        public void lookupAsMethod(Symbols syms) {
            //创建新的
            Symbols newSyms = new Symbols(syms);
            //在方法中放入this
            newSyms.putNew(SymbolThis.NAME);
            //方法的参数
            ((ParamsEx)parameters()).lookup(newSyms);
            //方法的body
            ((ASTreeOptEx)revise(body())).lookup(newSyms);
            size = newSyms.size();
        }
    }
    @Reviser public static class DotEx extends Dot {
        public DotEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env, Object value) {
            String member = name();
            //.左侧的值是什么，点左侧是classOInfo
            //创建class的对象
            if (value instanceof OptClassInfo) {
                if ("new".equals(member)) {
                    OptClassInfo ci = (OptClassInfo)value;
                    //新创建一个array的env
                    //仅保存了this的值
                    ArrayEnv newEnv = new ArrayEnv(1, ci.environment());
                    //首先创建一个OptStoneObject
                    OptStoneObject so = new OptStoneObject(ci, ci.size());
                    //将this放到数组中，this就是so
                    newEnv.put(0, 0, so);
                    initObject(ci, so, newEnv);
                    return so;
                }
            }
            //.左侧是stone对象
            else if (value instanceof OptStoneObject) {
                try {
                    return ((OptStoneObject)value).read(member);
                } catch (AccessException e) {}
            }
            throw new StoneException("bad member access: " + member, this);
        }
        protected void initObject(OptClassInfo ci, OptStoneObject obj,
                                  Environment env)
        {
            if (ci.superClass() != null)
                initObject(ci.superClass(), obj, env);
            ((ClassBodyEx)ci.body()).eval(env);
        }
    }
    @Reviser public static class NameEx2 extends EnvOptimizer.NameEx {
        public NameEx2(Token t) { super(t); }
        @Override public Object eval(Environment env) {
            if (index == UNKNOWN)
                return env.get(name());
            else if (nest == MemberSymbols.FIELD)
                return getThis(env).read(index);
            else if (nest == MemberSymbols.METHOD)
                return getThis(env).method(index);
            else
                return ((EnvEx2)env).get(nest, index);
        }

//        @Override
//        public Object eval(Environment env) {
//            if (index == UNKNOWN) {
//
//            }
//        }

        @Override public void evalForAssign(Environment env, Object value) {
            if (index == UNKNOWN)
                env.put(name(), value);
            else if (nest == MemberSymbols.FIELD)
                getThis(env).write(index, value);
            else if (nest == MemberSymbols.METHOD)
                throw new StoneException("cannot update a method: " + name(),
                                         this);
            else
                ((EnvEx2)env).put(nest, index, value);
        }

//        @Override
//        public void evalForAssign(Environment env, Object value) {
//
//        }

        protected OptStoneObject getThis(Environment env) {
            return (OptStoneObject)((EnvEx2)env).get(0, 0);
        }

        //重点
        //在name中获取对应的this
//        protected OptStoneObject getThis(Environment env) {
//            //获取下标为0的值，就是this
//            return ((EnvEx2)env).get(0, 0);
//        }

    }
    @Reviser public static class AssignEx extends BasicEvaluator.BinaryEx {
        public AssignEx(List<ASTree> c) { super(c); }
        @Override
        protected Object computeAssign(Environment env, Object rvalue) {
            ASTree le = left();
            if (le instanceof PrimaryExpr) {
                PrimaryEx p = (PrimaryEx)le;
                if (p.hasPostfix(0) && p.postfix(0) instanceof Dot) {
                    //递归计算
                    //evalSubExpr方法是重点
                    Object t = ((PrimaryEx)le).evalSubExpr(env, 1);
                    if (t instanceof OptStoneObject)
                        return setField((OptStoneObject)t, (Dot)p.postfix(0),
                                        rvalue);
                }
            }
            return super.computeAssign(env, rvalue);
        }

        //重写赋值
//        @Override
//        public Object computeAssign(Environment env, Object rvalue) {
//            ASTree le = left();
//
//        }

        protected Object setField(OptStoneObject obj, Dot expr, Object rvalue) {
            String name = expr.name();
            try {
                obj.write(name, rvalue);
                return rvalue;
            } catch (AccessException e) {
                throw new StoneException("bad member access: " + name, this);
            }
        }
    }
}
