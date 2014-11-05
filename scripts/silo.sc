s = Server.local.boot;

b = Buffer.read(s, "~/mysc/kalimba_mono.wav".standardizePath);

(
play({
	GrainBuf.ar(
		numChannels:   2,
		trigger:       Impulse.kr(LFNoise1.kr(0.025).range(2, 40)),
		dur:           LFNoise1.kr.range(0.01, 0.2),
		sndbuf:        b,
		rate:          LFNoise1.kr.range(0.5, 2),
		pos:           LFNoise2.kr(0.1).range(0, 1),
		interp:        2,
	)
});
)
