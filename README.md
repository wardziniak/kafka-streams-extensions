# kafka-streams-extensions

[![Build Status](https://travis-ci.com/wardziniak/kafka-streams-extensions.svg?branch=master)](https://travis-ci.com/wardziniak/kafka-streams-extensions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.wardziniak/streams-custom-session_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.wardziniak/streams-custom-session_2.12)


[maven-central]:       https://maven-badges.herokuapp.com/maven-central/com.wardziniak/streams-custom-session_2.12

Lightweight library, that adds to Kafka Streams DSL option to define Custom Session

Sample usage:

```

import com.wardziniak.kafka.streams.session.ExtendedKStream._

val output: KStream[String, String] = 
  builder
    .stream[String, String](InputTopic)
    .aggregate[String](() => "", (_, value, agg) => s"${agg}_$value", (_, _, agg) => agg.length > 10, Serdes.String, Serdes.String)
```
