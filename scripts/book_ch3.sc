s = Server.local.boot;

~something = { Pulse.ar(80) * EnvGen.ar(Env.perc, doneAction: 2); };
~something.play;

SystemClock.sched(1, { "foo".postln; 1.0.rand });
SystemClock.clear;

SystemClock.sched(1, { SCWindow.new.front });

(
t = TempoClock.new;
t.sched(0, {"hello".postln; 1});
)
t.tempo = 2;
t.clear;

(
r = Routine({
	"foo".yield;
	"bar".yield;
	"baz".yield
});
)
r.value;

(
r = Routine({
	"foo".postln;
	1.yield;
	"bar".postln;
	1.yield;
	"foobar".postln;
});
SystemClock.sched(0, r);
)

(
r = Routine({
	x = Synth(\default, [ \freq, 76.midicps ]);
	1.wait;

	x.release(0.1);
	y = Synth(\default, [ \freq, 73.midicps ]);
	"Waiting...".postln;
	nil.yield; // fermata

	y.release(0.1);
	z = Synth(\default, [ \freq, 69.midicps ]);
	2.wait;
	z.release;
});
// play, then wait for the fermata
r.play;
// feel the sweet tonic...
r.play;
)

(

t = Task({
	loop({
		3.do({
			x.release(0.1);
			x = Synth(\default, [ \freq, 76.midicps ]);
			0.5.wait;
			x.release(0.1);
			x = Synth(\default, [ \freq, 73.midicps ]);
			0.5.wait;
		});
		"I'm waiting for you to press resume".postln;
		nil.yield; // fermata
		x.release(0.1);
		x = Synth(\default, [ \freq, 69.midicps ]);
		1.wait;
		x.release;
	});
});

w = Window.new("Task Example", Rect(400, 400, 200, 30)).front;
w.view.decorator = FlowLayout(w.view.bounds);

Button.new(w, Rect(0, 0, 100, 20)).states_([[
	"Play/Resume", Color.black, Color.clear
]]).action_({ t.resume(0); });

Button.new(w, Rect(0, 0, 40, 20)).states_([[
	"Finish", Color.black, Color.clear
]]).action_({
	t.stop;
	x.release(0.1);
	w.close;
});

)

(

r = Routine({
	c = TempoClock.new;
	// start a 'wobbly' loop
	t = Task({
		loop({
			x.release(0.1);
			x = Synth(\default, [ \freq, 61.midicps, \amp, 0.2 ]);
			0.2.wait;
			x.release(0.1);
			x = Synth(\default, [ \freq, 67.midicps, \amp, 0.2 ]);
			rrand(0.075, 0.25).wait; // random wait
		});
	}, c);
	t.start;
	nil.yield;

	// add some notes
	y = Synth(\default, [ \freq, 73.midicps, \amp, 0.3 ]);
	nil.yield;
	y.release(0.1);
	y = Synth(\default, [ \freq, 79.midicps, \amp, 0.3 ]);
	c.tempo = 2; // double time
	nil.yield;
	t.stop;
	y.release(1);
	x.release(0.1);
});

)

r.next; // start loop
r.next; // first note
r.next; // second note, loop goes 'double time'
r.next; // stop loop and fade

( // random notes from the lydian b7 scale
p = Pxrand([64, 66, 68, 70, 71, 73, 74, 76], inf).asStream;
// ordered sequence of durations
q = Pseq([1, 2, 0.5], inf).asStream;
t = Task({
	loop({
		x.release(2);
		x = Synth(\default, [ freq: p.value.midicps ]);
		q.value.wait;
	});
});
t.start;
)
t.stop; x.release(2);

( // using 'messaging style' Score
SynthDef(\ScoreSine, {
	arg freq = 440;
	Out.ar(0, SinOsc.ar(freq, 0, 0.2) * Line.kr(1, 0, 0.5, doneAction: 2))
}).add;
x = [
	// args for s_new are synthdef, nodeID, addAction, targetID, synth args...
	[0.0, [ \s_new, \ScoreSine, 1000, 0, 0, \freq, 1413 ]],
	[0.5, [ \s_new, \ScoreSine, 1001, 0, 0, \freq, 712  ]],
	[1.0, [ \s_new, \ScoreSine, 1002, 0, 0, \freq, 417  ]],
	[2.0, [ \c_set, 0, 0 ]], // dummy command to mark end of NRT synthesis time
];
z = Score(x);
z.play;
)

(
// play from a buffer, with a fadeout
SynthDef(\playbuf, { arg out = 0, buf, gate = 1;
	var pb, ampenv;
	pb = PlayBuf.ar(1, buf, BufRateScale.kr(buf), loop: 1.0);
	ampenv = Linen.kr(gate, doneAction: 2);
	Out.ar(out, pb * ampenv * 0.6);
}).add;
)
// load some sounds
~the_sounds = "~/Music/BrianSorahan/*".pathMatch.collect { | path | Buffer.read(s, path) };
~nowPlaying = Synth(\playbuf, [buf: ~the_sounds[0]]);
// change the index below to play a different sound
~nowPlaying.release; ~nowPlaying = Synth(\playbuf, [ buf: ~the_sounds[4]]);

