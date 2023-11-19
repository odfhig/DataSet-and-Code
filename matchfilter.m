function signal_match = matchfilter(signal, ref)

    signal = reshape(signal, 480, []);
    ht = conj(fliplr(ref));
    Hf = fft(ht);
    for ii = 1 : size(signal, 2)
        Sf = fft(signal(:, ii));
        signal_match{ii} = ifft(fftshift(Hf).*fftshift(Sf));
    end
    signal_match = reshape(signal_match, 1, []);
    
end