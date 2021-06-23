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



2、依赖 maven jar
<dependency>
	<groupId>com.weihui</groupId>
	<artifactId>dynamic-threadpool</artifactId>
	<version>20210601.2.SNAPSHOT</version>
</dependency>

3、引入spring xml配置文件
在spirng xml 中 import dynamic-threadpool.xml
<import resource="classpath*:META-INF/spring/dynamic-threadpool.xml"/>

4、通过 spring xml 注入 ExecutorService 动态线程池实现类 
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
  

二、线程池监控
支持ELK 和 prometheus 两种,对应ReportStrategy接口的两个实现类; 

 
 
