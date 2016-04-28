package OpenSoC

import Chisel._
import scala.collection.mutable.LinkedHashMap
import Array._

class SimpleRouterTester (c: SimpleRouterTestWrapper) extends Tester(c) {
	val routerLatencyInClks = 3

	var headFlitMap = LinkedHashMap[String, BigInt]()
	var bodyFlitMap = LinkedHashMap[String, BigInt]()
	headFlitMap     = LinkedHashMap(
		("Dest_0" 		-> 0 ),
		("Dest_1" 		-> 0 ),
		("Dest_2"		-> 0 ),
		("packetType"	-> 0 ),
		("vcPort"		-> 0 ),
		("isTail"		-> 1 ),
		("packetID"		-> 0 )
	)

	bodyFlitMap 	= LinkedHashMap(
		("payload"		-> 0xDEAD ),
		("flitID"		-> 0xC ),
		("vcPort"		-> 0 ),
		("isTail"		-> 0 ),
		("packetID"		-> 0 )
	)
	
	poke(c.io.headFlitIn, headFlitMap.values.toArray)
	poke(c.io.bodyFlitIn, bodyFlitMap.values.toArray)
	step(1)
	var zeroFlit = peek(c.io.bodyFlitOut)

	for (i <- 0 until c.numInChannels) {
		poke(c.io.inChannels(i).flitValid,  0)
		poke(c.io.inChannels(i).credit.grant,  0)
		// poke(c.io.inChannels(i).credit.isTail, 0)
	}
	step(1)
	scala.Predef.printf("-------------------- Test 1 ----------------------\n")
	scala.Predef.printf("Drive Simple 2-flit packet from port 0 to port 1\n")
	//drive a flit on port 0
	headFlitMap("Dest_0") 	= 1
	headFlitMap("Dest_1") 	= 0
	headFlitMap("Dest_2") 	= 0
	headFlitMap("isTail") 	= 0
	headFlitMap("packetID") = 3
	bodyFlitMap("packetID") = 3
	bodyFlitMap("isTail") 	= 1
	poke(c.io.headFlitIn, headFlitMap.values.toArray)
	poke(c.io.bodyFlitIn, bodyFlitMap.values.toArray)
	step(1)
	var myHeadFlit = peek(c.io.headFlitOut)
	var myBodyFlit = peek(c.io.bodyFlitOut)
	
	step(1)
	for (i <- 0 until c.numInChannels) {
		poke(c.io.inChannels(i).flitValid, 0)
		poke(c.io.outChannels(i).credit.grant, 0)
	}
	poke(c.io.inChannels(0).flitValid, 1)
	poke(c.io.inChannels(0).flit, myHeadFlit)
	step(1)
	poke(c.io.inChannels(0).flitValid, 1)
	poke(c.io.inChannels(0).flit, myBodyFlit)
	step(1)
	poke(c.io.inChannels(0).flit, zeroFlit)
	poke(c.io.inChannels(0).flitValid, 0)
	step(routerLatencyInClks-2)
	expect(c.io.outChannels(1).flit, myHeadFlit)
	step(1)
	expect(c.io.outChannels(1).flit, myBodyFlit)
	scala.Predef.printf("------------------ END Test 1 ---------------------\n\n")
	
	step(1)
	scala.Predef.printf("-------------------- Test 1.5 ----------------------\n")
	scala.Predef.printf("Drive Simple 3-flit packet from Router (0,0) to Router (1,1) (port 0 to port 1)\n")
	//drive a flit on port 0
	headFlitMap("Dest_0") 	= 0
	headFlitMap("Dest_1") 	= 1
	headFlitMap("Dest_2") 	= 0
	headFlitMap("isTail") 	= 0
	headFlitMap("packetID") = 3
	bodyFlitMap("packetID") = 3
	bodyFlitMap("isTail") 	= 0
	poke(c.io.headFlitIn, headFlitMap.values.toArray)
	poke(c.io.bodyFlitIn, bodyFlitMap.values.toArray)
	step(1)
	myHeadFlit = peek(c.io.headFlitOut)
	myBodyFlit = peek(c.io.bodyFlitOut)
	step(1)
	bodyFlitMap("isTail") 	= 1
	poke(c.io.bodyFlitIn, bodyFlitMap.values.toArray)
	step(1)
	var my2ndBodyFlit = peek(c.io.bodyFlitOut)
	
