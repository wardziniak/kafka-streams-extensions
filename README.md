# kafka-streams-extensions

Lightweight library, that adds to Kafka Streams DSL option to define Custom Session

Sample usage:

```

import com.wardziniak.kafka.streams.session.ExtendedKStream._

val output: KStream[String, String] = 
  builder
    .stream[String, String](InputTopic)
    .aggregate[String](() => "", (_, value, agg) => s"${agg}_$value", (_, _, agg) => agg.length > 10, Serdes.String, Serdes.String)
```
