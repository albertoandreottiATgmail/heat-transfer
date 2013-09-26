%Extract formant frequencies from a single frame using LPC, the input window should be already windowed.
%length(sample_window) is 2*length(frame)
function [formants] =  LPCFormants(sample_window, Fs)

	%8 coefficients (rule of thumb?: polinomial order is two times the expected number of formants plus 2)
	A = lpc(sample_window, 12);
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
end