	step(1)
	for (i <- 0 until c.numInChannels) {
		poke(c.io.inChannels(i).flitValid, 0)
		poke(c.io.outChannels(i).credit.grant, 0)
	}
	poke(c.io.inChannels(0).flitValid, 1)
	poke(c.io.inChannels(0).flit, myHeadFlit)
	step(1)
	poke(c.io.inChannels(0).flitValid, 1)
	poke(c.io.inChannels(0).flit, myBodyFlit)
	step(1)
	poke(c.io.inChannels(0).flitValid, 1)
	poke(c.io.inChannels(0).flit, my2ndBodyFlit)
	step(1)
	poke(c.io.inChannels(0).flit, zeroFlit)
	poke(c.io.inChannels(0).flitValid, 0)
	expect(c.io.outChannels(3).flit, myHeadFlit)
	step(1)
	expect(c.io.outChannels(3).flit, myBodyFlit)
	step(1)
	expect(c.io.outChannels(3).flit, my2ndBodyFlit)
	scala.Predef.printf("------------------ END Test 1.5 ---------------------\n\n")
	step(5)
	
	scala.Predef.printf("-------------------- Test 2 ----------------------\n")
	scala.Predef.printf("Drive 2-flit packets on each port, with destination\n")
	scala.Predef.printf("     of neighbor port\n")
	
	//Create an array of 2-flit packets:
	var packets = ofDim[Array[BigInt]](c.numInChannels,2)
	val dest    =  Array(3, 0, 1, 2, 3)
	for(i <- 0 until c.numInChannels){
		headFlitMap("Dest_0") 	= dest(i) & 1 
		headFlitMap("Dest_1") 	= (dest(i) & 2) >> 1 
		headFlitMap("Dest_2") 	= 0
		headFlitMap("isTail") 	= 0
		headFlitMap("packetID") = i 
		bodyFlitMap("packetID") = i
		bodyFlitMap("isTail") 	= 1
		poke(c.io.headFlitIn, headFlitMap.values.toArray)
		poke(c.io.bodyFlitIn, bodyFlitMap.values.toArray)
		scala.Predef.printf("Dest: %d, %d, %d\n", headFlitMap("Dest_0"), headFlitMap("Dest_1"), headFlitMap("Dest_2"))
		step(1)
		packets(i)(0) = peek(c.io.headFlitOut)
		packets(i)(1) = peek(c.io.bodyFlitOut)
	}
	
	//Drive all head flits	
	for(i <- 0 until c.numInChannels){
		poke(c.io.inChannels(i).flitValid, 1)
		poke(c.io.outChannels(i).credit.grant, 1)
		poke(c.io.inChannels(i).flit, packets(i)(0))
	}
	step (1)
	//Drive all body flits	
	for(i <- 0 until c.numInChannels){
		poke(c.io.inChannels(i).flitValid, 1)
		poke(c.io.outChannels(i).credit.grant, 1)
		poke(c.io.inChannels(i).flit, packets(i)(1))
	}
	step (1)
	for(i <- 0 until c.numInChannels){
		poke(c.io.inChannels(i).flitValid, 0)
		poke(c.io.outChannels(i).credit.grant, 0)
		poke(c.io.inChannels(i).flit, zeroFlit)
	}
	step(4)
	for (i <- 0 until c.numOutChannels){
		peek(c.io.outChannels(i).flit)	
	}
	step (1)
	for (i <- 0 until c.numOutChannels){
		peek(c.io.outChannels(i).flit)	
	}
	step (1)
	for (i <- 0 until c.numOutChannels){
		peek(c.io.outChannels(i).flit)	
	}
	step (1)
	for (i <- 0 until c.numOutChannels){
		peek(c.io.outChannels(i).flit)	
	}
	
	
	scala.Predef.printf("------------------ END Test 2 ---------------------\n\n")
	
}
