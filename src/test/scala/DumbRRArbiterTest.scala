package OpenSoC

import Chisel._

class DumbRRArbiterTest(c: RRArbiter) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	val numPorts : Int = c.numReqs

	val cyclesToRun = 8

	step(1)

	for (cycle <- 0 until cyclesToRun) {
		poke(c.io.resource.ready, 1)
		for ( i <- 0 until numPorts) {
			poke(c.io.requests(i).request, 1)
			poke(c.io.requests(i).releaseLock, 1)
		}
		step(1)
		expect(c.io.chosen, if (cycle % 8 > 5) (cycle - 6) else (cycle + 2) )
		expect(c.io.resource.valid, 1)
		for ( i <- 0 until numPorts) {
			expect(c.io.requests(i).grant, (i == (cycle + 2)) || (i == (cycle - 6)))
		}
	}
	step(5)
}
