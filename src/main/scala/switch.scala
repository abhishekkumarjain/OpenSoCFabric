package OpenSoC

import Chisel._

/*
abstract class SwitchParams {
	val NumInPorts = UInt()
	val NumOutPorts = UInt()
	val FlitWidth = UInt()
	val Implementation = UInt() // Should be Enum
}
*/

class Switch[T <: Data](val gen: T, parms: Parameters) extends Module(parms) {
	val numInPorts 	= parms.get[Int]("numInPorts")
	val numOutPorts	= parms.get[Int]("numOutPorts")
	val io = new Bundle {
		val inPorts = Vec.fill(numInPorts) {gen.cloneType.asInput}
		val outPorts = Vec.fill(numOutPorts) {gen.cloneType.asOutput}
		val sel = Vec.fill(numOutPorts) {UInt(width = log2Up(numInPorts))}.asInput
	}
	for( i <- 0 until numOutPorts) {
		var m = Chisel.Module (
			new MuxN[T](gen,
				parms.child(("SwitchMux", i), Map(("n"->Soft(numInPorts))))
			)
		)
		m.io.ins <> io.inPorts
		m.io.sel := io.sel(i)
		io.outPorts(i) := m.io.out
	}
}

class MuxN[T <: Data](val gen: T, parms: Parameters) extends Module(parms) {
	val n = parms.get[Int]("n")
	val io = new Bundle {
		val ins = Vec.fill(n) {gen.cloneType.asInput}
		val sel = UInt(INPUT, log2Up(n))
		val out = gen.cloneType.asOutput
	}

	io.out := io.ins(io.sel)
}

/*class DeMux[T <: Data](parms: Parameters) extends OpenSoC.Module {
	super(parms)
	val n = parms.get[Int]("n")
	val io = new Bundle {
		val input = new T().asInput
		val sel = UInt(INPUT, log2Up(n))
		val outputs = Vec.fill(n) {new T().asOutput}
	}

	io.outputs[io.sel] := io.input
}*/
