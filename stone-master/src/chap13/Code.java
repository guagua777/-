package chap13;

/**
 * 用于保存转换过程中必须的信息
 */
public class Code {
    //虚拟机的引用
    protected StoneVM svm;
    protected int codeSize;
    protected int numOfStrings;
    //当前正在使用的寄存器的数量
    protected int nextReg;
    //当前正在转换的栈帧的大小
    protected int frameSize;

    public Code(StoneVM stoneVm) {
        svm = stoneVm;
        codeSize = 0;
        numOfStrings = 0;
    }
    public int position() { return codeSize; }
    public void set(short value, int pos) {
        svm.code()[pos] = (byte)(value >>> 8);
        svm.code()[pos + 1] = (byte)value;
    }
    public void add(byte b) {
        //往二进制代码code中，添加新的代码
        svm.code()[codeSize++] = b;
    }
    public void add(short i) {
        add((byte)(i >>> 8));
        add((byte)i);
    }
    public void add(int i) {
        add((byte)(i >>> 24));
        add((byte)(i >>> 16));
        add((byte)(i >>> 8));
        add((byte)i);
    }
    public int record(String s) {
        svm.strings()[numOfStrings] = s;
        return numOfStrings++;
    }
}
