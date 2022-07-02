import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private final static String TOPIC_NAME = "test1";
    private final static String BOOTSTRAP_SERVERS = "public.itseasy.site:10006";

    public static void main(String[] args) {

        Properties configs = new Properties();
        configs.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS
        );
        configs.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName()
        );
        configs.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName()
        );
        configs.put(
                ProducerConfig.PARTITIONER_CLASS_CONFIG,
                CustomPartitioner.class
        );

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        int partitionNo = 0;
        String keyValue = "key1";
        String messageValue = "MessageWithoutKey";
        
        /**
         * 키없이 레코드 생성
         */
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);
        producer.send(record);

        /**
         * 키가 있는 레코드 생성
         */
        ProducerRecord<String, String> recordWithKey = new ProducerRecord<>(TOPIC_NAME, keyValue, "keyMessage");
        producer.send(recordWithKey);

        /**
         * 파티션 번화와 키가 있는 레코드 생성
         */
        ProducerRecord<String, String> recordWithKeyAndPartitionNo = new ProducerRecord<>(TOPIC_NAME, partitionNo, keyValue, "partitionMessage");
        producer.send(recordWithKeyAndPartitionNo);

        /**
         * 커스텀 파티셔너를 통해 레코드를 파티션 0번에 전송
         */
        ProducerRecord<String, String> recordWithCustomPartitioner = new ProducerRecord<>(TOPIC_NAME, "PartitionNo0", "partitionMessage");
        producer.send(recordWithCustomPartitioner);

        logger.info("{}", record);
        logger.info("{}", recordWithKey);
        logger.info("{}", recordWithKeyAndPartitionNo);
        logger.info("{}", recordWithCustomPartitioner);

        producer.flush();
        producer.close();
    }
}
