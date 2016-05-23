package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap

class SwitchTest(c: Switch[UInt]) extends MapTester(c, Array(c.io)) {
	defTests {
		var allGood = true
		val numInPorts : Int = c.numInPorts
		val numOutPorts : Int = c.numOutPorts
		val vars = new HashMap[Node, Node]()
		val ins = (1 to numInPorts).map(x => rnd.nextInt(Math.pow(2,c.gen.getWidth).toInt))
		
		for ( i <- 0 until numInPorts) {
			vars(c.io.inPorts(i)) = UInt(ins(i))
		}

		for ( i <- 0 until numOutPorts) {
			for (j <- 0 until numInPorts) {
				vars(c.io.sel(i)) = UInt(j)
				vars(c.io.outPorts(i)) = UInt(ins(j))
				allGood &= step(vars)
			}
		}
		for ( i <- 0 until numOutPorts) {
			for (j <- numInPorts-1 until -1 by -1) {
				vars(c.io.sel(i)) = UInt(j)
				vars(c.io.outPorts(i)) = UInt(ins(j))
				allGood &= step(vars)
			}
		}
		allGood
	}
}
