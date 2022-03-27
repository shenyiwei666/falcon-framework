
# falcon-logging-spring-boot-starter

##我是什么
ELK已成为目前最流行的集中式日志解决方案，而在日志查询中traceId的重要性不言而喻。所以你猜的没错，我就是来集成ELK和管理traceId的。

##我做了什么
1. 把每次写入的日志都加上traceId，方便查询全链路日志
2. 把写入的日志发送到kafka，然后通过ELK处理查询日志
3. 对所有controller接口和dubbo接口都记录入参和出参日志，每个请求生成1个traceId
4. 服务间调用时传递traceId，目前支持的方式有RestTemplate、Feign、Dubbo
5. MQ生产者的traceId传递到消费者，目前支持的MQ有RabbitMQ

##怎么使用我
1. 在自己项目中添加falcon-logging-spring-boot-starter依赖
2. 在项目中添加配置
3. 运行项目产生日志后就可以到kibana里面查询日志了，查询前要先创建索引模式，关于kibana的使用方法百度下

######添加配置
	spring.application.name=falcon-logging-demo // 必选，应用名
	spring.profiles.active=local // 必选，环境
	
	logging.falcon.kafka.servers=localhost:9092 // 必选，kafka地址
	logging.falcon.kafka.topic=topic-logging // 必选，把日志发送到这个topic

	logging.falcon.console=false // 可选，是否需要在控制台输出日志，values[true, false]，默认false
    logging.falcon.debug=false // 可选，是否启用调试模式，开启后可通过http请求头控制是否打印日志，values[true, false]，默认false
	logging.falcon.searchIndex=falcon-all // 可选，es索引，如果为空则按spring.application.name创建索引
	
	logging.falcon.kafka.acks=0 // 可选，kafka的Producer参数
	logging.falcon.kafka.retries=1 // 可选，kafka的Producer参数
	logging.falcon.kafka.compressionType=gzip // 可选，kafka的Producer参数
	logging.falcon.kafka.bufferMemory=33554432 // 可选，kafka的Producer参数
	logging.falcon.kafka.lingerMs=0 // 可选，kafka的Producer参数
	logging.falcon.kafka.maxRequestSize=1048576 // 可选，kafka的Producer参数
	logging.falcon.kafka.requestTimeoutMs=30000 // 可选，kafka的Producer参数

	logging.falcon.mail.host=smtp.qq.com // 可选，邮件服务器地址
	logging.falcon.mail.port=25 // 可选，邮件服务器默认端口
	logging.falcon.mail.senderAccount=666666@qq.com // 可选，发件人邮箱账号
	logging.falcon.mail.senderPassword=123456 // 可选，发件人邮箱密码
	logging.falcon.mail.receiverAccounts=888888@qq.com,999999@qq.com // 可选，收件人邮箱账号，多个使用英文逗号分隔
	logging.falcon.mail.frequency=3600 // 可选，同一错误日志多久内不能重复发送邮件，单位秒，默认不限制，如果开启限制则必须配置RedisTemplate相关配置，保证RedisTemplate可用
	logging.falcon.mail.protocol=smtp // 可选，邮件发送协议，默认从host截取第一段
	logging.falcon.mail.auth=true // 可选，是否需要验证用户名密码，values[true, false]，默认true
	logging.falcon.mail.debug=false // 可选，是否启用调试模式，values[true, false]，默认true
	

## 常见问题
##### 主线程的traceId怎么传递到子线程？
    // 使用MdcRunnable将主线程中的MDC信息传递到子线程，这样子线程输入的日志就会带上主线程的traceId
    threadPool.execute(new MdcRunnable() {
        @Override
        public void call() {
            log.info("子线程第1次日志");
        }
    });

#####怎么设置某个接口不打印入参出参日志？
1、spring在controller的方法上增加@IgnoreLog注解。<br/>
2、dubbo在provider的方法上增加@IgnoreLog注解（要加到接口上，不能加到实现类上）。

####性能压测时会产生大量日志导致topic积压，影响其它业务查询日志，如何让压测的请求不打印日志？
1、服务添加配置logging.falcon.debug=true。<br/>
2、在压测的请求Header头加上X-Logging-Ignore=true。


