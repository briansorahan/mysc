s = Server.local.boot;

b = Buffer.read(s, "~/mysc/paddling_mono.wav".standardizePath);

(
play({
	var grainDur = 0.01; //sec

	GrainBuf.ar(
		numChannels:   2,
		trigger:       Impulse.kr(1 / grainDur),
		dur:           grainDur,
		sndbuf:        b,
		rate:          1,
		pos:           SinOsc.kr(freq: 0.0625).range(0, 1),
		interp:        2,
	)
});
)

(
{Splay.ar(Ringz.ar(Impulse.ar([2, 1, 4], [0.1, 0.11, 0.12]), [0.1,
0.1, 0.5])) * EnvGen.kr(Env([1, 1, 0], [120, 10]), doneAction: 2)}.play;
)

{t=HPZ1.kr(LFNoise0.kr(4));{Pulse.ar((t*10000+0.0001).lag(0, 0.1))}.dup+(SinOsc.ar([220, 330])*Integrator.kr(t))*0.1}.play;
