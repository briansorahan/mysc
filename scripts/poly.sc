
// initialization
(
s = Server.local.boot;

SynthDef(\poly, {
	arg note, gain,
	// fm
	harmonicity, modIndex,
	// amp envelope
	ampAttack, ampDecay, ampSustain, ampRelease,
	// mod envelope
	modAttack, modDecay, modSustain, modRelease;

	var mod = SinOsc.ar(note.midicps * harmonicity);
	var car = SinOsc.ar(note.midicps + (modIndex * mod));
	var ampEnv = EnvGen.kr(Env.adsr(ampAttack, ampDecay, ampSustain, ampRelease));
	var modEnv = EnvGen.kr(Env.adsr(modAttack, modDecay, modSustain, modRelease));
	Out.ar(0, [car, car]);
}).load(s);

~polyphony = 4;
~voices = Array.new(~polyphony);

// add the ith voice
~createVoice = { |i|
	~voices.put(i, Synth(\poly, []));
};

// free the ith voice
~freeVoice = { |i|
	~voices.at(i).free;
};
)



// create voices
(
~polyphony.do(~createVoice);
)



// setup MIDI interface
(
)



// setup OSC interface
(
)
