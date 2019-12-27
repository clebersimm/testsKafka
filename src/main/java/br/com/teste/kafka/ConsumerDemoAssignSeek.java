package br.com.teste.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoAssignSeek {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);

//		String groupId = "my-7java-app";
		String topic = "first_topic";

		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

		TopicPartition partitionToRead = new TopicPartition(topic, 0);
		consumer.assign(Arrays.asList(partitionToRead));
		long offsetToReadFrom = 15L;
		consumer.seek(partitionToRead, offsetToReadFrom);

		int numberMessageRead = 5;
		boolean keep = true;
		int numberOfMessageRead = 0;

		while (keep) {
			ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(2));
			for (ConsumerRecord<String, String> record : consumerRecords) {
				numberOfMessageRead += 1;
				logger.info("Key: " + record.key() + ", Value: " + record.value());
				logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
				if (numberOfMessageRead >= numberMessageRead) {
					keep = false;
					break;
				}
			}
		}
		logger.info("finish");
	}

}
