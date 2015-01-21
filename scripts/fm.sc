s = Server.local.boot;

// add the synthdef
(
SynthDef(\fm, {
	arg gain=0.75, note=60, harmonicity=1.0, modIndex=0.0;
	var mod = SinOsc.ar(note.midicps * harmonicity);
	var car = SinOsc.ar(note.midicps + (modIndex * mod));
	Out.ar(0, [car, car]);
}).load(s);
)

(
x = Synth(\fm, [
	\gain,          0.5,
	\modIndex,      100.0
]);
)

x.set(\note, 60);
x.set(\modIndex, 1000.0);
x.set(\harmonicity, 13.0);

x.free;

(
w = Window("fm", Rect(100, 100, 140, 140));
t = Slider2D(w, Rect(20, 20, 80, 80))
.x_(0.5)
.y_(1)
.action_({ |sl|
	x.set(\modIndex, sl.x * 1000.0);
	x.set(\harmonicity, sl.y * 16.0);
});
w.front;
)
