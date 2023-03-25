package com.example
import scala.io.StdIn.readLine
object helloInteractive extends App{
    println("Please enter your name : ")
    val name = readLine()
    println("Hello,"+name+"!")
}