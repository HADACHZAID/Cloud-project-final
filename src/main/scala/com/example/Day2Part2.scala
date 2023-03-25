package com.example

//This file is about implementing key-value store in different actor.

import akka.actor.{Actor, ActorRef,ActorLogging,ActorSystem, Props,Address, Deploy}
import scala.collection.mutable.HashMap
import java.io._
import scala.io.Source
import scala.io.StdIn.readLine
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success
import scala.util.Failure

object Storage2{
  case class StoreMessageFile(key: String, value: String)
  case class StoreMessageCache(key: String, value: String)
  case class LookupMessageFile(key: String)
  case class LookupMessageCache(key: String)
  case class DeleteMessageFile(key: String)
  case class DeleteMessageCache(key: String)
  case class ConsoleInputMessage(input: String)
}

class KeyValueStoreActorCache (KeyValueStoreActorFile2: ActorRef) extends Actor with ActorLogging {
  import Storage2._
  val store = collection.mutable.HashMap[String, String]()
  override def receive: Receive = {
    case StoreMessageCache(key, value) =>
      store += (key -> value)
      log.info(s"Stored key-value pair: ($key, $value)")
      KeyValueStoreActorFile2 ! StoreMessageFile(key, value)
    case LookupMessageCache(key) =>
      val value = store.get(key)
      if (value.equals(None)){
        KeyValueStoreActorFile2 ! LookupMessageFile(key)
      }else{
        log.info(s"$value")
      }
    case DeleteMessageCache(key) =>
      Thread.sleep(100000)
      store -= key
      log.info(s"Deleted key: $key")
      KeyValueStoreActorFile2 ! DeleteMessageFile(key)
  }
}

class KeyValueStoreActorFile2 extends Actor with ActorLogging{
  import Storage2._
    var file  = new File("Data2.txt")
    override def receive: Receive = {
    case StoreMessageFile(key, value) =>
        val bw =  new BufferedWriter(new FileWriter(file, true))
        bw.write(s"$key,$value\n")
        bw.close()
    case LookupMessageFile(key) =>
        var filename= "Data2.txt"
        var value = " "
        for (line <- Source.fromFile(filename).getLines) {
            var key1 = line.split(",")(0)
            var key2 = line.split(",")(1)
            if(key1.equals(key) && !key2.equals("deleted") ){
                value = key2
            }
            else if(key1.equals(key) && key2.equals("deleted") ){
                value = "deleted"
            } 
          }
        if (value.equals("")){println("There is no such key")}
        else {println(value) }
    case DeleteMessageFile(key) =>
      
        val bw =  new BufferedWriter(new FileWriter(file, true))
        bw.write(s"$key,deleted\n")
        bw.close()
      println(s"Deleted key: $key")
    case _ => println("I did not understand")
  }
}
class ConsoleReaderActorCache(KeyValueStoreActorCache: ActorRef) extends Actor with ActorLogging{
  import Storage2._
  override def receive: Receive = {
    case ConsoleInputMessage(input) =>
      val command = input.split(" ")
      command.headOption match {
        case Some("store") =>
          val Array(_, key, value) = command
          KeyValueStoreActorCache! StoreMessageCache(key, value)
        case Some("lookup") =>
          val Array(_, key) = command
          KeyValueStoreActorCache ! LookupMessageCache(key)
        case Some("delete") =>
          val Array(_, key) = command
          KeyValueStoreActorCache ! DeleteMessageCache(key)
        case _ =>
          println(s"Invalid command: $input")
      }
    case _ => println("error")
  }
}

object Main3 extends App {
  import Storage2._
  val system = ActorSystem("KeyValueStoreSystem")
  val keyValueStoreActorFile2 = system.actorOf(Props[KeyValueStoreActorFile2], "keyValueStoreActorFile2")
  val keyValueStoreActorCache = system.actorOf(Props(new KeyValueStoreActorCache(keyValueStoreActorFile2)), "keyValueStoreActorCache")  
  val consoleReaderActorCache = system.actorOf(Props(new ConsoleReaderActorCache(keyValueStoreActorCache)), "consoleReaderActorCache")
  while (true) {
    val input = scala.io.StdIn.readLine("> ")
    consoleReaderActorCache ! ConsoleInputMessage(input)
  }
}