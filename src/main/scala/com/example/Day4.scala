package com.example
import com.example._



object Configs {
  private val root = ConfigFactory.load()
  val app1 = root.getConfig("App1")
  val app2 = root.getConfig("App2")
  val app3 = root.getConfig("App3")
  val app4 = root.getConfig("App4")
}

object Main5 extends App {
  import Configs._
  val syst1 = ActorSystem("App1", app1)
  val keyValueStoreActorFile3 = syst1.actorOf(Props[KeyValueStoreActorFile3], "keyValueStoreActorFile3")
 
  val syst2 = ActorSystem("App2", app2)
  val keyValueStoreActorCache2 = syst2.actorOf(Props(new KeyValueStoreActorCache2(keyValueStoreActorFile3)), "keyValueStoreActorCache2")  
 
  val syst3 = ActorSystem("App3", app3)
  al consoleReaderActorCache2 = syst3.actorOf(Props(new ConsoleReaderActorCache2(keyValueStoreActorCache2)), "consoleReaderActorCache2")
 
  val syst4 = ActorSystem("App4", app4)
  val client = syst4.actorOf(Props(new ClientActor(server, cache)), "client")
}
