s = Server.local.boot;

{ SinOsc.ar(220, mul: EnvGen.kr(Env.perc, doneAction: 2)) }.play;

MIDIIn.connect;

(
SynthDef(\testTone, { | out = 0, freq = 440, dur = 1, amp = 0.75 |
	Out.ar(out, SinOsc.ar(freq, mul: EnvGen.kr(Env.perc, doneAction: 2) * amp));
}).add;
)

Synth(\testTone, [ \freq, rrand(36, 84).midicps ]);

(
MIDIIn.noteOn = { | port, chan, note, vel |
	var polyphony = 4;
	// [ "note on", note, vel ].postln;
	Synth(\testTone, [
		\freq, note.midicps,
		\amp,  vel / 127.0 / polyphony
	]);
};
)

(
SynthDef(\polysynth, { | freq = 440, formfreq = 100, gate = 0.0, bwfreq = 800 |
	var x, fund;
	fund = SinOsc.kr(Saw.ar(0.1, 0.03, 0.01), 0, 10, freq);
	x = Formant.ar(fund, formfreq, bwfreq);
	x = EnvGen.kr(Env.adsr, gate, Latch.kr(gate, gate), doneAction: 2) * x;
	Out.ar([0, 1], x);
}).send(s);
)

(
z = Array.newClear(128);
y = -1;

MIDIIn.noteOn = { | port, chan, note, vel |
	var x;
	[ "noteOn", port, chan, note, vel ].postln;
	x = Synth(\polysynth, [
		\freq,      note.midicps / 4.0,
		\gate,      vel / 200.0,
		\formfreq,  vel / 127.0 * 1000.0
	]);
	z.put(note, x);
	y = note;
};

MIDIIn.noteOff = { | port, chan, note, vel |
	[ "noteOff", port, chan, note, vel ].postln;
	z.at(note).set(\gate, 0.0);
};

MIDIIn.bend = { | port, chan, val |
	[ "bend", port, chan, val ].postln;
	[ port, chan, val ].postln;
};
)

(
play {
	var lfsaw = LFSaw.ar([1,0,99], [0,0,6], 2000, 2000);
	SinOsc.ar(OnePole.ar(Mix(lfsaw.trunc([400,600]) * [1,-1]), 0.98)).dup * 0.1
};
)

