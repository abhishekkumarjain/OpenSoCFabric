package OpenSoC

import Chisel._

class VCIQTest(c: InjectionChannelQ) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	val queueDepth : Int = c.queueDepth
	val flitWidth : Int = c.flitWidth
	
	val randoms = (0 to 4*queueDepth).map(x => rnd.nextInt(Math.pow(2, flitWidth).toInt))
	
	// scala.Predef.printf("Flit Width: %d\n", flitWidth)

	// scala.Predef.printf("destCordWidth: %d\t destCordDim: %d\n", c.destCordWidth, c.destCordDim)
	// scala.Predef.printf("Destination(1) Width: %d\t getWidth: %d\n", c.destination(1).width, c.destination(1).getWidth)
	// scala.Predef.printf("Destination Width: %d\t getWidth: %d\n", c.destination.toBits.width, c.destination.toBits.getWidth)

	for (i <- 0 to 4*queueDepth) scala.Predef.printf("%d:\t0x%X (%d)\n", i, randoms(i), randoms(i))
	scala.Predef.printf("---\n")
	scala.Predef.printf("queueDepth: %d\n", queueDepth)
	scala.Predef.printf("flitWidth: %d\n", flitWidth)

	// peek(c.creditCon.credCount)
	poke(c.io.in.flitValid, 0)
	expect(c.io.in.credit.grant, 1)
	// poke(c.io.out.credit.grant, 0)
	// expect(c.io.out.flitValid, 0)
	step(1)
	scala.Predef.printf("---\n")
	
	scala.Predef.printf("--- First Round: Filling the FIFO ---\n")
	for (i <- 0 to 2*queueDepth) {
		scala.Predef.printf("Value of i: %d\n", i)
		// scala.Predef.printf("Randoms x%h\n", randoms(1))
		// peek(c.creditCon.credCount)
		poke(c.io.in.flit, Array(BigInt(randoms(i))))
		poke(c.io.in.flitValid, 1)
		// poke(c.io.out.credit.grant, 0)
		step(1)
		// peek(c.creditCon.credCount)
		expect(c.io.in.credit.grant, (i < 2*queueDepth-1))
		// expect(c.io.out.flitValid, 1)
		if (i > queueDepth)	expect(c.io.out.flit, Array(BigInt(randoms(queueDepth))))
		else 				expect(c.io.out.flit, Array(BigInt(randoms(i))))

		peek(c.io.in.flit) //c.iQueue.queue.io.enq.bits)

		scala.Predef.printf("---\n")

	}
	poke(c.io.in.flitValid, 0)
	step(10)
}
