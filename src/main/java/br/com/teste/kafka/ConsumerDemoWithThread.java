package br.com.teste.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoWithThread {

	public static void main(String[] args) {
		new ConsumerDemoWithThread().run();
	}

	private ConsumerDemoWithThread() {

	}

	private void run() {
		Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThread.class);

		String groupId = "my-6java-app";
		String topic = "first_topic";
		String bootstrapServer = "127.0.0.1:9092";

		CountDownLatch latch = new CountDownLatch(1);

		Runnable con = new ConsumerRunnable(latch, topic, groupId, bootstrapServer);
		Thread t = new Thread(con);
		t.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("shutdown");
			((ConsumerRunnable) con).shutdown();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("exit");
		}));

		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("app error", e);
		} finally {
			logger.info("app closing");
		}
	}

	public class ConsumerRunnable implements Runnable {

		private CountDownLatch latch;
		private KafkaConsumer<String, String> consumer;
		private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

		public ConsumerRunnable(CountDownLatch latch, String topic, String groupId, String bootstrapServer) {
			this.latch = latch;
			Properties properties = new Properties();
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			consumer = new KafkaConsumer<>(properties);
			consumer.subscribe(Arrays.asList(topic));
		}

		@Override
		public void run() {
			try {
				while (true) {
					ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(2));
					consumerRecords.forEach(record -> {
						logger.info("Key: " + record.key() + ", Value: " + record.value());
						logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
					});
				}
			} catch (WakeupException e) {
				logger.info("shutdown signal");
			} finally {
				consumer.close();
				latch.countDown();
			}
		}

		public void shutdown() {
			consumer.wakeup();
		}

	}
}
