package chap13;
import chap11.ResizableArrayEnv;

//代表堆区
//堆区中存放全局变量
public class StoneVMEnv extends ResizableArrayEnv implements HeapMemory {
    protected StoneVM svm;
    protected Code code;
    public StoneVMEnv(int codeSize, int stackSize, int stringsSize) {
        //this代码堆区
        svm = new StoneVM(codeSize, stackSize, stringsSize, this);
        code = new Code(svm);
    }

//    //字段和构造函数
//    protected StoneVM svm;
//    protected Code code;


    public StoneVM stoneVM() {
        return svm;
    }
    public Code code() {
        return code;
    }
    public Object read(int index) {
        return values[index];
    }
    public void write(int index, Object v) {
        values[index] = v;
    }

//    public Object read(int index) {
//        return values[index];
//    }
//    public void write(int index, Object v) {
//        values[index] = v;
//    }
}
