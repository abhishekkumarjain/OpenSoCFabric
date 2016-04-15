package OpenSoC

import Chisel._

class OpenSoC_CMesh_DecoupledWrapper_Tester(c: OpenSoC_CMesh_DecoupledWrapper, parms: Parameters) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	expect(c.io.inPorts(0).ready, 1)
}
