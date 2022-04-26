package com.hys.mylogrecord.parse.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * 匹配左右大括号索引位置的堆栈实现
 * 相比于{@link java.util.Stack}，{@link BracketsIndexStack}中的方法不会用synchronized关键字修饰，避免没必要的加锁
 *
 * @author Robert Hou
 * @since 2022年04月23日 18:55
 **/
@Slf4j
public class BracketsIndexStack {

    private static final List<Integer> BRACKETS_LIST = new LinkedList<>();

    /**
     * 在堆栈顶部插入一个新元素
     */
    public void push(int bracket) {
        BRACKETS_LIST.add(bracket);
    }

    /**
     * 删除堆栈顶部元素并返回
     */
    public Integer pop() {
        if (!isEmpty()) {
            return BRACKETS_LIST.remove(BRACKETS_LIST.size() - 1);
        } else {
            log.error("堆栈元素为空，删除失败");
            return null;
        }
    }

    /**
     * 判断堆栈是否为空
     */
    public boolean isEmpty() {
        return BRACKETS_LIST.isEmpty();
    }
}
