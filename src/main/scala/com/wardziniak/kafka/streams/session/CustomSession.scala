package com.wardziniak.kafka.streams.session

trait CustomSession[K, V, VT] {

  def isClosed(key: K, value: V, agg: VT): Boolean
}
