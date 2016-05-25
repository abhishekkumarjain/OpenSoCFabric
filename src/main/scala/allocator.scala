package OpenSoC

import Chisel._

/*
abstract class AllocatorParams {
	val AllocatorType = UInt() // Enum
	val NumReqs = UInt()
	val NumGrants = UInt()
	val NumResources = UInt()
	val NumArbiters = UInt()
}
*/

abstract class Allocator(parms: Parameters) extends Module(parms) {
	val numReqs = parms.get[Int]("numReqs")
	val numRes = parms.get[Int]("numRes")
	val arbCtor = parms.get[Parameters=>Arbiter]("arbCtor")
	val io = new Bundle {
		val requests = Vec(numRes, Vec(numReqs, { new RequestIO(parms) })).flip
		val resources = Vec(numRes, new ResourceIO)
		val chosens = Vec(numRes, UInt(OUTPUT, Chisel.log2Up(numReqs)))
	}
}

class SwitchAllocator(parms: Parameters) extends Allocator(parms) {	
	for (i <- 0 until numRes) {
		val arb = Chisel.Module ( arbCtor(parms.child(("SWArb", i), Map(
			("numReqs"->Soft(numReqs))
		))) )
		arb.io.requests <> io.requests(i)
		io.resources(i) <> arb.io.resource
		io.chosens(i) <> arb.io.chosen
	}
}

class VCAllocator(parms: Parameters) extends Allocator(parms) {
	val numVCs = parms.get[Int]("numVCs")
	for (i <- 0 until numRes) {
		val arb = Chisel.Module ( arbCtor(parms.child(("SWArb", i), Map(
			("numReqs"->Soft(numReqs))
		))) )
		arb.io.requests <> io.requests(i)
		io.resources(i) <> arb.io.resource
		io.chosens(i) <> arb.io.chosen
	}

	// FARZAD: In my opinion, based on things discussed, we need two sets allocators here
	// One allocator will assign an input to an output. This output is actually a set of VCs.
	// This second allocator will select which VC the input will be assigned to. The best way I can think of at the moment is to have an inverted allocator, one where the requests come from VCs and the resourses are inputs.
}

