package OpenSoC

import Chisel._

// //////////////////Tester for Arbiter without priority ////////////////////////

class RRArbiterTest(c: RRArbiter) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	def noting(i: Int) : Int = if (i == 1) 0 else 1

	val numPorts : Int = c.numReqs

	val inputArray = Array(
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00100001", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00010000", 2),
		Integer.parseInt("00100000", 2),
		Integer.parseInt("01000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000000", 2)
		)
	val lockArray = Array(
		Integer.parseInt("00000000", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111110", 2),
		Integer.parseInt("11011111", 2),
		Integer.parseInt("11111011", 2),
		Integer.parseInt("01111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111110", 2),
		Integer.parseInt("11111101", 2),
		Integer.parseInt("11111011", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11110111", 2),
		Integer.parseInt("11101111", 2),
		Integer.parseInt("11011111", 2),
		Integer.parseInt("10111111", 2),
		Integer.parseInt("01111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2)
	)
	val outputArray = Array(
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00100000", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00010000", 2),
		Integer.parseInt("00100000", 2),
		Integer.parseInt("01000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000000", 2)
		)
	val chosenPort = Array(0, 0, 0, 0, 5, 2, 7, 0, 0, 1, 2, 3, 3, 4, 5, 6, 7, 0)
	val resourceReady = Array(false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false)
	val cyclesToRun = 18
	
	step(1)
	
	for (cycle <- 0 until cyclesToRun) {
		poke(c.io.resource.ready, resourceReady(cycle))
		println("inputArray(cycle): " + inputArray(cycle).toBinaryString)
		for ( i <- 0 until numPorts) {
			poke(c.io.requests(i).request, (inputArray(cycle) & (1 << i)) >> i)
			poke(c.io.requests(i).releaseLock, noting((lockArray(cycle) & (1 << i)) >> i))
		}
		step(1)
		expect(c.io.chosen, chosenPort(cycle))
		expect(c.io.resource.valid, resourceReady(cycle) && inputArray(cycle) != 0)
		for ( i <- 0 until numPorts) {
			expect(c.io.requests(i).grant, (outputArray(cycle) & (1 << i)) >> i)
		}
	}
	step(1)
}
