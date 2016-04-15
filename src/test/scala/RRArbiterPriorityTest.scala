package OpenSoC

import Chisel._

// //////////////////Tester for Arbiter with priority ////////////////////////



class RRArbiterPriorityTest(c: RRArbiterPriority) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	def noting(i: Int) : Int = if (i == 1) 0 else 1

	val numPorts : Int = c.numReqs
	
	val plevelArray = Array(
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0),
	        Array(0,0,0,0,0,0,0,0)
	        )	
	
/*	val plevelArray = Array(
	        Array(4,3,6,7,2,0,5,1),//3
	        Array(5,5,4,4,4,0,0,0),//0
	        Array(5,5,5,5,3,2,4,1),//1
	        Array(5,5,5,5,0,0,0,4),//2
	        Array(5,5,5,5,0,0,0,7),//7
	        Array(2,4,3,0,1,0,5,6),//7
	        Array(7,5,5,4,3,0,0,7),//0
	        Array(5,7,7,7,7,7,7,3),//1
	        Array(1,0,0,0,2,0,6,6),//6
	        Array(2,2,2,2,2,2,2,2),//7
	        Array(6,6,6,6,6,6,6,6),//0
	        Array(6,6,6,4,4,6,0,0),//1
	        Array(2,3,3,4,4,0,1,1),//3
	        Array(3,2,7,4,1,5,0,7),//7
	        Array(0,0,3,2,3,3,0,1),//2
	        Array(0,0,0,0,0,0,0,0),//3
	        Array(0,0,0,0,0,0,0,0),//4
	        Array(6,6,3,5,4,2,5,2)//0
	        )	
*/

	val inputArray = Array(
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2),
		Integer.parseInt("00000011", 2)
	)
	
/*	val inputArray = Array(
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2)
	)
*/
/*
	val lockArray = Array(
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2),
		Integer.parseInt("11111111", 2)
	)
*/
	val lockArray = Array(
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000000", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2)
	)

	val outputArray = Array(
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000010", 2)
		)
	
/*	val outputArray = Array(
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00010000", 2),
		Integer.parseInt("00100000", 2),
		Integer.parseInt("01000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00010000", 2),
		Integer.parseInt("00100000", 2),
		Integer.parseInt("01000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2)
		)
*/
/*	val outputArray = Array(
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("01000000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000001", 2),
		Integer.parseInt("00000010", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("10000000", 2),
		Integer.parseInt("00000100", 2),
		Integer.parseInt("00001000", 2),
		Integer.parseInt("00010000", 2),
		Integer.parseInt("00000001", 2)
		)
*/
//	val chosenPort = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
	val chosenPort = Array(0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1)
//	val chosenPort = Array(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1)
//	val chosenPort = Array(3, 0, 1, 2, 7, 7, 0, 1, 6, 7, 0, 1, 3, 7, 2, 3, 4, 0)
	val resourceReady = Array(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)
	val cyclesToRun = 18
	
	step(1)
	
	for (cycle <- 0 until cyclesToRun) {
		poke(c.io.resource.ready, resourceReady(cycle))
		println("inputArray(cycle): " + inputArray(cycle).toBinaryString)
		println("lockArray(cycle): " + lockArray(cycle).toBinaryString)
		for ( i <- 0 until numPorts) {
			poke(c.io.requests(i).request, (inputArray(cycle) & (1 << i)) >> i)
			poke(c.io.requests(i).releaseLock, noting((lockArray(cycle) & (1 << i)) >> i))
			poke(c.io.requests(i).priorityLevel, plevelArray(cycle)(i))
		}
		step(1)
	//	expect(
		expect(c.io.chosen, chosenPort(cycle))
		expect(c.io.resource.valid, resourceReady(cycle) && inputArray(cycle) != 0)
		for ( i <- 0 until numPorts) {
			expect(c.io.requests(i).grant, (outputArray(cycle) & (1 << i)) >> i)
		}
	}
	step(1)
}
