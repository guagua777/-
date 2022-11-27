package chap13;
import static chap13.Opcode.*;
import static chap13.Opcode.decodeRegister;

import chap8.NativeFunction;
import stone.StoneException;
import stone.ast.ASTree;
import stone.ast.ASTList;
import java.util.ArrayList;

/**
 * 四个区域
 * 四个额外提供的寄存器
 *
 */
public class StoneVM {
    //机器语言代码
    protected byte[] code;
    protected Object[] stack;
    //字符串数组
    protected String[] strings;
    protected HeapMemory heap;

    public int pc, fp, sp, ret;
    //寄存器数组？？
    protected Object[] registers;
    //寄存器的数量
    public final static int NUM_OF_REG = 6;
    //值为8
    public final static int SAVE_AREA_SIZE = NUM_OF_REG + 2;

    public final static int TRUE = 1;
    public final static int FALSE = 0;

    public StoneVM(int codeSize, int stackSize, int stringsSize, HeapMemory hm) {
        code = new byte[codeSize];
        stack = new Object[stackSize];
        strings = new String[stringsSize];
        registers = new Object[NUM_OF_REG];
        heap = hm;
    }

//    public StoneVM(int codeSize, int stackSize, int stringsSize, HeapMemory hm) {
//
//    }

//    //字段和构造函数
//    //四个区域
//    //程序代码区
//    protected byte[] code;
//    //栈区
//    protected Object[] stack;
//    //文字常量区
//    protected String[] strings;
//    //堆区
//    protected HeapMemory heap;
//
//    //四个寄存器
//    public int pc;
//    public int fp;
//    public int sp;
//    public int ret;


    public Object getReg(int i) {
        return registers[i];
    }
    public void setReg(int i, Object value) {
        registers[i] = value;
    }

//    //寄存器的值也以Object来表示
//    public Object getReg(int i) {
//        return registers[i];
//    }
//    public void setReg(int i, Object value) {
//        registers[i] = value;
//    }

    public String[] strings() {
        return strings;
    }
    public byte[] code() {
        return code;
    }
    public Object[] stack() {
        return stack;
    }
    public HeapMemory heap() {
        return heap;
    }

    public void run(int entry) {
        pc = entry;
        fp = 0;
        sp = 0;
        ret = -1;
        while (pc >= 0)
            mainLoop();
    }

//    public void run(int entry) {
//        pc = entry;
//        fp = 0;
//        sp = 0;
//        ret = -1;
//        while (pc >= 0) {
//            mainLoop();
//        }
//    }


