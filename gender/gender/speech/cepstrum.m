%Compute cepstral coefficients, perform low pass liftering.
%receives a window, returns the coefficients.
function aspec2 = cepstrum(window, plot_progress)

	% Set default value for plot progress
	if ~exist('window', 'var') || isempty(window)
		return;
	end

	% Set default value for plot progress
	if ~exist('plot_progress', 'var') || isempty(plot_progress)
		plot_progress = false;
	end
	
	%Windowing with Hamming window
	snd32=window.*hamming(length(window));
	
	
	ftSnd32=fft(snd32);
	aftSnd32=abs(ftSnd32);
	
	
	laftSnd32=log(aftSnd32);
	cepstrum=ifft(laftSnd32);
	%plot(real(cepstrum(2:300)));
	%pause
	
	cliftered=zeros(1,length(cepstrum));
    cliftered(1:5)=cepstrum(1:5);
    aspec=abs(exp(fft(cliftered)));
    center = int32(length(aspec)/2);
	aspec2 = aspec(center:end);

	%Done with the function per se, now plot if required
	if plot_progress
	
		plot(aspec2);
	    pause;
		
		semilogy(real(aspec))
		fprintf("Now we plot the liftered Cepstrum");
		pause
	
		%Take different chunks of the cepstrum
		clift1=zeros(1,length(cepstrum))
		clift2=clift1;
		clift3=clift1;
		clift4=clift1;
		clift1(1:40)=cepstrum(1:40);
		clift2(1:20)=cepstrum(1:20);
		clift3(1:5)=cepstrum(1:5);
	
		%high pass
		clift4(70:length(cepstrum))=cepstrum(70:length(cepstrum));
	
		m1=abs(exp(fft(clift1)));
		m2=abs(exp(fft(clift2)));
		m3=abs(exp(fft(clift3)));
		m4=abs(exp(fft(clift4)));
		
		fprintf("Now we plot the 40, 20, 5 coefficients liftered Cepstrum");
		pause
		semilogy(real(m1),'r',real(m2),'g',real(m3),'b')
		
		aspec=abs(fft(snd32));
		fprintf("Now we plot the high pass liftered Cepstrum");
		pause
		semilogy(aspec);
	end

end