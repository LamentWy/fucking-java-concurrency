package com.lament.z.solution.HashMapHangDemo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  我对并发环境下 HashMap 的各种问题的处理办法通常就是： <br/>
 *  <h1>不要在并发环境用 HashMap ！</h1>
 *  简单说下死循环的问题，以前 HashMap 的实现就是经典的数组+链表的实现，并发环境下多个线程同时 PUT 就有概率出现环形链表，当 get 进入环形链表之后，死循环就发生了。 <br/>
 *
 *  最偷懒的解决办法就是利用 Collections.synchronizedMap(Map map) 方法，把 HashMap 转换成同步模式。它的原理是给 Map 的所有方法加了 synchronized，如果竞争多的话效率挺差的。
 *
 *  并发就用 ConcurrentHashMap 就好了，代码略。
 */
public class HashMapHangSulotion {
    final Map<Integer, Object> holder = new HashMap<>();

    final Map<Integer, Object> synchronizedHolder = Collections.synchronizedMap(holder);

}
