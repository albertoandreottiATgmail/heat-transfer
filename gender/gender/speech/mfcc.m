%MFCC computation
%receives a window, returns the mfccs.

function coefs = mfcc(window)
     
	ftSnd32=fft(window);
	aftSnd32=abs(ftSnd32);
	
	%cepstrum
	laftSnd32=log(aftSnd32);
	cepstrum=ifft(laftSnd32);
	
	%low pass liftering
	lcepstrum = zeros(1, length(cepstrum));
	lcepstrum(1:15) = cepstrum(1:15);
	aftSnd32=abs(exp(fft(lcepstrum)));
	
	%Pass signal thru the Mel bank
	windowSize = length(window);
	melFB;
	aftSnd32 = aftSnd32(1:windowSize/2)*melFilterBank;
	semilogy(kCenterFreq(2:end-1), aftSnd32,'r+-');
	coefs = aftSnd32;
end