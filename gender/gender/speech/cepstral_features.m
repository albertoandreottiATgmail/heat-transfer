%Compute speech features for a short window, based on Cepstrum
function [pitch, formants] = cepstral_features(window)

	%cepstrum
	ftSnd32=fft(window);
	aftSnd32=abs(ftSnd32);
	laftSnd32=log(aftSnd32);
	_cepstrum=ifft(laftSnd32);
	_cepstrum = _cepstrum(1:length(_cepstrum)/2);
	
	%high pass liftering
	lcepstrum = zeros(1, length(_cepstrum));
	lcepstrum(50:end) = _cepstrum(50:end);
	
	[y_val, pitch] = max(real(lcepstrum));
	%plot(real(lcepstrum));
	formants = 0;
	pitch = 16000/pitch;
end