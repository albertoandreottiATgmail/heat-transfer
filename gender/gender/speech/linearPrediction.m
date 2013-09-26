%Extract features from a single frame using LPC, the input window should be already windowed.
%length(sample_window) is 2*length(frame)
function [pitch, formants] =  linearPrediction(sample_window, Fs)

	%8 coefficients (rule of thumb?: polinomial order is two times the expected number of formants plus 2)
	ncoeffs = 12;
	A = lpc(sample_window, ncoeffs);
	rts = roots(A);
		
	%Choose the roots that have one sign for the imaginary part
	rts = rts(imag(rts)>=0);
	angz = atan2(imag(rts),real(rts));
	
	%bandwidths of the formants
	[frqs,indices] = sort(angz.*(Fs/(2*pi)));
	bw = -1/2*(Fs/(2*pi))*log(abs(rts(indices)));
	
	%formant frequencies should be greater than 90 Hz with bandwidths less than 400 Hz
	nn = 1;
	for kk = 1:length(frqs)
		if (frqs(kk) > 90 && bw(kk) <400)
			formants(nn) = frqs(kk);
			nn = nn+1;
		end
	end
	
	pitch = 0;

	return
	%TODO: can we do this even faster?
	padded_window = [zeros(length(A),1); sample_window];
	for i=2:length(sample_window)
		convoluted(i) = A(2:end)*fliplr(padded_window(i:length(A)+i-2));
	end
	
	%TODO: Check that this faster solution is still working as expected
	%convoluted = fftconv(A(2:end), sample_window);
	
	%Autocorrelation, small displacement.
	error = sample_window + convoluted(ncoeffs:end)(:);
	correlated = xcorr(error);
	[PKS LOC] = findpeaks(correlated, "DoubleSided");
	lags = abs(diff(LOC(:)));
	candidates = 0; j = 1;
	
	%Examine all possible lag candidates
	for i=1:length(lags)
		%Accept lag values tha may lead to possible pitch periods
		if lags(i)>53 && lags(i)<200
			candidates(j) = lags(i);
			j = j + 1;
		end
	end
	
	if (length(candidates)>1)
	    pitch = Fs/mean(candidates);
	else
		pitch = 0;
	end
	
end
