package com.wikia.search.demo.util

import akka.actor.{LoggingFSM, Actor, ActorRef}
import collection.immutable
import scala.concurrent.duration._

object AggregatorActor {
  trait State
  case object Waiting extends State
  case object Hoarding extends State

  case class Data[T]( queue: immutable.List[T] )
}

/**
 * Aggregates messages into fixed size chunks and sends chunks to consumer.
 * @param maxAggregateSize max size of batch of data sent to consumer
 * @param consumer consumer of output batches
 * @tparam T type of received messages
 */
class AggregatorActor[T]( val maxAggregateSize: Int, val consumer: ActorRef )
  extends Actor with LoggingFSM[AggregatorActor.State, AggregatorActor.Data[T]] {
  import AggregatorActor._
  startWith(Waiting, Data(immutable.List()))

  when(Waiting) {
    case Event(message:T, _) => goto(Hoarding) using Data( List(message) )
  }

  when(Hoarding, stateTimeout = 1.second) {
    case Event(StateTimeout, Data(queue)) => goto(Waiting) using Data(List())
    case Event(message:T, Data(queue)) => {
      if (queue.length == maxAggregateSize) goto(Waiting) using Data(List())
      else goto(Hoarding) using Data(message::queue)
    }
  }

  onTransition {
    case Hoarding -> Waiting =>
      stateData match {
        case Data(queue) => log.debug("Sending: " + queue.length); consumer ! queue
      }
  }
}
