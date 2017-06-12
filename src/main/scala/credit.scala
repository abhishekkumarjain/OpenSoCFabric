package OpenSoC

import Chisel._

class Credit extends Bundle {
	val grant = Bool(OUTPUT)
	// val ready = Bool(INPUT)
	// val isHead = Bool(OUTPUT)
	// val isTail = Bool(OUTPUT)
	/*
	val CreditType = UInt(width = 2)	// Should be an Enum
	val VC = UInt(width = VCWidth)
	val Count = UInt(width = CountWidth)
	// Possible Extentions
	//	Error Detection/Correction
	*/
}

class CreditGen(parms: Parameters) extends Module(parms) {
	val io = new Bundle {
		val outCredit = new Credit()
		val inGrant = Bool(INPUT)
		// val outReady = Bool(OUTPUT)
		// val inIsTail = Bool(INPUT)
	}
	
	io.outCredit.grant := io.inGrant
	// io.outReady := io.outCredit.ready
	// io.outCredit.isTail := io.inIsTail
}

class CreditCon(parms: Parameters) extends Module(parms) {
	val numCreds = parms.get[Int]("numCreds")
	val threshold = parms.get[Int]("credThreshold")
	val io = new Bundle {
		val inCredit = new Credit().flip()
		val inConsume = Bool(INPUT)
		val outCredit = Bool(OUTPUT)
		val almostOut = Bool(OUTPUT)

		 //val credCount = UInt(width = log2Up(numCreds)+1).asOutput
	}
	val credCount = Reg(init=UInt(numCreds, log2Up(numCreds)+1))
	
	when (credCount === UInt(numCreds)) {
		credCount := credCount - io.inConsume.asUInt
	} .elsewhen ((credCount > UInt(threshold))) {// && (credCount < UInt(numCreds))) {
		credCount := credCount + io.inCredit.grant.asUInt - io.inConsume.asUInt
	} .otherwise {
		credCount := credCount + io.inCredit.grant.asUInt
	}

    assert(credCount <= UInt(numCreds), "CreditCon: Exceeded max credits")

	io.outCredit := (credCount > UInt(threshold))
	io.almostOut := (credCount === UInt(threshold+1))
	// io.inCredit.ready := io.inValid
}

class CreditTester(parms: Parameters) extends Module(parms) {
	val io = new Bundle {
		val inGrant = Bool(INPUT)
		// val outReady = Bool(OUTPUT)
		// val inIsTail = Bool(INPUT)

		val inConsume = Bool(INPUT)
		val outCredit = Bool(OUTPUT)
		// val outIsTail = Bool(OUTPUT)
	}
	val numCreds = parms.get[Int]("numCreds")
	
	val creditGen = Chisel.Module ( new CreditGen( parms.child("MyGen")) )
	val creditCon = Chisel.Module ( new CreditCon( parms.child("MyCon", Map(
		("numCreds"->Soft(numCreds))))) )

	creditCon.io.inCredit <> creditGen.io.outCredit
	
	creditGen.io.inGrant <> io.inGrant
	// creditGen.io.inIsTail <> io.inIsTail
	// io.outReady <> creditGen.io.outReady

	creditCon.io.inConsume <> io.inConsume
	io.outCredit <> creditCon.io.outCredit
	// io.outIsTail <> creditCon.io.outIsTail
}
