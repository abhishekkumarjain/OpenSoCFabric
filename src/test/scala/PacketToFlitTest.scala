package OpenSoC

import Chisel._
	
class PacketToFlitTest(c: PacketToFlit) extends Tester(c) {
	implicit def bool2BigInt(b:Boolean) : BigInt = if (b) 1 else 0

	reset(1)
	poke(c.io.packetValid, 0)
	poke(c.io.flitReady, 1)
	step(1) 	
	val totalLength = 10
	val payloadLength = 5

	//setup a packet

	poke(c.io.packet.sourceAddress, 0xDEAD)
	poke(c.io.packet.destAddress, 0xBEEF)
	poke(c.io.packet.length(0), totalLength)
	poke(c.io.packet.length(PacketFieldIndex.payloadLength), payloadLength)
	poke(c.io.packet.length(PacketFieldIndex.additionalFlags), 0)
	poke(c.io.packet.command(PacketFieldIndex.command), 0xA)
	poke(c.io.packet.command(PacketFieldIndex.commandOptions), 0x5)
	poke(c.io.packet.debug, 0)
	for(phase <- 0 until payloadLength){
		poke(c.io.packet.payload(phase), 10*phase)
	}
	
	step(1)
	expect(c.io.flitValid, 0)
	expect(c.io.packetReady, 1)
	poke(c.io.packetValid, 1)
	step(1)
	expect(c.io.packetReady, 0)
	expect(c.io.flitValid, 1)
	step(9)
	expect(c.io.packetReady, 0)
	expect(c.io.flitValid, 1)
	step(1)
	expect(c.io.flitValid, 1)
	expect(c.io.packetReady, 0)
	step(1)
	expect(c.io.flitValid, 0)
	expect(c.io.packetReady, 1)
	poke(c.io.packetValid, 0)
	step(5)
	
	
	
}
