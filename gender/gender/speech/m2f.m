
%Convert from mel frequency to frequency.

function f = m2f(m)

	f=700*(10.^(m/2595)-1);

end