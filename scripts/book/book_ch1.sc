s = Server.local.boot;

(
play({
	GVerb.ar(
		RLPF.ar(
			Dust.ar([2, 85]),
			LFNoise1.ar(1/[3, 4], 1500, 1600),
			0.02
		),
		drylevel: 1
	)
});
)

(
play({
	var sines = 50, speed = 16;
	Mix.fill(
		sines,
		{ arg x;
			Pan2.ar(
				SinOsc.ar(
					exprand(100, 10000),
					mul: max(0, LFNoise1.kr(speed) + Line.kr(1, -1, 30))
				),
				rand2(1.0)
			)
		}
	) / sines;
})
)

(
play({
	CombN.ar(
		SinOsc.ar(
			midicps(
				LFNoise1.ar(
					freq:   3,
					mul:    24,
					add:    LFSaw.ar([5, 5.123], 0, 3, 80)
				)
			),
			phase:   0,
			mul:     0.4
		),
		maxdelaytime: 1,
		delaytime:    0.3,
		decaytime:    2
	)
})
)