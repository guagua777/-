package chap12;

public class OptStoneObject {
    public static class AccessException extends Exception {}
    //从该字段中获取位置信息
    protected OptClassInfo classInfo;
    protected Object[] fields;
    public OptStoneObject(OptClassInfo ci, int size) {
        classInfo = ci;
        fields = new Object[size];
    }

//    //记录对象对应的class的信息
//    protected OptClassInfo classInfo;
//    //这个字段是干什么的？
//    //记录字段的值？？
//    protected Object[] fields;

    public OptClassInfo classInfo() { return classInfo; }
    //两组read和write方法
    //this对象使用的是哪一个？
    //根据名称获取下标，然后根据下标获取值
    public Object read(String name) throws AccessException {
        Integer i = classInfo.fieldIndex(name);
        if (i != null)
            return fields[i];
        else {
            i = classInfo.methodIndex(name);
            if (i != null)
                return method(i);
        }
        throw new AccessException();
    }

//    public Object read(String name) throws AccessException {
//        Integer i = classInfo.fieldIndex(name);
//        if (i != null) {
//
//        } else {
//            classInfo.methodIndex(name);
//        }
//    }

    public void write(String name, Object value) throws AccessException {
        Integer i = classInfo.fieldIndex(name);
        if (i == null)
            throw new AccessException();
        else
            fields[i] = value;
    }

//    public void write(String name, Object value) throws AccessException {
//        classInfo.fieldIndex(name);
//    }


    public Object read(int index) {
        return fields[index];
    }
    public void write(int index, Object value) {
        fields[index] = value;
    }
    public Object method(int index) {
        return classInfo.method(this, index);
    }

//    public Object method(int index) {
//        return classInfo.method(this, index);
//    }


}