    protected void mainLoop() {
        switch (code[pc]) {
        case ICONST :
            registers[decodeRegister(code[pc + 5])] = readInt(code, pc + 1);
            pc += 6;
            break;

//        case ICONST:
//            //将整数值保存到reg中
//            //整数值来自哪里？来自机器语言代码中
//            //下一个下标处的值就是
//            int value = readInt(code, pc + 1);
//            //pc + 5代表 赋值语句等号左侧的变量
//            byte i = code[pc + 5];
//            //获取i对应的寄存器
//            int r = decodeRegister(i);
//            //将第r个寄存器的值设置为value
//            registers[r] = value;
//            //程序计数器向前移动
//            pc = pc + 6;
//            break;


        case BCONST :
            registers[decodeRegister(code[pc + 2])] = (int)code[pc + 1];
            pc += 3;
            break;


//        case BCONST:
//            //将整数值保存到寄存器
//            //跟ICONST一样
//            (int)code[pc + 1]


        case SCONST :
            registers[decodeRegister(code[pc + 3])]
                = strings[readShort(code, pc + 1)];
            pc += 4;
            break;

//        case SCONST:
//            //将字符串保存到寄存器
//            int s = readShort(code, pc + 1);
//            //获取对应的寄存器
//            int r = decodeRegister(code[pc + 3]);
//            //将字符串区的某个字符赋值给reg
//            registers[r] = strings[s];
//            //前移pc计数器
//            pc = pc + 4;
//            break;

        case MOVE :
            moveValue();
            break;

//        case MOVE:
//            moveValue();
//            break;


        case GMOVE :
            moveHeapValue();
            break;

//        case GMOVE:
//            moveHeapValue();
//            break;


        case IFZERO : {
            Object value = registers[decodeRegister(code[pc + 1])];
            if (value instanceof Integer && ((Integer)value).intValue() == 0)
                //跳转到int16分支
                pc += readShort(code, pc + 2);
            else
                pc += 4;
            break;
        }

//        case IFZERO: {
//
//        }


        case GOTO :
            //强制增加pc的值
            pc += readShort(code, pc + 1);
            break;

        case CALL :
            callFunction();
            break;

        case RETURN :
            //跳转到ret寄存器中的地址
            pc = ret;
            break;

        case SAVE :
            saveRegisters();
            break;


        case RESTORE :
            restoreRegisters();
            break;


        case NEG : {
            int reg = decodeRegister(code[pc + 1]);
            Object v = registers[reg];
            if (v instanceof Integer)
                registers[reg] = -((Integer)v).intValue();
            else
                throw new StoneException("bad operand value");
            pc += 2;
            break;
        }
        default :
            if (code[pc] > LESS)
                throw new StoneException("bad instruction");
            else
                computeNumber();
            break;
        }
    }


//    protected void mainLoop() {
//        //code里面存储的是指令
//        //code[pc]为第pc个下标的存储的指令
//        byte pcValue = code[pc];
//        switch (pcValue) {
//            case ICONST:
//                //保存32整数值到reg中
//                //为什么是pc + 5 而不是pc + 4？？
//                byte b = code[pc + 5];
//                registers[]
//
//        }
//    }




    //在栈与寄存器，或者是寄存器之间复制值
    //int8
    protected void moveValue() {
        byte src = code[pc + 1];
        byte dest = code[pc + 2];
        Object value;
        if (isRegister(src))
            value = registers[decodeRegister(src)];
        else
            value = stack[fp + decodeOffset(src)];
        if (isRegister(dest))
            registers[decodeRegister(dest)] = value;
        else
            stack[fp + decodeOffset(dest)] = value;
        pc += 3;
    }

    //包括取值和赋值
//    protected void moveValue() {
//        byte src = code[pc + 1];
//        byte dest = code[pc + 2];
//        Object value;
//        if (isRegister(src)) {
//            //从寄存器中取值
//            value = registers[decodeRegister(src)];
//        } else {
//            //从栈中取值
//            //fp为栈的起始位置，offset为偏移量
//            value = stack[fp + decodeOffset(src)];
//        }
//
//        if (isRegister(dest)) {
//            registers[decodeRegister(dest)] = value;
//        } else {
//            stack[fp + decodeOffset(dest)] = value;
//        }
//
//        pc = pc + 3;
//    }


    protected void moveHeapValue() {
        byte rand = code[pc + 1];
        // 从寄存器到堆
        if (isRegister(rand)) {
            int dest = readShort(code, pc + 2);
            heap.write(dest, registers[decodeRegister(rand)]);
        }
        //从堆到寄存器
        else {
            int src = readShort(code, pc + 1);
            registers[decodeRegister(code[pc + 3])] = heap.read(src);
        }
        pc += 4;
    }
    protected void callFunction() {
        //取出寄存器中的值
        Object value = registers[decodeRegister(code[pc + 1])];
        //参数个数
        int numOfArgs = code[pc + 2];
        //自定义函数
        if (value instanceof VmFunction
            && ((VmFunction)value).parameters().size() == numOfArgs) {
            //ret是什么？
            //保存call完成后的指令地址，用于call完成后进行返回，将指令地址保存到ret寄存器中
            ret = pc + 3;
            //跳到函数的入口
            pc = ((VmFunction)value).entry();
        }
        //native函数
        else if (value instanceof NativeFunction
                && ((NativeFunction)value).numOfParameters() == numOfArgs) {
            Object[] args = new Object[numOfArgs];
            for (int i = 0; i < numOfArgs; i++) {
                //从栈中取出各个参数的值
                args[i] = stack[sp + i];
            }
            //调用native方法，使用反射的方式调用
            stack[sp] = ((NativeFunction)value).invoke(args,
                                        new ASTList(new ArrayList<ASTree>()));

            //程序计数器迁移
            pc += 3;
        }
        else
            throw new StoneException("bad function call");
    }


//    protected void callFunction() {
//
//    }

