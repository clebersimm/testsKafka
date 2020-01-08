package br.com.teste.kafka.stream;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

public class StreamsFilter {

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "demo-streams");
		properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
		properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

		StreamsBuilder streamsBuilder = new StreamsBuilder();
		KStream<String, String> inputTopic = streamsBuilder.stream("first_topic");
		KStream<String, String> filteredStream = inputTopic.filter((k, v) -> v.contains("test"));

		filteredStream.to("second_topic");

		KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);

		kafkaStreams.start();

	}

}
