package com.example

import akka.actor.{Actor, ActorLogging, ActorSystem, ActorRef, Props}
import scala.collection.mutable 
import scala.io.StdIn.readLine

// Message types used by the actors
case class StoreMessage(key: String, value: String)
case class LookupMessage(key: String)
case class DeleteMessage(key: String)
case class ConsoleInputMessage(input: String)

class KeyValueStoreActor extends Actor with ActorLogging {
  val store = collection.mutable.HashMap[String, String]()

  override def receive: Receive = {
    case StoreMessage(key, value) =>
      store += (key -> value)
      log.info(s"Stored key-value pair: ($key, $value)")
    case LookupMessage(key) =>
      val value = store.get(key)
      log.info(s"$value")
    case DeleteMessage(key) =>
      store -= key
      log.info(s"Deleted key: $key")
  }
}

class ConsoleReaderActor(keyValueStoreActor: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case ConsoleInputMessage(input) =>
      val command = input.split(" ")
      command.headOption match {
        case Some("store") =>
          val Array(_, key, value) = command
          keyValueStoreActor ! StoreMessage(key, value)
        case Some("lookup") =>
          val Array(_, key) = command
          keyValueStoreActor ! LookupMessage(key)
        case Some("delete") =>
          val Array(_, key) = command
          keyValueStoreActor ! DeleteMessage(key)
        case _ =>
          log.warning(s"Invalid command: $input")
      }
  }
}

object Main extends App {
  val system = ActorSystem("KeyValueStoreSystem")

  val keyValueStoreActor = system.actorOf(Props[KeyValueStoreActor], "keyValueStoreActor")
  val consoleReaderActor = system.actorOf(Props(new ConsoleReaderActor(keyValueStoreActor)), "consoleReaderActor")

  while (true) {
    val input = scala.io.StdIn.readLine("> ")
    consoleReaderActor ! ConsoleInputMessage(input)
  }
}

