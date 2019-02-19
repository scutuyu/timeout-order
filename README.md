# timeout-order

订单超时，自动关闭的实现，采用循环队列，参考[这篇文章][link_ref_article]。

# 程序演示
下载代码后，直接运行`com.tuyu.TimeoutOrderApplication`类，默认监听8081端口
```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.3.RELEASE)

2019-02-19 16:48:25.012  INFO 12529 --- [           main] com.tuyu.TimeoutOrderApplication         : Starting TimeoutOrderApplication on scutuyu.local with PID 12529 (/Users/tuyu/Desktop/test/timeout-order/target/classes started by tuyu in /Users/tuyu/Desktop/test/timeout-order)
2019-02-19 16:48:25.015  INFO 12529 --- [           main] com.tuyu.TimeoutOrderApplication         : No active profile set, falling back to default profiles: default
2019-02-19 16:48:26.011  INFO 12529 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8081 (http)
2019-02-19 16:48:26.036  INFO 12529 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2019-02-19 16:48:26.036  INFO 12529 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.16]
2019-02-19 16:48:26.041  INFO 12529 --- [           main] o.a.catalina.core.AprLifecycleListener   : The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [/Users/tuyu/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.]
2019-02-19 16:48:26.123  INFO 12529 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-02-19 16:48:26.123  INFO 12529 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1064 ms
2019-02-19 16:48:26.173  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 0, next point: 1
2019-02-19 16:48:26.310  INFO 12529 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2019-02-19 16:48:26.386  INFO 12529 --- [           main] o.s.b.a.w.s.WelcomePageHandlerMapping    : Adding welcome page: class path resource [static/index.html]
2019-02-19 16:48:26.460  INFO 12529 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
2019-02-19 16:48:26.463  INFO 12529 --- [           main] com.tuyu.TimeoutOrderApplication         : Started TimeoutOrderApplication in 1.835 seconds (JVM running for 2.243)
2019-02-19 16:48:27.176  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 1, next point: 2
2019-02-19 16:48:28.175  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 2, next point: 3
2019-02-19 16:48:29.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 3, next point: 4
2019-02-19 16:48:30.176  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 4, next point: 5
2019-02-19 16:48:31.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 5, next point: 6
2019-02-19 16:48:32.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 6, next point: 7
2019-02-19 16:48:33.176  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 7, next point: 8
2019-02-19 16:48:34.178  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 8, next point: 9
2019-02-19 16:48:35.173  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 9, next point: 10
2019-02-19 16:48:36.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 10, next point: 11
2019-02-19 16:48:37.178  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 11, next point: 12
```

程序后每秒钟打印当前指针的索引

终端执行
```shell
curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' --header 'Accept: text/xml' -d 'name=香烟1&price=12220&delay=6' 'http://localhost:8081/order'
```
就可以得到如下的返回
```
订单提交成功
```

程序控制台输出如下：
```
2019-02-19 16:50:01.171  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 95, next point: 96
2019-02-19 16:50:02.172  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 96, next point: 97
2019-02-19 16:50:02.989  INFO 12529 --- [nio-8081-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2019-02-19 16:50:02.989  INFO 12529 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2019-02-19 16:50:02.994  INFO 12529 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 5 ms
2019-02-19 16:50:03.027  INFO 12529 --- [nio-8081-exec-1] com.tuyu.common.CircularQueue            : submit an order: df7a434b89824c6b84918c15f683daeb, timeout in 6 seconds
2019-02-19 16:50:03.028  INFO 12529 --- [nio-8081-exec-1] com.tuyu.common.CircularQueue            : idx: 103
2019-02-19 16:50:03.028  INFO 12529 --- [nio-8081-exec-1] com.tuyu.common.CircularQueue            : set: [CircularQueue.TimingTask(orderUuid=df7a434b89824c6b84918c15f683daeb, order=Order(orderUuid=df7a434b89824c6b84918c15f683daeb, name=香烟1, price=12220, orderTime=null, createTime=null, updateTime=null, timeout=false, payed=false, payTime=null), delay=6, cycleNum=0)]
2019-02-19 16:50:03.028  INFO 12529 --- [nio-8081-exec-1] com.tuyu.common.CircularQueue            : cachedMap: {df7a434b89824c6b84918c15f683daeb=103}
2019-02-19 16:50:03.171  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 97, next point: 98
2019-02-19 16:50:04.173  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 98, next point: 99
2019-02-19 16:50:05.173  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 99, next point: 100
2019-02-19 16:50:06.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 100, next point: 101
2019-02-19 16:50:07.172  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 101, next point: 102
2019-02-19 16:50:08.172  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 102, next point: 103
2019-02-19 16:50:09.174  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 103, next point: 104
2019-02-19 16:50:09.174  INFO 12529 --- [pool-2-thread-1] com.tuyu.common.CircularQueue            : timeout task uuid: df7a434b89824c6b84918c15f683daeb
2019-02-19 16:50:09.174  INFO 12529 --- [pool-2-thread-1] com.tuyu.common.CircularQueue            : order timeout: Order(orderUuid=df7a434b89824c6b84918c15f683daeb, name=香烟1, price=12220, orderTime=null, createTime=null, updateTime=Tue Feb 19 16:50:09 CST 2019, timeout=true, payed=false, payTime=null)
2019-02-19 16:50:09.174  INFO 12529 --- [pool-2-thread-1] com.tuyu.common.CircularQueue            : set: []
2019-02-19 16:50:09.175  INFO 12529 --- [pool-2-thread-1] com.tuyu.common.CircularQueue            : cachedMap: {}
2019-02-19 16:50:10.170  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 104, next point: 105
2019-02-19 16:50:11.173  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 105, next point: 106
2019-02-19 16:50:12.171  INFO 12529 --- [pool-1-thread-1] com.tuyu.common.CircularQueue            : cur point: 106, next point: 107
```

提交的订单，在6秒钟后自动被关闭了，并从循环队列中移除。

# Changelog
[learn more about the changelog][link_changelog]

[link_ref_article]: https://blog.csdn.net/verifocus/article/details/79135895
[link_changelog]: https://github.com/scutuyu/timeout-order/blob/master/CHANGELOG.md