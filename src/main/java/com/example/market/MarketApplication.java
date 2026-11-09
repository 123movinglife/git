package com.example.market;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//加入一个注解
@SpringBootApplication//这个本身是Spring Boot定义的一个类,它的地址对应引入的第三个包
@MapperScan("com.example.market.mapper")//扫描mapper包,对应引入的第一个包
@EnableScheduling//开启定时任务:没有这个注解,@Scheduled方法不会被执行
public class MarketApplication {
    public static void main(String[] args) {
        //创建Spring容器-->执行上面那些自动配置-->扫描并实例化你所有的组件-->启动内嵌的Tomcat开始监听端口(默认为8080)
        SpringApplication.run(MarketApplication.class, args);//第一个参数MarketApplication.class是告诉Spring"以这个类作为配置和扫描的起点";args是把命令行参数透传出去
    }

}
