// Boot the server before doing anything
s = Server.local.boot;

// Run the synth
(
var makeSynth = {
	arg buf;

	play({
		var grains = GrainBuf.ar(
			numChannels:   2,
			trigger:       Impulse.kr(LFNoise1.kr(0.025).range(2, 40)),
			dur:           LFNoise1.kr.range(0.01, 0.2),
			sndbuf:        buf,
			rate:          LFNoise1.kr.range(0.5, 2),
			pos:           LFNoise2.kr(0.1).range(0, 1),
			interp:        2,
		);

		FreeVerb.ar(grains);
	});
};

var onBoot = {
	var file = "~/mysc/kalimba_mono.wav".standardizePath;
	var buf = Buffer.read(s, file, action: makeSynth);
};

s.waitForBoot(onBoot);
)