package com.example


import akka.actor.{Actor, ActorRef,ActorLogging,ActorSystem, Props,Address, Deploy}
import scala.collection.mutable.HashMap
import java.io._
import scala.io.Source
import scala.io.StdIn.readLine
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success
import scala.util.Failure

object Storage{
  case class StoreMessage(key: String, value: String)
  case class LookupMessage(key: String)
  case class DeleteMessage(key: String)
  case class ConsoleInputMessage(input: String)
}
class KeyValueStoreActorFile extends Actor with ActorLogging{
  import Storage._
    var file  = new File("Data.txt")
    override def receive: Receive = {
    case StoreMessage(key, value) =>
        val bw =  new BufferedWriter(new FileWriter(file, true))
        bw.write(s"$key,$value\n")
        bw.close()
        println(s"Stored key-value pair: ($key, $value)")
    case LookupMessage(key) =>
        var filename= "Data.txt"
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
    case DeleteMessage(key) =>
      
        val bw =  new BufferedWriter(new FileWriter(file, true))
        bw.write(s"$key,deleted\n")
        bw.close()
      println(s"Deleted key: $key")
    case _ => println("I did not understand")
  }
}

class ConsoleReaderActor2(KeyValueStoreActorFile: ActorRef) extends Actor with ActorLogging{
  import Storage._
  override def receive: Receive = {
    case ConsoleInputMessage(input) =>
      val command = input.split(" ")
      command.headOption match {
        case Some("store") =>
          val Array(_, key, value) = command
          KeyValueStoreActorFile ! StoreMessage(key, value)
        case Some("lookup") =>
          val Array(_, key) = command
          KeyValueStoreActorFile ! LookupMessage(key)
        case Some("delete") =>
          val Array(_, key) = command
          KeyValueStoreActorFile ! DeleteMessage(key)
        case _ =>
          println(s"Invalid command: $input")
      }
    case LookupMessage(value) => println(value)
    case _ => println("error")
  }
}

object Main2 extends App {
  import Storage._
  val system = ActorSystem("KeyValueStoreSystem")

  val keyValueStoreActorFile = system.actorOf(Props[KeyValueStoreActorFile], "keyValueStoreActorFile")
  val consoleReaderActor2 = system.actorOf(Props(new ConsoleReaderActor2(keyValueStoreActorFile)), "consoleReaderActor2")

  while (true) {
    val input = scala.io.StdIn.readLine("> ")
    consoleReaderActor2 ! ConsoleInputMessage(input)
  }
}