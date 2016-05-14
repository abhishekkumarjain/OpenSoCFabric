package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap

class RouterRegFile(parms: Parameters) extends Module(parms) {
	val regWidth = parms.get[Int]("widthRegFile")
	val regDepth = parms.get[Int]("depthRegFile")
	val pipelineDepth = parms.get[Int]("pipelineDepth")
	val io = new Bundle {
		val writeData = UInt(INPUT, width = regWidth)
		val writeEnable = Bool(INPUT)
		val full = Bool(OUTPUT)
		val readData = UInt(OUTPUT, width = regWidth)
		val readValid = Bool(OUTPUT)
		val readIncrement = Bool(INPUT)

		val writePipelineReg = Vec(pipelineDepth, UInt(INPUT, width = regWidth) )
		val wePipelineReg = Vec(pipelineDepth, Bool(INPUT) )
		val readPipelineReg = Vec(pipelineDepth, UInt(OUTPUT, width = regWidth) )
		val rvPipelineReg = Vec(pipelineDepth, Bool(OUTPUT) )
	}

	val regFile = Reg(init = Vec.fill(regDepth)(UInt(0, width = regWidth)))
	val regFileValid = Reg(init = Vec.fill(regDepth)(Bool(false)))
	
	val writePointer = Reg(init = UInt(0, width = log2Up(regDepth)) )
	val readPointer = Reg(init = UInt(0, width = log2Up(regDepth)) )

	val regPipelineRegs = (0 until pipelineDepth).map( a => Reg( init = UInt(0, width = regWidth) ) )
	val regRVPipelineRegs = (0 until pipelineDepth).map( a => Reg( Bool(false) ) )


	io.full := andR(regFileValid.toBits())
	io.readValid := (writePointer =/= readPointer) && orR(regFileValid.toBits().toUInt())

	when (io.writeEnable && !regFileValid(writePointer)) {
		regFile(writePointer) := io.writeData
		regFileValid(writePointer) := Bool(true)

		writePointer := writePointer + UInt(1)
		when (writePointer === UInt(regDepth)) {
			writePointer := UInt(0)
		}
	}

	io.readData := regFile(readPointer)
	when (io.readIncrement) {
		regFileValid(readPointer) := Bool(false)
		readPointer := readPointer + UInt(1)
		when (readPointer === UInt(regDepth)) {
			readPointer := UInt(0)
		}

		regPipelineRegs.map( _ := Bool(false) )
		// for (i <- 0 until pipelineDepth) {
		// 	regRVPipelineRegs(i) := Bool(false)
		// }
	}

	for ( i <- 0 until pipelineDepth ) {
		regRVPipelineRegs(i) := io.wePipelineReg(i)
		regPipelineRegs(i) := io.writePipelineReg(i)
	}

	regPipelineRegs.zipWithIndex.foreach{ case (a,i) =>
		io.readPipelineReg(i) := a
	}
	regRVPipelineRegs.zipWithIndex.foreach{ case (a,i) =>
		io.rvPipelineReg(i) := a
	}

	// for (i <- 0 until pipelineDepth) {
	// 	val regPipelineReg = Reg(init = UInt(0, width = regWidth))
	// 	when (io.wePipelineReg(i)) {
	// 		regPipelineReg := io.writePipelineReg(i)
	// 	}
	// 	io.readPipelineReg(i) := regPipelineReg
	// }
}
