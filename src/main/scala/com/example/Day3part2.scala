package com.example

import scala.util.Random
import akka.actor.ActorRef
import com.example.{KeyValueStoreActorCache2}
class RandomClient(store: ActorRef) {
  import Storage3._

  // Populate the storage with values for keys from 1-500
  for (i <- 1 to 500) {
    KeyValueStoreActorCache2  ! Store(i.toString, s"value$i")
  }

  // Initialize the last key looked-up to a random value between 1-500
  var lastKey = Random.nextInt(500) + 1

  // Define the probability of picking the last key looked-up for a lookup operation
  val lookupLastKeyProbability = 0.5

  // Define the probability of picking a key from the first 1-500 range for a lookup operation
  val lookupFirstKeysProbability = 0.75

  // Define the total number of operations to perform
  val totalOperations = 10000

  // Loop through the total number of operations and make random requests to the storage service
  for (i <- 1 to totalOperations) {
    // Pick a random operation from store, delete, and lookup
    val operation = Random.nextInt(3) match {
      case 0 => Store
      case 1 => Delete
      case 2 => Lookup
    }

    // Pick a random key
    val key = if (operation == Lookup && Random.nextDouble() < lookupLastKeyProbability) {
      lastKey
    } else {
      if (Random.nextDouble() < lookupFirstKeysProbability) {
        Random.nextInt(500) + 1
      } else {
        Random.nextInt(500) + 501
      }
    }

    // If the operation is a store or delete, make sure that the key picked is greater than 500
    val realKey = operation match {
      case Store | Delete => if (key < 501) key + 500 else key
      case _ => key
    }

    // Perform the operation
    operation match {
      case Store => store ! Store(realKey.toString, s"value$realKey")
      case Delete => store ! Delete(realKey.toString)
      case Lookup =>
        store ! Lookup(realKey.toString)
        lastKey = realKey
    }
  }
}
// To run the system against the version without the cache and the version with the cache, we can modify the Part22 object as follows:


object Part22 extends App {
  val system = ActorSystem("KeyValueStoreSystem")
  val keyValueStoreActorFile3 = system.actorOf(Props[KeyValueStoreActorFile3], "keyValueStoreActorFile3")
  val keyValueStoreActorCache2 = system.actorOf(Props(new KeyValueStoreActorCache2(keyValueStoreActorFile3)), "keyValueStoreActorCache2")  
  val consoleReaderActorCache2 = system.actorOf(Props(new ConsoleReaderActorCache2(keyValueStoreActorCache2)), "consoleReaderActorCache2")
  // consoleReader ! Messages.Start(cashStore)

  // Run the system without the cache
  val t1 = System.currentTimeMillis
  println("Running the system without the cache")
  val randomClientWithoutCache = new RandomClient(keyValueStoreActorCache2)
  println(s"system without the cache took ${System.currentTimeMillis - t1} millis")
  Thread.sleep(10000)


  val t2 = System.currentTimeMillis
  // Run the system with the cache
  println("Running the system with the cache")
  val randomClientWithCache = new RandomClient(cashStore)
  println(s"system with the cache took ${System.currentTimeMillis - t2} millis")

  
}