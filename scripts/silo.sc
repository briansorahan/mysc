// Boot the server
~server = Server.local.boot;

// Create a synth
~makeSynth = {
	arg buf;
	var synth = Synth(\silo, [
		\buf,             buf
	]);
};

// Function to run when the server boots
~onBoot = {
	var def, file, buf;

	def = SynthDef.new(\silo, {
		arg buf, out=0;

		var grains = GrainBuf.ar(
			numChannels:   2,
			trigger:       Impulse.kr(LFNoise1.kr(0.025).range(2, 40)),
			dur:           LFNoise1.kr.range(0.01, 0.2),
			sndbuf:        buf,
			rate:          LFNoise1.kr.range(0.5, 2),
			pos:           LFTri.kr(0.1).range(0, 1),
			interp:        2,
		);

		var output = FreeVerb.ar(grains);

		Out.ar(out, [output, output]);
	});

	def.load(s);

	file = "~/mysc/kalimba_mono.wav".standardizePath;
	buf = Buffer.read(s, file, action: ~makeSynth);
};

// Start it
~server.waitForBoot(~onBoot);