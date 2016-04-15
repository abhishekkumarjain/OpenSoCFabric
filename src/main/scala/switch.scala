package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap
import scala.util.Random

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

class MuxNTest(c: MuxN[UInt]) extends MapTester(c, Array(c.io)) {
	defTests {
		var allGood = true
		val n : Int = c.n
		val vars = new HashMap[Node, Node]()
		val ins = (1 to n).map(x => Random.nextInt(Math.pow(2,c.gen.getWidth).toInt))
		
		for ( s <- 0 until n ) {
			for ( i <- 0 until n ) {
				vars(c.io.ins(i)) = UInt(ins(i))
			}
			vars(c.io.sel) = UInt(s)
			vars(c.io.out) = UInt(ins(s))
			allGood &= step(vars)
		}
		allGood
	}
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