    //更改fp和sp的值
    //将寄存器中的值转移到栈中
    protected void saveRegisters() {
        int size = decodeOffset(code[pc + 1]);
        int dest = size + sp;
        //将寄存器中值全部复制到栈中
        for (int i = 0; i < NUM_OF_REG; i++)
            stack[dest++] = registers[i];

        //保存上一个函数fp的值
        stack[dest++] = fp;
        fp = sp;
        sp += size + SAVE_AREA_SIZE;
        //为什么要加这一步
        //被调用函数的返回值，放到被调动函数的栈帧中
        stack[dest++] = ret;
        pc += 2;
    }

    //还原save保存的值
    protected void restoreRegisters() {
        int dest = decodeOffset(code[pc + 1]) + fp;
        //将栈中的数据还原到寄存器中
        for (int i = 0; i < NUM_OF_REG; i++)
            registers[i] = stack[dest++];
        sp = fp;
        fp = ((Integer)stack[dest++]).intValue();
        ret = ((Integer)stack[dest++]).intValue();
        pc += 2;
    }


    //对数字进行计算
    protected void computeNumber() {
        int left = decodeRegister(code[pc + 1]);
        int right = decodeRegister(code[pc + 2]);
        Object v1 = registers[left];
        Object v2 = registers[right];
        boolean areNumbers = v1 instanceof Integer && v2 instanceof Integer; 
        if (code[pc] == ADD && !areNumbers)
            registers[left] = String.valueOf(v1) + String.valueOf(v2);
        else if (code[pc] == EQUAL && !areNumbers) {
            if (v1 == null)
                registers[left] = v2 == null ? TRUE : FALSE;
            else
                registers[left] = v1.equals(v2) ? TRUE : FALSE;
        }
        else {
            if (!areNumbers)
                throw new StoneException("bad operand value"); 
            int i1 = ((Integer)v1).intValue();
            int i2 = ((Integer)v2).intValue();
            int i3;
            switch (code[pc]) {
            case ADD :
                i3 = i1 + i2;
                break;
            case SUB:
                i3 = i1 - i2;
                break;
            case MUL:
                i3 = i1 * i2;
                break;
            case DIV:
                i3 = i1 / i2;
                break;
            case REM:
                i3 = i1 % i2;
                break;
            case EQUAL:
                i3 = i1 == i2 ? TRUE : FALSE;
                break;
            case MORE:
                i3 = i1 > i2 ? TRUE : FALSE;
                break;
            case LESS:
                i3 = i1 < i2 ? TRUE : FALSE;
                break;
            default:
                throw new StoneException("never reach here");
            }
            //if分支中，将条件表达式的值，放入r0中，此时left的值为0
            registers[left] = i3;
        }
        pc += 3;
    }

    //获取连续数组中，从某个下标开始的32为的整数值
    public static int readInt(byte[] array, int index) {
        return (array[index] << 24)
                //oxff为十六进制全1
                | ((array[index + 1] & 0xff) << 16)
                | ((array[index + 2] & 0xff) << 8)
                | (array[index + 3] & 0xff);
    }

//    public static int readInt(byte[] array, int index) {
//
//    }

    //读取16个byte的字符
    public static int readShort(byte[] array, int index) {
        return (array[index] << 8) | (array[index + 1] & 0xff);
    }


}
