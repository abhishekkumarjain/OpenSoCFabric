package OpenSoC

import Chisel._

class BusProbeTest(c: BusProbe) extends Tester(c) {

	poke(c.io.inValid(0), 0)
	step(1)
}