(
// a counter
n = 0;
// GUI code
w = Window.new("Simple CuePlayer", Rect(400, 400, 200, 30)).front;
w.view.decorator = FlowLayout(w.view.bounds);
// this will play each cue in turn
Button.new(w, Rect(0, 0, 80, 20)).states_([[
	"Play Cue", Color.black, Color.clear
]]).action_({
	if (n < ~the_sounds.size) {
		if (n != 0) {
			~nowPlaying.release;
		};
		~nowPlaying = Synth(\playbuf, [ buf: ~the_sounds[n]]);
		n = n + 1;
	}
});
// this sets the counter to the first buf
Button.new(w, Rect(0, 0, 80, 20)).states_([[
	"Stop / Reset", Color.black, Color.clear
]]).action_({
	n = 0;
	~nowPlaying.release;
});
// free the buffers when the window is closed
w.onClose = { ~the_sounds do: _.free };
)

(
f = 40;
n = 50;
{
	Mix.fill(n, { |i|
		Resonz.ar(Dust2.ar(5), f * (i + 1), 0.001, 100)
	}) * n.reciprocal;
}.play;
)

(
var file, soundPath;
~buffers = List[];
Dialog.getPaths({
	arg paths;
	paths.do({
		arg soundPath;
		soundPath.postln;
		~buffers.add(Buffer.read(s, soundPath));
	});
});
)

~buffers;
~buffers.size;
~buffers[4].release;
~buffers[5].stopAll;

(
SynthDef(\samplePlayer, {
	arg out = 0, buf = 0, rate = 1, at = 0.01, rel = 0.1, pos = 0, pSpeed = 0, lev = 0.5;
	var sample, panT, amp, aux;
	sample = PlayBuf.ar(1, buf, rate * BufRateScale.kr(buf), 1, 0, 0);
	panT = FSinOsc.kr(pSpeed);
	amp = EnvGen.ar(Env.perc(at, rel, lev), doneAction: 2);
	Out.ar(out, Pan2.ar(sample, panT, amp));
}).add;
)

(
Synth(\samplePlayer, [
	\out, 0,
	\bufnum, ~buffers[1],
	\rel, 0.25
]);
)

(
~stut = Routine({
	var dur, pos;
	~stutPatt = Pseq([ Pgeom(0.1, 1.1707, 18), Pn(0.1, 1), Pgeom(0.1, 0.94, 200) ]);
	~str = ~stutPatt.asStream;
	100.do {
		dur = ~str.next;
		dur.postln;
		~sample = Synth(\samplePlayer, [
			\out, 0,
			\buf, ~buffers[3],
			\at, 0.1,
			\rel, 0.05,
			\pSpeed, 0.5
		]);
		dur.wait;
	};
});
)

~stut.play;
~stut.reset;

Pbind(\freq, Prand([300, 500, 231.2, 399.2], 30), \dur, 0.1).play;

~gest1 = Pbind(\instrument, \samplePlayer, \dur, 2, \rel, 1.9);
~player = ~gest1.play;
~player.stream = Pbind(\instrument, \samplePlayer, \dur, 1/8, \rate, Pxrand([1/2, 1, 2/3, 4], inf), \rel, 0.9).asStream;
~player.stop;

~gest2 = Pbind(\instrument, \samplePlayer, \dur, Pgeom(0.01, 1.1707, 20), \rel, 1.9);
~gest2.play;

Pbind(\instrument, \samplePlayer, \dur, Pseq([ Pgeom(0.01, 1.1707, 20), Pgeom(0.01, 0.93, 20) ], 1), \rel, 1.9, \pSpeed, 0.5).play;
Pbind(\instrument, \samplePlayer, \dur, Pseq([ Pgeom(0.01, 1.1707, 20), Pgeom(0.01, 0.93, 20) ], 1), \rate, Pxrand([ 1/2, 1, 2/3, 4 ], inf), \rel, 1.9, \pSpeed, 0.5).play;

~rhythm1 = Pseq([ 1/4, 1/4, 1/8, 1/12, 1/24, nil ]);
~gest3 = Pdef(\a, Pbind(\instrument, \samplePlayer, \dur, ~rhythm1, \rel, 1.9, \pSpeed, 0.5));
~gest3.play;

~rhythm1 = Pseq([ 1/64, 1/64, 1/64, 1/32, 1/32, 1/32, 1/32, 1/24, 1/16, 1/12, nil ]);

~gest4 = Pdef(\a, Pbind(\instrument, \samplePlayer, \att, 0.5, \rel, 3, \lev, { rrand(0.1, 0.2) }, \dur, 0.05, \rate, Pseq([ Pbrown(0.8, 1.01, 0.01, 20) ])));
~gest4.play;


