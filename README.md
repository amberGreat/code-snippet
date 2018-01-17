# code-snippet


java/HiveLineageInfo.java => http://lxw1234.com/archives/2015/09/476.htm
SimpleLineageInfo.java => 也是一个LineageInfo的使用，跟上一个文件差不多。

java/TreeDemo.java => 使用JTree将ast的结果封装了一下，可以查看相应的树形结构。 来自：https://www.cnblogs.com/serendipity/articles/3738158.html


java/pom_with_comment.xml => 对maven的pom.xml的一些token进行注释说明






###### Usage of SimpleLineageInfo.java
#/bin/bash
1、
java -cp .:/home/hadoop/hive-test/lib/*:/home/hadoop/exec/lib/*  org.apache.hadoop.hive.ql.tools.LineageInfo "$1"


2、如上在classpath中加入各种依赖的jar包，然后咱们就开始执行之：
hadoop@hzbxs-bigdata16:~/hive-test$ ./test.sh "insert into tableA (id,name) select id,name from tableB"
InputTable=tableB
OutputTable=tableA

hadoop@hzbxs-bigdata16:~/hive-test$ ./test.sh "select * from tableA join (select * from tableB where id like '%helloworld%') temp on tableA.id = temp.id" 
InputTable=tableA
InputTable=tableB

3、但是发现一个问题，就是Create table as select 语句不能正常分析出血缘关系：

hadoop@hzbxs-bigdata16:~/hive-test$ ./test.sh "create table tableA as select * from tableB"
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
InputTable=tableB

