package OpenSoC

import Chisel._
	
class RingBufferTest(c: RingBuffer) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	val bufferWidth		= c.bufferWidth 
	val pointerCount	= c.pointerCount
	val totalBufferEntries	= c.totalBufferEntries 
	printf("Blah %d\n", pointerCount)

	for (i <- 0 until pointerCount) {
		poke(c.io.writeEnable(i), 0)
		poke(c.io.writePointerData(i), 0)
	}
	poke(c.io.pushData, 0)
	poke(c.io.push, 0)
	poke(c.io.pop, 0)
	expect(c.io.pushReady, 1)
	step(1)
	for (i <- 0 until (totalBufferEntries)) {
		poke(c.io.push, 1)
		poke(c.io.pushData, (i + 3))
		expect(c.io.pushReady,1)
		step(1)
	}
		poke(c.io.push, 1)
		poke(c.io.pushData, 0xBEEF)
		expect(c.io.pushReady,0)
	step(1)

	expect(c.io.pushReady,0)
	poke(c.io.pop, 1)
	poke(c.io.push, 0)
	step(1)
	expect(c.io.pushReady,1)
	
	for (i <- 0 until totalBufferEntries-1) {
		expect(c.io.readPointerData(0), (i+3))
		expect(c.io.readDataValid(0), 1)
		expect(c.io.readPointerData(1), (i+4))
		expect(c.io.readDataValid(1), 1)
		step(1)
	}
		expect(c.io.readPointerData(0), (totalBufferEntries+2))
		expect(c.io.readDataValid(0), 1)
		expect(c.io.readDataValid(1), 0)
		step(1)
		expect(c.io.readDataValid(0), 0)
		expect(c.io.readDataValid(1), 0)
		step(1)


		
	printf("---------------\n")

}
