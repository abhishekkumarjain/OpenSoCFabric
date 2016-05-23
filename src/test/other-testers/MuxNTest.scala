package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap

class MuxNTest(c: MuxN[UInt]) extends MapTester(c, Array(c.io)) {
	defTests {
		var allGood = true
		val n : Int = c.n
		val vars = new HashMap[Node, Node]()
		val ins = (1 to n).map(x => rnd.nextInt(Math.pow(2,c.gen.getWidth).toInt))
		
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
