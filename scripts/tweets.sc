s = Server.local.boot;

(
// randomized clicking and percussion
play{
	t = Impulse.ar(6) + Dust.ar(1);
	x = (TExpRand.ar(_,_,t));
	Limiter.ar(GVerb.ar(GrainFM.ar(2,t,x.(1e-4,0.01),f=x.(80,400),f/4,9),9,0.5))
};
)

(
// the landing of the lunar module
play {
	l = LFGauss.ar(4, 1/8);
	a = Blip.ar(60,4,l);
	a = a/4 + LocalIn.ar(2);
	a = FreqShift.ar(a,LFNoise0.kr(1/4,90));
	LocalOut.ar(DelayC.ar(a,1,0.1,0.9));
	a
}
)

// be careful!!
{RHPF.ar(GbmanN.ar([2300,1150]),LFSaw.ar(Pulse.ar(4,[1,2]/8,1,LFPulse.ar(1/8)/5+1))+2)}.play;

// random square-wave honks
{LocalOut.ar(a=DynKlank.ar(`[LocalIn.ar.clip2(LFPulse.kr([1,2,1/8]).sum/2)**100*100],Impulse.ar(10)));HPF.ar(a).clip2!2}.play

// really pretty
{GVerb.ar(SinOsc.ar(Select.kr(Hasher.kr(Duty.kr((1..4)/4,0,Dwhite(0,1)))*5,midicps([0,3,5,7,10]+60))).sum,200,3)/20}

// bass drum beats, yo
play{l=LocalIn.ar(2)+Decay.ar(CoinGate.ar(0.3,Impulse.ar(8)));l=BPF.ar(l,99,2);LocalOut.ar(DelayN.ar(l,1,LFPulse.kr(1/4,1/4,1/2)));l}

// rising tones
play{a=0;6.do{x=Sweep.ar(Dust2.kr(0.1+2.0.rand),9.rand+9)+LFNoise1.kr(0.1,60,80);a=a+Pan2.ar(Gendy1.ar(1,1,1,1,x,x+9),LFNoise2.kr(1))};a}
