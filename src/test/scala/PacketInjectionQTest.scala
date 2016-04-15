package OpenSoC

import Chisel._

class PacketInjectionQTest(c: PacketInjectionQ[Packet]) extends Tester(c) {

	reset(1)

	poke(c.io.in.packetValid, 0)
	step(1)
	
	val totalLength = 10
	val payloadLength = 5

	//setup a packet

	poke(c.io.in.packet.sourceAddress, 0xDEAD)
	poke(c.io.in.packet.destAddress, 0xBEEF)
	poke(c.io.in.packet.length(0), totalLength)
	poke(c.io.in.packet.length(PacketFieldIndex.payloadLength), payloadLength)
	poke(c.io.in.packet.length(PacketFieldIndex.additionalFlags), 0)
	poke(c.io.in.packet.command(PacketFieldIndex.command), 0xA)
	poke(c.io.in.packet.command(PacketFieldIndex.commandOptions), 0x5)
	poke(c.io.in.packet.debug, 0)
	for(phase <- 0 until payloadLength){
		poke(c.io.in.packet.payload(phase), 10*phase)
	}
	
	
	step(1)
	poke(c.io.in.packetValid, 1)
	step(1)
	poke(c.io.in.packetValid, 0)
	expect(c.io.out.flitValid, 1)
	
	step(1)
	expect(c.io.out.flitValid, 1)
	
	step(1)
	expect(c.io.out.flitValid, 1)
	
	step(1)
	expect(c.io.out.flitValid, 1)
	
	step(1)
	expect(c.io.out.flitValid, 1)
	
	step(1)
	expect(c.io.out.flitValid, 1)
	
	step(2)
	expect(c.io.out.flitValid, 1)
	expect(c.io.in.packetReady, 1)

	step(1)
	expect(c.io.out.flitValid, 1)
	expect(c.io.in.packetReady, 0)
	step(3)
	expect(c.io.out.flitValid, 0)
	expect(c.io.in.packetReady, 0)
	poke(c.io.in.packetValid, 0)
	step(1)
	expect(c.io.out.flitValid, 0)
	expect(c.io.in.packetReady, 1)
	step(10)
	
}
