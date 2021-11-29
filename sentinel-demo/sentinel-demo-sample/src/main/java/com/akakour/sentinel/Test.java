package com.akakour.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class Test {
    public static void main(String[] args) {
        // 创建本次链路调用的上下文，对应的是EntranceNode的概念
        ContextUtil.enter("entrances1", "Appa");
        Entry resource1 = null;
        Entry resource2 = null;
        try {
            // 创建资源操作对象 对应的是DefaulltNode对象
            resource1 = SphU.entry("resource1");
            // todo 能跑过entry，说明sentinel限流不满足，放开运行
        } catch (BlockException e) {
            e.printStackTrace();
            // todo 出现限流异常，说明已经被限流，需要走降级处理
        } finally {
            // 退出资源控制
            resource1.exit();
        }
        try {
            resource2 = SphU.entry("resource2");
        } catch (BlockException e) {
            e.printStackTrace();
        } finally {
            resource2.exit();
        }

        // 退出上下文
        ContextUtil.exit();

        ContextUtil.enter("entrances2", "Appa");
        Entry resource3 = null;
        Entry resource4 = null;

        try {
            // 创建资源操作对象 对应的是DefaulltNode对象
            resource3 = SphU.entry("resource3");
            // todo 能跑过entry，说明sentinel限流不满足，放开运行
        } catch (BlockException e) {
            e.printStackTrace();
            // todo 出现限流异常，说明已经被限流，需要走降级处理
        } finally {
            // 退出资源控制
            resource3.exit();
        }
        try {
            resource4 = SphU.entry("resource2"); // 一个线程多次相同资源操作
        } catch (BlockException e) {
            e.printStackTrace();
        } finally {
            resource4.exit();
        }
        ContextUtil.exit();

        // 上面两次对resource2 资源的操作，即两个DefaultNode组成ClusterNode
    }
}
