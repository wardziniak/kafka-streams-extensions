package com.wardziniak.kafka.streams.session

trait CloseSessionPredicate[K, V, VT] {
  def apply(key: K, value: V, agg: VT): Boolean
}
