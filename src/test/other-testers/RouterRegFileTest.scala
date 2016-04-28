package OpenSoC

import Chisel._
import scala.util.Random

class RouterRegFileTest(c: RouterRegFile) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0
	
	val regWidth : Int = c.regWidth
	val regDepth : Int = c.regDepth

	val nums = (1 to regDepth + 1).map(x => BigInt(Random.nextInt(Math.pow(2,c.regWidth).toInt)))

	for (i <- 0 until regDepth + 1) {
		poke(c.io.writeData, nums(i))
		poke(c.io.writeEnable, true)
		expect(c.io.full, i == regDepth)
		poke(c.io.readIncrement, false)
		expect(c.io.readData, if (i == 0) BigInt(0) else nums(0))
		poke(c.io.writePipelineReg(0), if (i == 0) BigInt(0) else nums(0))
		poke(c.io.wePipelineReg(0), true)
		expect(c.io.readPipelineReg(0), if (i < 2) BigInt(0) else nums(0))
		step(1)
	}
	step(1)
	for (i <- 0 until regDepth + 1) {
		poke(c.io.readIncrement, true)
		expect(c.io.readData, nums(i))
		poke(c.io.writeEnable, i < 2)
		expect(c.io.full, i < 1)
		poke(c.io.writePipelineReg(0), nums(i))
		poke(c.io.wePipelineReg(0), true)
		expect(c.io.readPipelineReg(0), if (i == 0) nums(0) else nums(i-1))
		step(1)
	}
}
