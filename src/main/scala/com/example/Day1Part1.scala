package com.example
import scala.collection.mutable 
import scala.io.StdIn.readLine
class Essai1 {
    object StoreSystem {   val store: mutable.HashMap[String, String] = mutable.HashMap()   
        def Store(key: String, value: String): Unit = {     store += (key -> value)   }   
        def Lookup(key: String): String = {    return  store.get(key).getOrElse(" ")}  
        def Delete(key: String): Unit = {     store -= key   } }
}
object exampleObject extends App{
    var store= new Essai1()
    var torun:Boolean=true 
    while (torun){
        println("What to do Store OR Lookup OR Delete :");
        val command:String = readLine()
        command match{
            case "Store"=> {
                println("Enter key")
                var key: String= readLine()
                println("Enter Value")
                var value: String = readLine()
                store.StoreSystem.Store(key= key, value=value)
            }
            case "Lookup"=>{
                println("Enter the key to lookup :")
                var key: String= readLine();
                var value : String = store.StoreSystem.Lookup(key= key)
                println(s"The value is $value")
            }
            case "Delete"=>{
                println("Enter the key to delete:")
                var key: String= readLine();
                var value : String = store.StoreSystem.Lookup(key= key)
                store.StoreSystem.Delete(key=key)
                println(s"The value $value of the $key is deleted as asked")
            }          
            case "Stop"=>{
                torun=false
            }
        }
    }

}