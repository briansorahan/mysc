(
// array to store the synth instances
~voices = List.newClear(128);

// free the ith voice
~freeVoice = { |i|
	~voices.at(i).free;
	~voices.removeAt(i);
};

~onBoot = {
	var harmonicity, modIndex;

	SynthDef(\poly, {
		arg voice=0, note=60, velocity=96,
		// fm
		harmonicity=1.0, modIndex=100.0,
		// amp envelope
		ampAttack=0.01, ampDecay=1, ampSustain=1.0, ampRelease=0.01,
		// mod envelope
		modAttack=0.01, modDecay=15, modSustain=0.0, modRelease=0.01;
		// patch
		var gain = velocity / 128.0;
		var ampEnv = EnvGen.kr(Env.adsr(ampAttack, ampDecay, ampSustain, ampRelease), doneAction: 2);
		var modEnv = EnvGen.kr(Env.adsr(modAttack, modDecay, modSustain, modRelease));
		var mod = SinOsc.ar(note.midicps * harmonicity, mul: modEnv);
		var car = SinOsc.ar(note.midicps + (modIndex * mod), mul: ampEnv * gain);
		// output
		Out.ar(0, [car, car]);
	}).load(s);

	harmonicity=1.0;
	modIndex=100.0;

	// setup MIDI interface
	MIDIIn.connectAll;
	MIDIFunc.noteOn({
		arg velocity, note;
		var synth, voiceIndex;
		[note, velocity].postln;
		synth = Synth(\poly, [
			\note,           note,
			\velocity,       velocity,
			\harmonicity,    harmonicity,
			\modIndex,       modIndex
		]);
		~voices.put(note, synth);
		synth.onFree({
			~voices.removeAt(note);
		});
	});
	MIDIFunc.noteOff({
		arg velocity, note;
		~voices.at(note).free;
	});
	MIDIFunc.cc({
		arg val, ctl;
		switch(ctl,
			16,  { harmonicity = val * 8; },
			17,  { modIndex    = val * 16; },
			18,  { [ctl, val].postln; },
			19,  { [ctl, val].postln; },
			13,  { [ctl, val].postln; }
		);
	});
};

// boot the server
~server = Server.local.boot;
~server.waitForBoot(~onBoot);
)