#记录一下遇到的问题(MarkDown现学现卖)：

* 1.jpa中get与find有什么区别:
>>>findOne返回的是一个实体对象，而getOne返回的是一个对象的指针或引用，在使用时需要谨慎小心，防止报错
* 2.jpa如何组建SQL语句：
>>>[看这里](https://www.sojson.com/blog/295.html)
* 3.Controller返回一个类的时候不能自动转化成json，报错Failed to write HTTP message: org.springframework.http.converter.HttpMessageNotWritableException: No converter found for return value of type: class com.xing.imheres.entity.LoginResult:
>>>不要忘记返回类的get，set方法，有博客说还要有json相关的依赖，但是貌似这个版本的Springboot内置了json转化，我的pom包内没有json相关依赖