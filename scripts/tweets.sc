s = Server.local.boot;

(
play{
	t = Impulse.ar(6) + Dust.ar(1);
	x = (TExpRand.ar(_,_,t));
	Limiter.ar(GVerb.ar(GrainFM.ar(2,t,x.(1e-4,0.01),f=x.(80,400),f/4,9),9,0.5))
};
)

(
play {
	l = LFGauss.ar(4, 1/8);
	a = Blip.ar(60,4,l);
	a = a/4 + LocalIn.ar(2);
	a = FreqShift.ar(a,LFNoise0.kr(1/4,90));
	LocalOut.ar(DelayC.ar(a,1,0.1,0.9));
	a
}
)

{RHPF.ar(GbmanN.ar([2300,1150]),LFSaw.ar(Pulse.ar(4,[1,2]/8,1,LFPulse.ar(1/8)/5+1))+2)}.play;

{LocalOut.ar(a=DynKlank.ar(`[LocalIn.ar.clip2(LFPulse.kr([1,2,1/8]).sum/2)**100*100],Impulse.ar(10)));HPF.ar(a).clip2!2}.play

// really pretty
{GVerb.ar(SinOsc.ar(Select.kr(Hasher.kr(Duty.kr((1..4)/4,0,Dwhite(0,1)))*5,midicps([0,3,5,7,10]+60))).sum,200,3)/20}

{l=LocalIn.ar(2)+Decay.ar(CoinGate.ar(0.3,Impulse.ar(8)));l=BPF.ar(l,99,2);LocalOut.ar(DelayN.ar(l,1,LFPulse.kr(1/4,1/4,1/2)));l}

play{SendTrig.kr(Impulse.kr(4),1,Sweep.kr(Impulse.kr(1/8),1)+1)};OSCresponder(nil,'/tr',{|…m|round(m@2@3).asString.speak(0,true)}).add