<br/><br/><br/>
## ELK搭建
elk是指elasticsearch、logstash、kibana。应用程序将日志发送到kafka，然后logstash从kafka里面读取日志再写到elasticsearch，kibana再从elasticsearch里面搜索日志展示到界面。

###下载地址
	zookeeper：https://archive.apache.org/dist/zookeeper/
	kafka：https://kafka.apache.org/downloads
	elasticsearch：https://www.elastic.co/cn/downloads/past-releases/elasticsearch-7-10-0
	logstash：https://www.elastic.co/cn/downloads/past-releases/logstash-7-10-0
	kibana：https://www.elastic.co/cn/downloads/past-releases/kibana-7-10-0

###注意事项
1. 服务都安装到D:\install\elk目录下方便复制命令启动。
2. 按顺序启动以下服务，如果之前运行过这些中间件的不同版本，最好是删除下之前产生的数据文件（D:\tmp\*），不然可能会启动报错。
3. windows没有后台运行启动服务，所以使用start 打开一个新窗口启动，linux后台运行启动服务使用：nohup xxx.sh &
4. 加粗的命令表示第一次之后的运行只需要执行这些命令就可以了，加粗方便过滤不需要执行的命令。

###启动zookeeper
1. 下载解压zookeeper带-bin的包，并重命名到D:\install\elk\zookeeper-3.7.0
2. 将conf目录中的zoo_sample.cfg复制且重命名为zoo.cfg
3. cmd到D盘后进入安装目录：**cd install\elk\zookeeper-3.7.0**
4. 启动zookeeper：**start bin\zkServer.cmd**

###启动kafka
1. 下载解压kafka到D:\install\kafka_2.13-2.7.1
2. cmd到D盘后进入安装目录：**cd ..\kafka_2.13-2.7.1**
3. 启动kafka：**start bin\windows\kafka-server-start.bat config\server.properties**
4. 创建日志的topic：bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic-logging
5. *启动生产者（测试使用）：start bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic topic-logging*
6. *启动消费者（测试使用）：start bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic topic-logging --from-beginning*

###启动elasticsearch
1. 下载解压elasticsearch到D:\install\elk\elasticsearch-7.10.0
2. cmd到D盘后进入安装目录：**cd ..\elasticsearch-7.10.0**
3. 启动elasticsearch：**start bin\elasticsearch.bat**

###启动logstash
1. 下载解压zookeeper到D:\install\elk\logstash-7.10.0
2. 将conf目录中的logstash-sample.conf复制且重命名为logstash.conf
3. 修改logstash.conf文件 
4. cmd到D盘后进入安装目录：**cd ..\logstash-7.10.0**
5. 启动logstash：**start bin\logstash.bat -f config\logstash.conf**

######logstash.conf文件修改内容

    input {
		kafka {
	         bootstrap_servers => ["localhost:9092"] #kafka地址，可以是集群
	         client_id => "logging"   
	         auto_offset_reset => "latest"  #从最新的偏移量开始消费
	         topics => ["topic-logging"]  # 数组类型，可配置多个topic
	         decorate_events => true  #此属性会将当前topic、offset、group、partition等信息也带到message中
	         consumer_threads => 5
		}
	}

    filter {
    	json {
    		source => "message"
    	}
    	mutate {
    		remove_field => ["message"]
    	}
    }

    output {
    	elasticsearch {
    		hosts => ["http://localhost:9200"]
    		index => "log-%{app}-%{+yyyyMMdd}"
    		#user => "elastic"
    		#password => "changeme"
    	}
    	stdout{
    		codec => rubydebug #输出到屏幕上，本地调试才打开
    	}
    }


###启动kibana
1. 下载解压kibana并重命名到D:\install\elk\kibana-7.10.0
2. 修改conf目录中的kibana.yml
3. cmd到D盘后进入安装目录：**cd ..\kibana-7.10.0**
4. 启动kibana：**start bin\kibana.bat**

######kibana.yml文件修改内容
    server.port: 5601
	server.host: "localhost"
	elasticsearch.hosts: ["http://localhost:9200"]
	kibana.index: ".kibana"

