package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap

class RingBuffer(parms: Parameters) extends Module(parms) {

	val bufferWidth			= parms.get[Int]("widthRingBuffer")
	val pointerCount		= parms.get[Int]("pointerCount")
	val totalBufferEntries	= parms.get[Int]("totalRingBufferEntries")

	//Ring Buffer interface:
	//	- read and write enables are per pointer.  
	//	- when enable is asserted, it is assumed valid data exisits in the buffer
	//	- push and pushData are used to add data to the end of the ring
	//		if the buffer is empty, or at t=0, push will write to the entry pointed to
	//		by read / write pointer 0. 
	// 	- all enables (including push) are assumed to be single cycle pulses
	// 	- if you assert pop and an enable on the same cycle... WTF happens?
	val io = new Bundle {
			val writeEnable			= Vec(pointerCount, Bool( INPUT  ))
			val readDataValid		= Vec(pointerCount, Bool( OUTPUT ))
			val pop					= Bool( INPUT )	//Advances all read and write pointers
			val readPointerData		= Vec(pointerCount, UInt( OUTPUT, width = bufferWidth ))
			val writePointerData	= Vec(pointerCount, UInt( INPUT , width = bufferWidth ))
			val push				= Bool( INPUT )
			val pushData			= UInt( INPUT, width = bufferWidth)
			val pushReady			= Bool( OUTPUT )
	}
	
	val buffer 			= Reg(init = Vec(Seq.fill(totalBufferEntries)(UInt(0, width = bufferWidth))))
	val bufferValids	= Reg(init = Vec(Seq.fill(totalBufferEntries)(Bool(false))))
	val accessPointers	= Reg(init = Vec(Seq.fill(pointerCount)(UInt(0, width = log2Up(totalBufferEntries)))))
	val pushPointer		= Reg(init = UInt(0, width = log2Up(totalBufferEntries)) )	

	val sel  	 		= accessPointers(0) =/= UInt(0)
	val bufferEmpty 	= ~bufferValids.asUInt.orR
	val pushReady		= ~bufferValids.asUInt.andR

	io.pushReady 		:= pushReady
	

	when(io.push && pushReady){
		buffer(pushPointer) 		:= io.pushData
		bufferValids(pushPointer)	:= UInt(1)
		when(pushPointer === UInt(totalBufferEntries-1)){
			pushPointer 			:= UInt(0)
		}.otherwise{
			pushPointer	   			:= (pushPointer + UInt(1)) 
		}
	}

	for(ptr <- 1 until pointerCount){
		accessPointers(ptr) := accessPointers(UInt(ptr) - UInt(1)) + UInt(1)
	}
	
	
	for (ptr <- 0 until pointerCount) {
		val readPointerDataReg      = Reg(init = UInt(0, width = bufferWidth))
		val readDataValidReg   	    = Reg(init = UInt(0, width = 1))
		readPointerDataReg := buffer(accessPointers(ptr))
		readDataValidReg   := bufferValids(accessPointers(ptr))
		when(io.writeEnable(ptr)) {
			buffer(accessPointers(ptr)) 		:= io.writePointerData(ptr)
			bufferValids(accessPointers(ptr)) 	:= UInt(1)
		}
//		when(io.pop) {
//			accessPointers(ptr) := accessPointers(ptr) + UInt(1)
//		}
		io.readDataValid(ptr)   := readDataValidReg
		io.readPointerData(ptr) := readPointerDataReg 
	}

	when(io.pop){
		bufferValids(accessPointers(0)) 	:= UInt(0)	
		for(ptr <- 0 until pointerCount){
			accessPointers(ptr) := accessPointers(ptr) + UInt(1)
		}
	}


}
