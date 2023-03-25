package com.example
import com.example.Day3part1



object Configs {
  private val root = ConfigFactory.load()
  val app1 = root.getConfig("App1")
  val app2 = root.getConfig("App2")
  val app3 = root.getConfig("App3")
  val app4 = root.getConfig("App4")
}

object Main extends App {
  import Configs._
  val syst1 = ActorSystem("App1", app1)
  val keyValueStore = syst1.actorOf(Props[KeyValueStore], "keyValueStore")
 
  val syst2 = ActorSystem("App2", app2)
  val cache = syst2.actorOf(Props(new Cache(keyValueStore)), "cache")
 
  val syst3 = ActorSystem("App3", app3)
  val server = syst3.actorOf(Props(new Server(cache)), "server")
 
  val syst4 = ActorSystem("App4", app4)
  val client = syst4.actorOf(Props(new ClientActor(server, cache)), "client")
}
