package OpenSoC

import Chisel._

class CMDORTester (c: CMeshDOR) extends Tester(c) {
	val flitWidth = c.io.inHeadFlit.getWidth
	val numVCs = c.io.inHeadFlit.numVCs
	val packetIDWidth = c.io.inHeadFlit.packetIDWidth
	val packetTypeWidth = c.io.inHeadFlit.packetTypeWidth
	val destCordWidth = c.io.inHeadFlit.destCordWidth
	val destCordDim = c.io.inHeadFlit.destCordDim

	val routerCoord = c.routingCoord
	
	scala.Predef.printf("numResources: %d\n", c.numResources)
	scala.Predef.printf("destCordWidth: %d\tdestCordDim: %d\n", destCordWidth, destCordDim)

	val numTestRuns = 10

	val randoms = Array.fill(numTestRuns) ( Array.fill(7) (BigInt(0)) )
	val results = Array.fill(numTestRuns) ( BigInt(0) )

	for (j <- 0 until numTestRuns) {
		for (i <- 0 until 7) {
			i match {
				case 6 =>
					randoms(j)(i) = BigInt(rnd.nextInt(Math.pow(2, packetIDWidth).toInt))
				case 5 =>
					randoms(j)(i) = BigInt(rnd.nextInt(Math.pow(2, 1).toInt))
				case 4 =>
					randoms(j)(i) = BigInt(rnd.nextInt(numVCs))
				case 3 =>
					randoms(j)(i) = BigInt(rnd.nextInt(Math.pow(2, packetTypeWidth).toInt))
				case 2 =>
					randoms(j)(i) = BigInt(rnd.nextInt(c.C).toInt)//0) // BigInt(rnd.nextInt(Math.pow(2, destCordWidth).toInt))
				case 1 =>
					randoms(j)(i) = BigInt(rnd.nextInt(c.K(1)).toInt)
				case 0 =>
					randoms(j)(i) = BigInt(rnd.nextInt(c.K(0)).toInt)
			}
		}
		scala.Predef.printf("randoms(0): %d\trandoms(1): %d\n", randoms(j)(0), randoms(j)(1))
		if (randoms(j)(0) > BigInt(routerCoord(0))) {
			results(j) = 0 * 2 + 0 + c.C
		} else if (randoms(j)(0) < BigInt(routerCoord(0))) {
			results(j) = 0 * 2 + 1 + c.C
		} else if (randoms(j)(1) > BigInt(routerCoord(1))) {
			results(j) = 1 * 2 + 0 + c.C
		} else if (randoms(j)(1) < BigInt(routerCoord(1))) {
			results(j) = 1 * 2 + 1 + c.C
		} else {
			results(j) = randoms(j)(2)
		}
	}

	scala.Predef.printf("---\n")
	for (i <- 0 until numTestRuns) {
		poke(c.io.inHeadFlit, randoms(i))
		step(1)
		expect(c.io.outHeadFlit, randoms(i))
		// peek(c.io.result)
		expect(c.io.result, results(i))
		scala.Predef.printf("---\n")
	}
}
