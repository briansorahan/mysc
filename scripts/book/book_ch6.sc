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

Synth(\sine);

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

(
Pbind( *[
	stretch:      Pseg([0, 0.1, 0.2, 1], 8).linexp(0, 1, 1, 0.125),
	midinote:     100.cpsmidi,
	harmonic:     Pwhite(1, 16),
	legato:       Pkey(\stretch) * Pkey(\harmonic) / 2,
	db:           -10 - Pkey(\harmonic),
	detune:       Pwhite(0.0, 3.0),
	dur:          0.2
]).play;
)

(
a = Pbind( *[
	scale: Pn( Pstep([ [0, 2, 4, 5, 7, 9, 11], [0, 1, 3, 5, 6, 8, 11] ], 5)),
	db: Pn( Pseg([-20, -30, -25, -30], 0.4))
]);

b = Pbind( *[
	degree: Pbrown(0, 6, 1),
	mtranspose: Prand([\rest, Pseq([0], 5.rand)], inf),
	dur: 0.2,
	octave: 6
]);

c = Pbind( *[
	degree: [0, 2, 4],
	mtranspose: Pbrown(0, 6, 1),
	dur: 0.4,
	db: -35
]);

d = Pchain(Ppar([b, c]), a);
d.play;
)

(
Prout({ |ev|
	ev = (freq: 400).embedInStream(ev.copy);
	ev = (freq: 500).embedInStream(ev.copy);
	ev = (freq: 600).embedInStream(ev.copy);
	ev = (freq: 700).embedInStream(ev.copy);
	ev = (freq: 800).embedInStream(ev.copy);
}).play;
)

(
Prout({ |ev|
	var pat, refPat;
	refPat = Pbind( *[ dur: 0.2, note: Pseq([0,0,0,7,0,7]) ]);

	loop {
		ev = refPat.embedInStream(ev);
		pat = Pbind( *[
			dur: [0.2, 0.4].choose,
			note: Pseq(Array.fill(5, { 10.rand }), 3.rand)
		]);

		ev = pat.embedInStream(ev);
	}
}).play;
)

(
~patA = Pbind( *[
	dur: 0.2,
	degree: Prout({ |ev|
		var noteArray = (0..5);
		loop {
			ev = Pseq(noteArray).embedInStream(ev);
			noteArray[6.rand] = 7.rand;
		}
	})
]);

~patB = Prout({ |ev|
	var pat, pats = [
		Pbind( *[ degree: Pseq([0, 7]), dur: 0.2 ]),
		Pbind( *[ degree: Pseq([11, 7]), dur: 0.2 ]),
		Pbind( *[ degree: Pseq([16, 7]), dur: 0.2 ]),
		(type: \rest, delta: 1)
	];

	loop {
		pat = pats.choose;
		ev = pat.embedInStream(ev);
	};
});

Pchain(
	Pbind( *[
		db: Pn(Pstep([-15, -25, -25, -20, -30, -25], 0.2)) + Pseq([-30, -5, -10, -40], 12)
	]),
	Ptpar([
		0, ~patA,
		0, ~patA,
		12, ~patB
	])
).play;
)

s = Server.local.boot;

(freq: 220).play;

(
// ERROR: SynthDef default not found
~pattern = Pbind( *[
	instrument: \default,
	freq: Pseq([100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100], 5),
	db: Pseq([-10, -30, -20, -30], inf),
	dur: Pseq([0, 2, 0.2, 0.2, 0.2, 0.4, 0.4, 0.8], inf),
	legato: Pseq([2, 0.5, 0.75, 0.5, 0.25], inf)
]);

~score = ~pattern.asScore(24 * 11/7);
~score.render(thisProcess.platform.recordingsDir ++ "/test.wav");
SoundFile(thisProcess.platform.recordingsDir ++ "/test.wav").play;
)
