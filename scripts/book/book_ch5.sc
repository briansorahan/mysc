s = Server.local.boot;

10 do: { |i| [i, i.squared, i.isPrime].postln };
{ 10.rand * 3 } ! 5;
30.for(35, { arg i; i.postln });
2.0.forBy(10, 1.5, { arg i; i.postln });

(
var window, routine;
window = Window("close me to stop").front;
window.onClose = { routine.stop };
routine = {
	loop {
		(degree: -10 + 30.xrand, dur: 0.05, amp: 0.1.rand).play;
		0.05.rand.wait;
	}
}.fork;
)

_.squared ! 10;
var pt = _@_.(30, 40);
(1..8).collect([\a, \b, _]);

(
var iterative_factorial;
iterative_factorial = {
	arg n;
	var factorial = 1;
	n do: { |i| factorial = factorial * (i + 1) };
	factorial;
};
// iterative_factorial.(10);
iterative_factorial.def.dumpByteCodes;
)

(
var counter_maker;
var window, button1, button2;

counter_maker = { |max_count|
	var current_count = 0;
	{
		if (current_count == max_count) {
			format("finished counting to %", max_count).postln;
			max_count;
		} {
			current_count = current_count + 1;
			format("counting % of %", current_count, max_count).postln;
			current_count
		}
	}
};

window = Window("Counters", Rect(400, 400, 200, 80));
button1 = Button(window, Rect(10, 10, 180, 20));
button1.states = [["counting to 10"]];
button1.action = counter_maker.(10);
button2 = Button(window, Rect(10, 40, 180, 20));
button2.states = [["counting to 5"]];
button2.action = counter_maker.(5);
window.front;
)

(
var counter_maker, make_counters_gui;

counter_maker = { | max_count |
	var current_count = 0;
	(
		count1: {
			if (current_count == max_count) {
				format("finished counting to %", max_count).postln;
			} {
				current_count = current_count + 1;
				format("counting % of %", current_count, max_count).postln;
			}
		},
		reset_count: {
			format("resetting % counter", max_count).postln;
			current_count = 0;
		},
		max_count: { max_count }
	)
};

make_counters_gui = { | ... counts |
	var counter, window;
	window = Window("Counters", Rect(400, 400, 200, 50 * counts.size + 10));
	window.view.decorator = FlowLayout(window.view.bounds, 5@5, 5@5);
	counts collect: counter_maker.(_) do: { | counter |
		Button(window, Rect(0, 0, 190, 20))
		.states_([["Counting to: " ++ counter.max_count.asString]])
		.action = {counter.count1};
		Button(window, Rect(0, 0, 190, 20))
		.states_([["Reset"]])
		.action = { counter.reset_count };
	};
	window.front;
};

make_counters_gui.(5, 10, 27);

)

(
var degrees, window, button;
window = Window("melodies", Rect(400, 400, 200, 200));
button = Button(window, window.view.bounds.insetBy(10, 10));
button.states = [["click me to add a note"]];
button.action = {
	degrees = degrees add: 0.rrand(15);
	Pbind(\degree, Pseq(degrees), \dur, Prand([0.1, 0.2, 0.4], inf)).play;
};
window.front;
)

(a: 1, b: 2)[\a];

Set[1, 2, 3, 4, 5] select: (_ > 2);

(4..8) *.s (5..12);

(
var fact = { |n|
	Array.series(n - 1, 2).inject(1, _*_);
};
fact.(6);
)

Array.series(5, 10, 2);



Object.dumpClassSubtree;

(

SynthDef(\ping, {
	arg freq = 440;
	Out.ar(0, SinOsc.ar(freq, 0, EnvGen.kr(Env.perc(level: 0.1), doneAction: 2)));
}).add;

SynthDef(\wham, {
	Out.ar(0, BrownNoise.ar(EnvGen.kr(Env.perc(level: 0.1), doneAction: 2)));
}).add;

)

~sound_adapter = {
	arg counter, what, count;
	switch(what,
		\reset, { Synth(\wham); },
		\max_reached, { counter.reset },
		\count, {
			Synth(\ping, [
				\freq, count.postln * 10 + counter.max_count * 20
			])
		}
	)
};

~make_display = {
	arg counter;
	var window, rect, label, adapter, stagger;
	stagger = UniqueID.next % 20 * 20 + 400;
	rect = Rect(stagger, stagger, 200, 50);
	window = Window("counting to " ++ counter.max_count.asString, rect);
	label = StaticText(window, window.view.bounds.insetBy(10, 10));
	adapter = { | counter, what, count |
		{ label.string = counter.current_count.asString }.defer
	};
	counter addDependant: adapter;
	// remove the adapter when window closes to prevent error in
	// updating non-existent views
	window.onClose = { counter removeDependant: adapter };
	window.front
};

~counters = (6, 11 .. 26) collect: Counter.new(_);
~counters do: _.addDependant(~sound_adapter);
~count = {
	loop {
		~counters do: _.count1;
		0.25.wait
	}
}.fork;

~counters do: ~make_display.(_);
