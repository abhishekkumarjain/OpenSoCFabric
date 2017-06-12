package OpenSoC

import Chisel._
import scala.collection.mutable.HashMap

class BusProbe(parms : Parameters) extends Module(parms) {

	val Dim = parms.get[Int]("TopologyDimension") // Dimension of topology
	val C = parms.get[Int]("Concentration") // Processors (endpoints) per router.
	val routerRadix = parms.get[Int]("routerRadix") //2 * Dim + C

	val counterMax = UInt(32768)

	val io = new Bundle {
		val inFlit 				= Vec(routerRadix, { new Flit(parms) }).asInput
		val inValid				= Vec(routerRadix, Bool()).asInput
		//val routerCord		= Vec(Dim, UInt(INPUT, width = log2Up(Dim)))
		val routerCord			= UInt(INPUT, width = log2Up(Dim))
		val startRecording		= Bool(INPUT)
		val cyclesChannelBusy	= Vec(routerRadix, UInt(width = counterMax.getWidth)).asOutput
		val cyclesRouterBusy	= UInt(OUTPUT,width = counterMax.getWidth) 
	}

	val freeRunningCounter = Reg(init = UInt(0, width = counterMax.getWidth))
	freeRunningCounter := Mux(freeRunningCounter === counterMax, UInt(0) , freeRunningCounter + UInt(1))

	val cyclesChannelBusy = Reg(init = Vec(Seq.fill(routerRadix)(UInt(0, width = counterMax.getWidth))))
	val cyclesRouterBusy  = Reg(init = UInt(0, counterMax.getWidth)) 
	var cyclesChannelBusyScoreboard = Reg(init = Vec(Seq.fill(routerRadix)(Bool(false))))

	
    assert((UInt(routerRadix) > UInt(1)), "BusProbe: RouterRadix must be > 1")	
	for(c <- 0 until routerRadix){
		val flit		 = io.inFlit(c)
		val flitValid	 = io.inValid(c)
		
		when(flitValid){
			cyclesChannelBusy(c) := cyclesChannelBusy(c) + UInt(1)
			cyclesChannelBusyScoreboard(c) := UInt(1)
		}.otherwise{
			cyclesChannelBusyScoreboard(c) := UInt(0)
		}
		io.cyclesChannelBusy(c) := cyclesChannelBusy(c)
	}
	
	when (cyclesChannelBusyScoreboard.asUInt.orR ){
		cyclesRouterBusy := cyclesRouterBusy + UInt(1)
	}
	io.cyclesRouterBusy := cyclesRouterBusy

/*	println("PROBE: Router " + io.routerCord + " stats: Total cycles: " + freeRunningCounter + " Total cycles busy: " + cyclesRouterBusy + " Channel cycles busy: ")
	for (i <- 0 until routerRadix) {
		scala.Predef.printf(" %s:%s", UInt(i), cyclesChannelBusy(i))
	}
	scala.Predef.printf("\n")*/

}
