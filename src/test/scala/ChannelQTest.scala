package OpenSoC

import Chisel._

class ChannelQTest(c: GenericChannelQ) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	val queueDepth : Int = c.queueDepth
	val flitWidth : Int = c.flitWidth
	
	val randoms = (0 to 4*queueDepth).map(x => rnd.nextInt(Math.pow(2, flitWidth).toInt))
	
	// printf("Flit Width: %d\n", flitWidth)

	// printf("destCordWidth: %d\t destCordDim: %d\n", c.destCordWidth, c.destCordDim)
	// printf("Destination(1) Width: %d\t getWidth: %d\n", c.destination(1).width, c.destination(1).getWidth)
	// printf("Destination Width: %d\t getWidth: %d\n", c.destination.toBits.width, c.destination.toBits.getWidth)

	for (i <- 0 to 4*queueDepth) printf("%d:\t0x%X (%d)\n", i, randoms(i), randoms(i))
	printf("---\n")
	printf("queueDepth: %d\n", queueDepth)
	printf("flitWidth: %d\n", flitWidth)

	// peek(c.creditCon.credCount)
	poke(c.io.in.flitValid, 0)
	expect(c.io.in.credit.grant, 1)
	poke(c.io.out.credit.grant, 0)
	expect(c.io.out.flitValid, 0)
	step(1)
	printf("---\n")
	
	printf("--- First Round: Filling the FIFO ---\n")
	for (i <- 0 to 2*queueDepth) {
		printf("Value of i: %d\n", i)
		// printf("Randoms x%h\n", randoms(1))
		// peek(c.creditCon.credCount)
		poke(c.io.in.flit, Array(BigInt(randoms(i))))
		poke(c.io.in.flitValid, 1)
		poke(c.io.out.credit.grant, 0)
		step(1)
		// peek(c.creditCon.credCount)
		expect(c.io.in.credit.grant, (i < 2*queueDepth-1))
		expect(c.io.out.flitValid, 1)
		if (i > queueDepth)	expect(c.io.out.flit, Array(BigInt(randoms(queueDepth))))
		else 				expect(c.io.out.flit, Array(BigInt(randoms(i))))
		printf("---\n")

	}

	printf("---\n")

	printf("--- Second Round: Emptying the FIFO ---\n")
	for (i <- 2*queueDepth to (4*queueDepth)) {
		// peek(c.creditCon.credCount)
		poke(c.io.in.flitValid, (i == 2*queueDepth+1))
		poke(c.io.in.flit, Array(BigInt(randoms(2*queueDepth+1))))
		poke(c.io.out.credit.grant, 1)
		step(1)
		printf("Value of i: %d\n", i)
		expect(c.io.in.credit.grant, (i != 2*queueDepth))
		expect(c.io.out.flitValid, (i < 3*queueDepth))
		if (i < 3*queueDepth) 		expect(c.io.out.flit, Array(BigInt(randoms(i - queueDepth))))
		else 						expect(c.io.out.flit, Array(BigInt(randoms(queueDepth))))
		printf("---\n")
	}
	step(1)
	expect(c.io.out.flitValid, 0)
	// printf("Size of Output Flit: %d\n",c.io.out.flit.width)
	// printf("Size of Input Flit: %d\n",c.io.in.flit.width)
}
