# dynamic-threadpool
一、如何接入
1、在apollo配置中心新建nameSpace

这里区分两种场景：

① 应用有独立的appid ，比如 wh007_cmf 

     新建 private 类型 nameSpace，name固定为 ：PMD.threadPool

②应用没有独立的appid，比如 wh022_cmf-task

     新建 public 类型 nameSpace，name为以下格式 ：PMD.cmf-task.threadPool， 其中“wh022_cmf-task” 为Apollo app.properties 中的 app.id=wh022_cmf-task，在整个 apollo 中 public nameSpace 全局唯一

两个nameSpace同时存在，则优先取 private nameSpace。

nameSpace内容：
```Java
#threadpools 是固定前缀
#singleSendThreadExecutor 为应用线程池唯一标识（应用内唯一，类似spring中的bean id），需开发填写
#corePoolSize 是线程池参数，为固定可选值，选择范围（corePoolSize 、maximumPoolSize 、queueCapacity 、monitorRunningState 、monitorExecuteResult）
threadpools.singleSendThreadExecutor.corePoolSize = 30
threadpools.singleSendThreadExecutor.maximumPoolSize = 30
#queueType=ResizableLinkedBlockIngQueue 选填
threadpools.singleSendThreadExecutor.queueCapacity = 500
#监控线程池运行情况
threadpools.singleSendThreadExecutor.monitorRunningState = true
#监控线程池执行结果
threadpools.singleSendThreadExecutor.monitorExecuteResult = true
```


2、依赖 maven jar
```Java
<dependency>
	<groupId>com.weihui</groupId>
	<artifactId>dynamic-threadpool</artifactId>
	<version>20210601.2.SNAPSHOT</version>
</dependency>
```

3、引入spring xml配置文件
在spirng xml 中 import dynamic-threadpool.xml
```Java
<import resource="classpath*:META-INF/spring/dynamic-threadpool.xml"/>
```

4、通过 spring xml 注入 ExecutorService 动态线程池实现类 
```Java
<bean id="testThreadPoolExecutor" class="weihui.bcss.support.dtp.core.factory.DynamicThreadPoolFactoryBean">
   <property name="threadPoolConfig">
       <bean class="weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig" >
           <property name="corePoolSize" value="10"/>
           <property name="maximumPoolSize" value="20"/>
           <property name="queueCapacity" value="100"/>
           <property name="unit" value="SECONDS"/>
           <property name="keepAliveTime" value="120"/>
           <property name="rejectedExecutionHandler">
               <bean class="java.util.concurrent.ThreadPoolExecutor$DiscardPolicy"/>
           </property>
           <property name="threadFactory">
               <bean class="threadpool.TestThreadFactory" >
                   <constructor-arg value="myTest"></constructor-arg>
               </bean>
           </property>
       </bean>
   </property>
</bean>
```

二、线程池监控
支持ELK 和 prometheus 两种,对应ReportStrategy接口的两个实现类; 

1.以log方式输出监控指标示例:
```Java
2021-06-23 10:52:07.209 [INFO] [] [] [] w.b.s.d.c.m.r.s.LogReportStrategy - {monitor.type=TPRS,thread.pool.name=singleSendThreadExecutor} thread.pool.dynamic.activeCount=0 thread.pool.dynamic.completedTaskCount=8 thread.pool.dynamic.largestCount=8 thread.pool.dynamic.queue.capacity=500 thread.pool.dynamic.rejeuctCount=0 thread.pool.dynamic.taskCount=8 thread.pool.dynamic.threadCount=8 thread.pool.dynamic.waitTaskCount=0 thread.pool.static.coreSize=15 thread.pool.static.maxSize=25 
2021-06-23 10:52:07.209 [INFO] [] [] [] w.b.s.d.c.m.r.s.LogReportStrategy - {monitor.type=TPTS,thread.pool.name=singleSendThreadExecutor,thread.task.type=defaultTask} transaction.elapsed.avg=0 transaction.elapsed.max=0 transaction.elapsed.min=0 transaction.failure.count=0 transaction.success.count=0 
```

1.以prometheus方式输出监控指标示例:
TODO
