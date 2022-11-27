package chap12;
import java.util.List;
import stone.StoneException;
import stone.ast.ASTree;
import stone.ast.Dot;
import chap6.Environment;
import javassist.gluonj.*;

@Require(ObjOptimizer.class)
@Reviser public class InlineCache {
    @Reviser public static class DotEx2 extends ObjOptimizer.DotEx {
        //添加两个字段，classInfo和index字段实现缓存
        protected OptClassInfo classInfo = null;
        protected boolean isField;
        protected int index;
        public DotEx2(List<ASTree> c) { super(c); }

//        //实现缓存功能inline-cache
//        protected OptClassInfo classInfo = null;
//        protected int index;
//        //标识
//        protected boolean isField;
//
//        public DotEx2(List<ASTree> c) {
//            super(c);
//        }

        @Override public Object eval(Environment env, Object value) {
            if (value instanceof OptStoneObject) {
                OptStoneObject target = (OptStoneObject)value;
                if (target.classInfo() != classInfo)
                    updateCache(target);
                if (isField)
                    return target.read(index);
                else
                    return target.method(index);
            }
            else
                return super.eval(env, value);
        }

//        @Override
//        public Object eval(Environment env, Object value) {
//            if (value instanceof OptStoneObject) {
//                OptStoneObject target = (OptStoneObject)value;
//                if (target.classInfo != classInfo) {
//                    updateCache(target);
//                }
//                if (isField) {
//
//                } else {
//
//                }
//            } else {
//                return super.eval(env, value);
//            }
//        }

        protected void updateCache(OptStoneObject target) {
            String member = name();
            classInfo = target.classInfo();
            // 这里是fieldIndex，而不是methodIndex，
            // 当然换成methodIndex也可以，只不过下面的逻辑需要对应修改
            Integer i = classInfo.fieldIndex(member);
            if (i != null) {
                isField = true;
                index = i;
                return;
            }
            i = classInfo.methodIndex(member);
            if (i != null) {
                isField = false;
                index = i;
                return;
            }
            throw new StoneException("bad member access: " + member, this);
        }

//        protected void updateCache(OptStoneObject target) {
//
//        }

    }
    @Reviser public static class AssignEx2 extends ObjOptimizer.AssignEx {
        protected OptClassInfo classInfo = null;
        protected int index;
        public AssignEx2(List<ASTree> c) { super(c); }

        //添加两个字段classInfo和index
//        protected OptClassInfo classInfo = null;
//        protected int index;

        @Override protected Object setField(OptStoneObject obj, Dot expr,
                                            Object rvalue)
        {
            //如果不一样
            if (obj.classInfo() != classInfo) {
                String member = expr.name();
                classInfo = obj.classInfo();
                Integer i = classInfo.fieldIndex(member);
                if (i == null)
                    throw new StoneException("bad member access: " + member,
                                             this);       
                index = i;
            }
            //如果classInfo的值跟以前一样
            obj.write(index, rvalue);
            return rvalue;
        }

        //设置字段的值
        //如果classInfo的值跟以前的一样，直接利用原来的classInfo的值，修改字段的值
//        @Override
//        protected Object setField(OptStoneObject obj, Dot expr, Object value) {
//            String member = expr.name();
//            classInfo = obj.classInfo();
//            Integer i = classInfo.fieldIndex(member);
//
//        }

    }
}
