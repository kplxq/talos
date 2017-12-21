# talos-sample

## 准备事项

1、完成Talos的部署，见[部署文档]()
2、talos-sample下载到本地。
3、在工程路径下执行

```
mvn clean install
mvn jetty:run
```

### AOP服务

访问http://localhost:9908/talos-sample/aop， 页面提示`aop finish`

查看[talos-dashboard](http://talos-dashboard:8080/talos-dashboard/search/page)，输入url作为查询条件

![aop1](https://kplxq.github.io/img/talos/aop1.png)
![aop2](https://kplxq.github.io/img/talos/aop2.png)
![aop3](https://kplxq.github.io/img/talos/aop3.png)

在真实使用过程中，使用Talos.collect(key,value)进行手动打点的，可以作为搜索条件在搜索页进行搜索，如：

访问http://localhost:9908/talos-sample/with/param?userName=luohui&&password=pwd

可以根据输入的参数luohui来进行搜索

![withparam1](https://kplxq.github.io/img/talos/withparam1.png)
![withparam2](https://kplxq.github.io/img/talos/withparam2.png)



### http服务

访问http://localhost:9908/talos-sample/http,页面提示`http service finish with your param:http-client-param`

查看[talos-dashboard](http://talos-dashboard:8080/talos-dashboard/search/page)，输入url作为查询条件

![http1](https://kplxq.github.io/img/talos/http1.png)
![http2](https://kplxq.github.io/img/talos/http2.png)

### 异常信息展示

访问http://localhost:9908/talos-sample/exception,页面提示`exception finish`

![ex1](https://kplxq.github.io/img/talos/ex1.png)
![ex2](https://kplxq.github.io/img/talos/ex2.png)
![ex3](https://kplxq.github.io/img/talos/ex3.png)

### 多线程

访问http://localhost:9908/talos-sample/thread,页面提示`thread finish`

![thread1](https://kplxq.github.io/img/talos/thread1.png)
![thread2](https://kplxq.github.io/img/talos/thread2.png)

### 耗时信息

访问http://localhost:9908/talos-sample/sleep1s

![sleep1s](https://kplxq.github.io/img/talos/sleep1s.png)