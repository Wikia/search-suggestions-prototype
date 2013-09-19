package com.wikia.search.demo.util

import akka.actor.{LoggingFSM, Actor, ActorRef}
import collection.immutable
import scala.concurrent.duration._

object AggregatorActor {
  trait State
  case object Waiting extends State
  case object Hoarding extends State

  case class Data( queue: immutable.List[Any] )
}

/**
 * Aggregates messages into fixed size chunks and sends chunks to consumer.
 * @param maxAggregateSize max size of batch of data sent to consumer
 * @param consumer consumer of output batches
 */
class AggregatorActor( val maxAggregateSize: Int, val consumer: ActorRef )
  extends Actor with LoggingFSM[AggregatorActor.State, AggregatorActor.Data] {
  import AggregatorActor._
  startWith(Waiting, Data(immutable.List()))

  when(Waiting) {
    case Event(message:Any, Data(List())) => maxAggregateSize match {
      case 1 => send( List(message) ); stay()
      case _ => goto(Hoarding) using Data( List(message) )
    }
  }

  when(Hoarding, stateTimeout = 1.second) {
    case Event(StateTimeout, Data(queue)) => send(queue); goto(Waiting) using Data(List())
    case Event(message, Data(queue)) => {
      if (queue.length >= maxAggregateSize - 1) {
        send( message::queue )
        goto(Waiting) using Data(List())
      }
      else stay using Data(message::queue)
    }
  }

  def send( queue:List[Any] ) {
    log.debug( "aggregate:" + queue.length )
    consumer ! queue
  }
}
