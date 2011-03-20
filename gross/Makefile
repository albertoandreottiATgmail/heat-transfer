WC_CLASSES = heat_transfer
HADOOP_HOME = /home/beto/hadoop-0.20.2
HADOOP_VERSION = 0.20.2

HeatTransfer: KeyArrayValue HeatTransfer.java MatrixData
	mkdir -p ${WC_CLASSES}
	rm -r -f ${WC_CLASSES}/HeatTransfer*
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d ${WC_CLASSES} HeatTransfer.java
	jar -cvf heattransfer.jar -C ${WC_CLASSES}/ .

OutputInspector: FloatArrayWritable OutputInspector.java
	rm -r -f ${WC_CLASSES}/OutputInspector*
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d ${WC_CLASSES} OutputInspector.java
	jar -cvf outputinspector.jar -C ${WC_CLASSES}/ .


InspectOutput:OutputInspector
        #copy from HDFS to local dir(don't do that with big files!!)
	rm -f part-r-0000*
	hadoop fs -copyToLocal /output/part-r-00000 .
	hadoop jar outputinspector.jar org.apache.hadoop.examples.OutputInspector part-r-00000

RunMe: SequenceFileWriteDemo HeatTransfer 
	hadoop jar SequenceFileWriteDemo.jar org.apache.hadoop.examples.SequenceFileWriteDemo 
	-hadoop fs -rmr /mySeq
	hadoop fs -copyFromLocal mySeq /
	hadoop jar heattransfer.jar org.apache.hadoop.examples.HeatTransfer /mySeq /output
	#hadoop fs -ls /output/*

RunCycle:
	hadoop dfsadmin -safemode leave
	-hadoop fs -rmr /home/beto/output	
	hadoop jar heattransfer.jar org.apache.hadoop.examples.HeatTransfer /home/beto/mySeq /home/beto/output

clean:
	-hadoop fs -rmr /home/beto/output
	-hadoop fs -rmr /home/beto/mySeq

SequenceFileWriteDemo: KeyArrayValue MatrixData clean SequenceFileWriteDemo.java
	hadoop dfsadmin -safemode leave
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d ${WC_CLASSES} SequenceFileWriteDemo.java
	jar -cvf SequenceFileWriteDemo.jar -C ${WC_CLASSES}/ .
	hadoop jar SequenceFileWriteDemo.jar org.apache.hadoop.examples.SequenceFileWriteDemo 

FloatArrayWritable: FloatArrayWritable.java
	mkdir -p ${WC_CLASSES}	
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:. -d 	${WC_CLASSES} FloatArrayWritable.java


KeyArrayValue: FloatArrayWritable KeyArrayValue.java
	mkdir -p ${WC_CLASSES}
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d  ${WC_CLASSES} KeyArrayValue.java

GraphingData:FloatArrayWritable GraphingData.java
	rm -r -f ${WC_CLASSES}/OutputInspector*
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d ${WC_CLASSES} GraphingData.java
	jar -cvf graphingdata.jar -C ${WC_CLASSES}/ .
	hadoop jar graphingdata.jar org.apache.hadoop.examples.GraphingData         

MatrixData: MatrixData.java
	mkdir -p ${WC_CLASSES}
	javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar:${HADOOP_HOME}/lib/commons-cli-1.2.jar:${WC_CLASSES} -d  ${WC_CLASSES} MatrixData.java

