package br.com.teste.kafka;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoKeys {

	public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);

		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		for (int i = 0; i < 10; i++) {
			String key = "id_" + String.valueOf(i);
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", key,
					UUID.randomUUID().toString());
			producer.send(record, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if (exception == null) {
						logger.info("received new meta: \n" + "Topic: " + metadata.topic() + "\n" + "Partition: "
								+ metadata.partition() + "\nOffset: " + metadata.offset() + "\nTimeStamp: "
								+ metadata.timestamp());
					} else {
						logger.error("error", exception);
					}
				}
			});
			producer.flush();
		}

		// flush e close
		producer.close();
	}

}