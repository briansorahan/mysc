s = Server.local.boot;

(
SynthDef(\sine, {
	arg gate = 1, out = 0, freq = 400, amp = 0.4, pan = 0, ar = 1, dr = 1;
	var audio;
	audio = SinOsc.ar(freq, 0, amp);
	audio = audio * Linen.kr(gate, ar, 1, dr, 2);
	audio = Pan2.ar(audio, pan);
	OffsetOut.ar(out, audio);
}).add;
)

(
a = [
	type:         \note,
	instrument:   'sine',
	freq:         400,
	amp:          0.1,
	pan:          0,
	ar:           2,
	dr:           4,
	sustain:      2
];

e = (
	type:         \note,
	instrument:   'sine',
	freq:         400,
	amp:          0.1,
	pan:          0,
	ar:           2,
	dr:           4,
	sustain:      2
);
)

e.play;
e.delta;

e.asOSC.do { | osc | osc.postcs };

(
( type: \group, id: 2 ).play;
( type: \note, sustain: 100, group: 2 ).play;
)

(
( type: \off, id: 2 ).play;
( type: \kill, id: 2, lag: 3 ).play;
)

( degree: [-3, 0, 2],     sustain: 2,            db: [-20, -20, -10]             ).play;
( degree: [-3, 0, 2],     sustain: 2,            db: [-20, -10, -20]             ).play;
( degree: 0,              sustain: 2,        detune: [0, 3, 5]                   ).play;
( degree: [-3, 2, 4],     sustain: 2,        detune: [0, 3, 5]                   ).play;
( degree: [-3, 2, 4],     sustain: 2,        detune: [0, 0, 0, 3, 3, 3, 5, 5, 5] ).play;

(
var a, x;
a = Pfunc({ exprand(0.1, 2.0) + #[1, 2, 3, 6].choose }, { \reset.postln });
x = a.asStream;
x.nextN(20).postln;
x.reset;
)
