package OpenSoC

import Chisel._

class WHCreditTest(c: CreditTester) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	val randomSeed = rnd
	peek(c.creditCon.credCount)
	poke(c.io.inConsume, 0)
	expect(c.io.outCredit, 1)
	step(1)
	scala.Predef.printf("---\n")

	for (i <- 0 until c.numCreds) {
		// scala.Predef.printf("->\t")
		peek(c.creditCon.credCount)
		var isTail = randomSeed.nextBoolean()
		poke(c.io.inGrant, 0)
		// poke(c.io.inIsTail, isTail)
		poke(c.io.inConsume, 1)
		step(1)
		// scala.Predef.printf("->\t")
		peek(c.creditCon.credCount)
		expect(c.io.outCredit, i != c.numCreds - 1)
		// expect(c.io.outIsTail, isTail)
		// peek(c.io.outReady)
		scala.Predef.printf("---\n")
	}
	step(1) // Work around for Chisel's clock_hi/clock_lo issue
	peek(c.creditCon.credCount)
	expect(c.io.outCredit, 0)
	scala.Predef.printf("---\n")
	step(1)
	for (i <- 0 until (c.numCreds + 2)) {
		poke(c.io.inConsume, (i == 0) || (i == 3))
		poke(c.io.inGrant, 1)
		peek(c.creditCon.credCount)
		step(1)
		peek(c.creditCon.credCount)
		expect(c.io.outCredit, (1))
		// peek(c.io.outReady)
		scala.Predef.printf("---\n")
	}
	step(1)
	expect(c.io.outCredit, 1)
}
