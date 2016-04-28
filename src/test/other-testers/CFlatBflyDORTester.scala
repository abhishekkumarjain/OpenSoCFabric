package OpenSoC

import Chisel._
import scala.util.Random

class CFlatBflyDORTester (c: CFlatBflyDOR) extends Tester(c) {
	val flitWidth = c.io.inHeadFlit.getWidth
	val numVCs = c.io.inHeadFlit.numVCs
	val packetIDWidth = c.io.inHeadFlit.packetIDWidth
	val packetTypeWidth = c.io.inHeadFlit.packetTypeWidth
	val destCordWidth = c.io.inHeadFlit.destCordWidth
	val destCordDim = c.io.inHeadFlit.destCordDim

	val routerCoord = c.routingCoord
	
	scala.Predef.printf("numResources: %d\n", c.numResources)
	scala.Predef.printf("destCordWidth: %d\tdestCordDim: %d\n", destCordWidth, destCordDim)
	scala.Predef.printf("routerCoord: (%d, %d)\n", routerCoord(0), routerCoord(1))
	scala.Predef.printf("K: (%d, %d)\n", c.K(0), c.K(1))
	val numTestRuns = 100

	val randoms = Array.fill(numTestRuns) ( Array.fill(7) (BigInt(0)) )
	val results = Array.fill(numTestRuns) ( BigInt(0) )

	for (j <- 0 until numTestRuns) {
		for (i <- 0 until 7) {
			i match {
				case 6 =>
					randoms(j)(i) = BigInt(Random.nextInt(Math.pow(2, packetIDWidth).toInt))
				case 5 =>
					randoms(j)(i) = BigInt(Random.nextInt(Math.pow(2, 1).toInt))
				case 4 =>
					randoms(j)(i) = BigInt(Random.nextInt(numVCs))
				case 3 =>
					randoms(j)(i) = BigInt(Random.nextInt(Math.pow(2, packetTypeWidth).toInt))
				case 2 =>
					randoms(j)(i) = BigInt(Random.nextInt(c.C).toInt)//0) // BigInt(Random.nextInt(Math.pow(2, destCordWidth).toInt))
				case 1 =>
					randoms(j)(i) = BigInt(Random.nextInt(c.K(1)).toInt)
				case 0 =>
					randoms(j)(i) = BigInt(Random.nextInt(c.K(0)).toInt)
			}
		}
		scala.Predef.printf("%d: randoms(0): %d\trandoms(1): %d\n", j, randoms(j)(0), randoms(j)(1))
		if (randoms(j)(0) > BigInt(routerCoord(0))) {
			results(j) = randoms(j)(0) - routerCoord(0) + (c.C - 1)
		} else if (randoms(j)(0) < BigInt(routerCoord(0))) {
			results(j) = (c.K(0) - 1) - randoms(j)(0) + (c.C - 1)
		} else if (randoms(j)(1) > BigInt(routerCoord(1))) {
			results(j) = randoms(j)(1) - routerCoord(1) + (c.C - 1) + c.K.slice(0,1).sum - 1
		} else if (randoms(j)(1) < BigInt(routerCoord(1))) {
			results(j) = (c.K(1) - 1) - randoms(j)(1) + (c.C - 1) + c.K.slice(0,1).sum - 1
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
