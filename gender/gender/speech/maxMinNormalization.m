%Mean normalization
function normalized = maxMinNormalization(fvector)

	for i=1:size(fvector,2)
	    maxMin = max(fvector(:, i)) - min(fvector(:, i));
		fvector(:, i) = fvector(:, i)./maxMin;
	end
	normalized = fvector;
end

